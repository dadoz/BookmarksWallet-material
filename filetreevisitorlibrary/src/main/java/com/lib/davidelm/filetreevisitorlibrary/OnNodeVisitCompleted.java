package com.lib.davidelm.filetreevisitorlibrary;


import com.lib.davidelm.filetreevisitorlibrary.models.TreeNode;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

import java.util.List;

public interface OnNodeVisitCompleted {

    void addNodes(List<TreeNodeInterface> list);
    void addFolder(String name);
    void addFile(String name);
    void removeFolder(String name);

    void setParentNode(TreeNodeInterface parentNode);

    void removeNode(TreeNodeInterface childNode);
}
