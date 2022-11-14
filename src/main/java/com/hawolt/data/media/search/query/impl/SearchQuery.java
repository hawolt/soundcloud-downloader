package com.hawolt.data.media.search.query.impl;

import com.hawolt.data.media.Track;
import com.hawolt.data.media.search.query.BaseQuery;
import com.hawolt.data.media.search.query.Query;

import java.util.function.Predicate;

/**
 * Created: 13/11/2022 19:48
 * Author: Twitter @hawolt
 **/

public class SearchQuery extends Query {
    private final Builder builder;

    public SearchQuery(Builder builder) {
        super(builder.getBaseQuery());
        this.builder = builder;
    }

    @Override
    protected Predicate<Track> getSpecificPredicate() {
        return track -> true;
    }

    @Override
    public String getKeyword() {
        return builder.getSearchQuery();
    }

    public static class Builder {
        private final BaseQuery<?> baseQuery;
        private String query;

        public Builder(BaseQuery<?> baseQuery) {
            this.baseQuery = baseQuery;
        }

        public BaseQuery<?> getBaseQuery() {
            return baseQuery;
        }

        public Builder setSearchQuery(String query) {
            this.query = query;
            return this;
        }

        public String getSearchQuery() {
            return query;
        }

        public SearchQuery build() {
            return new SearchQuery(this);
        }
    }
}
