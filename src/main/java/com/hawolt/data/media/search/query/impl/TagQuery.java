package com.hawolt.data.media.search.query.impl;

import com.hawolt.cryptography.SHA256;
import com.hawolt.data.media.hydratable.impl.track.Track;
import com.hawolt.data.media.search.query.AdvancedQuery;
import org.json.JSONObject;

import java.util.function.Function;

/**
 * Created: 13/11/2022 19:17
 * Author: Twitter @hawolt
 **/

public class TagQuery extends AdvancedQuery {
    private final long timestamp;
    private final String tag;

    public TagQuery(long timestamp, String tag) {
        this.timestamp = timestamp;
        this.tag = tag;
    }

    public TagQuery(String tag) {
        this(System.currentTimeMillis(), tag);
    }

    @Override
    public String getKeyword() {
        return tag;
    }

    @Override
    public String checksum() {
        return SHA256.hash(String.join(getClass().getSimpleName(), tag));
    }

    @Override
    public Function<JSONObject, Track> getTransformer() {
        return object -> new Track(timestamp, object);
    }
}
