package com.application.material.bookmarkswallet.app.manager;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.lang.ref.WeakReference;

public class ClipboardManager {
    private static final String TAG = "ClipboardManager";
    private static ClipboardManager instance; //TODO take care - can cause leak
    private final WeakReference<Context> contextRef;
    private android.content.ClipboardManager clipboardService;

    private ClipboardManager(Context context) {
        this.contextRef = new WeakReference<>(context);
    }

    /**
     *
     * @param contextRef
     * @return
     */
    public static ClipboardManager getInstance(WeakReference<Context> contextRef) {
        return instance == null ? instance = new ClipboardManager(contextRef.get()) : instance;
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
        clipboardService = (android.content.ClipboardManager) contextRef.get().
                getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardService.getPrimaryClip() == null) {
            return null;
        }

        ClipData.Item item = clipboardService.getPrimaryClip().getItemAt(0);
        CharSequence pasteData = item.getText();
        return pasteData != null ? pasteData.toString() : null;
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
