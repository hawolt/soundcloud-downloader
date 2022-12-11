package com.hawolt.data.media.hydratable.impl.user;

import com.hawolt.data.media.hydratable.Hydratable;
import org.json.JSONObject;

/**
 * Created: 09/02/2022 12:41
 * Author: Twitter @hawolt
 **/

public class User extends Hydratable {

    private final long userId;

    public User(JSONObject object) {
        boolean nested = object.has("data");
        JSONObject data = nested ? object.getJSONObject("data") : object;
        this.userId = data.getLong("id");
    }

    public long getUserId() {
        return userId;
    }
}
