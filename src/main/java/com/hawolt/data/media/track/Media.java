package com.hawolt.data.media.track;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created: 09/02/2022 12:59
 * Author: Twitter @hawolt
 **/

public class Media {

    private final Transcoding[] transcoding;

    public Media(JSONObject o) {
        JSONArray array = o.getJSONArray("transcodings");
        this.transcoding = new Transcoding[array.length()];
        for (int i = 0; i < array.length(); i++) {
            this.transcoding[i] = new Transcoding(array.getJSONObject(i));
        }
    }

    public Transcoding[] getTranscoding() {
        return transcoding;
    }
}
