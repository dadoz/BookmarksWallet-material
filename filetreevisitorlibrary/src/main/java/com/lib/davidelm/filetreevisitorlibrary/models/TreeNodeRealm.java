package com.lib.davidelm.filetreevisitorlibrary.models;

import android.util.Log;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class TreeNodeRealm extends RealmObject implements TreeNodeInterface {
    public static final int ROOT_LEVEL = -1;
    private static final String TAG = "BaseNodeTAG";
    private int level;

    @PrimaryKey
    private int id;
    @Ignore
    private transient TreeNodeInterface mParent;
    private RealmList<TreeNodeRealm> children = new RealmList<>();
    private String name;
    private boolean folder;

    public TreeNodeRealm() {
    }

    public TreeNodeRealm(String nodeName, boolean folder, int level) {
        this.name = nodeName;
        this.folder = folder;
        this.level = level;
    }

    public static TreeNode root() {
        return new TreeNode(null, false, -1);
    }


    public TreeNodeInterface addChild(TreeNodeInterface childNode) {
        if (childNode instanceof TreeNodeRealm) {
            childNode.setParent(this);
            childNode.setId();
            children.add((TreeNodeRealm) childNode);
        }
        return this;
    }


    public TreeNodeInterface addChildren(TreeNode... nodes) {
        for (TreeNode n : nodes) {
            addChild(n);
        }
        return this;
    }

    public TreeNodeInterface addChildren(Collection<TreeNodeInterface> nodes) {
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
        return name;
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
        Log.e(TAG, "check parent node value" + name);
        return mParent == null && level == ROOT_LEVEL;
    }

    public boolean isFolder() {
        return folder;
    }

    public void setParent(TreeNodeInterface parent) {
        mParent = parent;
    }

    public TreeNodeInterface getChildByName(String name) {
        for (TreeNodeInterface item : children) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }

    /**
     * find max an increment
     */
    public void setId() {
        try {
            id = Realm.getDefaultInstance()
                    .where(TreeNodeRealm.class)
                    .max("id").intValue() + 1;
        } catch (Exception e) {
            id = 0;
        }
    }

    public void removeChildren() {
        children.clear();
    }
}
