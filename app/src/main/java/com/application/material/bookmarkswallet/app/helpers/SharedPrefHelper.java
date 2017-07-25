package com.application.material.bookmarkswallet.app.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefHelper {
    private static SharedPrefHelper instance;
    private static SharedPreferences sharedPref;

    public static boolean isNightModeTag(String s) {
        return SharedPrefKeysEnum.valueOf(s).equals(SharedPrefKeysEnum.NIGHT_MODE);
    }

    public boolean getBoolValue(SharedPrefKeysEnum key, boolean defValue) {
        if (getValue(key, defValue) instanceof Boolean)
            return getBoolValue(key, defValue);
        return false;
    }

    public enum SharedPrefKeysEnum {TUTORIAL_DONE, SYNC_STATUS, NO_FAVICON_MODE, SEARCH_URL_MODE, IMPORT_KEEP_NOTIFIED,
        IMPORT_ACCOUNT_NOTIFIED, EXPANDED_GRIDVIEW, CLOUD_SYNC, NIGHT_MODE
    }

    private SharedPrefHelper(Context ctx) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    /**
     *
     * @param ctx
     * @return
     */
    public static SharedPrefHelper getInstance(Context ctx) {
        return instance == null ?
                instance = new SharedPrefHelper(ctx) : instance;
    }

    /**
     * @param key
     * @param defValue
     * @return
     */
    public Object getValue(SharedPrefKeysEnum key, Object defValue) {
        if (defValue instanceof Boolean) {
            return sharedPref.getBoolean(key.name(), (boolean) defValue);
        }
        if (defValue instanceof Integer) {
            return sharedPref.getInt(key.name(), (int) defValue);
        }
        return null;
    }

    /**
     * @param tag
     * @param value
     */
    public void setValue(String tag, Object value) {
        SharedPrefKeysEnum key = SharedPrefKeysEnum.valueOf(tag);
        setValue(key, value);
    }

    /**
     *
     * @param key
     * @param value
     */
    public void setValue(SharedPrefHelper.SharedPrefKeysEnum key, Object value) {
        //TODO check type
        SharedPreferences.Editor editor = sharedPref.edit();
        if (value instanceof Boolean) {
            editor.putBoolean(key.name(), (boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key.name(), (int) value);
        }
        editor.apply();
    }
}
