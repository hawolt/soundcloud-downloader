package com.hawolt.data.media;

import com.hawolt.data.media.hydratable.Hydratable;
import org.json.JSONObject;

/**
 * Created: 09/02/2022 12:36
 * Author: Twitter @hawolt
 **/

public interface MediaInterface<T extends Hydratable> {
    T convert(JSONObject object);
}
