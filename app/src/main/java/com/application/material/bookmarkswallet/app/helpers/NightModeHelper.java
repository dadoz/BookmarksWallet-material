package com.application.material.bookmarkswallet.app.helpers;

import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import java.lang.ref.WeakReference;

public class NightModeHelper {
    private static NightModeHelper instance;

    /**
     *
     * @return
     */
    public static NightModeHelper getInstance() {
        return instance == null ? instance = new NightModeHelper() : instance;
    }

    /**
     *
     * @param context
     */
    public void setNightModeIfEnabled(WeakReference<Context> context) {
        boolean nightModeEnabled = (boolean) SharedPrefHelper.getInstance(context)
                .getValue(SharedPrefHelper.SharedPrefKeysEnum.NIGHT_MODE, false);
        AppCompatDelegate.setDefaultNightMode(nightModeEnabled ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

    }
}
