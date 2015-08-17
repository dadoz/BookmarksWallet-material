package com.application.material.bookmarkswallet.app.singleton;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import com.application.material.bookmarkswallet.app.singleton.search.SearchHandlerSingleton;

/**
 * Created by davide on 23/06/15.
 */
public class BackPressedSingleton {

    private static BackPressedSingleton instanceRef;
    private static AppCompatActivity activityRef;
    private static ActionbarSingleton actionbarSingleton;
    private static StatusSingleton statusSingleton;
    private static SearchHandlerSingleton mSearchHandlerSingleton;

    public BackPressedSingleton() {
    }

    /**
     *
     * @param activity
     * @return BackPressedSingleton
     */
    public static BackPressedSingleton getInstance(Activity activity) {
        activityRef = (AppCompatActivity) activity;
        initSingletonRef();
        return instanceRef == null ? instanceRef = new BackPressedSingleton() : instanceRef;
    }

    /**
     * init singleton ref
     */
    private static void initSingletonRef() {
        mSearchHandlerSingleton = SearchHandlerSingleton.getInstance(activityRef, null);
        actionbarSingleton = ActionbarSingleton.getInstance(activityRef);
        statusSingleton = StatusSingleton.getInstance();
    }

    /**
     * //TODO refactor :)
     * @return boolean
     */
    public boolean isBackPressedHandled() {
        boolean isHomeUpEnabled = homeUpEnabled();
        actionbarSingleton.udpateActionbar(isHomeUpEnabled);

        if (statusSingleton.isSearchMode()) {
            mSearchHandlerSingleton.collapseSearchView();
            statusSingleton.unsetStatus();
            return true;
        }

        //handle back
//        ((BookmarkListFragment) fragment).collapseSearchActionView();
        return false;
    }

    /**
     * check if home is enabled
     * @return
     */
    private boolean homeUpEnabled() {
        return activityRef.getSupportFragmentManager()
                .getBackStackEntryCount() >= 2;
    }

}
