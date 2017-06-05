package com.lib.davidelm.filetreevisitorlibrary.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.lib.davidelm.filetreevisitorlibrary.R;


/**
 * Created by davide on 21/05/2017.
 */

public class FolderCardviewView extends FrameLayout {

    private NavigateFolderUpView navigateUpFolderView;
    private FolderNodeView addBookmarkFolderListView;

    public FolderCardviewView(Context context) {
        super(context);
        initView();
    }

    public FolderCardviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public FolderCardviewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        inflate(getContext(), R.layout.folder_cardview_view, this);
        navigateUpFolderView = (NavigateFolderUpView) findViewById(R.id.navigateUpFolderViewId);
        addBookmarkFolderListView = (FolderNodeView) findViewById(R.id.addBookmarkFolderListViewId);
    }

    public View getAddBookmarkFolderListView() {
        return addBookmarkFolderListView;
    }

    public void setAddBookmarkFolderListView(FolderNodeView addBookmarkFolderListView) {
        this.addBookmarkFolderListView = addBookmarkFolderListView;
    }

    public View getNavigateUpFolderView() {
        return navigateUpFolderView;
    }

    public void setNavigateUpFolderView(NavigateFolderUpView navigateUpFolderView) {
        this.navigateUpFolderView = navigateUpFolderView;
    }

    public FolderNodeView getFolderListView() {
        return addBookmarkFolderListView;
    }
}
