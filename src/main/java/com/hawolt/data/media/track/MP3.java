package com.hawolt.data.media.track;

import com.hawolt.Request;
import com.hawolt.Response;
import com.hawolt.VirtualClient;
import com.hawolt.data.media.Track;
import com.hawolt.data.media.download.DownloadCallback;
import com.hawolt.data.media.download.TrackFile;
import com.hawolt.logging.Logger;
import org.json.JSONObject;

/**
 * Created: 09.02.2022 12:59
 * Author: Twitter @hawolt
 **/

public class MP3 {

    private final Track track;
    private EXTM3U extm3U;

    public MP3(Track track, Transcoding... transcodings) {
        this.track = track;
        for (Transcoding transcoding : transcodings) {
            if (transcoding.getProtocol().equalsIgnoreCase("hls")) {
                try {
                    String auth = String.join("=", "client_id", VirtualClient.getID());
                    String resource = String.join("?", transcoding.getUrl(), auth);
                    Logger.info(resource);
                    Request request = new Request(resource);
                    Response response = request.execute();
                    String target = new JSONObject(response.getBodyAsString()).getString("url");
                    extm3U = new EXTM3U(target);
                } catch (Exception e) {
                    Logger.error(e);
                }
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
