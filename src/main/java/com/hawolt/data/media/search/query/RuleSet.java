package com.hawolt.data.media.search.query;

import com.hawolt.data.media.hydratable.impl.track.Track;

import java.util.function.Predicate;

/**
 * Created: 14/12/2022 13:44
 * Author: Twitter @hawolt
 **/

public interface RuleSet {
    Predicate<Track> getMandatoryTagsPredicate();

    Predicate<Track> getTimestampPredicate();

    Predicate<Track> getDurationPredicate();

    Predicate<Track> getStreamPredicate();

    Predicate<Track> getLikePredicate();
}
