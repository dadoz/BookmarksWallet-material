package com.application.material.bookmarkswallet.app.adapter;

import android.view.View;

import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

/**
 * Created by davide on 28/05/2017.
 */

public interface OnMultipleSelectorCallback {
    void onFileNodeClickCb(View v, int position, TreeNodeInterface node);
    void onFileNodeLongClickCb(View v, int position, TreeNodeInterface item);
}
