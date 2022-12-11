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

public class SearchQuery extends AdvancedQuery {
    private final String keyword;

    public SearchQuery(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String getKeyword() {
        return keyword;
    }

    @Override
    public String checksum() {
        return SHA256.hash(String.join(getClass().getSimpleName(), keyword));
    }

    @Override
    public Function<JSONObject, Track> getTransformer() {
        return Track::new;
    }
}
