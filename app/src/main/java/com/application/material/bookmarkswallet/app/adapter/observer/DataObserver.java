package com.application.material.bookmarkswallet.app.adapter.observer;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.singleton.BookmarkProviderSingleton;
import com.application.material.bookmarkswallet.app.singleton.StatusSingleton;
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
    private BookmarkProviderSingleton mBookmarkProviderSingleton;
    private SearchHandlerSingleton mSearchHandlerSingleton;

    public DataObserver(StatusSingleton statusSingleton,
                        RecyclerView recyclerView,
                        View emptyLinkListView,
                        View emptySearchResultLayout,
                        SwipeRefreshLayout swipeRefreshLayout,
                        BookmarkProviderSingleton bookmarkProviderSingleton,
                        SearchHandlerSingleton searchHandlerSingleton) {

        this.mStatusSingleton = statusSingleton;
        this.mRecyclerView = recyclerView;
        this.mEmptyLinkListView = emptyLinkListView;
        this.mEmptySearchResultLayout = emptySearchResultLayout;
        this.mSwipeRefreshLayout = swipeRefreshLayout;
        this.mBookmarkProviderSingleton = bookmarkProviderSingleton;
        this.mSearchHandlerSingleton = searchHandlerSingleton;
    }

    @Override
    public void onChanged() {
        if (mStatusSingleton.isIdleMode()) {
            handleListView();
        } else if (mStatusSingleton.isSearchMode()) {
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
        boolean isEmptyData = mRecyclerView.getAdapter().getItemCount() == 0;
        mEmptyLinkListView.setVisibility(isEmptyData ? View.VISIBLE : View.GONE);
        mEmptyLinkListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeRefreshLayout.setRefreshing(true);
                mBookmarkProviderSingleton.addByDefaultBrowser();
            }
        });
    }

    /**
     * handle empty listview
     */
    private void handleSearchView() {
        boolean isEmptyData = mRecyclerView.getAdapter().getItemCount() == 0;
        mEmptySearchResultLayout.setVisibility(isEmptyData ? View.VISIBLE : View.GONE);
        ((TextView) mEmptySearchResultLayout.findViewById(R.id.searchResultQueryTextId))
                .setText(mSearchHandlerSingleton.getFilterString());
    }

}
