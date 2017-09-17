package com.application.material.bookmarkswallet.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.application.material.bookmarkswallet.app.MainActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.application.MaterialBookmarkApplication;
import com.application.material.bookmarkswallet.app.manager.SearchManager;
import com.application.material.bookmarkswallet.app.manager.StatusManager;
import com.application.material.bookmarkswallet.app.models.SparseArrayParcelable;
import com.application.material.bookmarkswallet.app.utlis.ConnectionUtils;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.application.material.bookmarkswallet.app.views.AddBookmarkSearchLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import icepick.Icepick;
import icepick.State;

public class AddBookmarkSearchFragment extends Fragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, AddBookmarkSearchLayout.OnEditorActionListenerCallbacks {
    public final static String FRAG_TAG = "AddBookmarkSearchFragment";
    @BindView(R.id.addBookmarkSearchLayoutId)
    AddBookmarkSearchLayout addBookmarkSearchLayout;
    @BindView(R.id.addBookmarkRefreshLayoutId)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.addBookmarkSearchButtonId)
    View addBookmarkSearchButton;
    private Unbinder unbinder;
    @State
    int folderNodeId = -1;

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
        unbinder = ButterKnife.bind(this, mainView);
        setHasOptionsMenu(true);
        initView();
        return mainView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null)
            unbinder.unbind();
    }

    /**
     *
     */
    public void initView() {
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.indigo_600, R.color.yellow_400);
        addBookmarkSearchLayout.setOnEditorActionLst(this);
        addBookmarkSearchButton.setOnClickListener(this);
        addBookmarkSearchLayout.setSelectedFolder("Hey bla", "Yeah");
        handleArguments();
    }

    /**
     *
     */
    private void handleArguments() {
        if (getArguments() != null) {
            if (getArguments().getString(MainActivity.SHARED_URL_EXTRA_KEY) != null) {
                addBookmarkSearchLayout.setUrl(getArguments()
                        .getString(MainActivity.SHARED_URL_EXTRA_KEY));
            }
            if (getArguments().getString(MainActivity.FOLDER_EXTRA_KEY) != null) {
                //do smthing with folderId
                folderNodeId = Integer.parseInt(getArguments().getString(MainActivity.FOLDER_EXTRA_KEY));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBookmarkSearchButtonId:
                searchAction();
                break;
        }
    }


    /**
     * search action triggered by view
     */
    public void searchAction() {
        SparseArrayParcelable<String> searchParamsArray = addBookmarkSearchLayout.getSearchParamsArray(folderNodeId);
        boolean isConnected = ConnectionUtils.isConnected(getContext());

        //handle error
        if (!isConnected ||
                !SearchManager.isSearchValid(searchParamsArray.get(0))) {
            onSearchError(isConnected);
            return;
        }

        //handle success
        onSearchSuccess(searchParamsArray);
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
    private void onSearchSuccess(SparseArrayParcelable<String> searchParamsArray) {
        //set mode
        StatusManager.getInstance().setOnResultMode();

        //set params on application
        ((MaterialBookmarkApplication) getActivity().getApplication())
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

    @Override
    public void onEditorActionCb(TextView textView) {
        searchAction();
    }
}
