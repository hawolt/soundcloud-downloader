package com.hawolt.data.media.impl;

import com.hawolt.data.media.Author;
import com.hawolt.data.media.MediaInterface;
import org.json.JSONObject;

/**
 * Created: 09/02/2022 12:38
 * Author: Twitter @hawolt
 **/

public class MediaAuthorInterface implements MediaInterface<Author> {

    @Override
    public Author convert(JSONObject object) {
        return new Author(object);
    }
}
