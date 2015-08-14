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
    private static ClipboardSingleton instance;
    private static ClipboardManager clipboardService;

    private ClipboardSingleton() {
    }

    /**
     *
     * @param activityRef
     * @return
     */
    public static ClipboardSingleton getInstance(Activity activityRef) {
        clipboardService = (ClipboardManager) activityRef.
                getSystemService(Context.CLIPBOARD_SERVICE);
        return instance == null ? instance = new ClipboardSingleton() : instance;
    }

    /**
     * clipboard is not empty
     * @return
     */
    public boolean hasClipboardText() {
        return (clipboardService.hasPrimaryClip()) &&
                (clipboardService.getPrimaryClipDescription()
                        .hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN));
    }

    /**
     *
     * @return
     */
    public String getTextFromClipboard() {
        ClipData.Item item = clipboardService.getPrimaryClip().getItemAt(0);
        CharSequence pasteData = item.getText();
        if (pasteData != null) {
            return pasteData.toString();
        }

        Log.e(TAG, "Clipboard contains an invalid data type");
        return null;
    }

    /**
     *
     * @return
     */
    public Uri getUriFromClipboard() {
        ClipData.Item item = clipboardService.getPrimaryClip().getItemAt(0);
        Uri pasteUri = item.getUri();
        if (pasteUri != null) {
            return pasteUri;
        }

        Log.e(TAG, "Clipboard contains an invalid data type");
        return null;
    }

}
