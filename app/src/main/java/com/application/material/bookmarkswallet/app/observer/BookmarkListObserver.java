package com.application.material.bookmarkswallet.app.observer;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.application.material.bookmarkswallet.app.manager.SearchManager;
import com.application.material.bookmarkswallet.app.manager.StatusManager;
import com.application.material.bookmarkswallet.app.manager.StatusManager.StatusEnum;

/**
 * set empty view on empty data TODO move in a presenter
 */
public class BookmarkListObserver extends RecyclerView.AdapterDataObserver {

    private StatusManager mStatusSingleton;
    private RecyclerView recyclerView;
    private View mEmptyLinkListView;
    private View mEmptySearchResultLayout;
    private SearchManager searchManager;

    public BookmarkListObserver(@NonNull View[] views, SearchManager searchMng) {

        mStatusSingleton = StatusManager.getInstance();
        recyclerView = (RecyclerView) views[0]; //recyclerView;
        mEmptyLinkListView = views[1]; //emptyLinkListView;
        mEmptySearchResultLayout = views[2]; //emptySearchResultLayout;
        searchManager = searchMng;
    }

    @Override
    public void onChanged() {
        StatusEnum status = mStatusSingleton.getCurrentStatus();
        if (status == StatusEnum.IDLE ||
                status == StatusEnum.EDIT) {
            handleListView();
            return;
        }

        if (status == StatusEnum.SEARCH) {
            handleSearchView();
        }

    }


    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
    }

    /**
     * handle empty listview
     */
    private void handleListView() {
//        mEmptyLinkListView.setVisibility(isEmptyData() ? View.VISIBLE : View.GONE);
        new Handler().postDelayed(() -> mEmptyLinkListView.setVisibility(isEmptyData() ? View.VISIBLE : View.GONE), 200);

    }

    /**
     * handle empty listview
     */
    private void handleSearchView() {
//        mEmptySearchResultLayout.setVisibility(isEmptyData() ? View.VISIBLE : View.GONE);
//        ((TextView) mEmptySearchResultLayout.findViewById(R.id.searchResultQueryTextId))
//                .setText(searchManager.getFilterString());
    }

    /**
     *
     * @return
     */
    private boolean isEmptyData() {
        return recyclerView.getAdapter().getItemCount() == 0;
    }
}
