package com.hawolt.data.media.download;

import com.hawolt.data.media.Track;
import com.hawolt.logging.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created: 09.02.2022 13:04
 * Author: Twitter @hawolt
 **/

public class FileManager {

    private static final ExecutorService SERVICE = Executors.newSingleThreadExecutor();
    public static Path path;

    static {
        FileManager.path = Paths.get(System.getProperty("user.home")).resolve(".hawolt").resolve("soundcloud").resolve("songs");
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            Logger.error(e);
        }
    }

    public static File[] getCache() {
        return path.toFile().listFiles();
    }

    public static File getFile(Track track) {
        String filename = String.format("%s.mp3", track.getPermalink());
        return path.resolve(filename).toFile();
    }

    public static boolean isCached(Track track) {
        String filename = String.format("%s.mp3", track.getPermalink());
        return path.resolve(filename).toFile().exists();
    }

    public static CompletableFuture<File> store(Track track, byte[] b) {
        return CompletableFuture.supplyAsync(() -> {
            String filename = String.format("%s.mp3", track.getPermalink());
            File file = FileManager.path.resolve(filename).toFile();
            try (FileOutputStream stream = new FileOutputStream(file)) {
                stream.write(b);
                stream.flush();
            } catch (IOException e) {
                Logger.error(e);
            }
            return file;
        }, SERVICE);
    }

}
