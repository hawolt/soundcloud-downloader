package com.hawolt.data.media.search.query.impl;

import com.hawolt.cryptography.SHA256;
import com.hawolt.data.media.hydratable.impl.playlist.Playlist;
import com.hawolt.data.media.hydratable.impl.track.Track;
import com.hawolt.data.media.hydratable.impl.user.User;
import com.hawolt.data.media.search.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created: 13/11/2022 19:17
 * Author: Twitter @hawolt
 **/

public class TrackQuery implements Query<Track> {
    private final long playlistId;
    private final long trackId;
    private final String secret;

    public TrackQuery(long trackId, long playlistId, String secret) {
        this.trackId = trackId;
        this.playlistId = playlistId;
        this.secret = secret;
    }

    public TrackQuery(long trackId) {
        this(trackId, 0L, null);
    }

    public long getPlaylistId() {
        return playlistId;
    }

    public String getSecret() {
        return secret;
    }

    @Override
    public String getKeyword() {
        return String.valueOf(trackId);
    }

    @Override
    public String checksum() {
        return SHA256.hash(String.join(getClass().getSimpleName(), String.valueOf(trackId)));
    }

    @Override
    public Predicate<Track> filter() {
        return track -> true;
    }

    @Override
    public Function<JSONObject, Track> getTransformer() {
        return Track::new;
    }
}
