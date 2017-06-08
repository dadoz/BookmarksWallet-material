package com.lib.davidelm.filetreevisitorlibrary.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
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
    @NonNull
    private RealmList<TreeNodeRealm> children = new RealmList<>();
    private boolean folder;
    private TreeNodeContentRealm nodeContent;
    private boolean selected;

    public TreeNodeRealm() {
    }

    public TreeNodeRealm(@NonNull TreeNodeContent nodeContent, boolean folder, int level) {
        //TODO mv to copy obj
        this.nodeContent = new TreeNodeContentRealm(nodeContent.getName(), nodeContent.getDescription(), nodeContent.getFileResource(), nodeContent.getFolderResource());
        this.folder = folder;
        this.level = level;
    }

    @NonNull
    public static TreeNode root() {
        return new TreeNode(null, false, -1);
    }


    @NonNull
    public TreeNodeInterface addChild(TreeNodeInterface childNode) {
        if (childNode instanceof TreeNodeRealm) {
            childNode.setParent(this);
            childNode.setId();
            Realm.getDefaultInstance().beginTransaction();
            children.add((TreeNodeRealm) childNode);
            Realm.getDefaultInstance().commitTransaction();
        }
        return this;
    }


    @NonNull
    public TreeNodeInterface addChildren(@NonNull TreeNode... nodes) {
        for (TreeNode n : nodes) {
            addChild(n);
        }
        return this;
    }

    @NonNull
    public TreeNodeInterface addChildren(@NonNull Collection<TreeNodeInterface> nodes) {
        for (TreeNodeInterface n : nodes) {
            addChild(n);
        }
        return this;
    }

    public int deleteChild(@Nullable TreeNodeInterface child) {
        if (children.size() == 0 ||
                child == null)
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
        List<TreeNodeInterface> list = Collections.unmodifiableList(children);
//        list.sort((o1, o2) -> o1.isFolder() ? 0 : 1);
        return list;
    }

    public TreeNodeInterface getParent() {
        return mParent;
    }

    public int getId() {
        return id;
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
//        Log.e(TAG, "check parent node value" + name);
        return mParent == null && level == ROOT_LEVEL;
    }

    public boolean isFolder() {
        return folder;
    }

    public void setParent(TreeNodeInterface parent) {
        mParent = parent;
    }

    @Nullable
    public TreeNodeInterface getChildById(long id) {
        for (TreeNodeInterface child : children)
            if (child.getId() == id)
                return child;
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

    @Override
    public TreeNodeContent getNodeContent() {
        return nodeContent;
    }

    public void removeChildren() {
        children.clear();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void toggleSelected() {
        selected = !selected;
    }

}
