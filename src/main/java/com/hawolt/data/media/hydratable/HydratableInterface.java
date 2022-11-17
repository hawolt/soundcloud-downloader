package com.hawolt.data.media.hydratable;

/**
 * Created: 09/02/2022 12:49
 * Author: Twitter @hawolt
 **/

public interface HydratableInterface<T extends Hydratable> {
    void accept(String link, T hydratable);
}
