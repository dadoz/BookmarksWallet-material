package com.application.material.bookmarkswallet.app.singleton;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.application.material.bookmarkswallet.app.helpers.StatusHelper;
import com.application.material.bookmarkswallet.app.manager.SearchManager;

import java.lang.ref.WeakReference;

/**
 * Created by davide on 23/06/15.
 */
public class BackPressedSingleton {

    private static BackPressedSingleton instanceRef;
    private static ActionbarSingleton actionbarSingleton;
    private static StatusHelper statusSingleton;
    private static SearchManager searchManager;
    private static WeakReference<Context> context;

    public BackPressedSingleton() {
    }

    /**
     *
     * @param ctx
     * @return BackPressedSingleton
     */
    public static BackPressedSingleton getInstance(WeakReference<Context> ctx) {
        context = ctx;
        initSingletonRef();
        return instanceRef == null ?
                instanceRef = new BackPressedSingleton() : instanceRef;
    }

    /**
     * init singleton ref
     */
    private static void initSingletonRef() {
        searchManager = SearchManager.getInstance(context, null);
        actionbarSingleton = ActionbarSingleton.getInstance(context);
        statusSingleton = StatusHelper.getInstance();
    }

    /**
     * //TODO refactor :)
     * @return boolean
     */
    public boolean isBackPressedHandled() {
        boolean isHomeUpEnabled = homeUpEnabled();
        actionbarSingleton.updateActionBar(isHomeUpEnabled);

        if (statusSingleton.isSearchMode()) {
            searchManager.collapseSearchView();
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
        return ((AppCompatActivity) context.get()).getSupportFragmentManager()
                .getBackStackEntryCount() >= 2;
    }

}
