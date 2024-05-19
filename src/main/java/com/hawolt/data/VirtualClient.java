package com.hawolt.data;

import com.hawolt.http.Request;
import com.hawolt.http.Response;
import com.hawolt.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created: 07/02/2022 12:02
 * Author: Twitter @hawolt
 **/

public class VirtualClient {

    private static final String SCRIPT_SRC = "<script(.*)src=\"(.*)\"(.*)script>";
    private static final String SEARCH_STRING = "query:{client_id:\"";
    private static final Pattern SCRIPT_PATTERN = Pattern.compile(SCRIPT_SRC);

    private static String clientID;
    private static long timestamp;

    public static String getID() throws Exception {
        return getID(false);
    }

    public static String getID(boolean force) throws Exception {
        if (clientID != null && (timestamp != 0L && !force)) {
            return clientID;
        }
        String clientID = fetch();
        if (clientID == null) throw new Exception("Unable to fetch Soundcloud client_id");
        VirtualClient.timestamp = System.currentTimeMillis();
        return (VirtualClient.clientID = clientID);
    }

    private static String fetch() throws Exception {
        int attempt = 0;
        do {
            String clientID = mimic();
            if (clientID != null) {
                return clientID;
            }
        } while ((attempt++) < 3);
        return null;
    }

    private static String mimic() throws IOException {
        Request request = new Request("https://soundcloud.com/");
        Response response = request.execute();
        String body = response.getBodyAsString();
        Matcher matcher = SCRIPT_PATTERN.matcher(body);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group(2));
        }
        for (String element : list) {
            String script = loadScript(element);
            if (script == null) continue;
            int index = script.indexOf(SEARCH_STRING);
            if (index == -1) continue;
            int startIndex = index + SEARCH_STRING.length();
            int endIndex = script.indexOf("\"", startIndex + SEARCH_STRING.length());
            return script.substring(startIndex, endIndex);
        }
        return null;
    }

    private static String loadScript(String source) {
        String body = null;
        try {
            Request request = new Request(source);
            Response response = request.execute();
            body = response.getBodyAsString();
        } catch (IOException e) {
            Logger.error(e);
        }
        return body;
    }
}