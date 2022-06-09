package com.hawolt.data.media;

import com.hawolt.Logger;
import com.hawolt.Request;
import com.hawolt.Response;
import com.hawolt.data.media.download.DownloadCallback;
import com.hawolt.data.media.hydratable.Hydratable;
import com.hawolt.data.media.hydratable.HydratableInterface;
import com.hawolt.data.media.hydratable.Hydration;
import com.hawolt.data.media.impl.MediaAuthorInterface;
import com.hawolt.data.media.impl.MediaPlaylistInterface;
import com.hawolt.data.media.impl.MediaTrackInterface;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
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

    public static long limit = 20;

    private static final Map<String, MediaInterface<? extends Hydratable>> MAPPING = new HashMap<>();
    private static final Map<Class<? extends Hydratable>, HydratableInterface<? extends Hydratable>> MANAGER = new HashMap<>();

    static {
        MAPPING.put("playlist", new MediaPlaylistInterface());
        MAPPING.put("user", new MediaAuthorInterface());
        MAPPING.put("sound", new MediaTrackInterface());
    }

    public static void register(Class<? extends Hydratable> clazz, HydratableInterface<? extends Hydratable> manager) {
        MANAGER.put(clazz, manager);
    }

    @SuppressWarnings("all")
    private static <T> T modify(Object type) {
        return (T) type;
    }

    public static void load(String link) {
        load(link, null);
    }

    static void load(String link, DownloadCallback callback) {
        Hydratable.EXECUTOR_SERVICE.execute(() -> {
            try {
                Request request = new Request(link);
                Response response = request.execute();
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
                CompletableFuture.supplyAsync(() -> MAPPING.get(hydratable).convert(available.get(hydratable))).whenComplete((capture, e) -> {
                    if (e != null) Logger.error(e);
                    if (capture == null) return;
                    MANAGER.get(capture.getClass()).accept(modify(capture));
                });
            } catch (IOException e) {
                if (callback == null) Logger.error(e.getMessage());
                else callback.onLoadFailure(link, e);
            }
        });
    }
}
