package com.application.material.bookmarkswallet.app;

import android.content.Intent;
import android.support.v4.app.*;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.application.material.bookmarkswallet.app.fragments.BookmarkRecyclerViewFragment;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.singleton.ActionbarSingleton;
import com.application.material.bookmarkswallet.app.singleton.BackPressedSingleton;
import com.application.material.bookmarkswallet.app.singleton.SharedPrefSingleton;
import com.flurry.android.FlurryAgent;
import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;

public class MainActivity extends AppCompatActivity
        implements OnChangeFragmentWrapperInterface {
    private String TAG = "MainActivity";
    private ActionbarSingleton mActionbarSingleton;
    private BackPressedSingleton mBackPressedSingleton;
    private SharedPrefSingleton mSharedPrefSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActionbarSingleton = ActionbarSingleton.getInstance(this);
        mBackPressedSingleton = BackPressedSingleton.getInstance(this);
        mActionbarSingleton.initActionBar(); //must be the last one
        mSharedPrefSingleton = SharedPrefSingleton.getInstance(this);

        FlurryAgent.setLogEnabled(true);
        FlurryAgent.init(this, getResources().getString(R.string.FLURRY_API_KEY));

        handleTutorial();

        onInitAppRate();
        onInitFragment();
    }

    @Override
    public void onResume() {
        mActionbarSingleton.setActivtyRef(this);
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    /**
     * init fragment function
     */
    public void onInitFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        int backStackCnt = getSupportFragmentManager().getBackStackEntryCount();
        Fragment frag = getSupportFragmentManager().
                findFragmentByTag(BookmarkRecyclerViewFragment.FRAG_TAG);

        if (backStackCnt > 0) {
            handleBackStackEntry(transaction);
            return;
        }

        if (backStackCnt == 0 &&
                frag != null) {
            transaction.replace(R.id.fragmentContainerFrameLayoutId,
                    frag, BookmarkRecyclerViewFragment.FRAG_TAG).commit();
            return;
        }

        //no fragment already adedd
        transaction.add(R.id.fragmentContainerFrameLayoutId,
                new BookmarkRecyclerViewFragment(), BookmarkRecyclerViewFragment.FRAG_TAG).commit();
    }

    /**
     *
     * @param transaction
     */
    private void handleBackStackEntry(FragmentTransaction transaction) {
        int fragCount = getSupportFragmentManager().getBackStackEntryCount();
        String fragTag = getSupportFragmentManager().getBackStackEntryAt(fragCount - 1).getName();
        Fragment frag = getSupportFragmentManager().findFragmentByTag(fragTag);
        transaction.replace(R.id.fragmentContainerFrameLayoutId,
                frag, fragTag).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //DEMANDED ON FRAGMENT
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
        if (! tag.equals(BookmarkRecyclerViewFragment.FRAG_TAG)) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
    }

    /**
     * init app rate dialog
     */
    private void onInitAppRate() {
        AppRate.with(this)
                .setInstallDays(2)
                .setLaunchTimes(10)
                .setRemindInterval(1)
                .setShowNeutralButton(true)
                .setDebug(false)
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        Log.d(MainActivity.class.getName(), Integer.toString(which));
                    }
                })
                .monitor();
        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);
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
    }

    /**
     * private method
     */
    private void handleTutorial() {
        boolean tutorialDone = (boolean) mSharedPrefSingleton.getValue(SharedPrefSingleton.TUTORIAL_DONE, false);
        if (! tutorialDone) {
            startActivity(new Intent(this, TutorialActivity.class));
            finish(); //this end activity
        }
    }

}
