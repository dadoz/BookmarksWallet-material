package com.application.material.bookmarkswallet.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.*;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.actionMode.EditBookmarkActionModeCallback;
import com.application.material.bookmarkswallet.app.actionMode.OnActionModeCallbacks;
import com.application.material.bookmarkswallet.app.adapter.BookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.adapter.OnMultipleSelectorCallback;
import com.application.material.bookmarkswallet.app.helpers.BookmarkActionHelper;
import com.application.material.bookmarkswallet.app.helpers.NightModeHelper;
import com.application.material.bookmarkswallet.app.manager.SearchManager.SearchManagerCallbackInterface;
import com.application.material.bookmarkswallet.app.models.SparseArrayParcelable;
import com.application.material.bookmarkswallet.app.manager.StatusManager;
import com.application.material.bookmarkswallet.app.manager.SearchManager;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.application.material.bookmarkswallet.app.views.AddFolderView;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;
import com.lib.davidelm.filetreevisitorlibrary.views.BreadCrumbsView;
import com.lib.davidelm.filetreevisitorlibrary.views.TreeNodeView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.lang.ref.WeakReference;

import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.application.material.bookmarkswallet.app.BuildConfig.KOFI_DAVE_URL;

public class BookmarkListFragment extends Fragment
        implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        SearchManagerCallbackInterface,
        AddBookmarkActivity.OnHandleBackPressed, OnMultipleSelectorCallback, OnActionModeCallbacks,
        AddFolderView.AddFolderCallbacks {
    public static final String FRAG_TAG = "LinksListFragment";
    @BindView(R.id.addBookmarkMenuFabId)
    FloatingActionsMenu addBookmarkMenuFab;
    @BindView(R.id.addBookmarkFabId)
    FloatingActionButton addBookmarkFab;
    @BindView(R.id.offerMeACoffeeFabId)
    FloatingActionButton offerMeACoffeeFab;
    @BindView(R.id.mainContainerViewId)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.emptyLinkListViewId)
    View mEmptyLinkListView;
    @BindView(R.id.emptySearchResultLayoutId)
    View emptySearchResultLayout;
    @BindView(R.id.importDefaultBookmarksButtonId)
    View importDefaultBookmarksButton;
    @BindView(R.id.treeNodeViewId)
    TreeNodeView displayNodeView;
    @BindView(R.id.breadCrumbsViewId)
    BreadCrumbsView breadCrumbsView;
    @BindView(R.id.addFolderViewId)
    AddFolderView addFolderView;

    private SearchManager searchManager;
    private BookmarkActionHelper mBookmarkActionSingleton;
    private String TAG ="BOOKmarklist";
    private Unbinder unbinder;
    private EditBookmarkActionModeCallback actionModeCallback;
    private BookmarkRecyclerViewAdapter adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBookmarkActionSingleton = BookmarkActionHelper.getInstance(new WeakReference<>(getContext()));
        searchManager = SearchManager.getInstance(new WeakReference<>(getContext()),
                Realm.getDefaultInstance(), new WeakReference<>(this));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        View mainView = inflater.inflate(R.layout.fragment_bookmark_list_layout,
                container, false);
        unbinder = ButterKnife.bind(this, mainView);
        setHasOptionsMenu(true);
        onInitView();
        return mainView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(NightModeHelper.getInstance().isNightMode() ? R.menu.menu_main_night :
                R.menu.menu_main, menu);

        MaterialSearchView searchView = ((MaterialSearchView) getView().getRootView()
                .findViewById(R.id.searchViewId));
        searchManager.initSearchView(menu, new View[] {searchView, addBookmarkMenuFab});
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null)
            unbinder.unbind();
    }

//    @Override
//    public void onStart() {
//        super.onStart();
////        updateRecyclerView();
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.importDefaultBookmarksButtonId:
                Snackbar.make(getView(), getString(R.string.import_default_bookmarks),
                        Snackbar.LENGTH_SHORT).show();
//                DefaultBookmarkImportManager.handleImportDefaultBookmarks(new WeakReference<>(getContext()),
//                        mEmptyLinkListView, mSwipeRefreshLayout, recyclerView);
                break;
            case R.id.addBookmarkFabId:
//                addBookmarkMenuFab.collapse();
                mBookmarkActionSingleton.addBookmarkAction(new WeakReference<>(this));
                break;
            case R.id.offerMeACoffeeFabId:
                addBookmarkMenuFab.collapse();
                mBookmarkActionSingleton.openLinkOnBrowser(KOFI_DAVE_URL);
                break;
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
//            ((BookmarkRecyclerViewAdapter) recyclerView.getAdapter())
//                    .updateData(RealmUtils.getResults(Realm.getDefaultInstance()));
            mSwipeRefreshLayout.setRefreshing(false);
//            recyclerView.getAdapter().notifyDataSetChanged();
        }, 2000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_folder:
                addFolderView.setVisibleAndUpdate(true);
                break;
        }
        return true;
    }

    /**
     *
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Utils.ADD_BOOKMARK_ACTIVITY_REQ_CODE) {
            //adding bookmarks
            if (data != null &&
                    data.getExtras() != null) {
                addBookmarksAction(data);
            }
        }
    }

    /**
     *
     * @param data
     */
    private void addBookmarksAction(Intent data) {
        SparseArrayParcelable searchParamsArray = ((SparseArrayParcelable) data.getExtras()
                .get("search_params_add_bookmark"));

        displayNodeView.addFile(Utils.createContentNode(searchParamsArray),
                Integer.valueOf(searchParamsArray.get(2).toString()));

    }

    /**
     * init view on recyclerView - setup adapter and other stuff
     * connected to main fragment app
     */
    private void onInitView() {
        //pull to refresh
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.indigo_600);

        //import
        importDefaultBookmarksButton.setOnClickListener(this);

        //add buton
        addBookmarkFab.setOnClickListener(this);
        offerMeACoffeeFab.setOnClickListener(this);

        //init display node view
        adapter = new BookmarkRecyclerViewAdapter(getContext(), this);
        displayNodeView.setAdapter(adapter);
        displayNodeView.setBreadCrumbsView(breadCrumbsView);
        //set action mode
        actionModeCallback = new EditBookmarkActionModeCallback(new WeakReference<>(getContext()),
                        new WeakReference<>(getActivity()),
                        new WeakReference<>(this));
        //add folder view
        addFolderView.setListener(new WeakReference<>(this));
    }

    @Override
    public void onFileNodeClickCb(View v, int position, TreeNodeInterface node) {
        mBookmarkActionSingleton.openLinkOnBrowser(node.getNodeContent().getFileUri());
    }

    @Override
    public void onFileNodeLongClickCb(View v, int position, TreeNodeInterface item) {
        //select adapter
        if (!StatusManager.getInstance().isEditMode()) {
            getActivity().startActionMode(actionModeCallback);
            StatusManager.getInstance().setEditMode();
        }

        //unselect adapter
        if (adapter != null &&
                adapter.getSelectedItemListSize() == 0) {
            StatusManager.getInstance().unsetStatus();
            actionModeCallback.forceToFinish();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        updateGridLayoutManager();
    }

    @Override
    public void updateSearchDataList(RealmResults list) {
//        ((BookmarkRecyclerViewAdapter) recyclerView.getAdapter()).updateData(list);
//        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onOpenSearchView() {
        StatusManager.getInstance().setSearchActionbarMode(true);
    }

    @Override
    public void onCloseSearchView() {
        emptySearchResultLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean handleBackPressed() {
        return false;
//        StatusManager status = StatusManager.getInstance();
//        if (status.isOnActionMenuMode() ||
//                status.isSearchActionbarMode()) {
//            status.unsetStatus();
//            addBookmarkFab.setVisibility(View.VISIBLE);
//            if (searchManager.getSearchView() != null)
//                searchManager.getSearchView().closeSearch();
//            return true;
//        }
//        return false;
    }

    @Override
    public void deleteActionModeCb() {
        StatusManager.getInstance().unsetStatus();
        //remove all selected files
        if (adapter != null) {
            //TODO clone object and return it - in the while clean obj
            displayNodeView.removeFiles(adapter.getSelectedItemIdArray());
            adapter.clearSelectedItemPosArray();
        }

        notifyToUser("delete bookmark");
    }

    @Override
    public void selectAllActionModeCb() {
        StatusManager.getInstance().setEditMode();
        if (adapter != null) {
            adapter.setSelectedAllItemPos(displayNodeView.getFiles());
            adapter.notifyDataSetChanged();
        }

        notifyToUser("select bookmark");
    }

    @Override
    public void onDestroyActionModeCb() {
        StatusManager.getInstance().unsetStatus();

//        remove all selected files
        if (adapter != null) {
            adapter.clearSelectedItemPosArray();
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * notify to user
     * @param message
     */
    private void notifyToUser(String message) {
        if (getView() != null)
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void addFolderActionCb(String name) {
        displayNodeView.addFolder(name, -1);
    }

    @Override
    public void onUpdatedVisibility(boolean isVisible) {
    }


//    @Override
//    public void onToggleRevealCb(boolean isShowing) {
//        if (openMenuItem != null) {
//            openMenuItem.setIcon(ContextCompat.getDrawable(getContext(),
//                    ActionMenuRevealHelper.getIconByShowingStatus(isShowing)));
//        }
//
//        //animate viewAnimation not object animation welll done
//        addBookmarkFab.setVisibility(isShowing ? View.GONE : View.VISIBLE);
//    }
//    /**
//     * context menu
//     */
//    @Override
//    public void hanldeExportContextMenu() {
//        ExportStrategy
//                .buildInstance(new WeakReference<>(getContext()), getView())
//                .checkAndRequestPermission();
//    }
//    @Override
//    public void hanldeSettingsContextMenu() {
//        statusManager.unsetStatus();
//        ActionbarHelper.setDefaultHomeEnambled(getActivity(), true);
//        startActivity(new Intent(getActivity(), SettingsActivity.class));
//    }
//    @Override
//    public void hanldeExportGridviewResizeMenu() {
//        expandedGridview = !expandedGridview;
//        SharedPrefHelper.getInstance(new WeakReference<>(getContext()))
//                .setValue(EXPANDED_GRIDVIEW, expandedGridview);
//
//        int count = Utils.getCardNumberInRow(getContext(), expandedGridview);
//        ((GridLayoutManager) recyclerView.getLayoutManager()).setSpanCount(count);
//    }

//    private void initRecyclerView() {
//        expandedGridview = (boolean) SharedPrefHelper.getInstance(new WeakReference<>(getContext()))
//                .getValue(EXPANDED_GRIDVIEW, false);
//        BookmarkRecyclerViewAdapter adapter =
//                new BookmarkRecyclerViewAdapter(new WeakReference<>(getContext()),
//                    new WeakReference<>(this), null);
//        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),
//                Utils.getCardNumberInRow(getContext(), expandedGridview)));
//        recyclerView.setAdapter(adapter);
//        actionMode = new EditBookmarkActionModeCallback(new WeakReference<>(getContext()), adapter);
//        registerDataObserver(adapter);
//    }

//    private void updateGridLayoutManager() {
//        if (recyclerView != null &&
//                recyclerView.getLayoutManager() != null) {
//            ((GridLayoutManager) recyclerView.getLayoutManager())
//                    .setSpanCount(Utils.getCardNumberInRow(getContext(), expandedGridview));
//        }
//
//    }

//    private void updateRecyclerView() {
//        if (recyclerView != null &&
//                    recyclerView.getAdapter() != null) {
//            ((BookmarkRecyclerViewAdapter) recyclerView.getAdapter())
//                    .setIsFaviconIsEnabled(new WeakReference<>(getContext()));
//            recyclerView.smoothScrollToPosition(0);
//            recyclerView.getAdapter().notifyDataSetChanged();
//        }
//    }

//    private void registerDataObserver(BookmarkRecyclerViewAdapter recyclerViewAdapter) {
//        //TODO leak
//        BookmarkListObserver observer = new BookmarkListObserver(new View[] {recyclerView,
//                mEmptyLinkListView, emptySearchResultLayout}, searchManager);
//        recyclerViewAdapter.registerAdapterDataObserver(observer);
//        recyclerViewAdapter.notifyDataSetChanged();
//    }


//    private void handleSelectItemByPos(int position) {
//        statusManager.setEditMode();
//        BookmarkRecyclerViewAdapter adapter = ((BookmarkRecyclerViewAdapter) recyclerView.getAdapter());
//        adapter.setSelectedItemPos(position);
//        recyclerView.getAdapter().notifyItemChanged(position);
//        actionMode.toggleVisibilityIconMenu(adapter.getSelectedItemListSize() <= 1);
//        actionMode.setSelectedItemCount(adapter.getSelectedItemListSize());
//        if (((BookmarkRecyclerViewAdapter) recyclerView.getAdapter()).isEmptySelectedPosArray()) {
//            actionMode.forceToFinish();
//        }
//    }

}