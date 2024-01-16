package com.hawolt;

import com.hawolt.data.VirtualClient;
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

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Created: 09/02/2022 11:41
 * Author: Twitter @hawolt
 **/

public class Soundcloud {
    private static final Map<String, MediaInterface<? extends Hydratable>> MAPPING = new HashMap<>();
    private static final Map<Class<? extends Hydratable>, List<HydratableInterface<? extends Hydratable>>> MANAGER = new HashMap<>();

    static {
        MAPPING.put("user", User::new);
        MAPPING.put("playlist", Playlist::new);
        MAPPING.put("sound", (timestamp, object) -> new Track(timestamp, object.getJSONObject("data")));
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
                JSONArray hydration = Hydration.from(response);
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
                CompletableFuture.supplyAsync(() -> MAPPING.get(hydratable).convert(System.currentTimeMillis(), available.get(hydratable)))
                        .whenComplete((capture, e) -> {
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

    public static List<Track> getRelatedAudio(Track track) throws Exception {
        return getRelatedAudio(track.getId());
    }

    public static List<Track> getRelatedAudio(long trackId) throws Exception {
        String base = "https://api-v2.soundcloud.com/tracks/%s/related?client_id=%s&limit=10&offset=0";
        String url = String.format(base, trackId, VirtualClient.getID());
        MediaLoader loader = new MediaLoader(url);
        Response response = loader.call();
        JSONObject root = new JSONObject(response.getBodyAsString());
        JSONArray collection = root.getJSONArray("collection");
        List<Track> list = new LinkedList<>();
        long timestamp = System.currentTimeMillis();
        for (int i = 0; i < collection.length(); i++) {
            list.add(new Track(timestamp, collection.getJSONObject(i)));
        }
        return list;
    }
}
