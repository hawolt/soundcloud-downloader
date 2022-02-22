package com.hawolt.interactive;

import com.hawolt.Logger;
import com.hawolt.data.SynchronizedInteger;
import com.hawolt.data.media.MediaManager;
import com.hawolt.data.media.Track;
import com.hawolt.data.media.download.FileManager;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created: 22/02/2022 16:34
 * Author: Twitter @hawolt
 **/

public class DefaultMediaManager extends MediaManager {

    private final Set<Long> set = new HashSet<>();
    private SynchronizedInteger counter = new SynchronizedInteger(0);

    @Override
    public void ping(Track track) {
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
        FileManager.store(track, b).whenComplete((file, fex) -> {
            if (fex != null) {
                Logger.error(fex);
            } else {
                Logger.info("Completed download for {}.mp3", track.getPermalink());
            }
            if (counter.incrementAndGet() == set.size()) {
                Logger.info("Download queue cleared, exiting");
                System.exit(0);
            }
        });
    }

    @Override
    public void onFailure(Track track, int fragment) {
        Logger.debug("Failed to load fragment {} for {}", fragment, track.getPermalink());
    }

    @Override
    public void onLoadFailure(String link, IOException exception) {
        Logger.error("Failed to load track {}: {}", link, exception.getMessage());
    }
}
