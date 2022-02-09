package com.hawolt.data.media.impl;

import com.hawolt.data.media.MediaInterface;
import com.hawolt.data.media.Playlist;
import org.json.JSONObject;

/**
 * Created: 09.02.2022 12:38
 * Author: Twitter @hawolt
 **/

public class MediaPlaylistInterface implements MediaInterface<Playlist> {

    @Override
    public Playlist convert(JSONObject object) {
        return new Playlist(object);
    }
}
