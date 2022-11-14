package com.hawolt.data.media.search.query;

import com.hawolt.data.media.Track;

import java.util.function.Predicate;

/**
 * Created: 14/11/2022 12:46
 * Author: Twitter @hawolt
 **/

public interface RuleSet {
    BaseQuery<?> getBaseQuery();

    Predicate<Track> getPredicate();

    Predicate<Track> getMandatoryTagsPredicate();

    Predicate<Track> getOptionalTagsPredicate();

    Predicate<Track> getTimestampPredicate();

    Predicate<Track> getDurationPredicate();

    Predicate<Track> getStreamPredicate();

    Predicate<Track> getLikePredicate();

}
