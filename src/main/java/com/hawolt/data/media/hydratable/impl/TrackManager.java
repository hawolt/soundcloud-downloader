package com.hawolt.data.media.hydratable.impl;

import com.hawolt.data.media.Soundcloud;
import com.hawolt.data.media.MediaCallback;
import com.hawolt.data.media.Track;
import com.hawolt.data.media.hydratable.HydratableInterface;

/**
 * Created: 09.02.2022 12:52
 * Author: Twitter @hawolt
 **/

public class TrackManager implements HydratableInterface<Track> {
    private final MediaCallback callback;

    public TrackManager(MediaCallback callback) {
        this.callback = callback;
        Soundcloud.register(Track.class, this);
    }

    @Override
    public void accept(Track hydratable) {
        callback.ping(hydratable);
    }
}
