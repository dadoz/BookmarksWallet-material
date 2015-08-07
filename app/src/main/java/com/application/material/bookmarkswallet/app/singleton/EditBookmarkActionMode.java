package com.application.material.bookmarkswallet.app.singleton;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import com.application.material.bookmarkswallet.app.R;

/**
 * Created by davide on 07/08/15.
 */
public class EditBookmarkActionMode implements ActionMode.Callback {

    private final RecyclerView mRecyclerView;
    private final View mItemView;
    private BookmarkActionSingleton mBookmarkActionSingleton;

    /**
     *
     * @param singletonRef
     * @param v
     * @param recyclerView
     */
    public EditBookmarkActionMode(BookmarkActionSingleton singletonRef,
                                  final View v, final RecyclerView recyclerView) {
        mItemView = v;
        mRecyclerView = recyclerView;
        mBookmarkActionSingleton = singletonRef;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.bottom_sheet_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shareActionId:
                mBookmarkActionSingleton.shareAction(mItemView, mRecyclerView);
                mode.finish();
                return true;
            case R.id.deleteActionId:
                mBookmarkActionSingleton.deleteAction(mItemView, mRecyclerView);
                mode.finish();
                return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
    }

}
