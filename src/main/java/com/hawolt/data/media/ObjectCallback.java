package com.hawolt.data.media;

/**
 * Created: 09/02/2022 12:57
 * Author: Twitter @hawolt
 **/

public interface ObjectCallback<T> {
    void ping(String link, T t);
}
