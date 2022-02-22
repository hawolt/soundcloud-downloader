package com.hawolt.data.media;

import com.hawolt.data.media.download.DownloadCallback;
import com.hawolt.data.media.hydratable.impl.PlaylistManager;
import com.hawolt.data.media.hydratable.impl.TrackManager;

/**
 * Created: 09/02/2022 12:57
 * Author: Twitter @hawolt
 **/

public abstract class MediaManager implements MediaCallback, DownloadCallback {

    private final PlaylistManager manager;

    public MediaManager() {
        this.manager = new PlaylistManager(this);
    }

    public PlaylistManager getPlaylistManager() {
        return manager;
    }

    public TrackManager getTrackManager() {
        return manager.getManager();
    }

    public void load(String link) {
        Soundcloud.load(link, this);
    }
}
