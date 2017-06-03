package com.lib.davidelm.filetreevisitorlibrary;


import android.util.SparseIntArray;

import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeContent;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

import java.util.ArrayList;
import java.util.List;

public interface OnNodeVisitCompleted {

    void addFolder(String name, int parentNodeId);
    void addFolder(TreeNodeContent nodeContent, int parentNodeId);
    void addFile(TreeNodeContent nodeContent, int parentNodeId);
    void addFile(String name, int parentNodeId);
    void removeFolder(TreeNodeInterface node);
    void removeFile(int position);
    void removeFiles(SparseIntArray selectedItemIdList);

    ArrayList<TreeNodeInterface> getFiles();

    void setParentNode(TreeNodeInterface parentNode);

    void removeFolder(int position);
    void removeNode(TreeNodeInterface childNode);

    void addNodes(List<TreeNodeInterface> children);
}
