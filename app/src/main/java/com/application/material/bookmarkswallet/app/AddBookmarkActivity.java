package com.application.material.bookmarkswallet.app;

//import android.app.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.application.material.bookmarkswallet.app.fragments.AddBookmarkFragment;
import com.application.material.bookmarkswallet.app.fragments.BaseFragment;
import com.application.material.bookmarkswallet.app.fragments.SettingsFragment;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnInitActionBarInterface;


public class AddBookmarkActivity extends ActionBarActivity
        implements OnChangeFragmentWrapperInterface, OnInitActionBarInterface {

    public static final int ADD_REQUEST = 99;
    public static String LINK_URL_EXTRA = "LINK_URL_EXTRA";
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onInitView();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public void onInitView() {
        AddBookmarkFragment baseFragment = new AddBookmarkFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerFrameLayoutId,
                        baseFragment, AddBookmarkFragment.FRAG_TAG).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case  R.id.action_settings:
                changeFragment(new SettingsFragment(), null, SettingsFragment.FRAG_TAG);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void changeFragment(Fragment fragment, Bundle bundle, String tag) {
        if(fragment == null) {
            Log.e(TAG, "null fragment injected");
            return;
        }
        fragment.setArguments(bundle);

        if(tag.equals(AddBookmarkFragment.FRAG_TAG)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerFrameLayoutId, fragment, tag)
                    .commit();
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerFrameLayoutId, fragment, tag)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void pushCurrentFragTag(String tag) {

    }

    @Override
    public void setCurrentFragTag(String tag) {

    }

    @Override
    public String popCurrentFragTag() {
        return null;
    }

    @Override
    public String getCurrentFragTag() {
        return null;
    }

    @Override
    public void startActivityForResultWrapper(Class activityClassName, int requestCode, Bundle bundle) {

    }

    @Override
    public void initActionBarWithCustomView(Toolbar toolbar) {
        //set action bar
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        try {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.actionbar_add_bookmark_layout);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initActionBar(Toolbar toolbar, String title) {
        //set action bar
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        try {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setTitle(title != null ?
                    title :
                    getResources().getString(R.string.app_name));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toggleEditActionBar(String title, boolean isSelecting) {
    }

}
