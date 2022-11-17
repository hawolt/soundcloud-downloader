package interactive;

import com.hawolt.data.media.hydratable.impl.track.Track;

import java.util.Map;

/**
 * Created: 15/11/2022 22:51
 * Author: Twitter @hawolt
 **/

public interface PlaylistCallback {
    void onPlaylist(String link, Map<Track, byte[]> playlist);
}
