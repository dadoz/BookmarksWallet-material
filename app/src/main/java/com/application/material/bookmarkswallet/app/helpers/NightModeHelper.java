package com.application.material.bookmarkswallet.app.helpers;

import android.app.UiModeManager;
import android.content.Context;

import static android.content.Context.UI_MODE_SERVICE;

/**
 * Created by davide on 05/07/2017.
 */

public class NightModeHelper {


    public static void setMode(int mode, Context context) {
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(UI_MODE_SERVICE);
        uiModeManager.setNightMode(mode);//UiModeManager.MODE_NIGHT_YES
    }
}
