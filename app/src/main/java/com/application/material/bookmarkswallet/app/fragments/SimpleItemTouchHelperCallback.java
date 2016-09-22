package com.application.material.bookmarkswallet.app.fragments;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.application.material.bookmarkswallet.app.adapter.BookmarkRecyclerViewAdapter;

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private final BookmarkRecyclerViewAdapter adapter;

    public SimpleItemTouchHelperCallback(BookmarkRecyclerViewAdapter adpt) {
        adapter = adpt;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.e("TAG", "Hey swipe");
        adapter.onItemDismiss(viewHolder.getAdapterPosition());
    }
}
