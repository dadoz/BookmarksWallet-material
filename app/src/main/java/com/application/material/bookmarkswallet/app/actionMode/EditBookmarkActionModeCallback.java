package com.application.material.bookmarkswallet.app.actionMode;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.*;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.BookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.helpers.ActionbarHelper;
import com.application.material.bookmarkswallet.app.helpers.BookmarkActionHelper;
import com.application.material.bookmarkswallet.app.helpers.NightModeHelper;
import com.application.material.bookmarkswallet.app.manager.StatusManager;
import com.application.material.bookmarkswallet.app.utlis.Utils;

import java.lang.ref.WeakReference;

public class EditBookmarkActionModeCallback implements ActionMode.Callback {

    private final StatusManager statusHelper;
    private final WeakReference<Context> ctx;
    private final int colorPrimaryDarkSelected;
    private final int colorPrimaryDark;
    private BookmarkActionHelper mBookmarkActionSingleton;
    private BookmarkRecyclerViewAdapter adapter;
    private ActionMode actionMode;

    /**
     *
     * @param
     */
    public EditBookmarkActionModeCallback(WeakReference<Context> context,
                                          BookmarkRecyclerViewAdapter adp) {
        ctx = context;
        adapter = adp;
        mBookmarkActionSingleton = BookmarkActionHelper.getInstance(context);
        statusHelper = StatusManager.getInstance(); 
        colorPrimaryDark = ContextCompat.getColor(ctx.get(), R.color.yellow_600);
        colorPrimaryDarkSelected = ContextCompat.getColor(ctx.get(), R.color.yellow_400);
    }

    /**
     *
     * @param color
     */
    private void initActionMode(int color) {
        actionMode.getMenu().findItem(R.id.action_share).setIcon(Utils
                .getColoredIcon(ctx.get(), actionMode.getMenu().findItem(R.id.action_share).getIcon(), color));
        actionMode.getMenu().findItem(R.id.action_delete).setIcon(Utils
                .getColoredIcon(ctx.get(), actionMode.getMenu().findItem(R.id.action_delete).getIcon(), color));
        actionMode.getMenu().findItem(R.id.action_select_all).setIcon(Utils
                .getColoredIcon(ctx.get(), actionMode.getMenu().findItem(R.id.action_select_all).getIcon(), color));
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        actionMode = mode;
        mode.getMenuInflater().inflate(R.menu.share_delete_menu, menu);
        initActionMode(NightModeHelper.getInstance().isNightMode() ? R.color.grey_50 : R.color.indigo_600);
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
                actionMode = mode; //TODO this is not initialized but why???
                setSelectedItemCount(adapter.getItemCount());
                toggleVisibilityIconMenu(false);
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
//        ActionbarHelper.getInstance(ctx).setStatusbarColor(colorPrimaryDark);
        unsetEditItem();
    }

    /**
     *
     * @param count
     */
    public void setSelectedItemCount(int count) {
        if (actionMode != null)
            actionMode.setTitle(Integer.toString(count));
    }
}
