package com.lib.davidelm.filetreevisitorlibrary.models;


import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class TreeNode implements Serializable, TreeNodeInterface {
    public static final int ROOT_LEVEL = -1;
    private static final String TAG = "TreeNodeTAG";
    private int level;

    private int id;
    private transient TreeNodeInterface mParent;
    private List<TreeNode> children = new ArrayList<>();
    private String mValue;
    private boolean folder;
    private int MAX_ID = 0; //TODO handle it

    //GSON parser
    public TreeNode() {}

    public TreeNode(String nodeName, boolean folder, int level) {
        this.mValue = nodeName;
        this.folder = folder;
        this.level = level;
    }

    public static TreeNode root() {
        return new TreeNode(null, false, -1);
    }


    public TreeNodeInterface addChild(TreeNodeInterface childNode) {
        if (childNode instanceof TreeNode) {
            childNode.setParent(this);
            childNode.setId();
            children.add(((TreeNode) childNode));
        }
        return this;
    }

    public void setId() {
        id = MAX_ID + 1;
    }

    public TreeNodeInterface addChildren(TreeNodeInterface... nodes) {
        for (TreeNodeInterface n : nodes) {
            addChild(n);
        }
        return this;
    }

    public TreeNode addChildren(Collection<TreeNodeInterface> nodes) {
        for (TreeNodeInterface n : nodes) {
            addChild(n);
        }
        return this;
    }

    public int deleteChild(TreeNodeInterface child) {
        if (children.size() == 0)
            return -1;

        for (int i = 0; i < children.size(); i++) {
            if (child.getId() == children.get(i).getId()) {
                children.remove(i);
                return i;
            }
        }
        return -1;
    }

    public List<TreeNodeInterface> getChildren() {
        //immutable types
        return Collections.unmodifiableList(children);
    }

    public TreeNodeInterface getParent() {
        return mParent;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return mValue;
    }

    public int getLevel() {
        int level = 0;
        TreeNodeInterface root = this;
        while (root.getParent() != null) {
            root = root.getParent();
            level++;
        }
        return level;
    }

    public boolean isRoot() {
        Log.e(TAG, "check parent node value" + mValue);
        return mParent == null && level == ROOT_LEVEL;
    }

    public boolean isFolder() {
        return folder;
    }

    public void setParent(TreeNodeInterface parent) {
         this.mParent = parent;
    }

    public TreeNodeInterface getChildByName(String name) {
        for (TreeNodeInterface item : children) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    public void removeChildren() {
        children.clear();
    }
}
