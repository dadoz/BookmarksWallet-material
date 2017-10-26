package com.application.material.bookmarkswallet.app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.OnSearchItemClickListener;
import com.application.material.bookmarkswallet.app.adapter.SearchResultAdapter;
import com.application.material.bookmarkswallet.app.manager.SearchManager;
import com.application.material.bookmarkswallet.app.utlis.BrowserUtils;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.lib.davidelm.filetreevisitorlibrary.manager.NodeListManager;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davide on 24/06/2017.
 */

public class SearchFragment extends BaseFragment implements SearchManager.SearchManagerCallbackInterface,
        SearchManager.SearchManagerPublishResultCallbackInterface,
        OnSearchItemClickListener {
    private static final String TAG = "SearchFrag";
    private SearchManager searchManager;
    private RecyclerView searchResultRecyclerView;
    private View searchBookmarksIcon;
    private TextView searchBookmarksNotFoundText;
    private String base;

    {
        layoutId = R.layout.fragment_search_layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        searchManager = SearchManager.getInstance();
        searchManager.setListener(this);
        searchManager.setList(NodeListManager.getInstance(getActivity()).getNodeList());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInitView(view);
    }

    /**
     *
     * @param view
     */
    private void onInitView(View view) {
        searchBookmarksIcon = view.findViewById(R.id.searchBookmarksIconId);
        searchBookmarksNotFoundText = (TextView) view.findViewById(R.id.searchBookmarksNotFoundTextId);
        searchResultRecyclerView = (RecyclerView) view.findViewById(R.id.searchResultRecyclerViewId);
        searchResultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchResultRecyclerView.setAdapter(new SearchResultAdapter(new ArrayList<>(), this));
        base = searchBookmarksNotFoundText.getText().toString();
    }

    @Override
    public void onOpenSearchView() {
    }

    @Override
    public void onCloseSearchView() {
        if (getActivity() != null)
            getActivity().getSupportFragmentManager().popBackStack();
    }

    @Override
    public void publishResultCb(CharSequence query, List<TreeNodeInterface> filteredList) {
        //empty string
        searchBookmarksNotFoundText.setVisibility(filteredList.size() > 0 ? View.GONE : View.VISIBLE);
        searchBookmarksNotFoundText.setText(String.format(base, query));
        searchBookmarksIcon.setVisibility(filteredList.size() > 0 ? View.GONE : View.VISIBLE);

        //set result
        ((SearchResultAdapter) searchResultRecyclerView.getAdapter()).setItems(filteredList);
    }

    @Override
    public void onSearchItemClick(TreeNodeInterface node) {
        //trying close keyboard
        Utils.hideKeyboard(getContext());

        //open url
        if (!BrowserUtils.openUrl(node.getNodeContent().getDescription(), getContext()))
            showError();
    }

    private void showError() {
        Log.e(getClass().getName(), "error on open url");
    }

}
