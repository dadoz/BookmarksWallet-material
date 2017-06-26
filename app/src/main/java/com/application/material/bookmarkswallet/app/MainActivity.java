package com.application.material.bookmarkswallet.app;

import com.application.material.bookmarkswallet.app.navigationDrawer.NavigationDrawerActivity;


public class MainActivity extends NavigationDrawerActivity {
    private String TAG = "MainActivity";
    public static String SHARED_URL_EXTRA_KEY = "SHARED_URL_EXTRA_KEY";

    protected MainActivity() {
        super(R.layout.activity_main_drawer_layout);
    }
}
