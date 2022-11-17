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

    public Playlist(JSONObject object) {
        JSONObject data = object.getJSONObject("data");
        JSONArray tracks = data.getJSONArray("tracks");
        for (int i = 0; i < tracks.length(); i++) {
            JSONObject track = tracks.getJSONObject(i);
            list.add(track.getLong("id"));

          /*  try {
                String resource = String.format("https://api-v2.soundcloud.com/tracks?ids=%s&client_id=%s", id, VirtualClient.getID());
                list.add(new MediaLoader(resource));
            } catch (Exception e) {
                Logger.error(e);
            }*/
            //this.list.add(new Track(new JSONArray(future.get().getBodyAsString()).getJSONObject(0)));
        }
    }

    public List<Long> getList() {
        return list;
    }

    @Override
    public Iterator<Long> iterator() {
        return list.iterator();
    }
}
