package com.application.material.bookmarkswallet.app.helpers;

import android.content.Context;
import android.view.View;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.animator.AnimatorBuilder;
import com.application.material.bookmarkswallet.app.manager.StatusManager;

import java.lang.ref.WeakReference;

public class ActionMenuRevealHelper {
    /**
     * move in a presenter?
     */
    public static void toggleRevealActionMenu(WeakReference<Context> ctx, View view, boolean isShowing,
                                              WeakReference<ActionMenuRevealCallbacks> listener) {
        AnimatorBuilder.getInstance(new WeakReference<>(ctx.get()))
                .buildRevealAnimation(view, isShowing,//actionMenuView, isShowing,
                        new WeakReference<AnimatorBuilder.OnRevealAnimationListener>(null))
                .start();
        setStatus(isShowing);
        if (listener.get() != null)
            listener.get().onToggleRevealCb(isShowing);
    }

    /**
     *
     * @param isShowing
     * @return
     */
    public static int getIconByShowingStatus(boolean isShowing) {
        return isShowing ?
                (NightModeHelper.getInstance().isNightMode() ? R.drawable.ic_keyboard_arrow_up_white_24dp :
                        R.drawable.ic_keyboard_arrow_up_black_24dp) :
                (NightModeHelper.getInstance().isNightMode() ? R.drawable.ic_keyboard_arrow_down_white_24dp :
                        R.drawable.ic_keyboard_arrow_down_black_24dp);
    }

    /**
     *
     * @param isShowing
     */
    private static void setStatus(boolean isShowing) {
        if (isShowing) {
            StatusManager.getInstance().setOnActionMenuMode();
            return;
        }

        StatusManager.getInstance().unsetStatus();
    }

    /**
     * interface
     */
    public interface ActionMenuRevealCallbacks {
        void onToggleRevealCb(boolean isShowing);
        void hanldeExportContextMenu();
        void hanldeSettingsContextMenu();
        void hanldeExportGridviewResizeMenu();
    }
}
