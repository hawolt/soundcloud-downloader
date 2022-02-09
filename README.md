# soundcloud
example usage
```java
import com.hawolt.data.media.MediaManager;
import com.hawolt.data.media.Track;
import com.hawolt.data.media.download.DownloadCallback;
import com.hawolt.data.media.download.FileManager;
import com.hawolt.logging.Logger;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created: 09.02.2022 12:22
 * Author: Twitter @hawolt
 **/

public class Example {
    public static void main(String[] args) {
        DownloadCallback callback = new DownloadCallback() {
            @Override
            public void onCompletion(Track track, byte[] b) {
                FileManager.store(track, b).whenComplete((file, fex) -> {
                    if (fex != null) {
                        Logger.error(fex);
                    } else {
                        Logger.info("Track stored in bsc cache");
                    }
                });
            }

            @Override
            public void onFailure(Track track, int fragment) {
                Logger.error("Failed to load fragment {} for {}", fragment, track.getPermalink());
            }

            @Override
            public void onLoadFailure(String link, IOException exception) {
                Logger.error("Failed to load track {}: {}", link, exception.getMessage());
            }
        };
        MediaManager manager = new MediaManager(callback) {
            @Override
            public void ping(Track track) {
                track.retrieveMP3().whenComplete((mp3, throwable) -> {
                    mp3.download(this.callback);
                });
            }
        };
        manager.load("https://soundcloud.com/zevranx/miracle-zevran-bootleg");
    }
}
```
