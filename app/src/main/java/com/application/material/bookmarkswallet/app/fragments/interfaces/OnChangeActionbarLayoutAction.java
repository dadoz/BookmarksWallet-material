package com.application.material.bookmarkswallet.app.fragments.interfaces;

import android.view.View;

/**
 * Created by davide on 07/11/14.
 */
public interface OnChangeActionbarLayoutAction {
    public void showLayoutByActionMenu(int actionId);
    public void hideLayoutByActionMenu(int layoutId);
    public void toggleLayoutByActionMenu(int actionId);
    public void setViewOnActionMenu(View mainView, View view, int layoutId);
    public void setViewOnActionMenu(View mainView, View view, int layoutId, View.OnClickListener listener);
    public void toggleInnerLayoutByActionMenu(int layoutId);
}
