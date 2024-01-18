package com.hawolt.data.media.download;

import com.hawolt.data.media.hydratable.impl.track.Track;

/**
 * Created: 09/02/2022 13:05
 * Author: Twitter @hawolt
 **/

public interface DownloadCallback {
    void onCompletion(Track track, byte[] b);

    void onFailure(Track track, int fragment);

    void onTrack(Track track, byte[] b);
}
