package com.application.material.bookmarkswallet.app.singleton;

import android.app.Activity;
import android.content.SharedPreferences;

import static com.application.material.bookmarkswallet.app.singleton.RecyclerViewActionsSingleton.SyncStatusEnum.NOT_SET;

/**
 * Created by davide on 17/07/15.
 */
public class SharedPrefSingleton {
    private static SharedPrefSingleton mInstance;
    private static SharedPreferences mSharedPref;

    public static final String TUTORIAL_DONE = "TUTORIAL_DONE";
    private static final String SYNC_STATUS = "SYNC_STATUS";
    public static final String SEARCH_URL_MODE = "SEARCH_URL_MODE";

    private static String BOOKMARKS_WALLET_SHAREDPREF = "BOOKMARKS_WALLET_SHAREDPREF";

    protected SharedPrefSingleton() {
    }

    public static SharedPrefSingleton getInstance(Activity activityRef) {
        mSharedPref = activityRef
                .getSharedPreferences(BOOKMARKS_WALLET_SHAREDPREF, 0);
        return mInstance == null ? mInstance = new SharedPrefSingleton() : mInstance;
    }


    public Object getValue(String key, Object defValue) {
        switch (key) {
            case SEARCH_URL_MODE:
                return mSharedPref.getBoolean(SEARCH_URL_MODE, (Boolean) defValue);
            case SYNC_STATUS:
                String syncName = mSharedPref.getString(SYNC_STATUS, (String) defValue);
                return RecyclerViewActionsSingleton.SyncStatusEnum.valueOf(syncName);
            case TUTORIAL_DONE:
                return mSharedPref.getBoolean(TUTORIAL_DONE, (boolean) defValue);
        }
        return null;
    }

    public void setValue(String key, Object value) {
        switch (key) {
            case SEARCH_URL_MODE:
                mSharedPref
                    .edit()
                    .putBoolean(SEARCH_URL_MODE, (Boolean) value)
                    .apply();
                break;
            case SYNC_STATUS:
                mSharedPref
                    .edit()
                    .putString(SYNC_STATUS, (String) value)
                    .apply();
                break;
            case TUTORIAL_DONE:
                mSharedPref
                    .edit()
                    .putBoolean(TUTORIAL_DONE, (boolean) value)
                    .apply();
        }

    }
}
