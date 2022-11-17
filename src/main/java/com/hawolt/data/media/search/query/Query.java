package com.hawolt.data.media.search.query;

import org.json.JSONObject;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created: 14/11/2022 22:40
 * Author: Twitter @hawolt
 **/

public interface Query<T> {
    String getKeyword();

    String checksum();

    Predicate<T> filter();

    Function<JSONObject, T> getTransformer();
}
