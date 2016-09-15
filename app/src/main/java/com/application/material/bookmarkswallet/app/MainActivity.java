package com.application.material.bookmarkswallet.app;

import android.content.Context;
import android.support.v4.app.*;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.application.material.bookmarkswallet.app.fragments.BookmarkListFragment;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.singleton.BackPressedSingleton;
import com.flurry.android.FlurryAgent;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity
        implements OnChangeFragmentWrapperInterface {
    private String TAG = "MainActivity";
    private BackPressedSingleton mBackPressedSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FlurryAgent.onStartSession(this);

        initSingletonRef();
        onInitFragment();
    }

    /**
     * init singleton references
     */
    private void initSingletonRef() {
        mBackPressedSingleton = BackPressedSingleton.getInstance(new WeakReference<Context>(this));
    }

    @Override
    public void onResume() {
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
                findFragmentByTag(BookmarkListFragment.FRAG_TAG);

        if (backStackCnt > 0) {
            handleBackStackEntry(transaction);
            return;
        }

        if (backStackCnt == 0 &&
                frag != null) {
            transaction.replace(R.id.fragmentContainerFrameLayoutId,
                    frag, BookmarkListFragment.FRAG_TAG).commit();
            return;
        }

        //no fragment already adedd
        transaction.add(R.id.fragmentContainerFrameLayoutId,
                new BookmarkListFragment(), BookmarkListFragment.FRAG_TAG).commit();
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
//        if (!mBackPressedSingleton.isBackPressedHandled()) {
//            return;
//        }
        super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
