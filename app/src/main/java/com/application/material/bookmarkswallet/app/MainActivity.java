package com.application.material.bookmarkswallet.app;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.*;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;
import com.application.material.bookmarkswallet.app.fragments.BookmarkListFragment;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.singleton.ActionbarSingleton;
import com.application.material.bookmarkswallet.app.singleton.BackPressedSingleton;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tjeannin.apprate.AppRate;
import icepick.Icepick;
import icepick.Icicle;

import static com.application.material.bookmarkswallet.app.singleton.ActionbarSingleton.NOT_SELECTED_ITEM_POSITION;


public class MainActivity extends AppCompatActivity
        implements OnChangeFragmentWrapperInterface {
    private String TAG = "MainActivity";
    private ActionbarSingleton mActionbarSingleton;
    private BackPressedSingleton mBackPressedSingleton;
    private AdView mAdView;
//    @Icicle
//    int mSelectedItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_main);
        mActionbarSingleton = ActionbarSingleton.getInstance(this);
        mActionbarSingleton.initActionBar();
        mBackPressedSingleton = BackPressedSingleton.getInstance(this);
//        handleIntent(getIntent());

        FlurryAgent.setLogEnabled(true);
        FlurryAgent.init(this, getResources().getString(R.string.FLURRY_API_KEY));

        new AppRate(this)
                .setMinDaysUntilPrompt(7)
                .setMinLaunchesUntilPrompt(20)
                .init();

        //TODO ads - move on fragment
//        mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        onInitFragment();
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        setIntent(intent);
//        handleIntent(intent);
//    }
//
//    private void handleIntent(Intent intent) {
//        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//            do smthing with query
//        }
//    }

    @Override
    public void onResume() {
        mActionbarSingleton.setActivtyRef(this);
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public void onInitFragment() {
        Fragment frag;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            int fragCount = getSupportFragmentManager().getBackStackEntryCount();
            String fragTag = getSupportFragmentManager().getBackStackEntryAt(fragCount - 1).getName();
            frag = getSupportFragmentManager().findFragmentByTag(fragTag);
            transaction.replace(R.id.fragmentContainerFrameLayoutId,
                    frag, fragTag).commit();
            return;
        }

        if (getSupportFragmentManager().getBackStackEntryCount() == 0 &&
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
        if (fragment == null) {
            Log.e(TAG, "null fragment injected");
            return;
        }
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().
                beginTransaction();

        transaction.replace(R.id.fragmentContainerFrameLayoutId, fragment, tag);
        if (! tag.equals(BookmarkListFragment.FRAG_TAG)) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (mBackPressedSingleton.isBackPressedHandled()) {
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
