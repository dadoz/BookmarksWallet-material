package com.application.material.bookmarkswallet.app.observer;

import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.manager.SearchManager;
import com.application.material.bookmarkswallet.app.helpers.StatusHelper;
import com.application.material.bookmarkswallet.app.helpers.StatusHelper.StatusEnum;

/**
 * set empty view on empty data TODO move
 */
public class BookmarkListObserver extends RecyclerView.AdapterDataObserver {

    private final View exportCardview;
    private final View recyclerviewLabelText;
    private StatusHelper mStatusSingleton;
    private RecyclerView mRecyclerView;
    private View mEmptyLinkListView;
    private View mEmptySearchResultLayout;
    private SearchManager searchManager;

    public BookmarkListObserver(@NonNull View[] views, SearchManager searchMng) {

        mStatusSingleton = StatusHelper.getInstance();
        mRecyclerView = (RecyclerView) views[0]; //recyclerView;
        mEmptyLinkListView = views[1]; //emptyLinkListView;
        mEmptySearchResultLayout = views[2]; //emptySearchResultLayout;
        recyclerviewLabelText = views[3]; //emptySearchResultLayout;
        exportCardview = views[4]; //emptySearchResultLayout;
        searchManager = searchMng;
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
        recyclerviewLabelText.setVisibility(isEmptyData() ? View.GONE : View.VISIBLE);
        exportCardview.setVisibility(isEmptyData() ? View.GONE : View.VISIBLE);
    }

    /**
     * handle empty listview
     */
    private void handleSearchView() {
        mEmptySearchResultLayout.setVisibility(isEmptyData() ? View.VISIBLE : View.GONE);
        ((TextView) mEmptySearchResultLayout.findViewById(R.id.searchResultQueryTextId))
                .setText(searchManager.getFilterString());
    }

    /**
     *
     * @return
     */
    private boolean isEmptyData() {
        return mRecyclerView.getAdapter().getItemCount() == 0;
    }
}
