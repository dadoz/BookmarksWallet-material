package com.application.material.bookmarkswallet.app.actionMode;

import android.content.Context;
import android.view.*;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.BookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.singleton.ActionsSingleton;
import com.application.material.bookmarkswallet.app.helpers.StatusHelper;

import java.lang.ref.WeakReference;

public class EditBookmarkActionModeCallback implements ActionMode.Callback {

    private final StatusHelper mStatusSingleton;
    private ActionsSingleton mBookmarkActionSingleton;
    private int position;
    private BookmarkRecyclerViewAdapter adapter;

    /**
     *
     * @param
     */
    public EditBookmarkActionModeCallback(WeakReference<Context> context, int pos,
                                          BookmarkRecyclerViewAdapter adp) {
        adapter = adp;
        position = pos;
        mBookmarkActionSingleton = ActionsSingleton.getInstance(context);
        mStatusSingleton = StatusHelper.getInstance();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.share_delete_menu, menu);
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
                mBookmarkActionSingleton.shareAction(adapter.getItem(position));
                mode.finish();
                return true;
            case R.id.deleteActionId:
                mBookmarkActionSingleton.deleteAction(adapter, position);
                mode.finish();
                return true;
        }
        return false;
    }

    /**
     *
     */
    private void unsetEditItem() {
        int pos = mStatusSingleton.getEditItemPos();
        if (pos != StatusHelper.EDIT_POS_NOT_SET) {
            adapter.notifyItemChanged(pos);
            mStatusSingleton.unsetStatus();
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        unsetEditItem();
    }

}
