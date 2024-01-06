package interactive;

import com.hawolt.Soundcloud;
import com.hawolt.data.media.download.DownloadCallback;
import com.hawolt.data.media.hydratable.impl.playlist.Playlist;
import com.hawolt.data.media.hydratable.impl.playlist.PlaylistManager;
import com.hawolt.data.media.hydratable.impl.track.Track;
import com.hawolt.data.media.hydratable.impl.track.TrackManager;
import com.hawolt.data.media.hydratable.impl.user.User;
import com.hawolt.data.media.hydratable.impl.user.UserManager;
import com.hawolt.data.media.search.Explorer;
import com.hawolt.data.media.search.query.ObjectCollection;
import com.hawolt.data.media.search.query.impl.TrackQuery;
import com.hawolt.logger.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created: 15/11/2022 17:36
 * Author: Twitter @hawolt
 **/

public abstract class AbstractMediaManager implements DownloadCallback, PlaylistCallback {
    private final PlaylistTracker tracker = new PlaylistTracker(this);
    private final Set<Long> set = new HashSet<>();

    public AbstractMediaManager() {
        Soundcloud.register(User.class, new UserManager(this::onUser));
        Soundcloud.register(Track.class, new TrackManager(this::onTrackData));
        Soundcloud.register(Playlist.class, new PlaylistManager(this::onPlaylistData));
    }

    public abstract void onUser(String link, User user);

    public void load(String uri) {
        Soundcloud.load(uri, this);
    }

    public void onPlaylistData(String link, Playlist playlist) {
        tracker.store(link, playlist);
        for (long id : playlist) {
            try {
                TrackQuery query = new TrackQuery(
                        playlist.getLoadReferenceTimestamp(),
                        id,
                        playlist.getId(),
                        playlist.getSecret()
                );
                ObjectCollection<Track> collection = Explorer.browse(query);
                for (Track track : collection) {
                    load(track.getLink());
                }
            } catch (Exception e) {
                Logger.error(e);
            }
        }
    }

    public void onTrackData(String link, Track track) {
        if (set.contains(track.getId())) {
            Logger.debug("track {} skipped as duplicate entry", track.getId());
            return;
        }
        set.add(track.getId());
        track.retrieveMP3().whenComplete((mp3, throwable) -> {
            if (throwable != null) Logger.error(throwable);
            if (mp3 == null) return;
            mp3.download(this);
        });
    }

    @Override
    public void onCompletion(Track track, byte[] b) {
        final DownloadCallback callback = this;
        tracker.assign(track, b, () -> callback);
    }

    @Override
    public void onFailure(Track track, int fragment) {
        Logger.debug("Failed to load fragment {} for {}", fragment, track.getPermalink());
    }

    @Override
    public void onLoadFailure(String link, Exception exception) {
        Logger.error("Failed to load track {}: {}", link, exception.getMessage());
    }
}
