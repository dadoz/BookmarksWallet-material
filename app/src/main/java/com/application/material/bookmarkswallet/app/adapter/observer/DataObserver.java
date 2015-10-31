package com.application.material.bookmarkswallet.app.adapter.observer;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.singleton.StatusSingleton;
import com.application.material.bookmarkswallet.app.singleton.StatusSingleton.StatusEnum;
import com.application.material.bookmarkswallet.app.singleton.search.SearchHandlerSingleton;

/**
 * set empty view on empty data TODO move
 */
public class DataObserver extends RecyclerView.AdapterDataObserver {

    private StatusSingleton mStatusSingleton;
    private RecyclerView mRecyclerView;
    private View mEmptyLinkListView;
    private View mEmptySearchResultLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SearchHandlerSingleton mSearchHandlerSingleton;

    public DataObserver(RecyclerView recyclerView,
                        View emptyLinkListView,
                        View emptySearchResultLayout,
                        SwipeRefreshLayout swipeRefreshLayout,
                        SearchHandlerSingleton searchHandlerSingleton) {

        this.mStatusSingleton = StatusSingleton.getInstance();
        this.mRecyclerView = recyclerView;
        this.mEmptyLinkListView = emptyLinkListView;
        this.mEmptySearchResultLayout = emptySearchResultLayout;
        this.mSwipeRefreshLayout = swipeRefreshLayout;
        this.mSearchHandlerSingleton = searchHandlerSingleton;
    }

    @Override
    public void onChanged() {
        StatusEnum status = mStatusSingleton.getCurrentStatus();
        if (status == StatusEnum.IDLE || status == StatusEnum.EDIT) {
            handleListView();
        } else if (status == StatusEnum.SEARCH) {
            handleSearchView();
        }
    }

    public void onItemRangeInserted(int positionStart, int itemCount) {
    }

    public void onItemRangeRemoved(int positionStart, int itemCount) {
    }

    /**
     * handle empty listview
     */
    private void handleListView() {
        mEmptyLinkListView.setVisibility(isEmptyData() ? View.VISIBLE : View.GONE);
    }

    /**
     * handle empty listview
     */
    private void handleSearchView() {
        mEmptySearchResultLayout.setVisibility(isEmptyData() ? View.VISIBLE : View.GONE);
        ((TextView) mEmptySearchResultLayout.findViewById(R.id.searchResultQueryTextId))
                .setText(mSearchHandlerSingleton.getFilterString());
    }

    /**
     *
     * @return
     */
    private boolean isEmptyData() {
        return mRecyclerView.getAdapter().getItemCount() == 0;
    }
}
