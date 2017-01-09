package com.application.material.bookmarkswallet.app.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.application.material.bookmarkswallet.app.utlis.Utils;

import java.lang.ref.WeakReference;

public class SharedPrefHelper {
    private static SharedPrefHelper instance;
    private static SharedPreferences sharedPref;

    public enum SharedPrefKeysEnum {TUTORIAL_DONE, SYNC_STATUS, NO_FAVICON_MODE, SEARCH_URL_MODE, IMPORT_KEEP_NOTIFIED,
        IMPORT_ACCOUNT_NOTIFIED, EXPANDED_GRIDVIEW, CLOUD_SYNC, NIGHT_MODE
    }

    private SharedPrefHelper(WeakReference<Context> ctx) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx.get());
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
        if (defValue instanceof Boolean) {
            return sharedPref.getBoolean(key.name(), (boolean) defValue);
        }
        if (defValue instanceof Integer) {
            return sharedPref.getInt(key.name(), (int) defValue);
        }
        return null;
    }

    /**
     * @param key
     * @param value
     */
    public void setValue(SharedPrefKeysEnum key, Object value) {
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
