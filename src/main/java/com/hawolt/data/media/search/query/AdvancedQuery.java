package com.hawolt.data.media.search.query;

import com.hawolt.cryptography.SHA256;
import com.hawolt.data.media.hydratable.impl.track.Track;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created: 17/11/2022 19:06
 * Author: Twitter @hawolt
 **/

public abstract class AdvancedQuery implements RuleSet, Query<Track> {
    private final List<String> mandatoryTags = new ArrayList<>();
    private long minStream, minLike, minDuration, minTimestamp,
            maxTimestamp = Long.MAX_VALUE,
            maxDuration = Long.MAX_VALUE,
            maxStream = Long.MAX_VALUE,
            maxLike = Long.MAX_VALUE;

    public AdvancedQuery addMandatoryTag(String tag) {
        this.mandatoryTags.add(tag);
        return this;
    }

    public AdvancedQuery setMinStream(long minStream) {
        this.minStream = minStream;
        return this;
    }

    public AdvancedQuery setMaxStream(long maxStream) {
        this.maxStream = maxStream;
        return this;
    }

    public AdvancedQuery setMinLike(long minLike) {
        this.minLike = minLike;
        return this;
    }

    public AdvancedQuery setMaxLike(long maxLike) {
        this.maxLike = maxLike;
        return this;
    }

    public AdvancedQuery setMinDuration(long minDuration) {
        this.minDuration = minDuration;
        return this;
    }

    public AdvancedQuery setMaxDuration(long maxDuration) {
        this.maxDuration = maxDuration;
        return this;
    }

    public AdvancedQuery setMinTimestamp(long minTimestamp) {
        this.minTimestamp = minTimestamp;
        return this;
    }

    public AdvancedQuery setMaxTimestamp(long maxTimestamp) {
        this.maxTimestamp = maxTimestamp;
        return this;
    }

    public long getMinStream() {
        return minStream;
    }

    public long getMaxStream() {
        return maxStream;
    }

    public long getMinLike() {
        return minLike;
    }

    public long getMaxLike() {
        return maxLike;
    }

    public long getMinDuration() {
        return minDuration;
    }

    public long getMaxDuration() {
        return maxDuration;
    }

    public long getMinTimestamp() {
        return minTimestamp;
    }

    public long getMaxTimestamp() {
        return maxTimestamp;
    }

    public List<String> getMandatoryTags() {
        return mandatoryTags;
    }


    @Override
    public String checksum() {
        BigInteger integer = new BigInteger("0")
                .add(BigInteger.valueOf(minStream))
                .add(BigInteger.valueOf(minLike))
                .add(BigInteger.valueOf(minDuration))
                .add(BigInteger.valueOf(minTimestamp))
                .add(BigInteger.valueOf(maxStream))
                .add(BigInteger.valueOf(maxLike))
                .add(BigInteger.valueOf(maxDuration))
                .add(BigInteger.valueOf(maxTimestamp));
        String plain = Stream.concat(
                Stream.of(integer.toString()),
                mandatoryTags.stream()
        ).collect(Collectors.joining());
        return SHA256.hash(getKeyword() + plain);
    }

    @Override
    public Predicate<Track> getMandatoryTagsPredicate() {
        Predicate<Track> predicate = track -> true;
        for (String tag : getMandatoryTags()) {
            predicate = predicate.and(track -> track.getTags().anyContains(tag));
        }
        return predicate;
    }

    @Override
    public Predicate<Track> getTimestampPredicate() {
        return track -> track.getCreatedAt() <= getMaxTimestamp() && track.getCreatedAt() >= getMinTimestamp();
    }

    @Override
    public Predicate<Track> getDurationPredicate() {
        return track -> track.getDuration() <= getMaxDuration() && track.getDuration() >= getMinDuration();
    }

    @Override
    public Predicate<Track> getStreamPredicate() {
        return track -> track.getPlaybackCount() <= getMaxStream() && track.getPlaybackCount() >= getMinStream();
    }

    @Override
    public Predicate<Track> getLikePredicate() {
        return track -> track.getLikeCount() <= getMaxLike() && track.getLikeCount() >= getMinLike();
    }

    @Override
    public Predicate<Track> filter() {
        return getMandatoryTagsPredicate()
                .and(getTimestampPredicate())
                .and(getDurationPredicate())
                .and(getStreamPredicate())
                .and(getLikePredicate());
    }
}
