package com.application.material.bookmarkswallet.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.application.material.bookmarkswallet.app.AddBookmarkActivity.OnHandleBackPressed;
import com.application.material.bookmarkswallet.app.navigationDrawer.BaseNavigationDrawerActivity;
import com.application.material.bookmarkswallet.app.fragments.BookmarkListFragment;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends BaseNavigationDrawerActivity {
    private String TAG = "MainActivity";
    public static String SHARED_URL_EXTRA_KEY = "SHARED_URL_EXTRA_KEY";

    protected MainActivity() {
        super(R.layout.activity_main_drawer_layout);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onItemMenuSelectedCallback(int position) {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            android.support.v4.app.FragmentManager.BackStackEntry frag = (getSupportFragmentManager()
                    .getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1));
//        boolean backPressHandled = ((OnHandleBackPressed) frag.getId()).handleBackPressed();
//        if (!backPressHandled) {
//            super.onBackPressed();
//        }
        }
        super.onBackPressed();
    }

    //    /**
//     *
//     */
//    public void initActionbar() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarId);
//        setSupportActionBar(toolbar);
//
//        getSupportActionBar().setTitle(R.string.bookmark_list_title);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);
//        ActionbarHelper.setElevationOnVIew(findViewById(R.id.appBarLayoutId), true);
//    }
}
