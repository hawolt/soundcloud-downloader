package interactive;

import com.hawolt.LoadCallback;
import com.hawolt.Soundcloud;
import com.hawolt.data.media.download.DownloadCallback;
import com.hawolt.data.media.download.FileManager;
import com.hawolt.data.media.download.TrackFile;
import com.hawolt.data.media.hydratable.impl.playlist.Playlist;
import com.hawolt.data.media.hydratable.impl.playlist.PlaylistManager;
import com.hawolt.data.media.hydratable.impl.track.Track;
import com.hawolt.data.media.hydratable.impl.track.TrackManager;
import com.hawolt.data.media.hydratable.impl.user.User;
import com.hawolt.data.media.hydratable.impl.user.UserManager;
import com.hawolt.data.media.search.Explorer;
import com.hawolt.data.media.search.query.ObjectCollection;
import com.hawolt.data.media.search.query.impl.TrackQuery;
import com.hawolt.data.media.track.MP3;
import com.hawolt.logger.Logger;
import org.json.JSONArray;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created: 15/11/2022 17:36
 * Author: Twitter @hawolt
 **/

public abstract class AbstractMediaManager implements DownloadCallback, PlaylistCallback, LoadCallback {
    private final ExecutorService service = Executors.newSingleThreadExecutor();
    private final PlaylistTracker tracker = new PlaylistTracker(this);

    public AbstractMediaManager() {
        Soundcloud.register(User.class, new UserManager(this::onUser));
        Soundcloud.register(Track.class, new TrackManager(this::onTrackData));
        Soundcloud.register(Playlist.class, new PlaylistManager(this::onPlaylistData));
    }

    public abstract void onUser(String link, User user, String... args);

    public void load(String uri) {
        Soundcloud.load(uri, this);
    }

    public void onPlaylistData(String link, Playlist playlist, String... args) {
        //tracker.store(link, playlist);
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

    public void onTrackData(String link, Track track, String... args) {
        if (FileManager.getFile(track).exists()) {
            Logger.debug("serving track {} from local", track.getId());
            try {
                byte[] b = Files.readAllBytes(FileManager.getFile(track).toPath());
                this.onCompletion(track, b);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        service.execute(() -> {
            if (FileManager.getFile(track).exists()) return;
            MP3 mp3 = track.getMP3();
            if (mp3 == null) return;
            TrackFile.get(this, mp3, null);
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
    public void onLoadFailure(String link, Exception exception, String... args) {
        Logger.error("Failed to load track {}: {}", link, exception.getMessage());
    }
}
