package com.hawolt.data.media.search.query;

import com.hawolt.data.media.Track;

import java.util.function.Predicate;

/**
 * Created: 14/11/2022 13:00
 * Author: Twitter @hawolt
 **/

public abstract class Query implements RuleSet, UniqueIdentifiable {
    protected BaseQuery<?> baseQuery;

    public Query(BaseQuery<?> baseQuery) {
        this.baseQuery = baseQuery;
    }

    @Override
    public BaseQuery<?> getBaseQuery() {
        return baseQuery;
    }

    @Override
    public Predicate<Track> getPredicate() {
        return getSpecificPredicate()
                .and(getMandatoryTagsPredicate())
                .and(getOptionalTagsPredicate())
                .and(getTimestampPredicate())
                .and(getDurationPredicate())
                .and(getStreamPredicate())
                .and(getLikePredicate());
    }

    @Override
    public Predicate<Track> getMandatoryTagsPredicate() {
        Predicate<Track> predicate = track -> true;
        for (String tag : baseQuery.getMandatoryTags()) {
            predicate = predicate.and(track -> track.getTags().anyContains(tag));
        }
        return predicate;
    }

    @Override
    public Predicate<Track> getOptionalTagsPredicate() {
        if (baseQuery.getOptionalTags().isEmpty()) return track -> true;
        Predicate<Track> predicate = track -> false;
        for (String tag : baseQuery.getOptionalTags()) {
            predicate = predicate.or(track -> track.getTags().anyContains(tag));
        }
        return predicate;
    }

    @Override
    public Predicate<Track> getTimestampPredicate() {
        return track -> track.getCreatedAt() <= baseQuery.getMaxTimestamp() && track.getCreatedAt() >= baseQuery.getMinTimestamp();
    }

    @Override
    public Predicate<Track> getDurationPredicate() {
        return track -> track.getDuration() <= baseQuery.getMaxDuration() && track.getDuration() >= baseQuery.getMinDuration();
    }

    @Override
    public Predicate<Track> getStreamPredicate() {
        return track -> track.getPlaybackCount() <= baseQuery.getMaxStream() && track.getPlaybackCount() >= baseQuery.getMinStream();
    }

    @Override
    public Predicate<Track> getLikePredicate() {
        return track -> track.getLikeCount() <= baseQuery.getMaxLike() && track.getLikeCount() >= baseQuery.getMinLike();
    }

    protected abstract Predicate<Track> getSpecificPredicate();

    public abstract String getKeyword();
}
