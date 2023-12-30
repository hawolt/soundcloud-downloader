package com.hawolt.data.media.search.query;

import com.hawolt.cryptography.SHA256;
import com.hawolt.data.media.hydratable.impl.track.Track;
import com.hawolt.data.media.hydratable.impl.user.User;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created: 17/11/2022 19:06
 * Author: Twitter @hawolt
 **/

public abstract class AdvancedQuery implements RuleSet, Query<Track> {
    private final List<Predicate<Track>> predicates = new ArrayList<>();
    private final List<String> blacklistedUser = new ArrayList<>();
    private final List<String> blacklistedTags = new ArrayList<>();
    private final List<String> mandatoryTags = new ArrayList<>();
    private int maximumTagThreshold = Integer.MAX_VALUE;
    private long minStream, minLike, minDuration, minTimestamp,
            maxTimestamp = Long.MAX_VALUE,
            maxDuration = Long.MAX_VALUE,
            maxStream = Long.MAX_VALUE,
            maxLike = Long.MAX_VALUE;
    private boolean authenticUserOnly;

    public AdvancedQuery addCustomPredicate(Predicate<Track> predicate) {
        this.predicates.add(predicate);
        return this;
    }

    public AdvancedQuery setMaximumTagThreshold(int maximumTagThreshold) {
        this.maximumTagThreshold = maximumTagThreshold;
        return this;
    }

    public AdvancedQuery setAuthenticUserOnly(boolean authenticUserOnly) {
        this.authenticUserOnly = authenticUserOnly;
        return this;
    }

    public AdvancedQuery addMandatoryTag(String tag) {
        this.mandatoryTags.add(tag);
        return this;
    }

    public AdvancedQuery addBlacklistedTag(String tag) {
        this.blacklistedTags.add(tag);
        return this;
    }

    public AdvancedQuery addBlacklistedUser(String user) {
        this.blacklistedUser.add(user.toLowerCase());
        return this;
    }

    public AdvancedQuery addBlacklistedUsersByName(List<String> users) {
        for (String user : users) {
            this.addBlacklistedUser(user);
        }
        return this;
    }

    public AdvancedQuery addBlacklistedUsersByUser(List<User> users) {
        for (User user : users) {
            this.addBlacklistedUser(user.getPermalink());
        }
        return this;
    }

    public AdvancedQuery addBlacklistedUsers(String... users) {
        return addBlacklistedUsersByName(Arrays.asList(users));
    }

    public AdvancedQuery addBlacklistedUsers(File file) {
        try {
            for (String user : Files.readAllLines(file.toPath())) {
                this.blacklistedUser.add(user.toLowerCase());
            }
        } catch (IOException e) {

        }
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

    public List<String> getBlacklistedTags() {
        return blacklistedTags;
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
        Stream<String> tagStream = Stream.concat(
                mandatoryTags.stream().map(tag -> "m:" + tag),
                blacklistedTags.stream().map(tag -> "b:" + tag)
        );
        String plain = Stream.concat(
                Stream.of(integer.toString()),
                tagStream
        ).collect(Collectors.joining());
        return SHA256.hash(getKeyword() + plain);
    }

    @Override
    public Predicate<Track> getBlacklistedTagsPredicate() {
        Predicate<Track> predicate = track -> true;
        for (String tag : getBlacklistedTags()) {
            predicate = predicate.and(track -> !track.getTags().anyContains(tag));
        }
        return predicate;
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
    public Predicate<Track> getAuthenticUserPredicate() {
        return track -> !track.getUser().getPermalink().startsWith("user-");
    }

    @Override
    public Predicate<Track> getMaximumTagThresholdPredicate() {
        return track -> track.getTags().getList().size() <= maximumTagThreshold;
    }

    @Override
    public Predicate<Track> getBlacklistedUserPredicate() {
        return track -> !blacklistedUser.contains(track.getUser().getPermalink().toLowerCase());
    }

    @Override
    public Predicate<Track> filter() {
        Predicate<Track> base = getMaximumTagThresholdPredicate()
                .and(getBlacklistedUserPredicate())
                .and(getBlacklistedTagsPredicate())
                .and(getAuthenticUserPredicate())
                .and(getMandatoryTagsPredicate())
                .and(getTimestampPredicate())
                .and(getDurationPredicate())
                .and(getStreamPredicate())
                .and(getLikePredicate());
        for (Predicate<Track> predicate : predicates) {
            base = base.and(predicate);
        }
        return base;
    }
}
