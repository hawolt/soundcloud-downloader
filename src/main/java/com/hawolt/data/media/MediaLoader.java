package com.hawolt.data.media;

import com.hawolt.data.media.hydratable.Hydratable;
import com.hawolt.http.Request;
import com.hawolt.http.Response;
import com.hawolt.logger.Logger;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Created: 22/02/2022 16:48
 * Author: Twitter @hawolt
 **/

public class MediaLoader implements Callable<Response> {

    private static final Random random = new Random();
    private final String resource;

    public MediaLoader(String resource) {
        this.resource = resource;
    }

    private int duration = 1;

    @Override
    public Response call() throws Exception {
        Response response;
        int code;
        do {
            Request request = new Request(resource);
            response = request.execute();
            code = response.getCode();
            if (code == 429 || code == 403 || code == 203) {
                Hydratable.snooze((duration *= 3) * 1000L);
            }
        } while (code == 429 || code == 403 || code == 203);
        return response;
    }
}