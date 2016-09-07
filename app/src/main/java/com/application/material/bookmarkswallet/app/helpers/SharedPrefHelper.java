package com.application.material.bookmarkswallet.app.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.ref.WeakReference;

public class SharedPrefHelper {
    private static SharedPrefHelper instance;
    private static SharedPreferences sharedPref;

    public enum SharedPrefKeysEnum {TUTORIAL_DONE, SYNC_STATUS, SEARCH_URL_MODE}
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
     *
     * @param key
     * @param defValue
     * @return
     */
    public Object getValue(SharedPrefKeysEnum key, Object defValue) {
        if (key.equals(SharedPrefKeysEnum.SEARCH_URL_MODE)) {
            return sharedPref.getBoolean(SharedPrefKeysEnum.SEARCH_URL_MODE.name(), (boolean) defValue);
        }

        if (key.equals(SharedPrefKeysEnum.TUTORIAL_DONE)) {
            return sharedPref.getBoolean(SharedPrefKeysEnum.TUTORIAL_DONE.name(), (boolean) defValue);
        }
        return null;
//            case SYNC_STATUS:
//                String syncName = sharedPref.getString(SYNC_STATUS, (String) defValue);
//                return SyncStatusEnum.valueOf(syncName);
    }

    /**
     *
     * @param key
     * @param value
     */
    public void setValue(SharedPrefKeysEnum key, Object value) {
        //TODO add value type check
        if (key.equals(SharedPrefKeysEnum.SEARCH_URL_MODE)) {
            sharedPref
                    .edit()
                    .putBoolean(SharedPrefKeysEnum.SEARCH_URL_MODE.name(), (boolean) value)
                    .apply();
        } else if (key.equals(SharedPrefKeysEnum.TUTORIAL_DONE)) {
            sharedPref
                .edit()
                .putBoolean(SharedPrefKeysEnum.TUTORIAL_DONE.name(), (boolean) value)
                .apply();
        }
        //            case SYNC_STATUS:
//                sharedPref
//                    .edit()
//                    .putString(SYNC_STATUS, (String) value)
//                    .apply();
//                break;

    }
}
