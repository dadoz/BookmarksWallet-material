package com.application.material.bookmarkswallet.app.singleton;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import com.application.material.bookmarkswallet.app.fragments.BookmarkListFragment;

/**
 * Created by davide on 23/06/15.
 */
public class BackPressedSingleton {

    private static BackPressedSingleton instanceRef;
    private static AppCompatActivity activityRef;
    private static ActionbarSingleton actionbarSingleton;
    private static StatusSingleton statusSingleton;

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

        if (! statusSingleton.isSearchMode()) {
            return false;
        }

        //handle back
//        ((BookmarkListFragment) fragment).collapseSearchActionView();
        return true;
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
