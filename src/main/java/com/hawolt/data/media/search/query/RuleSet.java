package com.hawolt.data.media.search.query;

import com.hawolt.data.media.hydratable.impl.track.Track;

import java.util.function.Predicate;

/**
 * Created: 17/11/2022 19:13
 * Author: Twitter @hawolt
 **/

public interface Ruleset {
    Predicate<Track> getMandatoryTagsPredicate();

    Predicate<Track> getTimestampPredicate();

    Predicate<Track> getDurationPredicate();

    Predicate<Track> getStreamPredicate();

    Predicate<Track> getLikePredicate();
}
