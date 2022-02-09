package com.hawolt.data.media;

import com.hawolt.VirtualClient;
import com.hawolt.data.media.hydratable.Hydratable;
import com.hawolt.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created: 09.02.2022 12:41
 * Author: Twitter @hawolt
 **/

public class Playlist extends Hydratable implements Iterable<Track> {

    private final ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private final List<Track> list = new ArrayList<>();

    public Playlist(JSONObject object) {
        JSONObject data = object.getJSONObject("data");
        JSONArray tracks = data.getJSONArray("tracks");
        List<TrackLoader> list = new ArrayList<>();
        for (int i = 0; i < tracks.length(); i++) {
            JSONObject track = tracks.getJSONObject(i);
            long id = track.getLong("id");
            try {
                String resource = String.format("https://api-v2.soundcloud.com/tracks?ids=%s&client_id=%s", id, VirtualClient.getID());
                list.add(new TrackLoader(resource));
            } catch (Exception e) {
                Logger.error(e);
            }
        }
        try {
            List<Future<Track>> futures = service.invokeAll(list);
            for (Future<Track> future : futures) {
                this.list.add(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            Logger.error(e);
        }
        service.shutdown();
    }

    @Override
    public Iterator<Track> iterator() {
        return list.iterator();
    }
}
