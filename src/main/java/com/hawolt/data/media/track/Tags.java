package com.hawolt.data.media.track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created: 09.02.2022 12:59
 * Author: Twitter @hawolt
 **/

public class Tags {

    private final List<String> list = new ArrayList<>();

    public Tags(String genre, String tagline) {
        int indexOf;
        while ((indexOf = tagline.indexOf("\"")) != -1) {
            int endIndex = tagline.indexOf("\"", indexOf + 1);
            list.add(tagline.substring(indexOf + 1, endIndex));
            tagline = tagline.replace(tagline.substring(indexOf, endIndex + 1), "");
        }
        Collections.addAll(list, tagline.split(" "));
        list.add(genre);
    }

    public boolean contains(String tag) {
        return list.contains(tag);
    }

    public boolean anyContains(String t) {
        return list.stream().anyMatch(tag -> tag.toLowerCase().contains(t.toLowerCase()));
    }

    public boolean anyMatch(String expression) {
        return list.stream().filter(tag -> tag.matches(expression)).count() > 0;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String tag : list) {
            if (tag.length() > 0) builder.append("#").append(tag).append(", ");
        }
        if (builder.length() > 0) builder.setLength(builder.length() - 2);
        return builder.toString().trim();
    }

}
