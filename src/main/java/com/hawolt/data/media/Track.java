package com.hawolt.data.media;

import com.hawolt.data.media.download.FileManager;
import com.hawolt.data.media.hydratable.Hydratable;
import com.hawolt.data.media.track.MP3;
import com.hawolt.data.media.track.Media;
import com.hawolt.data.media.track.Tags;
import com.hawolt.data.media.track.User;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.CompletableFuture;

/**
 * Created: 09.02.2022 12:39
 * Author: Twitter @hawolt
 **/

public class Track extends Hydratable {

    public static final long LOADED_AT = System.currentTimeMillis();

    private Media media;
    private User user;
    private Tags tags;

    private String description, title, genre, link, permalink, artwork;
    private int likeCount, commentCount, playbackCount;
    private long id, duration;

    public Track(long t) {
        this.id = t;
    }

    public Track(JSONObject o) {
        this.media = new Media(o.getJSONObject("media"));
        this.user = new User(o.getJSONObject("user"));
        this.description = !o.isNull("description") ? o.getString("description") : "";
        this.title = o.getString("title");
        this.genre = !o.isNull("genre") ? o.getString("genre") : "";
        this.permalink = !o.isNull("permalink") ? o.getString("permalink") : "";
        this.artwork = !o.isNull("artwork_url") ? o.getString("artwork_url") : null;
        this.link = o.getString("permalink_url");
        this.tags = new Tags(genre, o.getString("tag_list"));
        this.likeCount = !o.isNull("likes_count") ? o.getInt("likes_count") : 0;
        this.playbackCount = !o.isNull("playback_count") ? o.getInt("playback_count") : 0;
        this.id = !o.isNull("id") ? o.getLong("id") : 0;
        this.duration = !o.isNull("full_duration") ? o.getLong("full_duration") : 0;
        this.commentCount = !o.isNull("comment_count") ? o.getInt("comment_count") : 0;
    }

    public CompletableFuture<MP3> retrieveMP3() {
        return CompletableFuture.supplyAsync(() -> new MP3(this, media.getTranscoding()));
    }

    public boolean isCached() {
        return FileManager.isCached(this);
    }

    public File getFile() {
        return FileManager.getFile(this);
    }

    public long getDuration() {
        return duration;
    }

    public int getPlaybackCount() {
        return playbackCount;
    }

    public String getArtwork() {
        return artwork;
    }

    public long getId() {
        return id;
    }

    public String getPermalink() {
        return permalink;
    }

    public Media getMedia() {
        return media;
    }

    public User getUser() {
        return user;
    }

    public Tags getTags() {
        return tags;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public String getLink() {
        return link;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Track)) return false;
        Track track = (Track) o;
        return track.getId() == id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

}
