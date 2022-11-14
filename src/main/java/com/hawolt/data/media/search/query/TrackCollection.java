package com.hawolt.data.media.search.query;

import com.hawolt.data.media.Track;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created: 14/11/2022 14:33
 * Author: Twitter @hawolt
 **/

public class TrackCollection implements Iterable<Track> {
    public static TrackCollection EMPTY = new TrackCollection();

    private final List<Track> list = new ArrayList<>();

    public TrackCollection() {

    }

    public TrackCollection(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            list.add(new Track(array.getJSONObject(i)));
        }
    }

    public void append(Track track) {
        list.add(track);
    }

    public List<Track> getList() {
        return list;
    }

    @Override
    public Iterator<Track> iterator() {
        return list.iterator();
    }
}
