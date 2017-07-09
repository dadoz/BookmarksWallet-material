package com.application.material.bookmarkswallet.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.application.MaterialBookmarkApplication;
import com.application.material.bookmarkswallet.app.helpers.RetrieveIconHelper;
import com.application.material.bookmarkswallet.app.manager.StatusManager;
import com.application.material.bookmarkswallet.app.models.SparseArrayParcelable;
import com.application.material.bookmarkswallet.app.BaseActivity;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.application.material.bookmarkswallet.app.views.AddBookmarkResultLayout;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import icepick.Icepick;

import static com.application.material.bookmarkswallet.app.utlis.Utils.ADD_BOOKMARK_ACTIVITY_REQ_CODE;
import static com.application.material.bookmarkswallet.app.utlis.Utils.SEARCH_PARAMS;

public class AddBookmarkResultFragment extends Fragment implements
        View.OnClickListener,
        RetrieveIconHelper.OnRetrieveIconInterface, BaseActivity.OnBackPressedHandlerInterface {
    public static final String FRAG_TAG = "AddBookmarkFragmentTAG";
    private static final String TAG = "AddBookmarkFragment";
    @BindView(R.id.addBookmarkResultViewId)
    AddBookmarkResultLayout addBookmarkResultView;
    @BindView(R.id.addBookmarkRefreshLayoutId)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.addBookmarkDoneButtonId)
    Button addBookmarkDoneButton;

    private StatusManager statusManager;
    private RetrieveIconHelper retrieveIconHelper;
    private SparseArrayParcelable<String> searchParamsArray; //TODO?? LEAK
    private Unbinder unbinder;

    @Override
    public void onSaveInstanceState(Bundle savedInstance) {
        super.onSaveInstanceState(savedInstance);
        Icepick.saveInstanceState(this, savedInstance);
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        Icepick.restoreInstanceState(this, savedInstance);
        statusManager = StatusManager.getInstance();
        retrieveIconHelper = RetrieveIconHelper
                .getInstance(new WeakReference<>(this));
        searchParamsArray = ((MaterialBookmarkApplication) getActivity().getApplication())
                .getSearchParamsArray();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        View mainView = inflater.inflate(R.layout.fragment_add_bookmark_result_layout, container, false);
        unbinder = ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInitView(savedInstanceState);
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
        refreshLayout.setRefreshing(true);
        statusManager.setOnResultMode();
        Utils.hideKeyboard(getActivity());
        addBookmarkResultView.initView(searchParamsArray);
        addBookmarkDoneButton.setOnClickListener(this);
        retrieveIcon();
        retrieveTitle();

        if (savedInstanceState != null) {
            initResultViewOnConfigChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBookmarkDoneButtonId:
                retrieveIconHelper.unsubscribe();
                addBookmark();
                break;
        }
    }

    /**
     *
     */
    private void initResultViewOnConfigChanged() {
        if (statusManager.isOnResultMode()) {
            addBookmarkResultView.initView(searchParamsArray);
        }
    }

    /**
     * retrieve icon from gallery or url
     */
    private void retrieveIcon() {
        String url = searchParamsArray.get(0);
        refreshLayout.setRefreshing(true);
        retrieveIconHelper.retrieveIcon(url);
    }

    /**
     *
     */
    private void retrieveTitle() {
        String title = searchParamsArray.get(1);

        if (title != null &&
                title.compareTo("") == 0) {
            retrieveIconHelper.retrieveTitle(searchParamsArray.get(0));
        }
    }

    /**
     * add bookmark on orm db
     */
    private void addBookmark() {
        if (getActivity() != null) {
            Intent intent = new Intent();
            intent.putExtra(SEARCH_PARAMS, searchParamsArray);
            getActivity().setResult(ADD_BOOKMARK_ACTIVITY_REQ_CODE, intent);
            getActivity().finish();
        }
    }

    /**
     *
     */
    @Override
    public void onRetrieveIconSuccess(final String url) {
        if (getActivity() != null)
            getActivity().runOnUiThread(() -> {
                refreshLayout.setRefreshing(false);
                addBookmarkResultView.setIconByUrl(url);
                searchParamsArray.put(2, url);
            });
    }

    @Override
    public void onRetrieveTitleSuccess(final String title) {
        searchParamsArray.put(1, title.equals("") ? getString(R.string.no_title) : title);
        if (getActivity() != null)
            getActivity().runOnUiThread(() -> addBookmarkResultView.setTitle(title));
    }

    @Override
    public void onRetrieveIconFailure(final String error) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
//                addBookmarkResultView.setTitle(getString(R.string.no_title));
                refreshLayout.setRefreshing(false);
                Utils.buildSnackbar(error, getView(), getActivity().getApplicationContext(), true).show();
            });
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
