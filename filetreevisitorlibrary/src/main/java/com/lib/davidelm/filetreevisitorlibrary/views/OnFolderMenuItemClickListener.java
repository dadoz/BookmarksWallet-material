package com.lib.davidelm.filetreevisitorlibrary.views;

import android.view.MenuItem;

import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

/**
 * Created by davide on 26/05/2017.
 */

public interface OnFolderMenuItemClickListener {
    boolean onMenuItemClick(MenuItem item, TreeNodeInterface node);
}
