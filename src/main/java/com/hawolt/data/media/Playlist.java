package com.hawolt.data.media;

import com.hawolt.Request;
import com.hawolt.Response;
import com.hawolt.VirtualClient;
import com.hawolt.data.media.hydratable.Hydratable;
import com.hawolt.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created: 09.02.2022 12:41
 * Author: Twitter @hawolt
 **/

public class Playlist extends Hydratable implements Iterable<Track> {
    private final List<Track> list = new ArrayList<>();

    public Playlist(JSONObject object) {
        JSONObject data = object.getJSONObject("data");
        JSONArray tracks = data.getJSONArray("tracks");
        for (int i = 0; i < tracks.length(); i++) {
            JSONObject track = tracks.getJSONObject(i);
            long id = track.getLong("id");
            try {
                String resource = String.format("https://api-v2.soundcloud.com/tracks?ids=%s&client_id=%s", id, VirtualClient.getID());
                Request request = new Request(resource);
                Response response = request.execute();
                list.add(new Track(new JSONArray(response.getBodyAsString()).getJSONObject(0)));
            } catch (Exception e) {
                Logger.error(e);
            }
        }
    }

    @Override
    public Iterator<Track> iterator() {
        return list.iterator();
    }
}
