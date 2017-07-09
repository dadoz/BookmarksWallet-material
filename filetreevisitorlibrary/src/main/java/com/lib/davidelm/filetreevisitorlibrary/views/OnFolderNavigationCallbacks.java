package com.lib.davidelm.filetreevisitorlibrary.views;

import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

/**
 * Created by davide on 09/07/2017.
 */

public interface OnFolderNavigationCallbacks {
    void onFolderNodeClickCb(int position, TreeNodeInterface node);
    void onFolderNodeLongClickCb(int position, TreeNodeInterface item);
}
