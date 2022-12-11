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

    private final String authorization;
    private final Track track;
    private EXTM3U extm3U;

    public static MP3 load(Track track, String authorization, Transcoding... transcodings) {
        try {
            return new MP3(track, authorization, transcodings);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public MP3(Track track, String authorization, Transcoding... transcodings) throws Exception {
        this.track = track;
        this.authorization = authorization;
        for (Transcoding transcoding : transcodings) {
            if (transcoding.getProtocol().equalsIgnoreCase("hls")) {
                String client = String.join("=", "client_id", VirtualClient.getID());
                String auth = String.join("=", "track_authorization", authorization);
                String parameters = String.join("&", client, auth);
                String resource = String.join(transcoding.getUrl().contains("?") ? "&" : "?", transcoding.getUrl(), parameters);
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
