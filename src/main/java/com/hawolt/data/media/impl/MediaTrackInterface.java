package com.hawolt.data.media.impl;

import com.hawolt.data.media.MediaInterface;
import com.hawolt.data.media.Track;
import org.json.JSONObject;

/**
 * Created: 09.02.2022 12:38
 * Author: Twitter @hawolt
 **/

public class MediaTrackInterface implements MediaInterface<Track> {

    @Override
    public Track convert(JSONObject object) {
        return new Track(object.getJSONObject("data"));
    }
}
