package com.hawolt.data.media.download;

import com.hawolt.data.media.download.impl.TrackFragment;
import com.hawolt.data.media.track.EXTM3U;
import com.hawolt.data.media.track.MP3;
import com.hawolt.logging.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created: 09.02.2022 13:02
 * Author: Twitter @hawolt
 **/

public class TrackFile implements IFile, FileCallback {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private Map<Integer, TrackFragment> map = new HashMap<>();
    private DownloadCallback callback;
    private int fragments;
    private MP3 mp3;

    private TrackFile(DownloadCallback callback, MP3 mp3) {
        EXTM3U extm3U = mp3.getEXTM3U();
        if (extm3U == null) return;
        this.mp3 = mp3;
        this.callback = callback;
        List<String> list = extm3U.getFragmentList();
        for (int i = 0; i < list.size(); i++) {
            TrackFragment fragment = new TrackFragment(this, i, list.get(i));
            EXECUTOR_SERVICE.execute(fragment);
            map.put(i, fragment);
        }
    }

    public static TrackFile get(DownloadCallback callback, MP3 mp3) {
        return new TrackFile(callback, mp3);
    }

    @Override
    public byte[] getBytes() {
        byte[] bytes = new byte[0];
        for (int i = 0; i < map.size(); i++) {
            TrackFragment fragment = map.get(i);
            byte[] b = fragment.getBytes();
            int pointer = bytes.length;
            bytes = Arrays.copyOf(bytes, bytes.length + fragment.getBytes().length);
            if (bytes.length - pointer >= 0)
                System.arraycopy(b, 0, bytes, pointer, bytes.length - pointer);
        }
        return bytes;
    }

    @Override
    public void onAssembly(int index, IFile file) {
        if (++fragments == map.size()) {
            Logger.info("Assembled track {}", mp3.getTrack().getPermalink());
            final byte[] bytes = getBytes();
            callback.onCompletion(mp3.getTrack(), bytes);
        } else {
            Logger.info("Downloaded fragment [{}/{}] of {}", index, map.size(), mp3.getTrack().getPermalink());
        }
    }

    @Override
    public void onFailure(int index, int attempt, String url) {
        Logger.error("Failed to download fragment {}:{}", index, url);
        if (attempt > 3) {
            Logger.info("Failed to download track {}", mp3.getTrack().getPermalink());
            callback.onFailure(mp3.getTrack(), index);
        } else {
            EXECUTOR_SERVICE.execute(map.get(index));
        }
    }
}
