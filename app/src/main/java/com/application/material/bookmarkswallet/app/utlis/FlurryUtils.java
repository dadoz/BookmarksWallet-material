package com.application.material.bookmarkswallet.app.utlis;

import android.content.Context;

import com.flurry.android.FlurryAgent;

/**
 * Created by davide on 26/06/2017.
 */

public class FlurryUtils {
    /**
     *
     */
    public static void flurryStartSession(Context context) {
        try {
            FlurryAgent.onStartSession(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     *
     */
    public static void flurryStopSession(Context context) {
        try {
            FlurryAgent.onEndSession(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

