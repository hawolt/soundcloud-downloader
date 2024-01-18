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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created: 09/02/2022 11:41
 * Author: Twitter @hawolt
 **/

public class Soundcloud {
    private static ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
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
        load(link, null, new String[0]);
    }

    public static void load(String link, String... args) {
        load(link, null, args);
    }

    public static void load(String source, LoadCallback callback, String... args) {
        String link = source.split("\\?")[0];
        Logger.debug("Track {}", link);
        EXECUTOR_SERVICE.execute(() -> {
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
                String hydratable = available.containsKey("sound") ? "sound" : available.containsKey("playlist") ? "playlist" : available.containsKey("user") ? "user" : null;
                if (hydratable == null) return;
                Logger.debug("Hydratable {} for {}", hydratable, link);
                EXECUTOR_SERVICE.execute(() -> {
                    Hydratable klass = MAPPING.get(hydratable).convert(System.currentTimeMillis(), available.get(hydratable));
                    if (klass == null) return;
                    Logger.debug("Forward {} for {}", hydratable, link);
                    for (HydratableInterface<? extends Hydratable> hydratableInterface : MANAGER.get(klass.getClass())) {
                        hydratableInterface.accept(link, modify(klass), args);
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
