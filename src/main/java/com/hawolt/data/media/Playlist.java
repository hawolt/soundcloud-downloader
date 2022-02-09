package com.hawolt.data.media;

import com.hawolt.data.media.hydratable.Hydratable;
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
            list.add(new Track(tracks.getJSONObject(i)));
        }
    }

    @Override
    public Iterator<Track> iterator() {
        return list.iterator();
    }
}
