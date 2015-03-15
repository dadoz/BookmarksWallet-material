package com.application.material.bookmarkswallet.app.fragments.interfaces;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by davide on 07/11/14.
 */
public interface OnChangeActionbarLayoutAction {
    /**
     *
     * @param layoutId
     * @param listenerRef
     * @return
     */
    public View setDefaultActionMenu(int layoutId, View.OnClickListener listenerRef);
    /**
     *
     * @param actionMenu
     */
    public void showDefaultActionMenu(View actionMenu);

    public void showLayoutByMenuAction(int actionId);
    public void hideLayoutByMenuAction();
}
