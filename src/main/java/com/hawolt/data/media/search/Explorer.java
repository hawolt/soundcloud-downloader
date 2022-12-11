package com.hawolt.data.media.search;

import com.hawolt.data.VirtualClient;
import com.hawolt.data.media.MediaLoader;
import com.hawolt.data.media.search.endpoint.InstructionInterpreter;
import com.hawolt.data.media.search.query.ObjectCollection;
import com.hawolt.data.media.search.query.PartialCollection;
import com.hawolt.data.media.search.query.Query;
import com.hawolt.data.media.search.query.impl.*;
import com.hawolt.http.Response;
import com.hawolt.logger.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created: 13/11/2022 18:54
 * Author: Twitter @hawolt
 **/

public class Explorer<T> implements Iterator<PartialCollection<T>> {

    private static final Map<Class<? extends Query<?>>, String> map = new HashMap<Class<? extends Query<?>>, String>() {{
        put(SearchQuery.class, "https://api-v2.soundcloud.com/search/tracks?q=%s&offset=0&limit=50&linked_partitioning=1&client_id=$(client)");
        put(TagQuery.class, "https://api-v2.soundcloud.com/recent-tracks/%s?offset=0&limit=50&linked_partitioning=1&client_id=$(client)");
        put(FollowingQuery.class, "https://api-v2.soundcloud.com/users/%s/followings?offset=$(timestamp)&limit=200&client_id=$(client)");
        put(UploadQuery.class, "https://api-v2.soundcloud.com/users/%s/tracks?offset=0&limit=50&client_id=$(client)");
        put(TrackQuery.class, "https://api-v2.soundcloud.com/tracks?ids=%s&client_id=$(client)");
    }};

    public static <T> ObjectCollection<T> browse(Query<T> query) throws Exception {
        String base = map.get(query.getClass());
        if (query instanceof TrackQuery) {
            TrackQuery trackQuery = (TrackQuery) query;
            String playlistId = "playlistId=$(substitute " + trackQuery.getPlaylistId() + ")";
            String playlistSecretToken = "playlistSecretToken=$(substitute " + trackQuery.getSecret() + ")";
            base = String.join("&", base, playlistId, playlistSecretToken);
        }
        String uri = String.format(InstructionInterpreter.parse(base), URLEncoder.encode(query.getKeyword(), StandardCharsets.UTF_8.name()));
        Logger.debug("base_href={}", uri);
        MediaLoader loader = new MediaLoader(uri);
        Response response = loader.call();
        String body = response.getBodyAsString();
        boolean array = body.startsWith("[") && body.endsWith("]");
        String plain = !array ? body : new JSONObject().put("collection", new JSONArray().put(new JSONObject(body.substring(1, body.length() - 1)))).toString();
        return new ObjectCollection<>(new Explorer<>(query.filter(), query.getTransformer(), new JSONObject(plain)));
    }

    private final Function<JSONObject, T> transformer;
    private final Predicate<T> filter;
    private PartialCollection<T> collection;
    private JSONObject current;

    public Explorer(Predicate<T> filter, Function<JSONObject, T> transformer, JSONObject object) {
        this.transformer = transformer;
        this.current = object;
        this.filter = filter;
    }

    @Override
    public boolean hasNext() {
        return collection == null || (current != null && current.has("next_href") && !current.isNull("next_href"));
    }

    @Override
    public PartialCollection<T> next() {
        PartialCollection<T> collection = new PartialCollection<>(filter, transformer);
        if (this.collection == null) {
            this.collection = new PartialCollection<>(filter, transformer, current.getJSONArray("collection"));
        } else {
            try {
                this.collection = loadNext();
            } catch (Exception e) {
                Logger.error(e);
            }
        }
        for (T object : this.collection) {
            collection.append(object);
        }
        return this.collection = collection;
    }

    private PartialCollection<T> loadNext() throws Exception {
        String auth = String.format("client_id=%s", VirtualClient.getID());
        String next = String.join("&", current.getString("next_href"), auth);
        Logger.debug("next_href={}", next);
        MediaLoader loader = new MediaLoader(next);
        Response response = loader.call();
        this.current = new JSONObject(response.getBodyAsString());
        if (!current.has("collection")) return new PartialCollection<>(filter, transformer);
        return new PartialCollection<>(filter, transformer, current.getJSONArray("collection"));
    }

}
