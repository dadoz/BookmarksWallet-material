package com.application.material.bookmarkswallet.app.recyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by davide on 12/03/15.
 */
public class RecyclerViewCustom extends RecyclerView {
    private View emptyView = null;
    final AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            showHideEmptyView();
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
            showHideEmptyView();
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
            showHideEmptyView();
        }

    };

    private void showHideEmptyView() {
        if(emptyView == null ||
                getAdapter() == null) {
            setVisibility(VISIBLE);
            return;
        }

        boolean isEmpty = getAdapter().getItemCount() == 0;
        emptyView.setVisibility(isEmpty ? VISIBLE : GONE);
        setVisibility(isEmpty ? GONE : VISIBLE);
    }

    public RecyclerViewCustom(Context context) {
        super(context);
    }

    public RecyclerViewCustom(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewCustom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void setAdapter(Adapter adapter) {
        if(getAdapter() != null) {
            getAdapter().unregisterAdapterDataObserver(observer);
        }
        super.setAdapter(adapter);
        if(adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
        showHideEmptyView();
    }

    public void setEmptyView(View view) {
        emptyView = view;
    }
}