package com.hawolt.data.media.hydratable.impl;

import com.hawolt.data.media.MediaCallback;
import com.hawolt.data.media.Playlist;
import com.hawolt.data.media.Soundcloud;
import com.hawolt.data.media.Track;
import com.hawolt.data.media.hydratable.HydratableInterface;

/**
 * Created: 09/02/2022 12:53
 * Author: Twitter @hawolt
 **/

public class PlaylistManager implements HydratableInterface<Playlist> {

    private final TrackManager manager;

    public PlaylistManager(MediaCallback callback) {
        this.manager = new TrackManager(callback);
        Soundcloud.register(Playlist.class, this);
    }

    public TrackManager getManager() {
        return manager;
    }

    @Override
    public void accept(Playlist hydratable) {
        for (Track track : hydratable) {
            manager.accept(track);
        }
    }
}
