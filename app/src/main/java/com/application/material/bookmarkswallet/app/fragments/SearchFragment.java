package com.application.material.bookmarkswallet.app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.manager.SearchManager;

/**
 * Created by davide on 24/06/2017.
 */

class SearchFragment extends BaseFragment implements SearchManager.SearchManagerCallbackInterface {
    private static final String TAG = "SearchFrag";
    private SearchManager searchManager;

    {
        layoutId = R.layout.fragment_search_layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        searchManager = SearchManager.getInstance();
        searchManager.setListener(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInitView();
    }

    private void onInitView() {

    }

    @Override
    public void onOpenSearchView() {
    }

    @Override
    public void onCloseSearchView() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void searchBy(String searchValue, boolean mCaseSensitive) {
        Log.e(TAG, "hey - " + searchValue);
    }
}
