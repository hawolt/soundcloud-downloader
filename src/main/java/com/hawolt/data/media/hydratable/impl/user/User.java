package com.hawolt.data.media.hydratable.impl.user;

import com.hawolt.data.media.hydratable.Hydratable;
import org.json.JSONObject;

/**
 * Created: 09/02/2022 12:41
 * Author: Twitter @hawolt
 **/

public class User extends Hydratable {
    private final long userId;
    private final String permalink;

    public User(long timestamp, JSONObject object) {
        super(timestamp);
        boolean nested = object.has("data");
        JSONObject data = nested ? object.getJSONObject("data") : object;
        this.userId = data.getLong("id");
        this.permalink = data.getString("permalink");
    }

    public String getPermalink() {
        return permalink;
    }

    public long getUserId() {
        return userId;
    }
}
