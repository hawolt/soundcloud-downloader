package com.hawolt;

import com.hawolt.data.media.MediaInterface;
import com.hawolt.data.media.MediaLoader;
import com.hawolt.data.media.download.DownloadCallback;
import com.hawolt.data.media.hydratable.Hydratable;
import com.hawolt.data.media.hydratable.HydratableInterface;
import com.hawolt.data.media.hydratable.Hydration;
import com.hawolt.data.media.hydratable.impl.playlist.Playlist;
import com.hawolt.data.media.hydratable.impl.track.Track;
import com.hawolt.data.media.hydratable.impl.user.User;
import com.hawolt.http.Response;
import com.hawolt.logger.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created: 09/02/2022 11:41
 * Author: Twitter @hawolt
 **/

public class Soundcloud {
    private static final Map<String, MediaInterface<? extends Hydratable>> MAPPING = new HashMap<>();
    private static final Map<Class<? extends Hydratable>, List<HydratableInterface<? extends Hydratable>>> MANAGER = new HashMap<>();

    static {
        MAPPING.put("user", (MediaInterface<User>) User::new);
        MAPPING.put("playlist", (MediaInterface<Playlist>) Playlist::new);
        MAPPING.put("sound", (MediaInterface<Track>) object -> new Track(object.getJSONObject("data")));
    }

    public static void overwrite(String type, MediaInterface<? extends Hydratable> mediaInterface) {
        MAPPING.put(type, mediaInterface);
    }

    public static void register(Class<? extends Hydratable> clazz, HydratableInterface<? extends Hydratable> manager) {
        if (!MANAGER.containsKey(clazz)) MANAGER.put(clazz, new ArrayList<>());
        MANAGER.get(clazz).add(manager);
    }

    @SuppressWarnings("all")
    private static <T> T modify(Object type) {
        return (T) type;
    }

    public static void load(String link) {
        load(link, null);
    }

    public static void load(String source, DownloadCallback callback) {
        String link = source.split("\\?")[0];
        Logger.debug("Track {}", link);
        Hydratable.EXECUTOR_SERVICE.execute(() -> {
            try {
                MediaLoader loader = new MediaLoader(link);
                Response response = loader.call();
                JSONArray hydration = Hydration.from(response.getBodyAsString());
                Map<String, JSONObject> available = new HashMap<>();
                for (int i = 0; i < hydration.length(); i++) {
                    JSONObject object = hydration.getJSONObject(i);
                    if (!object.has("hydratable")) continue;
                    String hydratable = object.getString("hydratable");
                    if (!MAPPING.containsKey(hydratable)) continue;
                    available.put(hydratable, object);
                }
                String hydratable = available.containsKey("sound") ?
                        "sound" : available.containsKey("playlist") ?
                        "playlist" : available.containsKey("user") ?
                        "user" : null;
                if (hydratable == null) return;
                Logger.debug("Hydratable {} for {}", hydratable, link);
                CompletableFuture.supplyAsync(() -> MAPPING.get(hydratable).convert(available.get(hydratable))).whenComplete((capture, e) -> {
                    if (e != null) Logger.error(e);
                    if (capture == null) return;
                    Logger.debug("Forward {} for {}", hydratable, link);
                    for (HydratableInterface<? extends Hydratable> hydratableInterface : MANAGER.get(capture.getClass())) {
                        hydratableInterface.accept(link, modify(capture));
                    }
                });
            } catch (Exception e) {
                if (callback == null) Logger.error("{} {}", e.getMessage(), link);
                else callback.onLoadFailure(link, e);
            }
        });
    }
}
