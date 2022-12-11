package com.hawolt.data.media.hydratable.impl.playlist;

import com.hawolt.data.VirtualClient;
import com.hawolt.data.media.MediaLoader;
import com.hawolt.data.media.hydratable.Hydratable;
import com.hawolt.data.media.hydratable.impl.track.Track;
import com.hawolt.http.Response;
import com.hawolt.logger.Logger;
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

public class Playlist extends Hydratable implements Iterable<Long> {

    private final List<Long> list = new ArrayList<>();
    private final String secret;
    private final long id;

    public Playlist(JSONObject object) {
        JSONObject data = object.getJSONObject("data");
        this.id = data.getLong("id");
        this.secret = data.isNull("secret_token") ? "" : data.getString("secret_token");
        JSONArray tracks = data.getJSONArray("tracks");
        for (int i = 0; i < tracks.length(); i++) {
            JSONObject track = tracks.getJSONObject(i);
            list.add(track.getLong("id"));
        }
    }

    public String getSecret() {
        return secret;
    }

    public long getId() {
        return id;
    }

    public List<Long> getList() {
        return list;
    }

    @Override
    public Iterator<Long> iterator() {
        return list.iterator();
    }
}
