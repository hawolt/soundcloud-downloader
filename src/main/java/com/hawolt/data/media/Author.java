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

public class Author extends Playlist implements Iterable<Track> {

    private final List<Track> list = new ArrayList<>();

    private final long userId;

    public Author(JSONObject object) {
        this(object, -1L);
    }

    public Author(JSONObject object, long stopAtTrackId) {
        JSONObject data = object.getJSONObject("data");
        this.userId = data.getLong("id");
        List<MediaLoader> list = new ArrayList<>();
        try {
            String next = String.format("https://api-v2.soundcloud.com/users/%s/tracks?client_id=%s&limit=%s&offset=0", userId, VirtualClient.getID(), Soundcloud.limit);
            do {
                Logger.debug("Parsing {}", next);
                MediaLoader loader = new MediaLoader(next);
                Response response = loader.call();
                JSONObject main = new JSONObject(response.getBodyAsString());
                JSONArray tracks = main.getJSONArray("collection");
                if (main.isNull("next_href")) {
                    next = null;
                } else {
                    String clientID = String.join("=", "client_id", VirtualClient.getID());
                    next = String.join("&", main.getString("next_href"), clientID);
                }
                for (int i = 0; i < tracks.length(); i++) {
                    JSONObject track = tracks.getJSONObject(i);
                    long trackId = track.getLong("id");
                    String resource = String.format("https://api-v2.soundcloud.com/tracks?ids=%s&client_id=%s", trackId, VirtualClient.getID());
                    list.add(new MediaLoader(resource));
                    if (stopAtTrackId == trackId) {
                        next = null;
                        break;
                    }
                }
            } while (next != null);
        } catch (Exception e) {
            Logger.error(e);
        }
        try {
            Logger.debug("Loading metadata for playlist with {} element{}", list.size(), list.size() == 1 ? "" : "s");
            List<Future<Response>> futures = Hydratable.EXECUTOR_SERVICE.invokeAll(list);
            for (Future<Response> future : futures) {
                try {
                    this.list.add(new Track(new JSONArray(future.get().getBodyAsString()).getJSONObject(0)));
                } catch (Exception e) {
                    Logger.error("Invalid JSON {}", future.get().getBodyAsString());
                    Logger.error(e);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            Logger.error(e);
        }
    }

    public long getUserId() {
        return userId;
    }

    public List<Track> getTrackList() {
        return list;
    }

    @Override
    public Iterator<Track> iterator() {
        return list.iterator();
    }
}
