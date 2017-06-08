package com.lib.davidelm.filetreevisitorlibrary.views;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.lib.davidelm.filetreevisitorlibrary.OnNodeClickListener;
import com.lib.davidelm.filetreevisitorlibrary.OnNodeVisitCompleted;
import com.lib.davidelm.filetreevisitorlibrary.R;
import com.lib.davidelm.filetreevisitorlibrary.adapter.TreeNodeAdapter;
import com.lib.davidelm.filetreevisitorlibrary.decorator.SpaceItemDecorator;
import com.lib.davidelm.filetreevisitorlibrary.manager.RootNodeManager;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNode;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeContent;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeRealm;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TreeNodeView extends FrameLayout implements OnNodeClickListener, OnNodeVisitCompleted,
        BreadCrumbsView.OnPopBackStackInterface, OnFolderMenuItemClickListener {
    @NonNull
    private String TAG = "TAG";
    @Nullable
    private TreeNodeInterface currentNode;
    private TreeNodeInterface rootNode;
    private BreadCrumbsView breadCrumbsView;
    private RootNodeManager displayNodeListModel;
    private WeakReference<OnNavigationCallbacks> lst;
    private EmptyRecyclerView treeNodeFolderRecyclerView;
    private EmptyRecyclerView treeNodeFilesRecyclerView;

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
        treeNodeFolderRecyclerView = (EmptyRecyclerView) findViewById(R.id.treeNodeFolderRecyclerViewId);
        treeNodeFilesRecyclerView = (EmptyRecyclerView) findViewById(R.id.treeNodeFilesRecyclerViewId);
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
    public void setAdapter(@NonNull TreeNodeAdapter adapter) {
        treeNodeFilesRecyclerView.setAdapter(adapter);
        treeNodeFolderRecyclerView.setAdapter(new TreeNodeAdapter(new ArrayList<>(), new WeakReference<>(this), true));//TODO FIX it
        setEmptyRecyclerView();
        if (adapter.getItemCount() == 0) {
            //filter list -> files and folders
            addFolderNodes(rootNode.getChildren());
            addFileNodes(rootNode.getChildren());
        }
    }

    private void setEmptyRecyclerView() {
        View view = findViewById(R.id.emptyViewId);
//        view.findViewById(R.id.addFolderButtonId).setOnClickListener(view1 -> lst.get().onAddFolderEmptyViewCb());
        treeNodeFolderRecyclerView.setEmptyView(view);
    }

    /**
     * add custom item view :)
     */
//    @Deprecated
//    private void initRecyclerView() {
//        initRecyclerView(treeNodeFilesRecyclerView, true);
//        initRecyclerView(treeNodeFolderRecyclerView, false);
//    }

    /**
     *
     * @param recyclerView
     */
    private void initRecyclerView(@NonNull RecyclerView recyclerView, boolean compactVisibility) {
        if (compactVisibility) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            recyclerView.addItemDecoration(new SpaceItemDecorator(getResources().getDimensionPixelSize(R.dimen.grid_space)));
        }
        recyclerView.setAdapter(new TreeNodeAdapter(new ArrayList<>(), new WeakReference<>(this), true));
    }


    /**
     *
     * @param list
     */
    public void addFolderNodes(@NonNull List<TreeNodeInterface> list) {
        //filter list -> files and folders
        Iterator<TreeNodeInterface> foldersIterator = list
                .stream()
                .filter(TreeNodeInterface::isFolder)
                .iterator();
        setNodesOnAdapter(foldersIterator, treeNodeFolderRecyclerView, true);
    }

    /**
     *
     * @param list
     */
    public void addFileNodes(@NonNull List<TreeNodeInterface> list) {
        //filter list -> files and folders
        Iterator<TreeNodeInterface> foldersIterator = list
                .stream()
                .filter(obj -> !obj.isFolder())
                .iterator();

        setNodesOnAdapter(foldersIterator, treeNodeFilesRecyclerView, false);
    }

    /**
     *
     * @param nodesIterator
     * @param recyclerView
     */
    private void setNodesOnAdapter(@NonNull Iterator<TreeNodeInterface> nodesIterator, @NonNull RecyclerView recyclerView, boolean isCompactVisibility) {
        if (recyclerView.getAdapter() == null)
            initRecyclerView(recyclerView, isCompactVisibility);

        TreeNodeAdapter adapter = ((TreeNodeAdapter) recyclerView.getAdapter());
        //empty list
        if (!nodesIterator.hasNext())
            adapter.addItems(new ArrayList<>());

        adapter.clearItems();
        nodesIterator.forEachRemaining(adapter::addItem);

    }

    @Override
    public void setParentNode(@NonNull TreeNodeInterface parentNode) {
        Log.e(TAG, parentNode.getNodeContent().getName() != null ? parentNode.getNodeContent().getName() : "root");
        rootNode = currentNode = parentNode;
    }

    @Override
    public void removeNode(@NonNull TreeNodeInterface childNode) {
        RecyclerView recyclerView = childNode.isFolder() ? treeNodeFolderRecyclerView : treeNodeFilesRecyclerView;
        ((TreeNodeAdapter) recyclerView.getAdapter()).removeItem(childNode);
    }

    public void removeNode(int pos, boolean isFolder) {
        RecyclerView recyclerView = isFolder ? treeNodeFolderRecyclerView : treeNodeFilesRecyclerView;
        ((TreeNodeAdapter) recyclerView.getAdapter()).removeItem(pos);
    }

    @Override
    public void addNodes(@NonNull List<TreeNodeInterface> children) {
        addFolderNodes(children);
        addFileNodes(children);
    }

    @Override
    public void onFolderNodeCLick(View v, int position, @NonNull TreeNodeInterface node) {
        //set breadcrumbs
        breadCrumbsView.addBreadCrumb(node.getNodeContent().getName());

        //update and set view
        updateCurrentNode(node);
        addNodes(node.getChildren());
        treeNodeFolderRecyclerView.getAdapter().notifyDataSetChanged();
        treeNodeFilesRecyclerView.getAdapter().notifyDataSetChanged();

        if (lst != null && lst.get() != null)
            lst.get().onFolderNodeClickCb(position, node);
    }

    /**
     *
     * @param node
     */
    private boolean updateCurrentNode(@Nullable TreeNodeInterface node) {
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

    @Override
    public void onFolderNodeLongCLick(View v, int position, TreeNodeInterface item) {
        lst.get().onFolderNodeLongClickCb(position, item);
    }

    @Override
    public void onFileNodeLongCLick(View v, int position, TreeNodeInterface item) {
        lst.get().onFileNodeLongClickCb(position, item);
    }

    @Override
    public void onMoreSettingsClick(@NonNull View v, int position, TreeNodeInterface item) {
        showPopupMenu(v, item);
    }

    @Override
    public void onSelectButtonClick(View v, int position, TreeNodeInterface item) {
        Log.e(TAG, "hey selefction item");
    }


    /**
     *
     * @param v
     * @param node
     */
    private void showPopupMenu(@NonNull View v, TreeNodeInterface node) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.inflate(R.menu.folder_settings_menu);
        popupMenu.setOnMenuItemClickListener(item -> onMenuItemClick(item, node));//OnFolderMenuItemClickListener::onFolderMenuItemClick);
        popupMenu.show();
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
            ((TreeNodeAdapter) treeNodeFolderRecyclerView.getAdapter()).addItems(currentNode.getChildren());
        }
        return !isRootNode;
    }

    /**
     *
     * @param breadCrumbsView
     */
    public void setBreadCrumbsView(@NonNull BreadCrumbsView breadCrumbsView) {
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

        addNodes(currentNode.getChildren());
        treeNodeFolderRecyclerView.getAdapter().notifyDataSetChanged();
        treeNodeFilesRecyclerView.getAdapter().notifyDataSetChanged();
    }

    /**
     *
     * @param name
     */
    public void addFolder(String name, int parentNodeId) {
        try {
            displayNodeListModel.addNode(name, parentNodeId, true);
        } catch (IOException e) {
            showError(0, e.getMessage());
        }
    }
    /**
     *
     * @param nodeContent
     */
    public void addFolder(TreeNodeContent nodeContent, int parentNodeId) {
        try {
            displayNodeListModel.addNode(nodeContent, parentNodeId, true);
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
    public void addFile(TreeNodeContent nodeContent, int parentNodeId) {
        try {
            displayNodeListModel.addNode(nodeContent, parentNodeId, false);
        } catch (IOException e) {
            showError(1, e.getMessage());
        }
    }
    /**
     *
     * @param name
     */
    public void addFile(String name, int parentNodeId) {
        try {
            displayNodeListModel.addNode(name, parentNodeId, false);
        } catch (IOException e) {
            showError(1, e.getMessage());
        }
    }

    /**
     *
     * @param node
     */
    public synchronized void removeFolder(TreeNodeInterface node) {
        try {
            displayNodeListModel.removeNode(node);
        } catch (IOException e) {
            showError(2, e.getMessage());
        }

    }

    /**
     *
     * @param position
     */
    public synchronized void removeFile(int position) {
        try {
            displayNodeListModel.removeNode(currentNode.getChildren().get(position));
        } catch (IOException e) {
            showError(2, e.getMessage());
        }
    }

    public synchronized void removeFiles(@NonNull SparseIntArray selectedItemIdList) {
        for (int i = 0; i < selectedItemIdList.size(); i++) {
            try {
                displayNodeListModel.removeNodeById(selectedItemIdList.keyAt(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public void setMarginTop(int marginTop) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
        lp.setMarginStart(marginTop);
        setLayoutParams(lp);
    }

    @Override
    public boolean onMenuItemClick(@NonNull MenuItem item, TreeNodeInterface node) {
        if (item.getItemId() == R.id.action_delete) {
            removeFolder(node);
        }
        return false;
    }

    @Nullable
    public ArrayList<TreeNodeInterface> getFiles() {
        if (currentNode != null) {
            ArrayList<TreeNodeInterface> list = new ArrayList<>();
            Iterator<TreeNodeInterface> foldersIterator = currentNode.getChildren()
                    .stream()
                    .filter(item -> !item.isFolder())
                    .iterator();
            foldersIterator.forEachRemaining(list::add);
            return list;
        }
        return null;
    }
}
