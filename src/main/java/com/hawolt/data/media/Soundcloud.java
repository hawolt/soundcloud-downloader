package com.hawolt.data.media;

import com.hawolt.Logger;
import com.hawolt.Request;
import com.hawolt.Response;
import com.hawolt.data.media.download.DownloadCallback;
import com.hawolt.data.media.hydratable.Hydratable;
import com.hawolt.data.media.hydratable.HydratableInterface;
import com.hawolt.data.media.hydratable.Hydration;
import com.hawolt.data.media.impl.MediaPlaylistInterface;
import com.hawolt.data.media.impl.MediaTrackInterface;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Created: 09/02/2022 11:41
 * Author: Twitter @hawolt
 **/

public class Soundcloud {

    private static final Map<String, MediaInterface<? extends Hydratable>> MAPPING = new HashMap<>();
    private static final Map<Class<? extends Hydratable>, HydratableInterface<? extends Hydratable>> MANAGER = new HashMap<>();

    static {
        MAPPING.put("playlist", new MediaPlaylistInterface());
        MAPPING.put("sound", new MediaTrackInterface());
    }

    public static void register(Class<? extends Hydratable> clazz, HydratableInterface<? extends Hydratable> manager) {
        MANAGER.put(clazz, manager);
    }

    @SuppressWarnings("all")
    private static <T> T modify(Object type) {
        return (T) type;
    }

    static void load(String link) {
        load(link, null);
    }

    static void load(String link, DownloadCallback callback) {
        Hydratable.EXECUTOR_SERVICE.execute(() -> {
            try {
                Request request = new Request(link);
                Response response = request.execute();
                JSONArray hydration = Hydration.from(response.getBodyAsString());
                for (int i = 0; i < hydration.length(); i++) {
                    JSONObject object = hydration.getJSONObject(i);
                    if (!object.has("hydratable")) continue;
                    String hydratable = object.getString("hydratable");
                    if (!MAPPING.containsKey(hydratable)) continue;
                    CompletableFuture.supplyAsync(() -> MAPPING.get(hydratable).convert(object)).whenComplete((capture, e) -> {
                        if (e != null) Logger.error(e);
                        if (capture == null) return;
                        MANAGER.get(capture.getClass()).accept(modify(capture));
                    });
                }
            } catch (IOException e) {
                if (callback == null) Logger.error(e.getMessage());
                else callback.onLoadFailure(link, e);
            }
        });
    }
}
