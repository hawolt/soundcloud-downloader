package interactive;

import com.hawolt.data.media.download.DownloadCallback;
import com.hawolt.data.media.hydratable.impl.playlist.Playlist;
import com.hawolt.data.media.hydratable.impl.track.Track;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created: 15/11/2022 22:36
 * Author: Twitter @hawolt
 **/

public class PlaylistTracker {

    private final Map<String, Map<Track, byte[]>> playlist = new HashMap<>();
    private final Map<String, Integer> entries = new HashMap<>();
    private final Map<Long, String> reverse = new HashMap<>();
    private final PlaylistCallback callback;

    public PlaylistTracker(PlaylistCallback callback) {
        this.callback = callback;
    }

    public void store(String link, Playlist list) {
        List<Long> tracks = list.getList();
        for (long id : tracks) {
            reverse.put(id, link);
        }
        entries.put(link, tracks.size());
    }

    public void assign(Track track, byte[] b, Supplier<DownloadCallback> supplier) {
        String origin = reverse.get(track.getId());
        if (origin == null) {
            supplier.get().onTrack(track, b);
            return;
        }
        if (!playlist.containsKey(origin)) playlist.put(origin, new HashMap<>());
        playlist.get(origin).put(track, b);
        int entries = this.entries.get(origin);
        if (playlist.get(origin).size() < entries) return;
        this.entries.remove(origin);
        Map<Track, byte[]> map = playlist.get(origin);
        for (Track t : map.keySet()) {
            reverse.remove(t.getId());
        }
        playlist.remove(origin);
        callback.onPlaylist(origin, map);
    }
}
