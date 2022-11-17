package com.hawolt.data.media.track;

import com.hawolt.data.VirtualClient;
import com.hawolt.data.media.MediaLoader;
import com.hawolt.data.media.hydratable.impl.track.Track;
import com.hawolt.data.media.download.DownloadCallback;
import com.hawolt.data.media.download.TrackFile;
import com.hawolt.http.Response;
import com.hawolt.logger.Logger;
import org.json.JSONObject;

/**
 * Created: 09/02/2022 12:59
 * Author: Twitter @hawolt
 **/

public class MP3 {

    private final Track track;
    private EXTM3U extm3U;

    public static MP3 load(Track track, Transcoding... transcodings) {
        try {
            return new MP3(track,  transcodings);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

   /* public static MP3 load(Track track, int attempt, Transcoding... transcodings) {
        if (attempt > 10) return null;
        try {
            return new MP3(track, transcodings);
        } catch (Exception e) {
            if (e instanceof JSONException) {
                Logger.error(e.getMessage());
                return load(track, ++attempt, transcodings);
            } else {
                System.out.println("null bro");
                return null;
            }
        }
    }*/

    public MP3(Track track, Transcoding... transcodings) throws Exception {
        this.track = track;
        for (Transcoding transcoding : transcodings) {
            if (transcoding.getProtocol().equalsIgnoreCase("hls")) {
                String auth = String.join("=", "client_id", VirtualClient.getID());
                String resource = String.join("?", transcoding.getUrl(), auth);
                Logger.debug("stream for track {} at resource {}", track.getId(), resource);
                MediaLoader loader = new MediaLoader(resource);
                Response response = loader.call();
                String target = new JSONObject(response.getBodyAsString()).getString("url");
                extm3U = new EXTM3U(target);
                break;
            }
        }
    }

    public Track getTrack() {
        return track;
    }

    public EXTM3U getEXTM3U() {
        return extm3U;
    }

    public void download(DownloadCallback callback) {
        TrackFile.get(callback, this);
    }

}
