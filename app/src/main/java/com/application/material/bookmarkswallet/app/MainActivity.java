package com.application.material.bookmarkswallet.app;

import android.content.Context;

import com.application.material.bookmarkswallet.app.navigationDrawer.NavigationDrawerActivity;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends NavigationDrawerActivity {
    private String TAG = "MainActivity";
    public static String SHARED_URL_EXTRA_KEY = "SHARED_URL_EXTRA_KEY";

    protected MainActivity() {
        super(R.layout.activity_main_drawer_layout);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
