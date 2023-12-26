package com.hawolt.data.media.search.query;

import com.hawolt.data.media.search.Explorer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created: 15/11/2022 11:25
 * Author: Twitter @hawolt
 **/

public abstract class ObjectCollection<T> implements Iterable<T> {
    protected final Explorer<T> explorer;

    public ObjectCollection(Explorer<T> explorer) {
        this.explorer = explorer;
    }
}