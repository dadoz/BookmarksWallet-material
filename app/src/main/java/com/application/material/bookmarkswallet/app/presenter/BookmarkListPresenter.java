package com.application.material.bookmarkswallet.app.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Menu;

import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.actionMode.EditBookmarkActionModeCallback;
import com.application.material.bookmarkswallet.app.actionMode.OnActionModeCallbacks;
import com.application.material.bookmarkswallet.app.adapter.BookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.adapter.OnMultipleSelectorCallback;
import com.application.material.bookmarkswallet.app.fragments.SearchFragment;
import com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper;
import com.application.material.bookmarkswallet.app.manager.SearchManager;
import com.application.material.bookmarkswallet.app.manager.SearchManager.SearchManagerCallbackInterface;
import com.application.material.bookmarkswallet.app.manager.StatusManager;
import com.application.material.bookmarkswallet.app.models.SparseArrayParcelable;
import com.application.material.bookmarkswallet.app.utlis.ActivityUtils;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.lib.davidelm.filetreevisitorlibrary.views.BreadCrumbsView;
import com.lib.davidelm.filetreevisitorlibrary.views.TreeNodeView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.lang.ref.WeakReference;

import static com.application.material.bookmarkswallet.app.BaseActivity.FOLDER_EXTRA_KEY;
import static com.application.material.bookmarkswallet.app.BaseActivity.FOLDER_NAME_EXTRA_KEY;
import static com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper.SharedPrefKeysEnum.NO_FAVICON_MODE;

/**
 * Created by davide on 04/07/2017.
 */

public class BookmarkListPresenter {
    private final WeakReference viewRef;
    private final boolean noFaviconMode;
    private EditBookmarkActionModeCallback actionModeCallback;
    private BookmarkRecyclerViewAdapter adapter;
    private SearchManager searchManager;

    private WeakReference<Activity> activity;
    private WeakReference<Context> context;
    private TreeNodeView treeNodeView;
    private BreadCrumbsView breadCrumbsView;
    private WeakReference<OnMultipleSelectorCallback> multipleSelectorCallback;
    private WeakReference<OnActionModeCallbacks> actionModeCallbacks;
    private WeakReference<SearchManagerCallbackInterface> searchManagerCallback;

    public BookmarkListPresenter(BookmarkListView view, Activity activity, TreeNodeView treeNodeView,
                                 BreadCrumbsView breadCrumbsView,
                                 OnMultipleSelectorCallback multipleSelectorCallback,
                                 SearchManagerCallbackInterface searchManagerCallback,
                                 OnActionModeCallbacks actionModeCallbacks) {
        this.viewRef = new WeakReference<>(view);
        this.context = new WeakReference<>(activity.getApplicationContext());
        this.activity = new WeakReference<>(activity);
        this.treeNodeView = treeNodeView;
        this.breadCrumbsView = breadCrumbsView;
        this.multipleSelectorCallback = new WeakReference<>(multipleSelectorCallback);
        this.actionModeCallbacks = new WeakReference<>(actionModeCallbacks);
        this.searchManagerCallback = new WeakReference<>(searchManagerCallback);

        noFaviconMode = (boolean) SharedPrefHelper.getInstance(context.get()).getValue(NO_FAVICON_MODE,
                false);
    }

    /**
     * init
     */
    public void init() {
        //init display node view
        adapter = new BookmarkRecyclerViewAdapter(context.get(), multipleSelectorCallback.get(),
                noFaviconMode);
        treeNodeView.setAdapter(adapter);
        treeNodeView.setBreadCrumbsView(breadCrumbsView);

        //set action mode
        actionModeCallback = new EditBookmarkActionModeCallback(activity.get(), actionModeCallbacks.get());

        //search manager
        searchManager = SearchManager.getInstance();
//        importDefaultBookmarksButton.setOnClickListener(this);
    }

    /**
     * on file node long click
     */
    public void onFileNodeLongClick() {
        //select adapter
        if (!StatusManager.getInstance().isEditMode()) {
            activity.get().startActionMode(actionModeCallback);
            StatusManager.getInstance().setEditMode();
        }

        //unselect adapter
        if (adapter != null &&
                adapter.getSelectedItemListSize() == 0) {
            StatusManager.getInstance().unsetStatus();
            actionModeCallback.forceToFinish();
        }
    }

    /**
     *
     */
    public void deleteActionMode() {
        StatusManager.getInstance().unsetStatus();

        //remove all selected files
        if (adapter != null) {
            //TODO clone object and return it - in the while clean obj
            treeNodeView.removeFiles(adapter.getSelectedItemIdArray());
            adapter.clearSelectedItemPosArray();
        }
    }

    /**
     *
     */
    public void selectAllActionMode() {
        StatusManager.getInstance().setEditMode();

        //selecte all action mode
        if (adapter != null) {
            adapter.setSelectedAllItemPos(treeNodeView.getFiles());
            adapter.notifyDataSetChanged();
        }
    }

    /**
     *
     */
    public void onDestroyActionMode() {
        StatusManager.getInstance().unsetStatus();

//        remove all selected files
        if (adapter != null) {
            adapter.clearSelectedItemPosArray();
            adapter.notifyDataSetChanged();
        }

    }

    /**
     *
     * @param data
     */
    public void addBookmarksAction(Intent data) {
        SparseArrayParcelable searchParamsArray = ((SparseArrayParcelable) data.getExtras()
                .get(Utils.SEARCH_PARAMS));

        treeNodeView.addFile(Utils.createContentNode(searchParamsArray),
                Integer.valueOf(searchParamsArray.get(3).toString()));
    }

    /**
     *
     */
    public void addBookmark(Fragment frag, String[] params) {
        Intent intent = new Intent(context.get(), AddBookmarkActivity.class);
        intent.putExtra(FOLDER_EXTRA_KEY, params[0]);
        intent.putExtra(FOLDER_NAME_EXTRA_KEY, params[1]);
        frag.startActivityForResult(intent, Utils.ADD_BOOKMARK_ACTIVITY_REQ_CODE);
    }

    /**
     *
     */
    public void openSearchView(FragmentManager fragmentManager) {
        StatusManager.getInstance().setSearchActionbarMode(true);
        ActivityUtils.onChangeFragment(fragmentManager, new SearchFragment(),
                SearchFragment.FRAG_TAG);

    }


    /**
     * search view
     * @param menu
     */
    public void initSearchView(Menu menu, MaterialSearchView searchView) {
        searchManager.initSearchView(menu, searchView);
        setSearchListener();
    }

    /**
     * 
     */
    public void initSearchManager() {
        if (searchManager != null &&
                searchManager.getSearchView() != null) {
            setSearchListener();
        }
    }

    /**
     *
     */
    private void setSearchListener() {
        searchManager.getSearchView().closeSearch();
        searchManager.setListener(searchManagerCallback.get());
    }


    //    @Override
//    public void onToggleRevealCb(boolean isShowing) {
//        if (openMenuItem != null) {
//            openMenuItem.setIcon(ContextCompat.getDrawable(context.get(),
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
//                .buildInstance(new WeakReference<>(context.get()), getView())
//                .checkAndRequestPermission();
//    }
//    @Override
//    public void hanldeSettingsContextMenu() {
//        statusManager.unsetStatus();
//        ActionbarHelper.setDefaultHomeEnambled(activity.get(), true);
//        startActivity(new Intent(activity.get(), SettingsActivity.class));
//    }
//    @Override
//    public void hanldeExportGridviewResizeMenu() {
//        expandedGridview = !expandedGridview;
//        SharedPrefHelper.getInstance(new WeakReference<>(context.get()))
//                .setValue(EXPANDED_GRIDVIEW, expandedGridview);
//
//        int count = Utils.getCardNumberInRow(context.get(), expandedGridview);
//        ((GridLayoutManager) recyclerView.getLayoutManager()).setSpanCount(count);
//    }

//    private void initRecyclerView() {
//        expandedGridview = (boolean) SharedPrefHelper.getInstance(new WeakReference<>(context.get()))
//                .getValue(EXPANDED_GRIDVIEW, false);
//        BookmarkRecyclerViewAdapter adapter =
//                new BookmarkRecyclerViewAdapter(new WeakReference<>(context.get()),
//                    new WeakReference<>(this), null);
//        recyclerView.setLayoutManager(new GridLayoutManager(activity.get(),
//                Utils.getCardNumberInRow(context.get(), expandedGridview)));
//        recyclerView.setAdapter(adapter);
//        actionMode = new EditBookmarkActionModeCallback(new WeakReference<>(context.get()), adapter);
//        registerDataObserver(adapter);
//    }

//    private void updateGridLayoutManager() {
//        if (recyclerView != null &&
//                recyclerView.getLayoutManager() != null) {
//            ((GridLayoutManager) recyclerView.getLayoutManager())
//                    .setSpanCount(Utils.getCardNumberInRow(context.get(), expandedGridview));
//        }
//
//    }

//    private void updateRecyclerView() {
//        if (recyclerView != null &&
//                    recyclerView.getAdapter() != null) {
//            ((BookmarkRecyclerViewAdapter) recyclerView.getAdapter())
//                    .setIsFaviconIsEnabled(new WeakReference<>(context.get()));
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
