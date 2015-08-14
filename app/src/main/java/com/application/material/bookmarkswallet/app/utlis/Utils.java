package com.application.material.bookmarkswallet.app.utlis;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by davide on 17/07/15.
 */
public class Utils {
    public static final String SEARCH_URL_MODE = "SEARCH_URL_MODE";
    public static final String SYNC_STATUS = "SYNC_STATUS";
    public static final String IMPORT_TRIGGER = "IMPORT_TRIGGER";
    public static final int ADD_BOOKMARK_ACTIVITY_REQ_CODE = 99;
    public static String BOOKMARKS_WALLET_SHAREDPREF = "BOOKMARKS_WALLET_SHAREDPREF";

    /**
     * //TODO implement by regex
     * @param bookmarkUrl
     * @return
     */
    public static boolean validateUrl(String bookmarkUrl) {
        return true;
    }

    /**
     * hide Keyboard
     */
    public static void hideKeyboard(Activity activity) {
        try {
            View view = activity.getCurrentFocus();
            ((InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
