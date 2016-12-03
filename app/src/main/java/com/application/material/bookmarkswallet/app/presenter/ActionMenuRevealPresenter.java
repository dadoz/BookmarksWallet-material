package com.application.material.bookmarkswallet.app.presenter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.animator.AnimatorBuilder;
import com.application.material.bookmarkswallet.app.manager.StatusManager;

import java.lang.ref.WeakReference;

public class ActionMenuRevealPresenter implements AnimatorBuilder.OnRevealAnimationListener,
        ViewTreeObserver.OnPreDrawListener {

    private final WeakReference<Context> ctx;
    private final View actionMenuView;
    private final View mainView;
    private final ActionBar actionBar;
    private int revealLayoutHeight;

    public ActionMenuRevealPresenter(WeakReference<Context> context,
                                     ActionBar actionb,
                                     View[] views) {
        ctx = context;
        mainView = views[0];
        actionMenuView = views[1];
        actionBar = actionb;
        actionMenuView.getViewTreeObserver().addOnPreDrawListener(this);
    }
    /**
     * move in a presenter?
     */
    public void toggleRevealActionMenu(boolean isShowing, MenuItem openMenuItem) {
        if (openMenuItem != null) {
            openMenuItem.setIcon(ContextCompat.getDrawable(ctx.get(),
                    isShowing ? R.drawable.ic_keyboard_arrow_up_black_24dp :
                            R.drawable.ic_keyboard_arrow_down_black_24dp));
        }

        if (isShowing) {
//            int revealLayoutHeight = 180; //TODO need to calculated
            mainView.setTranslationY(revealLayoutHeight);
        }

        AnimatorBuilder.getInstance(new WeakReference<>(ctx.get()))
                .buildRevealAnimation(actionMenuView, isShowing,
                        new WeakReference<AnimatorBuilder.OnRevealAnimationListener>(this))
                .start();
        actionBar.setElevation(isShowing ? 0 : 3);
        setStatus(isShowing);
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

    @Override
    public void omRevealAnimationEnd() {
        mainView.setTranslationY(0);
    }

    @Override
    public boolean onPreDraw() {
        revealLayoutHeight = actionMenuView.getHeight();
        return true;
    }
}
