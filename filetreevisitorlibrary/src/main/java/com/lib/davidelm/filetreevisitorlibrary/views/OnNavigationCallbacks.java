package com.lib.davidelm.filetreevisitorlibrary.views;

import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

/**
 * Created by davide on 24/04/2017.
 */

public interface OnNavigationCallbacks extends OnFolderNavigationCallbacks {
    void onNodeError(int type, TreeNodeInterface currentNode, String message);
    void onFileNodeClickCb(int position, TreeNodeInterface node);
    void onFileNodeLongClickCb(int position, TreeNodeInterface item);
}
