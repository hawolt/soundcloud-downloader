package com.hawolt.data.media.search.query;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created: 13/11/2022 19:06
 * Author: Twitter @hawolt
 **/

public class BaseQuery<T> {

    private final List<String> mandatoryTags = new ArrayList<>();
    private final List<String> optionalTags = new ArrayList<>();
    private long minStream, minLike, minDuration, minTimestamp,
            maxTimestamp = Long.MAX_VALUE,
            maxDuration = Long.MAX_VALUE,
            maxStream = Long.MAX_VALUE,
            maxLike = Long.MAX_VALUE;

    public BaseQuery<T> addMandatoryTag(String tag) {
        this.mandatoryTags.add(tag);
        return this;
    }

    public BaseQuery<T> addOptionalTag(String tag) {
        this.optionalTags.add(tag);
        return this;
    }

    public BaseQuery<T> setMinStream(long minStream) {
        this.minStream = minStream;
        return this;
    }

    public BaseQuery<T> setMaxStream(long maxStream) {
        this.maxStream = maxStream;
        return this;
    }

    public BaseQuery<T> setMinLike(long minLike) {
        this.minLike = minLike;
        return this;
    }

    public BaseQuery<T> setMaxLike(long maxLike) {
        this.maxLike = maxLike;
        return this;
    }

    public BaseQuery<T> setMinDuration(long minDuration) {
        this.minDuration = minDuration;
        return this;
    }

    public BaseQuery<T> setMaxDuration(long maxDuration) {
        this.maxDuration = maxDuration;
        return this;
    }

    public BaseQuery<T> setMinTimestamp(long minTimestamp) {
        this.minTimestamp = minTimestamp;
        return this;
    }

    public BaseQuery<T> setMaxTimestamp(long maxTimestamp) {
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

    public List<String> getOptionalTags() {
        return optionalTags;
    }

    public T define(Function<BaseQuery<?>, T> function) {
        return function.apply(this);
    }
}
