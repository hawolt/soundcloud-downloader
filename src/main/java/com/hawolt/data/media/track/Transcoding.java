package com.hawolt.data.media.track;

import org.json.JSONObject;

/**
 * Created: 09.02.2022 12:59
 * Author: Twitter @hawolt
 **/

public class Transcoding {

    private final long duration;
    private final boolean snipped;
    private final String protocol, mime, preset, url, quality;

    public Transcoding(JSONObject o) {
        this.duration = o.getLong("duration");
        this.snipped = o.getBoolean("snipped");
        JSONObject format = o.getJSONObject("format");
        this.protocol = format.getString("protocol");
        this.mime = format.getString("mime_type");
        this.preset = o.getString("preset");
        this.url = o.getString("url");
        this.quality = o.getString("quality");
    }

    public long getDuration() {
        return duration;
    }

    public boolean isSnipped() {
        return snipped;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getMime() {
        return mime;
    }

    public String getPreset() {
        return preset;
    }

    public String getUrl() {
        return url;
    }

    public String getQuality() {
        return quality;
    }
}
