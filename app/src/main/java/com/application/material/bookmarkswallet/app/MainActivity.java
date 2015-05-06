package com.application.material.bookmarkswallet.app;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v4.app.*;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;
import com.application.material.bookmarkswallet.app.fragments.BookmarkListFragment;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.singleton.ActionBarHandlerSingleton;
import com.flurry.android.FlurryAgent;
import icepick.Icepick;

import static com.application.material.bookmarkswallet.app.singleton.ActionBarHandlerSingleton.NOT_SELECTED_ITEM_POSITION;


public class MainActivity extends ActionBarActivity
        implements OnChangeFragmentWrapperInterface {
    private String TAG = "MainActivity";
    private String EXTRA_DATA = "EXTRA_DATA";
    private ActionBarHandlerSingleton mActionBarHandlerSingleton;
//    @Icicle String edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_main);
        mActionBarHandlerSingleton = ActionBarHandlerSingleton.getInstance(this);
        mActionBarHandlerSingleton.initActionBar();
        handleIntent(getIntent());

        FlurryAgent.setLogEnabled(true);
        FlurryAgent.init(this, getResources().getString(R.string.FLURRY_API_KEY));
        onInitFragment();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //do smthing with query
        }

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

    public void onInitFragment() {
        Fragment frag;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            int fragCount = getSupportFragmentManager().getBackStackEntryCount();
            String fragTag = getSupportFragmentManager().getBackStackEntryAt(fragCount - 1).getName();
            frag = getSupportFragmentManager().findFragmentByTag(fragTag);
            transaction.replace(R.id.fragmentContainerFrameLayoutId,
                    frag, fragTag).commit();
            return;
        }

        if(getSupportFragmentManager().getBackStackEntryCount() == 0 &&
                (frag = getSupportFragmentManager().
                        findFragmentByTag(BookmarkListFragment.FRAG_TAG)) != null) {
            transaction.replace(R.id.fragmentContainerFrameLayoutId,
                    frag, BookmarkListFragment.FRAG_TAG).commit();
            return;
        }

        //no fragment already adedd
        transaction.add(R.id.fragmentContainerFrameLayoutId,
                new BookmarkListFragment(), BookmarkListFragment.FRAG_TAG).commit();
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
            transaction.addToBackStack(tag);
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
        Intent intent = new Intent(this, activityClassName);
        if(bundle != null) {
            intent.putExtra(EXTRA_DATA, bundle);
        }
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
        ActivityCompat.startActivityForResult(this, intent, requestCode, options.toBundle());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case AddBookmarkActivity.ADD_REQUEST:
                    try {
                        BookmarkListFragment fragment = (BookmarkListFragment)
                                getSupportFragmentManager().findFragmentByTag(BookmarkListFragment.FRAG_TAG);
                        String url = data.getExtras().getString(AddBookmarkActivity.LINK_URL_EXTRA);
                        fragment.addLinkOnRecyclerViewWrapper(url);

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
        boolean isBackOverridden = mActionBarHandlerSingleton.getOverrideBackPressed();
        boolean isEditMode = mActionBarHandlerSingleton.isEditMode();

        boolean isHomeUpEnabled = getSupportFragmentManager().getBackStackEntryCount() >= 2;
        mActionBarHandlerSingleton.toggleActionBar(isHomeUpEnabled,
                isBackOverridden, false); // u always must change color back to yellow

        if(isBackOverridden) {
            mActionBarHandlerSingleton.setOverrideBackPressed(false);
            Fragment fragment  = getSupportFragmentManager()
                    .findFragmentByTag(BookmarkListFragment.FRAG_TAG);
            if(fragment != null &&
                    isEditMode) {
//                mActionBarHandlerSingleton.toggleLayoutByActionMenu(R.id.infoOuterButtonId);

                ((BookmarkListFragment) fragment).undoEditBookmarkRecyclerViewWrapper();
            }
            return;
        }
        super.onBackPressed();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }
}
