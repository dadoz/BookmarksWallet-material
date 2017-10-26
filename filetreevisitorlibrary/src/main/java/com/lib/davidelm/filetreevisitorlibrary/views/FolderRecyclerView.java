package com.lib.davidelm.filetreevisitorlibrary.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.lib.davidelm.filetreevisitorlibrary.R;

/**
 * Created by davide on 21/05/2017.
 */

public class FolderRecyclerView extends RelativeLayout {
    protected EmptyRecyclerView recyclerView;

    public FolderRecyclerView(Context context) {
        super(context);
        initView();
    }

    public FolderRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public FolderRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public FolderRecyclerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
         inflate(getContext(), R.layout.folder_rv_view, this);
        recyclerView = (EmptyRecyclerView) findViewById(R.id.treeNodeFolderRecyclerViewId);
    }
}
