package com.application.material.bookmarkswallet.app.utlis;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by davide on 28/06/2017.
 */

public class BrowserUtils {
    /**y
     * open browser by intent (opening on bookmark url)
     * @param url
     */
    public static boolean openUrl(String url, Context context) {
        if (!isValidUrl(url)) {
            return false;
        }

        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Utils.buildUrl(url, true))));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
//    showErrorMessage(context.get().getString(R.string.wrong_url), view);
//    showErrorMessage(context.get().getString(R.string.cannot_load_url), view);

    /**
     * @param url
     * @return
     */
    public static boolean isValidUrl(String url) {
        Pattern p = Pattern.
                compile("(@)?(href=')?(HREF=')?(HREF=\")?(href=\")?(http://)?(https://)?(ftp://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/([#&\\n\\-=?\\+\\%/\\.\\w]+)?)?");

        Matcher m = p.matcher(url);
        return !url.equals("") &&
                m.matches();

    }
}
