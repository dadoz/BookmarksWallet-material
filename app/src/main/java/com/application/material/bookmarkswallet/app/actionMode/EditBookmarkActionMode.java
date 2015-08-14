package com.application.material.bookmarkswallet.app.actionMode;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.singleton.BookmarkActionSingleton;
import com.application.material.bookmarkswallet.app.singleton.StatusSingleton;

/**
 * Created by davide on 07/08/15.
 */
public class EditBookmarkActionMode implements ActionMode.Callback {

    private final RecyclerView mRecyclerView;
    private final View mItemView;
    private final StatusSingleton mStatusSingleton;
    private BookmarkActionSingleton mBookmarkActionSingleton;

    /**
     *
     * @param activity
     * @param v
     * @param recyclerView
     */
    public EditBookmarkActionMode(Activity activity, final View v, final RecyclerView recyclerView) {
        mItemView = v;
        mRecyclerView = recyclerView;
        mBookmarkActionSingleton = BookmarkActionSingleton.getInstance(activity);
        mStatusSingleton = StatusSingleton.getInstance();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.share_delete_menu, menu);

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

    /**
     *
     */
    public void unsetEditItem() {
        int pos = mStatusSingleton.getEditItemPos();
        if (pos != StatusSingleton.EDIT_POS_NOT_SET) {
            mRecyclerView.getAdapter().notifyItemChanged(pos);
            mStatusSingleton.unsetStatus();
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        unsetEditItem();
    }

}
