package com.application.material.bookmarkswallet.app.fragments;

/**
 * Created by davide on 10/08/15.
 */
public interface OnTaskCompleted {
    void onTaskCompleted(String url);
    void onTaskCompleted(boolean isRefreshEnabled);
    void onTaskCompleted(byte [] data);
}
