package com.application.material.bookmarkswallet.app.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.SettingsActivity;
import com.application.material.bookmarkswallet.app.actionMode.EditBookmarkActionModeCallback;
import com.application.material.bookmarkswallet.app.adapter.BookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.helpers.ActionbarHelper;
import com.application.material.bookmarkswallet.app.helpers.BookmarkActionHelper;
import com.application.material.bookmarkswallet.app.helpers.NightModeHelper;
import com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper;
import com.application.material.bookmarkswallet.app.manager.SearchManager.SearchManagerCallbackInterface;
import com.application.material.bookmarkswallet.app.helpers.ActionMenuRevealHelper;
import com.application.material.bookmarkswallet.app.strategies.ExportStrategy;
import com.application.material.bookmarkswallet.app.manager.StatusManager;
import com.application.material.bookmarkswallet.app.manager.SearchManager;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.utlis.RealmUtils;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.application.material.bookmarkswallet.app.observer.BookmarkListObserver;
import com.application.material.bookmarkswallet.app.views.ContextRevealMenuView;
import com.flurry.android.FlurryAgent;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper.SharedPrefKeysEnum.EXPANDED_GRIDVIEW;

//TODO refactor it
public class BookmarkListFragment extends Fragment
        implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        MenuItemCompat.OnActionExpandListener,
        BookmarkRecyclerViewAdapter.OnActionListenerInterface,
        SearchManagerCallbackInterface,
        AddBookmarkActivity.OnHandleBackPressed, ActionMenuRevealHelper.ActionMenuRevealCallbacks {
    public static final String FRAG_TAG = "LinksListFragment";
    private static final String DEFAULT_BOOKMARKS_FILE = "default_bookmarks.json";
    @BindView(R.id.addBookmarkFabId)
    FloatingActionButton addNewFab;
    @BindView(R.id.mainContainerViewId)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.bookmarkRecyclerViewId)
    RecyclerView recyclerView;
    @BindView(R.id.emptyLinkListViewId)
    View mEmptyLinkListView;
    @BindView(R.id.emptySearchResultLayoutId)
    View emptySearchResultLayout;
    @BindView(R.id.importDefaultBookmarksButtonId)
    View importDefaultBookmarksButton;
    @BindView(R.id.optionMenuContainerRevealLayoutId)
    ContextRevealMenuView optionMenuContainerRevealLayout;
    @BindView(R.id.fragmentBookmarkListMainFrameLayoutId)
    View fragmentBookmarkListMainFrameLayout;

    private SearchManager searchManager;
    private ActionbarHelper mActionbarHelper;
    private BookmarkActionHelper mBookmarkActionSingleton;
    private View mainView;
    private StatusManager statusHelper;
    private EditBookmarkActionModeCallback actionMode;
    private boolean expandedGridview;
    private MenuItem openMenuItem;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initSingletonInstances();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        mainView = inflater.inflate(R.layout.fragment_bookmark_list_layout,
                container, false);
        ButterKnife.bind(this, mainView);
        setHasOptionsMenu(true);
        onInitView();
        return mainView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        int layout = NightModeHelper.getInstance().isNightMode() ? R.menu.menu_main_night :
                R.menu.menu_main;
        inflater.inflate(layout, menu);

        MaterialSearchView searchView = ((MaterialSearchView) getView().getRootView().findViewById(R.id.searchViewId));
        searchManager.initSearchView(menu, new View[] {searchView, addNewFab});
        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        searchManager.handleMenuItemActionExpandLayout(new View[] {addNewFab});
        statusHelper.setSearchMode(true);
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        searchManager.handleMenuItemActionCollapsedLayout(new View []{addNewFab, emptySearchResultLayout});
        statusHelper.unsetStatus();
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        Log.e("TAG", "hey");
        switch (v.getId()) {
            case R.id.importDefaultBookmarksButtonId:
                Snackbar.make(mainView, getString(R.string.import_default_bookmarks),
                        Snackbar.LENGTH_SHORT).show();
                try {
                    handleImportDefaultBookmarks();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.addBookmarkFabId:
                mBookmarkActionSingleton.addBookmarkAction(new WeakReference<Fragment>(this));
                break;
        }
    }

    /**
     *
     * @throws IOException
     * @throws JSONException
     */
    private void handleImportDefaultBookmarks() throws IOException, JSONException {
        //update interface
        mEmptyLinkListView.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(true);

        //parse data helper class
        String json = Utils.readAssetsToString(getActivity().getAssets(), DEFAULT_BOOKMARKS_FILE);
        JSONArray bookmarksArray = new JSONArray(json);
        for (int i = 0; i < bookmarksArray.length(); i++) {
            JSONObject bookmark = bookmarksArray.getJSONObject(i);
            RealmUtils.addItemOnRealm(Realm.getDefaultInstance(), bookmark.getString("title"),
                    bookmark.getString("iconUrl"), null, bookmark.getString("url"));
        }

        //update interface
        mSwipeRefreshLayout.setRefreshing(false);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((BookmarkRecyclerViewAdapter) recyclerView.getAdapter())
                        .updateData(RealmUtils.getResults(Realm.getDefaultInstance()));
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_terms_and_licences:
                handleTermsAndLicences();
                return true;
            case R.id.action_open_menu:
                boolean isShowing = optionMenuContainerRevealLayout.getVisibility() == View.INVISIBLE;
                openMenuItem = item;
                optionMenuContainerRevealLayout
                        .toggleRevealActionMenu(isShowing);
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
            initSingletonInstances();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyDataChanged();
                    updateRecyclerView();
                }
            }, 500);
        }
    }

    /**
     * notify data changed
     */
    public void notifyDataChanged() {
        recyclerView.getAdapter().notifyDataSetChanged(); //TODO mv to inserted 0 only on insertion
    }

    /**
     * init view on recyclerView - setup adapter and other stuff
     * connected to main fragment app
     */
    private void onInitView() {
        handleEmptyView();
        initPullToRefresh();
        initRecyclerView();
        addNewFab.setOnClickListener(this);

        optionMenuContainerRevealLayout.initActionMenu(expandedGridview,
                new WeakReference<ActionMenuRevealHelper.ActionMenuRevealCallbacks>(this));
    }

    /**
     * init view on recyclerView - setup adapter and other stuff
     * connected to main fragment app
     */
    private void initRecyclerView() {
        expandedGridview = (boolean) SharedPrefHelper.getInstance(new WeakReference<>(getContext()))
                .getValue(EXPANDED_GRIDVIEW, false);
        BookmarkRecyclerViewAdapter adapter =
                new BookmarkRecyclerViewAdapter(new WeakReference<>(getContext()),
                    new WeakReference<BookmarkRecyclerViewAdapter.OnActionListenerInterface>(this));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),
                Utils.getCardNumberInRow(getContext(), expandedGridview)));
        recyclerView.setAdapter(adapter);
        actionMode = new EditBookmarkActionModeCallback(new WeakReference<>(getContext()), adapter);
        registerDataObserver(adapter);
    }

    /**
     *
     */
    private void updateGridLayoutManager() {
        if (recyclerView != null &&
                recyclerView.getLayoutManager() != null) {
            ((GridLayoutManager) recyclerView.getLayoutManager())
                    .setSpanCount(Utils.getCardNumberInRow(getContext(), expandedGridview));
        }

    }
    /**
     *
     */
    private void updateRecyclerView() {
        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(0);
        }
    }

    /**
     *
     * @param recyclerViewAdapter
     */
    private void registerDataObserver(BookmarkRecyclerViewAdapter recyclerViewAdapter) {
        //TODO leak
        BookmarkListObserver observer = new BookmarkListObserver(new View[] {recyclerView,
                mEmptyLinkListView, emptySearchResultLayout}, searchManager);
        recyclerViewAdapter.registerAdapterDataObserver(observer);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * pull to refresh init
     */
    private void initPullToRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout
                .setColorSchemeResources(R.color.indigo_600);
    }

    /**
     * set not fully sync bookmarks
     */
    private void setNotSyncBookmarks() {
        Snackbar.make(mainView, "hey snack", Snackbar.LENGTH_LONG)
                .setAction("SYNC", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "hey", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    /**
     * handle terms and licences
     */
    private void handleTermsAndLicences() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.apache.org/licenses/LICENSE-2.0"));
        startActivity(browserIntent);
    }

    /**
     * init singleton instances
     */
    private void initSingletonInstances() {
        statusHelper = StatusManager.getInstance();
        mActionbarHelper = ActionbarHelper.getInstance(new WeakReference<>(getContext()));
        mBookmarkActionSingleton = BookmarkActionHelper.getInstance(new WeakReference<>(getContext()));
        searchManager = SearchManager.getInstance(new WeakReference<>(getContext()),
                Realm.getDefaultInstance(), new WeakReference<SearchManagerCallbackInterface>(this));
    }

    @Override
    public boolean onLongItemClick(View view, int position) {
        if (!statusHelper.isEditMode()) {
            getActivity().startActionMode(actionMode);
            optionMenuContainerRevealLayout.toggleRevealActionMenu(false);
        }

        handleSelectItemByPos(position);
        return true;
    }

    @Override
    public void onItemClick(View view, int position) {
        if (statusHelper.isEditMode()) {
            handleSelectItemByPos(position);
            return;
        }
        Bookmark bookmark = ((BookmarkRecyclerViewAdapter) recyclerView.getAdapter()).getItem(position);
        mBookmarkActionSingleton.openLinkOnBrowser(bookmark.getUrl());
    }

    /**
     *
     * @param position
     */
    private void handleSelectItemByPos(int position) {
        statusHelper.setEditMode();
        BookmarkRecyclerViewAdapter adapter = ((BookmarkRecyclerViewAdapter) recyclerView.getAdapter());
        adapter.setSelectedItemPos(position);
        recyclerView.getAdapter().notifyItemChanged(position);
        actionMode.toggleVisibilityIconMenu(adapter.getSelectedItemListSize() <= 1);
        actionMode.setSelectedItemCount(adapter.getSelectedItemListSize());
        if (((BookmarkRecyclerViewAdapter) recyclerView.getAdapter()).isEmptySelectedPosArray()) {
            actionMode.forceToFinish();
        }
    }

    /**
     *
     */
    private void handleEmptyView() {
        importDefaultBookmarksButton.setOnClickListener(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateGridLayoutManager();
    }

    @Override
    public void updateSearchDataList(RealmResults list) {
        ((BookmarkRecyclerViewAdapter) recyclerView.getAdapter()).updateData(list);
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public boolean handleBackPressed() {
        StatusManager status = StatusManager.getInstance();
        if (status.isOnActionMenuMode()) {
            status.unsetStatus();
            addNewFab.setVisibility(View.VISIBLE);
            optionMenuContainerRevealLayout.toggleRevealActionMenu(false);
            return true;
        }
        return false;
    }


    @Override
    public void onToggleRevealCb(boolean isShowing) {
        if (openMenuItem != null) {
            openMenuItem.setIcon(ContextCompat.getDrawable(getContext(),
                    ActionMenuRevealHelper.getInstance(new WeakReference<Context>(getContext()))
                            .getIconByShowingStatus(isShowing)));
        }

        //animate viewAnimation not object animation welll done
        recyclerView.setTranslationY(isShowing ? optionMenuContainerRevealLayout.getHeight() : 0);
        addNewFab.setVisibility(isShowing ? View.GONE : View.VISIBLE);
    }

    @Override
    public void hanldeExportContextMenu() {
        ExportStrategy
                .buildInstance(new WeakReference<>(getContext()), mainView)
                .checkAndRequestPermission();
    }

    /**
     * handle setting option - open up a new activity with all preferences available
     */
    @Override
    public void hanldeSettingsContextMenu() {
        statusHelper.unsetStatus();
        mActionbarHelper.updateActionBar(true);
        startActivity(new Intent(getActivity(), SettingsActivity.class));
    }

    @Override
    public void hanldeExportGridviewResizeMenu() {
        expandedGridview = !expandedGridview;
        SharedPrefHelper.getInstance(new WeakReference<>(getContext()))
                .setValue(EXPANDED_GRIDVIEW, expandedGridview);

        int count = Utils.getCardNumberInRow(getContext(), expandedGridview);
        ((GridLayoutManager) recyclerView.getLayoutManager()).setSpanCount(count);
    }


}