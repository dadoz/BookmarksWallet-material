package com.lib.davidelm.filetreevisitorlibrary.views;

import com.lib.davidelm.filetreevisitorlibrary.models.TreeNode;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

/**
 * Created by davide on 24/04/2017.
 */

public interface OnNavigationCallbacks {
    void onNodeError(int type, TreeNodeInterface currentNode, String message);
    void onFolderNodeClickCb(int position, TreeNodeInterface node);
    void onFileNodeClickCb(int position, TreeNodeInterface node);
    void onAddFolderEmptyViewCb();
}
