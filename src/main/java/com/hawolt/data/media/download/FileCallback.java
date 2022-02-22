package com.hawolt.data.media.download;

/**
 * Created: 09/02/2022 13:05
 * Author: Twitter @hawolt
 **/

public interface FileCallback {
    void onAssembly(int index, IFile file);

    void onFailure(int index, int attempt, String url);
}
