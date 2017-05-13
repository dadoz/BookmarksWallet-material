package com.lib.davidelm.filetreevisitorlibrary.manager;


import android.content.Context;

import com.lib.davidelm.filetreevisitorlibrary.OnNodeVisitCompleted;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNode;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeContent;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;
import com.lib.davidelm.filetreevisitorlibrary.strategies.PersistenceStrategy;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

public class RootNodeManager {
    private static final String TAG = "RootNodePersistence";
    private static RootNodeManager instance;
    private final PersistenceStrategy persistenceStrategy;
    private TreeNodeInterface root;
    private WeakReference<OnNodeVisitCompleted> onNodeVisitCompletedLst;
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
    public static RootNodeManager getInstance(WeakReference<Context> context) {
        return instance == null ? instance = new RootNodeManager(context) : instance;
    }

    /**
     * add node
     * not optimized
     */
    public void removeNode(TreeNodeInterface node) throws IOException {
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
     * add node
     * not optimized
     */
    public void addNode(String nodeName, boolean folder) throws IOException {
        if (currentTreeNode == null ||
                nodeName.equals("")) {
            throw new IOException("not found");
        }

//        new TreeNodeRealm(nodeName, folder, currentTreeNode.getLevel() + 1);
        currentTreeNode.addChild(TreeNodeFactory
                .getChildByPersistenceType(persistenceStrategy.getPersistenceType(), nodeName, folder, currentTreeNode.getLevel() + 1));

        saveOnStorage();
    }

    /**
     * add node
     * not optimized
     */
    public void addNode(TreeNodeContent contentNode, boolean folder) throws IOException {
        if (currentTreeNode == null ||
                contentNode == null ||
                contentNode.getName().equals("")) {
            throw new IOException("not found");
        }

//        new TreeNodeRealm(nodeName, folder, currentTreeNode.getLevel() + 1);
        currentTreeNode.addChild(TreeNodeFactory
                .getChildByPersistenceType(persistenceStrategy.getPersistenceType(), contentNode, folder, currentTreeNode.getLevel() + 1));

        saveOnStorage();
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
     * recursion to handle se parent on each node
     * @param currentTreeNode
     * @param level
     */
    private void updateParentOnCurrentNode(TreeNodeInterface currentTreeNode, int level) {
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
