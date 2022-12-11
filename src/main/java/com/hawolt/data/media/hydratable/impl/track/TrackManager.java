package com.hawolt.data.media.hydratable.impl.track;

import com.hawolt.data.media.ObjectCallback;
import com.hawolt.data.media.hydratable.HydratableInterface;

/**
 * Created: 09/02/2022 12:52
 * Author: Twitter @hawolt
 **/

public class TrackManager implements HydratableInterface<Track> {
    private final ObjectCallback<Track> callback;

    public TrackManager(ObjectCallback<Track> callback) {
        this.callback = callback;
    }

    @Override
    public void accept(String link, Track hydratable) {
        callback.ping(link, hydratable);
    }
}
