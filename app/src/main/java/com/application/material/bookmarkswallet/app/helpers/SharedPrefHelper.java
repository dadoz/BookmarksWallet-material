package com.application.material.bookmarkswallet.app.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.ref.WeakReference;

public class SharedPrefHelper {
    private static SharedPrefHelper instance;
    private static SharedPreferences sharedPref;

    public enum SharedPrefKeysEnum {TUTORIAL_DONE, SYNC_STATUS, SEARCH_URL_MODE, IMPORT_KEEP_NOTIFIED, IMPORT_ACCOUNT_NOTIFIED }
    private static final String BOOKMARKS_WALLET_SHAREDPREF = "BOOKMARKS_WALLET_SHAREDPREF";

    protected SharedPrefHelper(WeakReference<Context> ctx) {
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
        if (key.equals(SharedPrefKeysEnum.SEARCH_URL_MODE) ||
            key.equals(SharedPrefKeysEnum.TUTORIAL_DONE) ||
            key.equals(SharedPrefKeysEnum.IMPORT_ACCOUNT_NOTIFIED) ||
            key.equals(SharedPrefKeysEnum.IMPORT_KEEP_NOTIFIED)) {
            return sharedPref.getBoolean(key.name(), (boolean) defValue);
        }
        return null;
    }

    /**
     * @param key
     * @param value
     */
    public void setValue(SharedPrefKeysEnum key, Object value) {
        if (key.equals(SharedPrefKeysEnum.SEARCH_URL_MODE) ||
            key.equals(SharedPrefKeysEnum.TUTORIAL_DONE) ||
            key.equals(SharedPrefKeysEnum.IMPORT_ACCOUNT_NOTIFIED) ||
            key.equals(SharedPrefKeysEnum.IMPORT_KEEP_NOTIFIED)) {
            sharedPref
                .edit()
                .putBoolean(key.name(), (boolean) value)
                .apply();
        }
    }
}
