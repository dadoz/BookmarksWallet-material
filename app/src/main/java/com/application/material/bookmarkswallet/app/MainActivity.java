package com.application.material.bookmarkswallet.app;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.application.material.bookmarkswallet.app.fragments.LinksListFragment;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.singleton.ActionBarHandlerSingleton;


public class MainActivity extends ActionBarActivity
        implements OnChangeFragmentWrapperInterface {

    private String TAG = "MainActivity";
    private String EXTRA_DATA = "EXTRA_DATA";
    private ActionBarHandlerSingleton mActionBarHandlerRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActionBarHandlerRef = ActionBarHandlerSingleton.getInstance(this);

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
        mActionBarHandlerRef.initActionBar();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerFrameLayoutId,
                        linksListFragment, LinksListFragment.FRAG_TAG).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //DEMANDING ON FRAGMENT
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
        mActionBarHandlerRef.initToggleSettings(mActionBarHandlerRef.isChangeFragment(), false);
        mActionBarHandlerRef.toggleActionBar(null);
        mActionBarHandlerRef.hideLayoutByMenuAction();

        if(! mActionBarHandlerRef.isChangeFragment()) {
            mActionBarHandlerRef.setIsChangeFragment(true);
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
