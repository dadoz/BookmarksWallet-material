package com.application.material.bookmarkswallet.app.manager;

import android.content.Context;

import com.application.material.bookmarkswallet.app.utlis.ConnectionUtils;
import com.application.material.bookmarkswallet.app.utlis.Utils;

import java.lang.ref.WeakReference;

public class SearchManager {
    /**
     *
     * @param query
     * @return
     */
    public static boolean search(WeakReference<Context> context, String query) {
        return ConnectionUtils.isConnected(context.get()) &&
            (Utils.isValidUrl(query));
//        && pingUrl(query);
    }

    /**
     * TODO require too much time -.- (do in bckgrnd)
     * @param ip
     * @return
     */
    private static boolean pingUrl(String ip) {
        try {
            Process p = Runtime.getRuntime().exec("ping -c 1 -t 10 " + ip);
            p.waitFor();
            return p.exitValue() == 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
