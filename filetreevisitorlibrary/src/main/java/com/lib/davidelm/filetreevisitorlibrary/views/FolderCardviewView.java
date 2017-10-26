package com.lib.davidelm.filetreevisitorlibrary.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.lib.davidelm.filetreevisitorlibrary.R;

import java.lang.ref.WeakReference;


/**
 * Created by davide on 21/05/2017.
 */

public class FolderCardviewView extends FrameLayout implements OnFolderNodeClickCallbacks, View.OnClickListener {

    private NavigateFolderUpView navigateUpFolderView;
    private FolderNodeView addBookmarkFolderListView;

    public FolderCardviewView(@NonNull Context context) {
        super(context);
        initView();
    }

    public FolderCardviewView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public FolderCardviewView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        inflate(getContext(), R.layout.folder_cardview_view, this);
        navigateUpFolderView = (NavigateFolderUpView) findViewById(R.id.navigateUpFolderViewId);
        addBookmarkFolderListView = (FolderNodeView) findViewById(R.id.addBookmarkFolderListViewId);
        navigateUpFolderView.setOnClickListener(this);
        addBookmarkFolderListView.setOnNodeFolderClickLst(this);
    }

    public FolderNodeView getFolderListView() {
        return addBookmarkFolderListView;
    }

    public void init() {
        navigateUpFolderView.setVisibility(GONE);
        clearSelectedItems();
    }

    @Override
    public void onFolderNodeClickCb(View v, boolean isRootNode) {
        navigateUpFolderView.setVisibility(isRootNode ? GONE : VISIBLE);
        clearSelectedItems();
    }

    @Override
    public void onClick(View v) {
        setVisibility(View.VISIBLE);
        if (!addBookmarkFolderListView.isCurrentNodeRoot()) {
            //paren current root node
            if (addBookmarkFolderListView.isParentCurrentNodeRoot())
                navigateUpFolderView.setVisibility(GONE);

            addBookmarkFolderListView.setParentNodeFolder();
            clearSelectedItems();
        }
    }

    public void clearSelectedItems() {
        if (getFolderListView() != null)
            getFolderListView().clearSelectedItem();
    }
}
