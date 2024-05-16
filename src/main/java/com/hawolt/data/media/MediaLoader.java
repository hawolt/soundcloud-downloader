package com.hawolt.data.media;

import com.hawolt.data.VirtualClient;
import com.hawolt.data.media.hydratable.Hydratable;
import com.hawolt.http.Request;
import com.hawolt.http.Response;
import com.hawolt.logger.Logger;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created: 22/02/2022 16:48
 * Author: Twitter @hawolt
 **/

public class MediaLoader implements Callable<Response> {

    private static final Pattern pattern = Pattern.compile("client_id=([^&]+)");
    private String resource;

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
            if (code == 401) {
                resource = getNewResourceLocation();
            } else if (code == 429 || code == 403 || code == 203) {
                Hydratable.snooze((duration *= 3) * 1000L);
            }
        } while (code == 429 || code == 403 || code == 401 || code == 203);
        return response;
    }

    private String getNewResourceLocation() throws Exception {
        String clientId = VirtualClient.getID(true);
        Matcher matcher = pattern.matcher(resource);
        if (matcher.find()) {
            return matcher.replaceAll("client_id=" + clientId);
        } else {
            throw new Exception("Unable to adjust client_id");
        }
    }
}