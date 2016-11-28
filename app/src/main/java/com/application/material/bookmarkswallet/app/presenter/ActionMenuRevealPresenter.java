package com.application.material.bookmarkswallet.app.presenter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.animator.AnimatorBuilder;

import java.lang.ref.WeakReference;

import io.codetail.widget.RevealFrameLayout;

/**
 * Created by davide on 26/11/2016.
 */

public class ActionMenuRevealPresenter implements AnimatorBuilder.OnRevealAnimationListener {

    private final WeakReference<Context> ctx;
    private final View actionMenuView;
    private final View mainView;
    private final MenuItem openMenuItem;
    private final ActionBar actionBar;

    public ActionMenuRevealPresenter(WeakReference<Context> context, MenuItem menuItem,
                                     ActionBar actionBar,
                                     View[] views) {
        ctx = context;
        openMenuItem = menuItem;
        mainView = views[0];
        actionMenuView = views[1];
        this.actionBar = actionBar;
    }
    /**
     * move in a presenter?
     */
    public void toggleRevealActionMenu(boolean isShowing) {
        if (openMenuItem != null) {
            openMenuItem.setIcon(ContextCompat.getDrawable(ctx.get(),
                    isShowing ? R.drawable.ic_keyboard_arrow_up_black_24dp :
                            R.drawable.ic_keyboard_arrow_down_black_24dp));
        }

        int revealLayoutHeight = 180;
        if (isShowing)
            mainView.setTranslationY(revealLayoutHeight);
        AnimatorBuilder.getInstance(new WeakReference<>(ctx.get()))
                .buildRevealAnimation(actionMenuView, isShowing,
                        new WeakReference<AnimatorBuilder.OnRevealAnimationListener>(this))
                .start();
        actionBar.setElevation(isShowing ? 0 : 3);
    }

    @Override
    public void omRevealAnimationEnd() {
        mainView.setTranslationY(0);
    }

}
