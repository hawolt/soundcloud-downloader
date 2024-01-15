package interactive;

import com.hawolt.cli.Argument;
import com.hawolt.cli.CLI;
import com.hawolt.cli.Parser;
import com.hawolt.cli.ParserException;
import com.hawolt.data.media.download.FileManager;
import com.hawolt.data.media.hydratable.Hydratable;
import com.hawolt.data.media.hydratable.impl.track.Track;
import com.hawolt.data.media.hydratable.impl.user.User;
import com.hawolt.data.media.search.Explorer;
import com.hawolt.data.media.search.query.impl.LikeQuery;
import com.hawolt.logger.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Created: 02/02/2022 15:17
 * Author: Twitter @hawolt
 **/

public class SoundcloudCLI {

    public static void main(String[] args) {
        Parser parser = new Parser();
        parser.add(Argument.create("f", "file", "parse links from file", false, true, false));
        parser.add(Argument.create("dl", "download", "resource(s) to download", false, false, false));
        parser.add(Argument.create("dir", "directory", "directory to save files", false, true, false));
        parser.add(Argument.create("t", "threads", "specify amount of threads", false, true, false));
        try {
            CLI cli = parser.check(args);
            int threads = Integer.parseInt(cli.has("threads") ? cli.getValue("threads") : "1");
            Logger.debug("Initializing Executor with {} thread{}", threads, threads == 1 ? "" : "s");
            Hydratable.EXECUTOR_SERVICE = Executors.newFixedThreadPool(threads);
            if (cli.has("directory")) {
                FileManager.setup(Paths.get(cli.getValue("directory")));
            }
            Logger.debug("Setting file directory as \"{}\"", FileManager.path);
            AbstractMediaManager manager = new AbstractMediaManager() {
                @Override
                public void onUser(String link, User user) {
                    Logger.debug("Loaded {}:{}", user.getUserId(), link);
                    LikeQuery likeQuery = new LikeQuery(user.getUserId());
                    try {
                        for (Track track : Explorer.browse(likeQuery)) {
                            System.out.println(track.getLink());
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void onTrack(Track track, byte[] b) {
                    store(track, b);
                    Logger.info("Completed download for {}", String.format("%s.%s.mp3", track.getUser().getPermalink(), track.getPermalink()));
                }

                @Override
                public void onPlaylist(String link, Map<Track, byte[]> playlist) {
                    for (Map.Entry<Track, byte[]> entry : playlist.entrySet()) {
                        store(entry.getKey(), entry.getValue());
                    }
                    Logger.info("Completed download for playlist {}", link);
                }

                private void store(Track track, byte[] b) {
                    FileManager.store(track, b).whenComplete((file, fex) -> {
                        if (fex != null) {
                            Logger.error(fex);
                        }
                    });
                }
            };
            List<String> resources = null;
            if (cli.has("download")) {
                resources = cli.get("download");
            } else if (cli.has("file")) {
                resources = Files.readAllLines(Paths.get(cli.getValue("file")));
            }
            if (resources == null) return;
            Logger.info("Preparing download for {} element{}", resources.size(), resources.size() == 1 ? "" : "s");
            for (String resource : resources) {
                manager.load(resource);
            }
        } catch (IOException | ParserException e) {
            Logger.error(e.getMessage());
            System.err.println(parser.getHelp());
            System.exit(0);
        }
    }
}
