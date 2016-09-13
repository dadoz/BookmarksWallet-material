package com.application.material.bookmarkswallet.app.application;

import android.app.Application;

import com.application.material.bookmarkswallet.app.R;
import com.flurry.android.FlurryAgent;

public class BookmarksWalletApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        new FlurryAgent.Builder()
                .withLogEnabled(false)
                .build(this, getString(R.string.FLURRY_API_KEY));
    }

}
