package com.lib.davidelm.filetreevisitorlibrary.views;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.lib.davidelm.filetreevisitorlibrary.OnNodeClickListener;
import com.lib.davidelm.filetreevisitorlibrary.R;
import com.lib.davidelm.filetreevisitorlibrary.adapter.TreeNodeAdapter;
import com.lib.davidelm.filetreevisitorlibrary.decorator.SpaceItemDecorator;
import com.lib.davidelm.filetreevisitorlibrary.manager.RootNodeManager;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNode;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by davide on 21/05/2017.
 */

public class FolderNodeView extends FolderRecyclerView implements OnNodeClickListener {
    private static final String TAG = "TAG";
    private final boolean isCompact = true;
    private RootNodeManager displayNodeListModel = RootNodeManager.getInstance(new WeakReference<Context>(getContext()));
    private TreeNodeInterface rootNode;
    private TreeNodeInterface currentNode;

    public FolderNodeView(Context context) {
        super(context);
        initView();
    }

    public FolderNodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public FolderNodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public FolderNodeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        currentNode = rootNode = displayNodeListModel.getRoot();
        initRecyclerView(isCompact);
    }

    /**
     *
     */
    private void initRecyclerView(boolean compactVisibility) {
        if (compactVisibility) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            recyclerView.addItemDecoration(new SpaceItemDecorator(getResources().getDimensionPixelSize(R.dimen.grid_space)));
        }

        if (rootNode != null)
            recyclerView.setAdapter(new TreeNodeAdapter(getFolderNodes(rootNode.getChildren()), new WeakReference<>(this)));
    }

    /**
     *
     * @return
     */
    public int getCurrentNodeId() {
        return currentNode != null ? currentNode.getId() : -1;
    }

    /**
     *
     * @return
     */
    public void setParentNodeFolder() {
        currentNode = currentNode != null ? currentNode.getParent() : rootNode;
        updateRv();
    }

    private void updateRv() {
        if (recyclerView != null) {
            ((TreeNodeAdapter) recyclerView.getAdapter()).addItems(getFolderNodes(currentNode.getChildren()));
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    /**
     *
     * @param list
     * @return
     */
    public ArrayList<TreeNodeInterface> getFolderNodes(List<TreeNodeInterface> list) {
        ArrayList<TreeNodeInterface> nodeList = new ArrayList<>();
        //filter list -> files and folders
        Iterator<TreeNodeInterface> nodesIterator = list
                .stream()
                .filter(TreeNodeInterface::isFolder)
                .iterator();

        //empty list
        if (nodesIterator.hasNext())
            nodesIterator.forEachRemaining(nodeList::add);
        return nodeList;
    }

    @Override
    public void onFolderNodeCLick(View v, int position, TreeNodeInterface node) {
        currentNode = node;
        updateRv();
    }

    @Override
    public void onFileNodeCLick(View v, int position, TreeNodeInterface node) {
        //THROW new exceptions
    }

    public boolean isCurrentNodeRoot() {
        return currentNode.isRoot();
    }
}
