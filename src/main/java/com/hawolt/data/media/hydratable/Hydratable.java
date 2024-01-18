package com.hawolt.data.media.hydratable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created: 09/02/2022 12:47
 * Author: Twitter @hawolt
 **/

public class Hydratable {
    protected final long loadReferenceTimestamp;
    public Hydratable(long loadReferenceTimestamp){
        this.loadReferenceTimestamp=loadReferenceTimestamp;
    }

    public long getLoadReferenceTimestamp() {
        return loadReferenceTimestamp;
    }

    public static void snooze(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {

        }
    }
}
