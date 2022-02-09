package com.hawolt.data.media.track;

import org.json.JSONObject;

/**
 * Created: 09.02.2022 12:59
 * Author: Twitter @hawolt
 **/

public class User {

    private final String permalink;

    public User(JSONObject o) {
        this.permalink = !o.isNull("permalink") ? o.getString("permalink") : "";
    }

    public String getPermalink() {
        return permalink;
    }
}
