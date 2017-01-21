package com.application.material.bookmarkswallet.app.helpers;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.animator.AnimatorBuilder;
import com.application.material.bookmarkswallet.app.helpers.NightModeHelper;
import com.application.material.bookmarkswallet.app.manager.StatusManager;
import com.application.material.bookmarkswallet.app.views.ContextRevealMenuView;

import java.lang.ref.WeakReference;

public class ActionMenuRevealHelper {//implements AnimatorBuilder.OnRevealAnimationListener,
//        ViewTreeObserver.OnPreDrawListener {

    private static WeakReference<Context> ctx;
//    private final View actionMenuView;
//    private final View mainView;
    private int revealLayoutHeight;
    private static ActionMenuRevealHelper instance;

    public ActionMenuRevealHelper() {
//        mainView = views[0];
//        actionMenuView = views[1];
//        actionMenuView.getViewTreeObserver().addOnPreDrawListener(this);
    }

    /**
     *
     * @param context
     * @return
     */
    public static ActionMenuRevealHelper getInstance(WeakReference<Context> context) {
        ctx = context;
        return instance == null ? instance = new ActionMenuRevealHelper() : instance;
    }

    /**
     * move in a presenter?
     */
    public void toggleRevealActionMenu(View view, boolean isShowing,
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
    public int getIconByShowingStatus(boolean isShowing) {

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
    private void setStatus(boolean isShowing) {
        if (isShowing) {
            StatusManager.getInstance().setOnActionMenuMode();
            return;
        }

        StatusManager.getInstance().unsetStatus();
    }

//    @Override
//    public void omRevealAnimationEnd() {
//        mainView.setTranslationY(0);
//    }

//    @Override
//    public boolean onPreDraw() {
//        revealLayoutHeight = actionMenuView.getHeight();
//        return true;
//    }

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
