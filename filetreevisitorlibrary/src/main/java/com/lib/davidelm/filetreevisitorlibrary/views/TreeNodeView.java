package com.lib.davidelm.filetreevisitorlibrary.views;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.lib.davidelm.filetreevisitorlibrary.OnNodeClickListener;
import com.lib.davidelm.filetreevisitorlibrary.OnNodeVisitCompleted;
import com.lib.davidelm.filetreevisitorlibrary.R;
import com.lib.davidelm.filetreevisitorlibrary.adapter.TreeNodeAdapter;
import com.lib.davidelm.filetreevisitorlibrary.decorator.SpaceItemDecorator;
import com.lib.davidelm.filetreevisitorlibrary.manager.RootNodeManager;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNode;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeContent;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class TreeNodeView extends FrameLayout implements OnNodeClickListener, OnNodeVisitCompleted,
        BreadCrumbsView.OnPopBackStackInterface {
    private RecyclerView treeNodeRecyclerView;
    private String TAG = "TAG";
    private TreeNodeInterface currentNode;
    private TreeNodeInterface rootNode;
    private BreadCrumbsView breadCrumbsView;
    private RootNodeManager displayNodeListModel;
    private WeakReference<OnNavigationCallbacks> lst;

    public TreeNodeView(@NonNull Context context) {
        super(context);
        initView();
    }

    public TreeNodeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TreeNodeView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public TreeNodeView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    /**
     * init view
     */
    private void initView() {
        inflate(getContext(), R.layout.tree_node_layout, this);
        treeNodeRecyclerView = (RecyclerView) findViewById(R.id.treeNodeRecyclerViewId);
        displayNodeListModel = RootNodeManager.getInstance(new WeakReference<>(getContext()));
        displayNodeListModel.init(new WeakReference<>(this));
    }

    /**
     * set listener on cb
     * @param lst
     */
    public void setNavigationCallbacksListener(WeakReference<OnNavigationCallbacks> lst) {
        this.lst = lst;
    }

    /**
     * add custom item view :)
     */
    public void setAdapter(TreeNodeAdapter adapter) {
        treeNodeRecyclerView.setAdapter(adapter);
    }

    /**
     * add custom item view :)
     */
    private void initRecyclerView() {
        treeNodeRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        treeNodeRecyclerView.addItemDecoration(new SpaceItemDecorator(getResources().getDimensionPixelSize(R.dimen.grid_space)));
        treeNodeRecyclerView.setAdapter(new TreeNodeAdapter(new ArrayList<>(), new WeakReference<>(this)));
    }

    /**
     *
     * @param list
     */
    public void addNodes(List<TreeNodeInterface> list) {
        if (treeNodeRecyclerView.getAdapter() == null)
            initRecyclerView();
        ((TreeNodeAdapter) treeNodeRecyclerView.getAdapter()).addItems(list);
    }

    @Override
    public void setParentNode(TreeNodeInterface parentNode) {
        Log.e(TAG, parentNode.getNodeContent().getName() != null ? parentNode.getNodeContent().getName() : "root");
        rootNode = currentNode = parentNode;
    }

    @Override
    public void removeNode(TreeNodeInterface childNode) {
        ((TreeNodeAdapter) treeNodeRecyclerView.getAdapter()).removeItem(childNode);
    }

    @Override
    public void onFolderNodeCLick(View v, int position, TreeNodeInterface node) {
        //set breadcrumbs
        breadCrumbsView.addBreadCrumb(node.getNodeContent().getName());

        //update and set view
        updateCurrentNode(node);
        ((TreeNodeAdapter) treeNodeRecyclerView.getAdapter()).addItems(node.getChildren());
        treeNodeRecyclerView.getAdapter().notifyDataSetChanged();

        if (lst != null && lst.get() != null)
            lst.get().onFolderNodeClickCb(position, node);
    }

    /**
     *
     * @param node
     */
    private boolean updateCurrentNode(TreeNodeInterface node) {
        //check if isRootNode
        boolean isRootNode = currentNode.isRoot();

        //set current node
        currentNode = (node != null) ? node :
                (currentNode != null && !isRootNode) ? currentNode.getParent() : rootNode;

        //update current node on displayNodeListModel
        displayNodeListModel.setCurrentNode(currentNode);

        //return current node is parent
        return isRootNode;
    }

    @Override
    public void onFileNodeCLick(View v, int position, TreeNodeInterface node) {
        if (lst != null && lst.get() != null)
            lst.get().onFileNodeClickCb(position, node);
    }

    /**
     * on back pressed cb
     * @return
     */
    private boolean onBackPressed() {
        boolean isRootNode = updateCurrentNode(null);
        if (!isRootNode) {
            //update breadCrumbs
            breadCrumbsView.removeLatestBreadCrumb();

            //update node viewsrr
            ((TreeNodeAdapter) treeNodeRecyclerView.getAdapter()).addItems(currentNode.getChildren());
        }
        return !isRootNode;
    }

    /**
     *
     * @param breadCrumbsView
     */
    public void setBreadCrumbsView(BreadCrumbsView breadCrumbsView) {
        this.breadCrumbsView = breadCrumbsView;
        breadCrumbsView.setLst(new WeakReference<>(this));
    }

    @Override
    public void popBackStackTillPosition(int position) {
        Log.e(TAG, "current node " + currentNode.getLevel());
        if (currentNode == null)
            return;

        while (currentNode.getLevel() != position &&
                currentNode.getLevel() > 0) {
            updateCurrentNode(currentNode.getParent());
        }

        ((TreeNodeAdapter) treeNodeRecyclerView.getAdapter()).addItems(currentNode.getChildren());
        treeNodeRecyclerView.getAdapter().notifyDataSetChanged();
    }

    /**
     *
     * @param name
     */
    public void addFolder(String name) {
        try {
            displayNodeListModel.addNode(name, true);
        } catch (IOException e) {
            showError(0, e.getMessage());
        }
    }
    /**
     *
     * @param nodeContent
     */
    public void addFolder(TreeNodeContent nodeContent) {
        try {
            displayNodeListModel.addNode(nodeContent, true);
        } catch (IOException e) {
            showError(0, e.getMessage());
        }
    }

    /**
     * show error cb
     * @param type
     * @param message
     */
    private void showError(int type, String message) {
        if (lst != null && lst.get() != null)
            lst.get().onNodeError(type, currentNode, message);
    }

    /**
     *
     * @param nodeContent
     */
    public void addFile(TreeNodeContent nodeContent) {
        try {
            displayNodeListModel.addNode(nodeContent, false);
        } catch (IOException e) {
            showError(1, e.getMessage());
        }
    }
    /**
     *
     * @param name
     */
    public void addFile(String name) {
        try {
            displayNodeListModel.addNode(name, false);
        } catch (IOException e) {
            showError(1, e.getMessage());
        }
    }

    /**
     *
     * @param name
     */
    public void removeFolder(String name) {
        try {
            displayNodeListModel.removeNode(currentNode.getChildByName(name));
        } catch (IOException e) {
            showError(2, e.getMessage());
        }

    }

    /**
     *
     * @param position
     */
    public void removeFolder(int position) {
        try {
            displayNodeListModel.removeNode(currentNode.getChildren().get(position));
        } catch (IOException e) {
            showError(2, e.getMessage());
        }
    }
}
