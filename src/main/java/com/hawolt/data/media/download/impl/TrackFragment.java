package com.hawolt.data.media.download.impl;

import com.hawolt.Logger;
import com.hawolt.Response;
import com.hawolt.data.media.MediaLoader;
import com.hawolt.data.media.download.FileCallback;
import com.hawolt.data.media.download.IFile;

/**
 * Created: 09/02/2022 13:01
 * Author: Twitter @hawolt
 **/

public class TrackFragment implements Runnable, IFile {

    private final FileCallback callback;
    private final String url;
    private final int index;
    private int failures;
    private byte[] b;

    public TrackFragment(FileCallback callback, int index, String url) {
        this.callback = callback;
        this.index = index;
        this.url = url;
    }

    public byte[] getBytes() {
        return b;
    }

    @Override
    public void run() {
        try {
            MediaLoader loader = new MediaLoader(url);
            Response response = loader.call();
            this.b = response.getBody();
            callback.onAssembly(index, this);
        } catch (Exception e) {
            Logger.error(e);
            callback.onFailure(index, failures++, url);
        }
    }
}
