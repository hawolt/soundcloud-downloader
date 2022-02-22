package com.hawolt.interactive;

import com.hawolt.*;
import com.hawolt.data.media.download.FileManager;
import com.hawolt.data.media.hydratable.Hydratable;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created: 02/02/2022 15:17
 * Author: Twitter @hawolt
 **/

public class SoundcloudCLI {

    public static void main(String[] args) {
        Parser parser = new Parser();
        parser.add(Argument.create("dl", "download", "resource(s) to download", true, false, false));
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
            List<String> resources = cli.get("download");
            Logger.info("Preparing download for {} element{}", resources.size(), resources.size() == 1 ? "" : "s");
            DefaultMediaManager manager = new DefaultMediaManager();
            for (String resource : resources) {
                manager.load(resource);
            }
        } catch (IOException | ParserException e) {
            Logger.error(e.getMessage());
            Logger.error(parser.getHelp());
            System.exit(0);
        }
    }
}
