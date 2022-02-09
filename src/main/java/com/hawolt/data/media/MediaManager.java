package com.hawolt.data.media;

import com.hawolt.data.media.download.DownloadCallback;
import com.hawolt.data.media.hydratable.impl.PlaylistManager;
import com.hawolt.data.media.hydratable.impl.TrackManager;

import java.io.IOException;

/**
 * Created: 09.02.2022 12:57
 * Author: Twitter @hawolt
 **/

public abstract class MediaManager implements MediaCallback {

    protected final DownloadCallback callback;
    private final PlaylistManager manager;

    public MediaManager(DownloadCallback callback) {
        this.manager = new PlaylistManager(this);
        this.callback = callback;
    }

    public PlaylistManager getPlaylistManager() {
        return manager;
    }

    public TrackManager getTrackManager() {
        return manager.getManager();
    }

    public void load(String link) {
        try {
            Soundcloud.load(link);
        } catch (IOException e) {
            this.callback.onLoadFailure(link, e);
        }
    }
}
