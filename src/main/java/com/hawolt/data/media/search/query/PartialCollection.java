package com.hawolt.data.media.search.query;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created: 15/11/2022 11:25
 * Author: Twitter @hawolt
 **/

public class PartialCollection<T> implements Iterable<T> {
    private final List<T> list = new ArrayList<>();

    public PartialCollection(Predicate<T> filter, Function<JSONObject, T> transformer) {
        this(filter, transformer, new JSONArray());
    }

    public PartialCollection(Predicate<T> filter, Function<JSONObject, T> transformer, JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            T t = transformer.apply(array.getJSONObject(i));
            if (filter.test(t)) list.add(t);
        }
    }

    public void append(T object) {
        list.add(object);
    }

    public List<T> getList() {
        return list;
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
}