package com.hawolt.data.media.search.query.impl;

import com.hawolt.data.media.Track;
import com.hawolt.data.media.search.query.BaseQuery;
import com.hawolt.data.media.search.query.Query;

import java.util.function.Predicate;

/**
 * Created: 13/11/2022 19:17
 * Author: Twitter @hawolt
 **/

public class TagQuery extends Query {
    private final Builder builder;

    public TagQuery(Builder builder) {
        super(builder.getBaseQuery());
        this.builder = builder;
    }

    @Override
    protected Predicate<Track> getSpecificPredicate() {
        return track -> track.getTags().contains(builder.getTag());
    }

    @Override
    public String getKeyword() {
        return builder.getTag();
    }

    public static class Builder {
        private final BaseQuery<?> baseQuery;
        private String tag;

        public Builder(BaseQuery<?> baseQuery) {
            this.baseQuery = baseQuery;
        }

        public BaseQuery<?> getBaseQuery() {
            return baseQuery;
        }

        public Builder setTag(String tag) {
            this.tag = tag;
            return this;
        }

        public String getTag() {
            return tag;
        }

        public TagQuery build() {
            return new TagQuery(this);
        }
    }
}
