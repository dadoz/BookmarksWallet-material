package com.application.material.bookmarkswallet.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by davide on 13/08/15.
 */
public class BookmarksProviderService extends IntentService {

    public BookmarksProviderService() {
        super("BookmarksProviderService");

    }
    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
