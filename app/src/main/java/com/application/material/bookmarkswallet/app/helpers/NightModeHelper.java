package com.application.material.bookmarkswallet.app.helpers;

import android.app.UiModeManager;
import android.content.Context;

import java.lang.ref.WeakReference;

import static android.app.UiModeManager.MODE_NIGHT_NO;
import static android.app.UiModeManager.MODE_NIGHT_YES;
import static android.content.Context.UI_MODE_SERVICE;
import static com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper.SharedPrefKeysEnum.NIGHT_MODE;

/**
 * Created by davide on 05/07/2017.
 */

public class NightModeHelper {
    private final WeakReference<Context> context;

    public NightModeHelper(Context context) {
        this.context = new WeakReference<>(context);
    }

    /**
     *
     */
    public void setMode() {
        setMode(isNightModeSharedPref() ? MODE_NIGHT_YES : MODE_NIGHT_NO);
    }
    /**
     *
     * @param mode
     */
    public void setMode(int mode) {
        if (context.get() != null) {
            UiModeManager uiModeManager = (UiModeManager) context.get().getSystemService(UI_MODE_SERVICE);
            uiModeManager.setNightMode(mode);//UiModeManager.MODE_NIGHT_YES

        }
    }

    /**
     *
     * @return
     */
    public boolean isNightModeSharedPref() {
        return context.get() != null &&
                SharedPrefHelper.getInstance(context.get()).getBoolValue(NIGHT_MODE, false);
    }

    /**
     * setNight
     */
    public void setNightModeSharedPref() {
        if (context.get() != null)
            SharedPrefHelper.getInstance(context.get()).setValue(NIGHT_MODE, !isNightModeSharedPref());
    }
}
