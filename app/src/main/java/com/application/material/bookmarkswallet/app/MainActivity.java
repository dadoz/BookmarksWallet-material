package com.application.material.bookmarkswallet.app;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.application.material.bookmarkswallet.app.fragments.BaseFragment;
import com.application.material.bookmarkswallet.app.fragments.LinksListFragment;
import com.application.material.bookmarkswallet.app.fragments.SettingsFragment;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnInitActionBarInterface;


public class MainActivity extends ActionBarActivity
        implements OnChangeFragmentWrapperInterface, OnInitActionBarInterface {

    private String TAG = "MainActivity";
    private String EXTRA_DATA = "EXTRA_DATA";
    private boolean isItemSelected = false;

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
        LinksListFragment linksListFragment = new LinksListFragment();
//        BaseFragment baseFragment = new BaseFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerFrameLayoutId,
                        linksListFragment, LinksListFragment.FRAG_TAG).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
//            case  R.id.action_settings:
//                changeFragment(new SettingsFragment(), null, SettingsFragment.FRAG_TAG);
//                return true;
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

        if(tag.equals(LinksListFragment.FRAG_TAG)) {
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
        Intent intent = new Intent(this, activityClassName);
        if(bundle != null) {
            intent.putExtra(EXTRA_DATA, bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    //TODO refactor it :D
/*    public void initActionBarWithCustomView(Toolbar toolbar) {
        //set action bar
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        try {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.actionbar_sliding_tabs_layout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    @Override
    public void initActionBarWithCustomView(Toolbar toolbar) {
    }

    public void initActionBar(Toolbar toolbar, String title) {
        //set action bar
        setSupportActionBar(toolbar);

        boolean isHomeView = title == null;

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        try {
            actionBar.setDisplayHomeAsUpEnabled(! isHomeView);
            actionBar.setDisplayShowHomeEnabled(! isHomeView);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setTitle(title != null ?
                    title :
                    getResources().getString(R.string.app_name));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toggleEditActionBar(String title, boolean isSelecting) {
        isItemSelected = isSelecting;
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(isSelecting);
//        actionBar.setDisplayShowCustomEnabled(! isSelecting);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title != null ?
                title :
                getResources().getString(R.string.app_name));
        actionBar.setBackgroundDrawable(getResources().
                getDrawable(isSelecting ?
                        R.color.material_blue_grey :
                        R.color.material_mustard_yellow));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case AddBookmarkActivity.ADD_REQUEST:
                    try {
                        LinksListFragment fragment = (LinksListFragment)
                                getSupportFragmentManager().findFragmentByTag(LinksListFragment.FRAG_TAG);
                        String url = data.getExtras().getString(AddBookmarkActivity.LINK_URL_EXTRA);
                        fragment.addLinkOnRecyclerView(url);

                        Log.e(TAG, url);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }


    @Override
    public void onBackPressed() {
        Log.d(TAG, "OnBackPressed - ");
        if(isItemSelected) {
            toggleEditActionBar(null, false);
            //notify to frag toggle selctedItemView
            Fragment fragment  = getSupportFragmentManager()
                    .findFragmentByTag(LinksListFragment.FRAG_TAG);
            if(fragment != null) {
                ((LinksListFragment) fragment).undoEditLinkRecyclerView();
            }
            return;
        }
        super.onBackPressed();

    }


}
