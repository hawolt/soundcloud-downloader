package com.hawolt.data.media;

import com.hawolt.Logger;
import com.hawolt.Response;
import com.hawolt.data.VirtualClient;
import com.hawolt.data.media.hydratable.Hydratable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created: 09/02/2022 12:41
 * Author: Twitter @hawolt
 **/

public class Playlist extends Hydratable implements Iterable<Track> {

    private final List<Track> list = new ArrayList<>();

    public Playlist(JSONObject object) {
        JSONObject data = object.getJSONObject("data");
        JSONArray tracks = data.getJSONArray("tracks");
        List<MediaLoader> list = new ArrayList<>();
        for (int i = 0; i < tracks.length(); i++) {
            JSONObject track = tracks.getJSONObject(i);
            long id = track.getLong("id");
            try {
                String resource = String.format("https://api-v2.soundcloud.com/tracks?ids=%s&client_id=%s", id, VirtualClient.getID());
                list.add(new MediaLoader(resource));
            } catch (Exception e) {
                Logger.error(e);
            }
        }
        try {
            Logger.debug("Loading metadata for playlist with {} element{}", list.size(), list.size() == 1 ? "" : "s");
            List<Future<Response>> futures = Hydratable.EXECUTOR_SERVICE.invokeAll(list);
            for (Future<Response> future : futures) {
                this.list.add(new Track(new JSONArray(future.get().getBodyAsString()).getJSONObject(0)));
            }
        } catch (InterruptedException | ExecutionException e) {
            Logger.error(e);
        }
    }

    @Override
    public Iterator<Track> iterator() {
        return list.iterator();
    }
}
