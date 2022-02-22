package com.hawolt.data.media.hydratable;

import com.hawolt.exception.HydrationException;
import org.json.JSONArray;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created: 09/02/2022 12:12
 * Author: Twitter @hawolt
 **/

public class Hydration {
    private static final String SCRIPT_SRC = "<script>window\\.__sc_hydration = (.*);</script>";
    private static final Pattern SCRIPT_PATTERN = Pattern.compile(SCRIPT_SRC);

    public static JSONArray from(String source) {
        Matcher matcher = SCRIPT_PATTERN.matcher(source);
        if (matcher.find()) {
            return new JSONArray(matcher.group(1));
        } else {
            throw new HydrationException("Unable to find Hydration");
        }
    }
}
