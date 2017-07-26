package com.application.material.bookmarkswallet.app.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.application.material.bookmarkswallet.app.BaseActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.actionMode.OnActionModeCallbacks;
import com.application.material.bookmarkswallet.app.adapter.OnMultipleSelectorCallback;
import com.application.material.bookmarkswallet.app.manager.SearchManager.SearchManagerCallbackInterface;
import com.application.material.bookmarkswallet.app.presenter.BookmarkListPresenter;
import com.application.material.bookmarkswallet.app.presenter.BookmarkListView;
import com.application.material.bookmarkswallet.app.utlis.BrowserUtils;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.application.material.bookmarkswallet.app.views.AddFolderView;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;
import com.lib.davidelm.filetreevisitorlibrary.views.BreadCrumbsView;
import com.lib.davidelm.filetreevisitorlibrary.views.OnFolderNavigationCallbacks;
import com.lib.davidelm.filetreevisitorlibrary.views.TreeNodeView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.application.material.bookmarkswallet.app.BuildConfig.KOFI_DAVE_URL;

public class BookmarkListFragment extends Fragment
        implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, SearchManagerCallbackInterface,
        BaseActivity.OnBackPressedHandlerInterface, OnMultipleSelectorCallback, OnActionModeCallbacks,
        AddFolderView.AddFolderCallbacks, OnFolderNavigationCallbacks, BookmarkListView {
    public static final String FRAG_TAG = "LinksListFragment";
    @BindView(R.id.addBookmarkMenuFabId)
    FloatingActionsMenu addBookmarkMenuFab;
    @BindView(R.id.addBookmarkFabId)
    FloatingActionButton addBookmarkFab;
    @BindView(R.id.offerMeACoffeeFabId)
    FloatingActionButton offerMeACoffeeFab;
    @BindView(R.id.mainContainerViewId)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.treeNodeViewId)
    TreeNodeView treeNodeView;
    @BindView(R.id.breadCrumbsViewId)
    BreadCrumbsView breadCrumbsView;
    @BindView(R.id.addFolderViewId)
    AddFolderView addFolderView;

    private Unbinder unbinder;
    private BookmarkListPresenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        View mainView = inflater.inflate(R.layout.fragment_bookmark_list_layout, container, false);
        unbinder = ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new BookmarkListPresenter(this, getActivity(), treeNodeView, breadCrumbsView,
                this, this, this);
        presenter.init();
        onInitView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        presenter.initSearchView(menu, ((MaterialSearchView) getView().getRootView()
                .findViewById(R.id.searchViewId)));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.initSearchManager();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null)
            unbinder.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                notifyToUser("about message");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        //init view -> sync
        breadCrumbsView.setRootNode();
        treeNodeView.initOnRootNode();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     *
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Utils.ADD_BOOKMARK_ACTIVITY_REQ_CODE) {
            if (data != null && data.getExtras() != null) {
                presenter.addBookmarksAction(data);
            }
        }
    }

    /**
     * init view on recyclerView - setup adapter and other stuff
     * connected to main fragment app
     */
    private void onInitView() {
        //option menu
        setHasOptionsMenu(true);

        //pull to refresh
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.indigo_600);

        //add buton
        addBookmarkFab.setOnClickListener(this);
        offerMeACoffeeFab.setOnClickListener(this);

        treeNodeView.addFolderNavigationCbListener(this);

        //add folder view
        addFolderView.setListener(new WeakReference<>(this));
    }

    @Override
    public void onFileNodeClickCb(View v, int position, TreeNodeInterface node) {
        BrowserUtils.openUrl(node.getNodeContent().getDescription(), getContext());
    }

    @Override
    public void onFileNodeLongClickCb(View v, int position, TreeNodeInterface item) {
        presenter.onFileNodeLongClick();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        updateGridLayoutManager();
    }

    @Override
    public boolean handleBackPressed() {
        return treeNodeView.onBackPressed();
    }

    @Override
    public void deleteActionModeCb() {
        presenter.deleteActionMode();
        notifyToUser("delete bookmark");
    }

    @Override
    public void selectAllActionModeCb() {
        presenter.selectAllActionMode();
        notifyToUser("select bookmark");
    }

    @Override
    public void onDestroyActionModeCb() {
        presenter.onDestroyActionMode();
    }

    @Override
    public void addFolderActionCb(String name) {
        treeNodeView.addFolder(name, -1);
    }

    @Override
    public void onAddFolderCollapsed(View bottomSheet) {
        addBookmarkMenuFab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAddFolderExpanded(View bottomSheet) {
        addBookmarkMenuFab.setVisibility(View.GONE);
    }

    @Override
    public void onOpenSearchView() {
        presenter.openSearchView(getActivity().getSupportFragmentManager());
    }

    @Override
    public void onCloseSearchView() {
        getActivity().getSupportFragmentManager().popBackStack();
        addBookmarkMenuFab.collapse();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.importDefaultBookmarksButtonId:
                notifyToUser(getString(R.string.import_default_bookmarks));
//                DefaultBookmarkImportManager.handleImportDefaultBookmarks(new WeakReference<>(getContext()),
//                        mEmptyLinkListView, mSwipeRefreshLayout, recyclerView);
                break;
            case R.id.addBookmarkFabId:
                addBookmarkMenuFab.collapseImmediately();
                presenter.addBookmark(this);
                break;
            case R.id.offerMeACoffeeFabId:
                addBookmarkMenuFab.collapse();
                BrowserUtils.openUrl(KOFI_DAVE_URL, getContext());
                break;
        }
    }

    /**
     * notify to user
     * @param message
     */
    private void notifyToUser(String message) {
        if (getView() != null) {
            Snackbar snackbar = Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey_50));
            snackbar.show();
        }
    }

    @Override
    public void onFolderNodeClickCb(int position, TreeNodeInterface node) {
        addFolderView.setCollapsed();
        addBookmarkMenuFab.setVisibility(!node.isRoot() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onFolderNodeLongClickCb(int position, TreeNodeInterface item) {
    }
}