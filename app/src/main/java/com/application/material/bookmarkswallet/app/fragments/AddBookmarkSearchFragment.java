package com.application.material.bookmarkswallet.app.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.material.bookmarkswallet.app.MainActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.application.BookmarksWalletApplication;
import com.application.material.bookmarkswallet.app.manager.ClipboardManager;
import com.application.material.bookmarkswallet.app.manager.SearchManager;
import com.application.material.bookmarkswallet.app.manager.StatusManager;
import com.application.material.bookmarkswallet.app.utlis.ConnectionUtils;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.application.material.bookmarkswallet.app.views.AddBookmarkSearchLayout;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;

public class AddBookmarkSearchFragment extends Fragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, TextView.OnEditorActionListener {
    public final static String FRAG_TAG = "AddBookmarkSearchFragment";
    @BindView(R.id.addBookmarkSearchLayoutId)
    AddBookmarkSearchLayout addBookmarkSearchLayout;
    @BindView(R.id.pasteClipboardFabId)
    FloatingActionButton pasteClipboardFab;
    @BindView(R.id.addBookmarkRefreshLayoutId)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.addBookmarkSearchButtonId)
    View addBookmarkSearchButton;

    @Override
    public void onSaveInstanceState(Bundle savedInstance) {
        super.onSaveInstanceState(savedInstance);
        Icepick.saveInstanceState(this, savedInstance);
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        Icepick.restoreInstanceState(this, savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        View mainView = inflater.inflate(R.layout.fragment_add_bookmark_layout, container, false);
        ButterKnife.bind(this, mainView);
        setHasOptionsMenu(true);
        initView(savedInstance);
        return mainView;
    }

    /**
     *
     * @param savedInstanceState
     */
    public void initView(Bundle savedInstanceState) {
        addBookmarkSearchLayout.initView(savedInstanceState);
        pasteClipboardFab.setOnClickListener(this);
        addBookmarkSearchLayout.getUrlEditTextView().setOnEditorActionListener(this);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.indigo_600, R.color.yellow_400);
        addBookmarkSearchButton.setOnClickListener(this);
        if (getArguments() != null) {
            handleArguments();
        }
    }

    /**
     *
     */
    private void handleArguments() {
        if (getArguments().getString(MainActivity.SHARED_URL_EXTRA_KEY) != null) {
            String sharedUrl = getArguments().getString(MainActivity.SHARED_URL_EXTRA_KEY);
            addBookmarkSearchLayout.getUrlEditTextView().setText(sharedUrl);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBookmarkSearchButtonId:
                searchAction();
                break;
            case R.id.pasteClipboardFabId:
                String url = ClipboardManager.getInstance(new WeakReference<>(getContext()))
                        .getTextFromClipboard();
                addBookmarkSearchLayout.getUrlEditTextView().setText(url);
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        searchAction();
        return true;
    }


    /**
     * search action triggerd by view
     */
    public void searchAction() {
        boolean isConnected = ConnectionUtils.isConnected(getContext());
        SparseArray<String> searchParamsArray = addBookmarkSearchLayout.getSearchParamsArray();
        if (isConnected &&
                SearchManager.search(searchParamsArray.get(0))) {
            onSearchSuccess(searchParamsArray);
            return;
        }

        onSearchError(isConnected);
    }

    /**
     * search error ui
     * @param isConnected
     */
    private void onSearchError(boolean isConnected) {
        if (isConnected) {
            addBookmarkSearchLayout.showErrorOnUrlEditText(true);
        }
        Utils.buildSnackbar(getString(isConnected ?
                R.string.no_item_found : R.string.no_network_connection), getView(), getContext(), true)
                .show();
    }

    /**
     * search success ui
     * @param searchParamsArray
     */
    private void onSearchSuccess(SparseArray<String> searchParamsArray) {
        StatusManager.getInstance().setOnResultMode();
        ((BookmarksWalletApplication) getActivity().getApplication())
                .setSearchParamsArray(searchParamsArray);

        //change frag
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainerFrameLayoutId,
                        new AddBookmarkResultFragment(), AddBookmarkResultFragment.FRAG_TAG)
                .addToBackStack(AddBookmarkResultFragment.FRAG_TAG)
                .commit();
    }

    @Override
    public void onRefresh() {
        refreshLayout.setRefreshing(false);
    }
}
