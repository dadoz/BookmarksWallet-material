package com.application.material.bookmarkswallet.app.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.api.network.BookmarksService;
import com.application.material.bookmarkswallet.app.application.BookmarksWalletApplication;
import com.application.material.bookmarkswallet.app.manager.StatusManager;
import com.application.material.bookmarkswallet.app.presenter.AddBookmarkResultPresenter;
import com.application.material.bookmarkswallet.app.presenter.AddBookmarkResultView;
import com.application.material.bookmarkswallet.app.utlis.RealmUtils;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.application.material.bookmarkswallet.app.views.AddBookmarkResultLayout;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import icepick.Icepick;
import io.realm.Realm;

public class AddBookmarkResultFragment extends Fragment implements
        AddBookmarkActivity.OnHandleBackPressed, AddBookmarkResultView {
    public static final String FRAG_TAG = "AddBookmarkFragmentTAG";
    @BindView(R.id.addBookmarkResultViewId)
    AddBookmarkResultLayout addBookmarkResultView;
    @BindView(R.id.addBookmarkRefreshLayoutId)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.addBookmarkDoneButtonId)
    Button addBookmarkDoneButton;

    private StatusManager statusManager;
    private SparseArray<String> searchParamsArray;
    private Unbinder unbinder;
    private AddBookmarkResultPresenter presenter;

    @Override
    public void onSaveInstanceState(Bundle savedInstance) {
        super.onSaveInstanceState(savedInstance);
        Icepick.saveInstanceState(this, savedInstance);
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        searchParamsArray = ((BookmarksWalletApplication) getActivity().getApplication())
                .getSearchParamsArray();
        Icepick.restoreInstanceState(this, savedInstance);
        statusManager = StatusManager.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        View mainView = inflater.inflate(R.layout.fragment_add_bookmark_result_layout, container, false);
        unbinder = ButterKnife.bind(this, mainView);
        onInitView(savedInstance);
        return mainView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     *
     * @param savedInstanceState
     */
    private void onInitView(Bundle savedInstanceState) {
        //handle retrieve icon and title
        BookmarksService bookmarksService = new BookmarksService();
        presenter = new AddBookmarkResultPresenter(this, bookmarksService.getService(),
                bookmarksService.getTagsByUrlService());
        presenter.retrieveIconAndTitle(searchParamsArray.get(0), searchParamsArray.get(1));

        Utils.hideKeyboard(getActivity());
//        refreshLayout.setRefreshing(true);
        statusManager.setOnResultMode();
        addBookmarkResultView.initView(searchParamsArray);
        addBookmarkDoneButton.setOnClickListener(v -> {
//                retrieveIconHelper.unsubscribe();
                addBookmark();
        });

        if (savedInstanceState != null &&
                statusManager.isOnResultMode()) {
            addBookmarkResultView.initView(searchParamsArray);
        }
    }


    /**
     *
     * @param url
     */
    public void onRetrieveIconSuccess(final String url) {
        refreshLayout.setRefreshing(false);
        addBookmarkResultView.setIconByUrl(url);
    }

    /**
     *
     * @param title
     */
    public void onRetrieveTitleSuccess(final String title) {
        refreshLayout.setRefreshing(false);
        searchParamsArray.put(1, title);
        addBookmarkResultView.setTitle(title);
    }

    /**
     *
     * @param error
     */
    public void onRetrieveTitleFailure(final String error) {
        refreshLayout.setRefreshing(false);
    }

    /**
     *
     * @param error
     */
    public void onRetrieveIconFailure(final String error) {
        addBookmarkResultView.setTitle(getString(R.string.no_title));
        refreshLayout.setRefreshing(false);
        Utils.buildSnackbar(error, getView(), getContext(), true).show();
    }

    /**
     * add bookmark on orm db
     */
    private void addBookmark() {
        String url = searchParamsArray.get(0);
        String title = searchParamsArray.get(1);

        RealmUtils.addItemOnRealm(Realm.getDefaultInstance(), title, null,
                Utils.convertBitmapToByteArray(addBookmarkResultView.getIconBitmap()), url);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    public boolean handleBackPressed() {
        if (statusManager.isOnResultMode()) {
            statusManager.setOnSearchMode();
            getActivity().getSupportFragmentManager().popBackStack();
            return true;
        }
        return false;
    }
}
