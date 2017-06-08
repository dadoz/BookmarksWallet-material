package com.lib.davidelm.filetreevisitorlibrary.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

public interface TreeNodeInterface {

    int deleteChild(TreeNodeInterface node);

    List<TreeNodeInterface> getChildren();

    int getId();

    int getLevel();

    void setParent(TreeNodeInterface currentTreeNode);

    @NonNull
    TreeNodeInterface addChild(TreeNodeInterface treeNode);

    TreeNodeInterface getParent();

    boolean isFolder();

    boolean isRoot();

//    TreeNodeInterface getChildByName(String name);
    TreeNodeInterface getChildById(long id);

    void setId();

    TreeNodeContent getNodeContent();

    boolean isSelected();
    void setSelected(boolean selected);

    void toggleSelected();
}
