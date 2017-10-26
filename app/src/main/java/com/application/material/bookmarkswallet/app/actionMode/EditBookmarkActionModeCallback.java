package com.application.material.bookmarkswallet.app.actionMode;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.helpers.ActionbarHelper;
import com.application.material.bookmarkswallet.app.utlis.Utils;

import java.lang.ref.WeakReference;

public class EditBookmarkActionModeCallback implements ActionMode.Callback {

    private final WeakReference<Context> ctx;
    private final int colorPrimaryDarkSelected;
    private final int colorPrimaryDark;
    private final WeakReference<Activity> activityRef;
    private ActionMode actionMode;
    private WeakReference<OnActionModeCallbacks> lst;

    /**
     *
     * @param
     */
    public EditBookmarkActionModeCallback(Activity activity,
                                          OnActionModeCallbacks listener) {
        activityRef = new WeakReference<>(activity);
        ctx = new WeakReference<>(activity.getApplicationContext());
        lst = new WeakReference<>(listener);
        colorPrimaryDark = ContextCompat.getColor(ctx.get(), R.color.yellow_600);
        colorPrimaryDarkSelected = ContextCompat.getColor(ctx.get(), R.color.yellow_400);
    }

    /**
     *
     * @param color
     */
    private void initActionMode(int color) {
        actionMode.getMenu().findItem(R.id.action_delete).setIcon(Utils
                .getColoredIcon(ctx.get(), actionMode.getMenu().findItem(R.id.action_delete).getIcon(), color));
        actionMode.getMenu().findItem(R.id.action_select_all).setIcon(Utils
                .getColoredIcon(ctx.get(), actionMode.getMenu().findItem(R.id.action_select_all).getIcon(), color));
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        actionMode = mode;
        mode.getMenuInflater().inflate(R.menu.share_delete_menu, menu);
//        initActionMode(NightModeHelper.getInstance().isNightMode() ? R.color.grey_50 : R.color.indigo_600);
        ActionbarHelper.setStatusBarColor(activityRef.get(), colorPrimaryDarkSelected);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                lst.get().deleteActionModeCb();
                mode.finish();
                return true;
            case R.id.action_select_all:
                lst.get().selectAllActionModeCb();
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


    @Override
    public void onDestroyActionMode(ActionMode mode) {
        ActionbarHelper.setStatusBarColor(activityRef.get(), colorPrimaryDark);
        lst.get().onDestroyActionModeCb();
    }

}
