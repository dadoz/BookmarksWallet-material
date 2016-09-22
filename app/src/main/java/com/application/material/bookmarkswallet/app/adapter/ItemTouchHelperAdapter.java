package com.application.material.bookmarkswallet.app.adapter;

/**
 * Created by davide on 21/09/16.
 */
public interface ItemTouchHelperAdapter {
    void onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
