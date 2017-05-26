package com.lib.davidelm.filetreevisitorlibrary;


import android.view.View;

import com.lib.davidelm.filetreevisitorlibrary.models.TreeNode;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;


public interface OnNodeClickListener {
    void onFolderNodeCLick(View v, int position, TreeNodeInterface node);
    void onFileNodeCLick(View v, int position, TreeNodeInterface node);
    void onFolderNodeLongCLick(View v, int position, TreeNodeInterface item);
    void onFileNodeLongCLick(View v, int position, TreeNodeInterface item);
    void onMoreSettingsClick(View v, int position, TreeNodeInterface item);
}
