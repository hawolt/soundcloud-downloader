package com.hawolt.data.media.hydratable.impl.playlist;

import com.hawolt.data.media.ObjectCallback;
import com.hawolt.data.media.hydratable.HydratableInterface;

/**
 * Created: 09/02/2022 12:52
 * Author: Twitter @hawolt
 **/

public class PlaylistManager implements HydratableInterface<Playlist> {
    private final ObjectCallback<Playlist> callback;

    public PlaylistManager(ObjectCallback<Playlist> callback) {
        this.callback = callback;
    }

    @Override
    public void accept(String link, Playlist hydratable, String... args) {
        callback.ping(link, hydratable, args);
    }
}
