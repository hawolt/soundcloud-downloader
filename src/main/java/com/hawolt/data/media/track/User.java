package com.hawolt.data.media.track;

import org.json.JSONObject;

/**
 * Created: 09/02/2022 12:59
 * Author: Twitter @hawolt
 **/

public class User {

    private final String permalink, username;

    public User(JSONObject o) {
        this.permalink = !o.isNull("permalink") ? o.getString("permalink") : "";
        this.username = !o.isNull("username") ? o.getString("username") : "";
    }

    public String getUsername() {
        return username;
    }

    public String getPermalink() {
        return permalink;
    }
}
