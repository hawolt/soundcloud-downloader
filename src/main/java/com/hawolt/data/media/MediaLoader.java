package com.hawolt.data.media;

import com.hawolt.Request;
import com.hawolt.Response;

import java.util.concurrent.Callable;

/**
 * Created: 22/02/2022 16:48
 * Author: Twitter @hawolt
 **/

public class MediaLoader implements Callable<Response> {

    private final String resource;

    public MediaLoader(String resource) {
        this.resource = resource;
    }

    @Override
    public Response call() throws Exception {
        Request request = new Request(resource);
        return request.execute();
    }
}