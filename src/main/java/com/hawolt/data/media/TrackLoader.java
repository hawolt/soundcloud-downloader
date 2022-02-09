package com.hawolt.data.media;

import com.hawolt.Request;
import com.hawolt.Response;
import org.json.JSONArray;

import java.util.concurrent.Callable;

/**
 * Created: 09.02.2022 15:33
 * Author: Twitter @hawolt
 **/

public class TrackLoader implements Callable<Track> {

    private final String resource;

    public TrackLoader(String resource) {
        this.resource = resource;
    }

    @Override
    public Track call() throws Exception {
        Request request = new Request(resource);
        Response response = request.execute();
        if (response.getCode() == 429) return call();
        return new Track(new JSONArray(response.getBodyAsString()).getJSONObject(0));
    }
}
