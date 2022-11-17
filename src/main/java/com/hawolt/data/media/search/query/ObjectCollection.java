package com.hawolt.data.media.search.query;

import com.hawolt.data.media.search.Explorer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * Created: 15/11/2022 11:25
 * Author: Twitter @hawolt
 **/

public class ObjectCollection<T> implements Iterable<T> {

    private final List<T> list = new ArrayList<>();

    public ObjectCollection(Explorer<T> explorer) {
        while (explorer.hasNext()) {
            PartialCollection<T> partialCollection = explorer.next();
            list.addAll(partialCollection.getList());
        }
    }

    public List<T> getList() {
        return list;
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}