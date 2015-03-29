package com.application.material.bookmarkswallet.app.fragments.interfaces;

import android.view.View;

/**
 * Created by davide on 07/11/14.
 */
public interface OnChangeActionbarLayoutAction {
    public void showLayoutByActionMenu(int actionId);
    public void hideLayoutByActionMenu(int layoutId);
    public void toggleLayoutByActionMenu(int actionId);
    public void setViewOnActionMenu(View view, int layoutId);
}
