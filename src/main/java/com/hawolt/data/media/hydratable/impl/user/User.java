package com.hawolt.data.media.hydratable.impl.user;

import com.hawolt.data.VirtualClient;
import com.hawolt.data.media.hydratable.Hydratable;
import com.hawolt.http.Response;
import com.hawolt.logger.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
