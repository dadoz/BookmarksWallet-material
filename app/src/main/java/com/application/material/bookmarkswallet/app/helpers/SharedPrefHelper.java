package com.application.material.bookmarkswallet.app.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.application.material.bookmarkswallet.app.utlis.Utils;

import java.lang.ref.WeakReference;

public class SharedPrefHelper {
    private static SharedPrefHelper instance;
    private static SharedPreferences sharedPref;

    public enum SharedPrefKeysEnum {TUTORIAL_DONE, SYNC_STATUS, NO_FAVICON_MODE, SEARCH_URL_MODE, IMPORT_KEEP_NOTIFIED,
        IMPORT_ACCOUNT_NOTIFIED, EXPANDED_GRIDVIEW, CLOUD_SYNC;
    }
    private static final String BOOKMARKS_WALLET_SHAREDPREF = "BOOKMARKS_WALLET_SHAREDPREF";

    private SharedPrefHelper(WeakReference<Context> ctx) {
        sharedPref = ctx.get().getSharedPreferences(BOOKMARKS_WALLET_SHAREDPREF, 0);
    }

    /**
     *
     * @param ctx
     * @return
     */
    public static SharedPrefHelper getInstance(WeakReference<Context> ctx) {
        return instance == null ?
                instance = new SharedPrefHelper(ctx) : instance;
    }

    /**
     * @param key
     * @param defValue
     * @return
     */
    public Object getValue(SharedPrefKeysEnum key, Object defValue) {
        //TODO check type
        return sharedPref.getBoolean(key.name(), (boolean) defValue);
    }

    /**
     * @param key
     * @param value
     */
    public void setValue(SharedPrefKeysEnum key, Object value) {
        //TODO check type
        sharedPref
            .edit()
            .putBoolean(key.name(), (boolean) value)
            .apply();
    }
}
