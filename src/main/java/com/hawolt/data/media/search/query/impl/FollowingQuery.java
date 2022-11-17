package com.hawolt.data.media.search.query.impl;

import com.hawolt.cryptography.SHA256;
import com.hawolt.data.media.hydratable.impl.track.Track;
import com.hawolt.data.media.hydratable.impl.user.User;
import com.hawolt.data.media.search.query.Query;
import org.json.JSONObject;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created: 13/11/2022 19:17
 * Author: Twitter @hawolt
 **/

public class FollowingQuery implements Query<User> {
    private final long userId;

    public FollowingQuery(long userId) {
        this.userId = userId;
    }

    @Override
    public String getKeyword() {
        return String.valueOf(userId);
    }

    @Override
    public String checksum() {
        return SHA256.hash(String.join(getClass().getSimpleName(), String.valueOf(userId)));
    }


    @Override
    public Predicate<User> filter() {
        return track -> true;
    }

    @Override
    public Function<JSONObject, User> getTransformer() {
        return User::new;
    }
}
