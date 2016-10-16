package com.application.material.bookmarkswallet.app.actionMode;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.*;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.BookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.helpers.ActionbarHelper;
import com.application.material.bookmarkswallet.app.helpers.BookmarkActionHelper;
import com.application.material.bookmarkswallet.app.helpers.StatusHelper;

import java.lang.ref.WeakReference;

public class EditBookmarkActionModeCallback implements ActionMode.Callback {

    private final StatusHelper statusHelper;
    private final WeakReference<Context> ctx;
    private BookmarkActionHelper bookmarkActionHelper;
    private BookmarkRecyclerViewAdapter adapter;
    private ActionMode actionMode;

    /**
     *
     * @param context
     * @param adp
     */
    public EditBookmarkActionModeCallback(WeakReference<Context> context,
                                          BookmarkRecyclerViewAdapter adp) {
        adapter = adp;
        bookmarkActionHelper = BookmarkActionHelper.getInstance(context);
        statusHelper = StatusHelper.getInstance();
        ctx = context;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        actionMode = mode;
        mode.getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        ActionbarHelper.getInstance(ctx).setStatusbarColor(ContextCompat.getColor(ctx.get(),
                R.color.yellow_400));
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                bookmarkActionHelper.shareAction(adapter);
                mode.finish();
                return true;
            case R.id.action_select_all:
                actionMode = mode; //TODO this is not initialized but why???
                toggleVisibilityIconMenu(false);
                bookmarkActionHelper.selectAllAction(adapter);
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
     * @param count
     */
    public void setTotalSelectedItem(int count) {
        actionMode.setTitle(Integer.valueOf(count).toString());
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
        ActionbarHelper.getInstance(ctx).setStatusbarColor(ContextCompat.getColor(ctx.get(),
                R.color.yellow_600));
        unsetEditItem();
    }

}
