package com.lib.davidelm.filetreevisitorlibrary.models;

import java.util.List;

public interface TreeNodeInterface {

    int deleteChild(TreeNodeInterface node);

    List<TreeNodeInterface> getChildren();

    int getId();

    int getLevel();

    void setParent(TreeNodeInterface currentTreeNode);

    TreeNodeInterface addChild(TreeNodeInterface treeNode);

    TreeNodeInterface getParent();

    boolean isFolder();

    boolean isRoot();

//    TreeNodeInterface getChildByName(String name);
    TreeNodeInterface getChildById(long id);

    void setId();

    TreeNodeContent getNodeContent();
}
