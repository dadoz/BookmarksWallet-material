package com.application.material.bookmarkswallet.app.actionMode;

import android.content.Context;
import android.view.*;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.BookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.helpers.BookmarkActionHelper;
import com.application.material.bookmarkswallet.app.helpers.StatusHelper;

import java.lang.ref.WeakReference;

public class EditBookmarkActionModeCallback implements ActionMode.Callback {

    private final StatusHelper statusHelper;
    private BookmarkActionHelper mBookmarkActionSingleton;
    private BookmarkRecyclerViewAdapter adapter;
    private ActionMode actionMode;

    /**
     *
     * @param
     */
    public EditBookmarkActionModeCallback(WeakReference<Context> context,
                                          BookmarkRecyclerViewAdapter adp) {
        adapter = adp;
        mBookmarkActionSingleton = BookmarkActionHelper.getInstance(context);
        statusHelper = StatusHelper.getInstance();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        actionMode = mode;
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
            case R.id.action_share:
                mBookmarkActionSingleton.shareAction(adapter);
                mode.finish();
                return true;
            case R.id.action_delete:
                mBookmarkActionSingleton.deleteAction(adapter);
                mode.finish();
                return true;
            case R.id.action_select_all:
                mBookmarkActionSingleton.selectAllAction(adapter);
                return true;
        }
        return false;
    }

    /**
     *
     */
    public void forceToFinish() {
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    /**
     *
     * @param isVisible
     */
    public void toggleVisibilityIconMenu(boolean isVisible) {
        actionMode.getMenu().findItem(R.id.action_share).setVisible(isVisible);
    }

    /**
     *
     */
    private void unsetEditItem() {
        statusHelper.unsetStatus();
        adapter.clearSelectedItemPosArray();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        unsetEditItem();
    }

}
