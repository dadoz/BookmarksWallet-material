package com.lib.davidelm.filetreevisitorlibrary.manager;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lib.davidelm.filetreevisitorlibrary.OnNodeVisitCompleted;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNode;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeContent;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeRealm;
import com.lib.davidelm.filetreevisitorlibrary.strategies.PersistenceStrategy;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

public class RootNodeManager {
    private static final String TAG = "RootNodePersistence";
    private static RootNodeManager instance;
    @NonNull
    private final PersistenceStrategy persistenceStrategy;
    @Nullable
    protected TreeNodeInterface root;
    private WeakReference<OnNodeVisitCompleted> onNodeVisitCompletedLst;
    @Nullable
    private TreeNodeInterface currentTreeNode; //TODO make it persistent

    /**
     * build manager
     * @param context
     */
    private RootNodeManager(WeakReference<Context> context) {
        persistenceStrategy = new PersistenceStrategy(context, PersistenceStrategy.PersistenceType.REALMIO);

        //check parsed node
        if ((root = persistenceStrategy.getStrategy().getPersistentNode()) == null) {
            root = initRootNode();
            persistenceStrategy.getStrategy().setPersistentNode(root);
        }

        //update parent current nodes
        updateParentOnCurrentNode(root, TreeNode.ROOT_LEVEL);

        //init current node
        currentTreeNode = root;
    }

    /**
     * init root node
     * @return
     */
    @Nullable
    private TreeNodeInterface initRootNode() {
        return TreeNodeFactory
                .getChildByPersistenceType(persistenceStrategy.getPersistenceType(), "ROOT", false, TreeNode.ROOT_LEVEL);
    }

    /**
     * init list
     * @param lst2
     */
    public void init(WeakReference<OnNodeVisitCompleted> lst2) {
        this.onNodeVisitCompletedLst = lst2;
        addNodeUpdateView();
    }

    /**
     *
     * @return
     */
    @NonNull
    public static RootNodeManager getInstance(WeakReference<Context> context) {
        return instance == null ? instance = new RootNodeManager(context) : instance;
    }

    /**
     * add node
     * not optimized
     */
    public void removeNode(@Nullable TreeNodeInterface node) throws IOException {
        if (currentTreeNode == null ||
                node == null) {
            throw new IOException("not found");
        }

        currentTreeNode.deleteChild(node);
        //save on storage
        persistenceStrategy.getStrategy().setPersistentNode(root);
        //update view
        removeNodeUpdateView(node);
    }

    /**
     *
     * @param id
     * @throws IOException
     */
    public void removeNodeById(int id) throws IOException {
        if (currentTreeNode == null ||
                id == -1) {
            throw new IOException("not found");
        }

        TreeNodeInterface node = currentTreeNode.getChildById(id);
        currentTreeNode.deleteChild(node);
        //save on storage
        persistenceStrategy.getStrategy().setPersistentNode(root);
        //update view
        removeNodeUpdateView(node);

    }

    /**
     * add node
     * not optimized
     */
    public void addNode(@Nullable Object nodeAttribute, int parentNodeId, boolean folder) throws IOException {
        if (currentTreeNode == null ||
                nodeAttribute == null) {
            throw new IOException("not found");
        }

        //set local node
        TreeNodeInterface localNode = findNodeById(currentTreeNode, parentNodeId);
        if (localNode == null)
            localNode = currentTreeNode;

        if (nodeAttribute instanceof String) {
            localNode.addChild(TreeNodeFactory
                    .getChildByPersistenceType(persistenceStrategy.getPersistenceType(),
                            (String) nodeAttribute, folder, localNode.getLevel() + 1));
        }

        if (nodeAttribute instanceof TreeNodeContent) {
            localNode.addChild(TreeNodeFactory
                    .getChildByPersistenceType(persistenceStrategy.getPersistenceType(),
                            (TreeNodeContent) nodeAttribute, folder, localNode.getLevel() + 1));
        }

        saveOnStorage();
    }

    /**
     * TODO please take care of this - RECURSIVE
     * @param node
     * @param nodeId
     * @return
     */
    private TreeNodeInterface findNodeById(@NonNull TreeNodeInterface node, int nodeId) {
        if (node.getChildren() == null ||
                nodeId == -1) {
            return null;
        }

        for (TreeNodeInterface parent : node.getChildren()) {
            findNodeById(parent, nodeId);
        }
        return node.getChildById(nodeId);
    }


    /**
     * save on storage method
     */
    private void saveOnStorage() {
        //save on storage
        persistenceStrategy.getStrategy().setPersistentNode(root);

        //update view
        addNodeUpdateView();
    }

    /**
     * build view by node children
     */
    public void addNodeUpdateView() {
        if (onNodeVisitCompletedLst.get() != null) {
            onNodeVisitCompletedLst.get().setParentNode(currentTreeNode);
            onNodeVisitCompletedLst.get().addNodes(currentTreeNode.getChildren());
        }
    }

    /**
     * build view by node children
     */
    public void removeNodeUpdateView(TreeNodeInterface chiild) {
        if (onNodeVisitCompletedLst.get() != null) {
            onNodeVisitCompletedLst.get().removeNode(chiild);
        }
    }

    /**
     *
     * @param currentTreeNode
     */
    public void setCurrentNode(TreeNodeInterface currentTreeNode) {
        this.currentTreeNode = currentTreeNode;
    }

    /**
     *
     */
    @Nullable
    public TreeNodeInterface getRoot() {
        return root;
    }


    /**
     * recursion to handle se parent on each node
     * @param currentTreeNode
     * @param level
     */
    private void updateParentOnCurrentNode(@NonNull TreeNodeInterface currentTreeNode, int level) {
        level += 1;
        List<TreeNodeInterface> list;
        if ((list = currentTreeNode.getChildren()) != null) {
            for (TreeNodeInterface item : list) {
                item.setParent(currentTreeNode);
                updateParentOnCurrentNode(item, level);
            }
        }
    }
}
