package com.hawolt;

public interface LoadCallback {

    void onLoadFailure(String link, Exception e, String... args);
}
