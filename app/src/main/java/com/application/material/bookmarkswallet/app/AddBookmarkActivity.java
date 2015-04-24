package com.application.material.bookmarkswallet.app;

//import android.app.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.application.material.bookmarkswallet.app.fragments.AddBookmarkFragment;
import com.application.material.bookmarkswallet.app.fragments.BookmarkListFragment;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.singleton.ActionBarHandlerSingleton;


public class AddBookmarkActivity extends ActionBarActivity
        implements OnChangeFragmentWrapperInterface {

    public static final int ADD_REQUEST = 99;
    public static String LINK_URL_EXTRA = "LINK_URL_EXTRA";
    private String TAG = "AddBookmarkActivity";
    private ActionBarHandlerSingleton mActionBarHandlerSingleton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActionBarHandlerSingleton = ActionBarHandlerSingleton.getInstance(this);

        onInitView();
    }

    @Override
    public void onResume() {
        mActionBarHandlerSingleton.setActivtyRef(this);
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public void onInitView() {
        mActionBarHandlerSingleton.initActionBar();

        AddBookmarkFragment addBookmarkFragment = new AddBookmarkFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerFrameLayoutId,
                        addBookmarkFragment, AddBookmarkFragment.FRAG_TAG).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return false;
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
    public void changeFragment(Fragment fragment, Bundle bundle, String tag) {
        if(fragment == null) {
            Log.e(TAG, "null fragment injected");
            return;
        }
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().
                beginTransaction();

        transaction.replace(R.id.fragmentContainerFrameLayoutId, fragment, tag);
        if(! tag.equals(BookmarkListFragment.FRAG_TAG)) {
            transaction.addToBackStack(null);
        }
        transaction.commit();

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

    public void initActionBar() {
        //set action bar
        Toolbar toolbar = null;
        String title = null;
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        try {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "on result");
        if(resultCode == RESULT_OK) {
            if(requestCode == AddBookmarkFragment.PICK_IMAGE_REQ_CODE) {
                Uri fileUrl = data.getData();
                Log.e(TAG, fileUrl.getPath());
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "OnBackPressed addBookmarks - ");
        super.onBackPressed();
    }

}

