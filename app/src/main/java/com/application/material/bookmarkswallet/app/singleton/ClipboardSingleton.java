package com.application.material.bookmarkswallet.app.singleton;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;

/**
 * Created by davide on 06/01/15.
 */
public class ClipboardSingleton {
    private static final String TAG = "ClipboardSingleton";
    private static ClipboardSingleton mInstance;
    private static Activity mActivityRef;
    private static ClipboardManager mClipboard;

    private ClipboardSingleton() {
    }

    public static ClipboardSingleton getInstance(Activity activtyRef) {
        if(mInstance == null) {
            mInstance = new ClipboardSingleton();
        }

        mActivityRef = activtyRef;
        mClipboard = (ClipboardManager) mActivityRef.
                getSystemService(Context.CLIPBOARD_SERVICE);

        return mInstance;
    }

    public boolean hasClipboardText() {
        // If the clipboard doesn't contain data, disable the paste menu item.
        // If it does contain data, decide if you can handle the data.
        return (mClipboard.hasPrimaryClip()) &&
                (mClipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN));
    }

    public String getTextFromClipboard() {
        // Examines the item on the mClipboard. If getText() does not return null, the clip item contains the
        // text. Assumes that this application can only handle one item at a time.
        ClipData.Item item = mClipboard.getPrimaryClip().getItemAt(0);

        // Gets the mClipboard as text.
        CharSequence pasteData = item.getText();
        // If the string contains data, then the paste operation is done
        if (pasteData != null) {
            return pasteData.toString();
        }

        // The mClipboard does not contain text. If it contains a URI, attempts to get data from it
        Uri pasteUri = item.getUri();
        // If the URI contains something, try to get text from it
        if (pasteUri != null) {

            // calls a routine to resolve the URI and get data from it. This routine is not
            // presented here.
            //TODO implement it if you need it:)
//            pasteData = resolveUri(Uri);
            return null;
        }

        // Something is wrong. The MIME type was plain text, but the mClipboard does not contain either
        // text or a Uri. Report an error.
        Log.e(TAG, "Clipboard contains an invalid data type");
        return null;
    }

}
