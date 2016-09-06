package com.application.material.bookmarkswallet.app.actionMode;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.BookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.singleton.BookmarkActionSingleton;
import com.application.material.bookmarkswallet.app.singleton.StatusSingleton;

import java.lang.ref.WeakReference;

public class EditBookmarkActionMode implements ActionMode.Callback {

    private final StatusSingleton mStatusSingleton;
    private BookmarkActionSingleton mBookmarkActionSingleton;
    private int position;
    private BookmarkRecyclerViewAdapter adapter;

    /**
     *
     * @param
     */
    public EditBookmarkActionMode(WeakReference<Context> context, int pos,
                                  BookmarkRecyclerViewAdapter adp) {
        adapter = adp;
        position = pos;
        mBookmarkActionSingleton = BookmarkActionSingleton.getInstance(context);
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
    public void unsetEditItem() {
        int pos = mStatusSingleton.getEditItemPos();
        if (pos != StatusSingleton.EDIT_POS_NOT_SET) {
            adapter.notifyItemChanged(pos);
            mStatusSingleton.unsetStatus();
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        unsetEditItem();
    }

}
