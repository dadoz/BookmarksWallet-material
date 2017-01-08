package com.application.material.bookmarkswallet.app.manager;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.lang.ref.WeakReference;

public class ClipboardManager {
    private static final String TAG = "ClipboardManager";
    private static ClipboardManager instance;
    private static android.content.ClipboardManager clipboardService;

    private ClipboardManager() {
    }

    /**
     *
     * @param contextRef
     * @return
     */
    public static ClipboardManager getInstance(WeakReference<Context> contextRef) {
        clipboardService = (android.content.ClipboardManager) contextRef.get().
                getSystemService(Context.CLIPBOARD_SERVICE);
        return instance == null ? instance = new ClipboardManager() : instance;
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
        if (clipboardService.getPrimaryClip() == null) {
            return null;
        }
        ClipData.Item item = clipboardService.getPrimaryClip().getItemAt(0);
        CharSequence pasteData = item.getText();
        if (pasteData != null) {
            return pasteData.toString();
        }

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
