package com.hawolt.data.media.hydratable;

import java.util.concurrent.ExecutorService;

/**
 * Created: 09/02/2022 12:47
 * Author: Twitter @hawolt
 **/

public class Hydratable {
    public static ExecutorService EXECUTOR_SERVICE;

    public static void snooze(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {

        }
    }
}
