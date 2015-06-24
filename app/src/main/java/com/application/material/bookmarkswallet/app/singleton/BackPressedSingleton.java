package com.application.material.bookmarkswallet.app.singleton;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import com.application.material.bookmarkswallet.app.fragments.BookmarkListFragment;

/**
 * Created by davide on 23/06/15.
 */
public class BackPressedSingleton {

    private static BackPressedSingleton mInstance;
    private static AppCompatActivity mActivityRef;
    private static ActionbarSingleton mActionbarSingleton;

    public BackPressedSingleton() {
    }

    /**
     *
     * @param activity
     * @return BackPressedSingleton
     */
    public static BackPressedSingleton getInstance(Activity activity) {
        mActivityRef = (AppCompatActivity) activity;
        mActionbarSingleton = ActionbarSingleton.getInstance(activity);
        return mInstance == null ? mInstance = new BackPressedSingleton() : mInstance;
    }

    /**
     *
     * @return boolean
     */
    public boolean isBackPressedHandled() {
        boolean editMode = mActionbarSingleton.isEditMode();
        boolean panelExpanded = mActionbarSingleton.isPanelExpanded();
        boolean searchMode = mActionbarSingleton.isSearchMode();

        boolean isHomeUpEnabled = mActivityRef
                .getSupportFragmentManager().getBackStackEntryCount() >= 2;
        mActionbarSingleton.changeActionbar(isHomeUpEnabled);

        //backPressed handler
        Fragment fragment  = mActivityRef.getSupportFragmentManager()
                .findFragmentByTag(BookmarkListFragment.FRAG_TAG);
        if ((! editMode && ! panelExpanded && ! searchMode) ||
                fragment == null) {
            return false;
        }

        //handle back
        ((BookmarkListFragment) fragment).collapseSearchActionView();
        ((BookmarkListFragment) fragment).showClipboardButton();
        ((BookmarkListFragment) fragment).collapseSlidingPanel();
        if (editMode) {
            ((BookmarkListFragment) fragment).undoEditBookmarkRecyclerViewWrapper();
        }
        return true;
    }

}
