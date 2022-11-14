package com.hawolt.data.media.search;

import com.hawolt.data.VirtualClient;
import com.hawolt.data.media.MediaLoader;
import com.hawolt.data.media.Track;
import com.hawolt.data.media.search.query.Query;
import com.hawolt.data.media.search.query.impl.SearchQuery;
import com.hawolt.data.media.search.query.impl.TagQuery;
import com.hawolt.data.media.search.query.TrackCollection;
import com.hawolt.http.Response;
import com.hawolt.logger.Logger;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

/**
 * Created: 13/11/2022 18:54
 * Author: Twitter @hawolt
 **/

public class Explorer implements Iterator<TrackCollection> {

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    private static final Map<Class<? extends Query>, String> map = new HashMap<Class<? extends Query>, String>() {{
        put(SearchQuery.class, "https://api-v2.soundcloud.com/search/tracks?q=%s&client_id=%s&limit=50&offset=0&linked_partitioning=1");
        put(TagQuery.class, "https://api-v2.soundcloud.com/recent-tracks/%s?client_id=%s&limit=50&offset=0&linked_partitioning=1");
    }};

    public static Explorer browse(Query query) throws Exception {
        String base = map.get(query.getClass());
        String uri = String.format(base, URLEncoder.encode(query.getKeyword(), StandardCharsets.UTF_8.name()), VirtualClient.getID());
        Logger.debug("base_href={}", uri);
        MediaLoader loader = new MediaLoader(uri);
        Response response = loader.call();
        return new Explorer(query.getPredicate(), new JSONObject(response.getBodyAsString()));
    }

    public static CompletableFuture<List<Track>> fetch(Query query) {
        CompletableFuture<List<Track>> future = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                Explorer explorer = Explorer.browse(query);
                List<Track> list = new ArrayList<>();
                do {
                    TrackCollection history = explorer.next();
                    for (Track track : history) {
                        list.add(track);
                    }
                } while (explorer.hasNext());
                list.sort((o1, o2) -> Long.compare(o2.getCreatedAt(), o1.getCreatedAt()));
                future.complete(list);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    private final Predicate<Track> predicate;
    private TrackCollection collection;
    private JSONObject current;

    public Explorer(Predicate<Track> predicate, JSONObject object) {
        this.predicate = predicate;
        this.current = object;
    }

    @Override
    public boolean hasNext() {
        return collection == null || (current != null && current.has("next_href") && !current.isNull("next_href"));
    }

    @Override
    public TrackCollection next() {
        TrackCollection collection = new TrackCollection();
        if (this.collection == null) {
            this.collection = new TrackCollection(current.getJSONArray("collection"));
        } else {
            try {
                this.collection = loadNext();
            } catch (Exception e) {
                Logger.error(e);
            }
        }
        for (Track track : this.collection) {
            if (predicate.test(track)) {
                collection.append(track);
            }
        }
        return this.collection = collection;
    }

    private TrackCollection loadNext() throws Exception {
        String auth = String.format("client_id=%s", VirtualClient.getID());
        String next = String.join("&", current.getString("next_href"), auth);
        Logger.debug("next_href={}", next);
        MediaLoader loader = new MediaLoader(next);
        Response response = loader.call();
        this.current = new JSONObject(response.getBodyAsString());
        if (!current.has("collection")) return TrackCollection.EMPTY;
        return new TrackCollection(current.getJSONArray("collection"));
    }

}
