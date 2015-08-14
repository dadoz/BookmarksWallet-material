package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.application.material.bookmarkswallet.app.MainActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.realm.BookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.adapter.realm.RealmModelAdapter;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.singleton.ActionbarSingleton;
import com.application.material.bookmarkswallet.app.singleton.BookmarkActionSingleton;
import com.application.material.bookmarkswallet.app.singleton.BookmarkProviderSingleton;
import com.application.material.bookmarkswallet.app.singleton.StatusSingleton;
import com.application.material.bookmarkswallet.app.singleton.search.SearchHandlerSingleton;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by davide on 04/08/15.
 */
public class BookmarkRecyclerViewFragment extends Fragment
        implements View.OnClickListener, View.OnLongClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        MenuItemCompat.OnActionExpandListener, OnTaskCompleted {
    public static final String FRAG_TAG = "LinksListFragment";
    @Bind(R.id.addBookmarkFabId)
    FloatingActionButton mAddBookmarkFab;
    @Bind(R.id.mainContainerViewId)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.linksListId)
    RecyclerView mRecyclerView;
    @Bind(R.id.emptyLinkListViewId)
    View mEmptyLinkListView;

    private MainActivity mMainActivityRef;
    private Realm mRealm;
    private SearchHandlerSingleton mSearchHandlerSingleton;
    private ActionbarSingleton mActionbarSingleton;
    private BookmarkActionSingleton mBookmarkActionSingleton;
    private View mView;
    private BookmarkProviderSingleton mBookmarkProviderSingleton;
    private StatusSingleton mStatusSingleton;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof OnChangeFragmentWrapperInterface)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }
        mMainActivityRef =  (MainActivity) activity;
        initSingletonInstances();
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        mView = inflater.inflate(R.layout.bookmark_recycler_view_layout,
                container, false);
        ButterKnife.bind(this, mView);
        setHasOptionsMenu(true);
        onInitView();

        return mView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
//        rvActionsSingleton.cancelAsyncTask();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        mSearchHandlerSingleton.handleSearch(menu);
        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        mAddBookmarkFab.setVisibility(View.GONE);
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        mAddBookmarkFab.setVisibility(View.VISIBLE);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBookmarkFabId:
                mBookmarkActionSingleton.addBookmarkAction(this);
                break;
            case R.id.backgroundLayoutId:
                mBookmarkActionSingleton.handleClickAction(v, mRecyclerView);
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        mBookmarkActionSingleton.handleLongClickAction(v, mRecyclerView);
        return true;
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareBookmark();
                break;
            case R.id.action_settings:
                handleSetting();
                return true;
            case R.id.action_export:
//                exportBookmarksSingleton.exportAction();
                return true;
            case R.id.action_terms_and_licences:
                handleTermsAndLicences();
                return true;
        }
        return true;
    }

    /**
     * init view on recyclerView - setup adapter and other stuff
     * connected to main fragment app
     */
    private void onInitView() {
        initPullToRefresh();
        initTitle();
        initRecyclerView();
        mAddBookmarkFab.setOnClickListener(this);
        setNotSyncBookmarks();
    }

    /**
     * init view on recyclerView - setup adapter and other stuff
     * connected to main fragment app
     */
    private void initRecyclerView() {
        BookmarkRecyclerViewAdapter recyclerViewAdapter =
                new BookmarkRecyclerViewAdapter(mMainActivityRef, this);
        recyclerViewAdapter.registerAdapterDataObserver(new EmptyDataObserver());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mMainActivityRef));
        mRecyclerView.setAdapter(recyclerViewAdapter);
        setRealmAdapter(recyclerViewAdapter);

        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setHasFixedSize(true);
    }

    /**
     * Realm io function to handle adapter and get
     * data from db
     */
    public RealmResults<Bookmark> getBookmarksList() {
        return mRealm.where(Bookmark.class).findAll();
    }

    public void setRealmAdapter(BookmarkRecyclerViewAdapter recyclerViewAdapter) {
        try {
            RealmResults realmResults = mRealm.where(Bookmark.class).findAll();
            realmResults.sort("timestamp");

            RealmModelAdapter realmModelAdapter = new RealmModelAdapter(mMainActivityRef, realmResults, true);
            recyclerViewAdapter
                    .setRealmBaseAdapter(realmModelAdapter);
            recyclerViewAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * init title - set
     */
    private void initTitle() {
        mActionbarSingleton.setTitle(null);
//        mActionbarSingleton.setDisplayHomeEnabled(false);
    }

    /**
     * pull to refresh init
     */
    private void initPullToRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout
                .setColorSchemeResources(android.R.color.holo_red_light,
                        android.R.color.holo_orange_light, android.R.color.holo_blue_bright,
                        android.R.color.holo_green_light);
    }

    /**
     * set not fully sync bookmarks
     */
    private void setNotSyncBookmarks() {
        Snackbar.make(mView, "hey snack", Snackbar.LENGTH_LONG)
                .setAction("SYNC", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mMainActivityRef, "hey", Toast.LENGTH_SHORT).show();
                    }
                }).show();
//        if (rvActionsSingleton.getSyncStatus() == CANCELED) {
//            rvActionsSingleton.setBookmarksNotSyncView(true);
//        }
    }

    /**
     * handle terms and licences
     */
    private void handleTermsAndLicences() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.apache.org/licenses/LICENSE-2.0"));
        startActivity(browserIntent);
    }

    /**
     * handle setting option - open up a new activity with all preferences available
     */
    private void handleSetting() {
        mActionbarSingleton.udpateActionbar(true);
        mMainActivityRef.changeFragment(new SettingsFragment(), null, SettingsFragment.FRAG_TAG);
    }

    /**
     * share bookmark through intent you like more
     */
    private void shareBookmark() {
        Intent intent = getIntentForEditBookmark(getSelectedItem());
        mMainActivityRef.startActivity(Intent.createChooser(intent, "share bookmark to..."));
    }

    /**
     * get bookmark obj by item pos on recycler view
     * @return
     */
    public Bookmark getSelectedItem() {
        BookmarkRecyclerViewAdapter adapter =
                (BookmarkRecyclerViewAdapter) mRecyclerView.getAdapter();
        return ((Bookmark) adapter.getItem(mStatusSingleton.getEditItemPos()));
    }

    /**
     * get shared intent
     * @param bookmark
     * @return
     */
    public Intent getIntentForEditBookmark(Bookmark bookmark) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, Bookmark.Utils.stringify(bookmark));
        shareIntent.setType("text/plain");
        return shareIntent;
    }

    /**
     * init singleton instances
     */
    private void initSingletonInstances() {
        mRealm = Realm.getInstance(mMainActivityRef);
        mStatusSingleton = StatusSingleton.getInstance();
        mSearchHandlerSingleton = SearchHandlerSingleton.getInstance(mMainActivityRef);
        mActionbarSingleton = ActionbarSingleton.getInstance(mMainActivityRef);
        mBookmarkActionSingleton = BookmarkActionSingleton.getInstance(mMainActivityRef);
        mBookmarkProviderSingleton = BookmarkProviderSingleton.getInstance(mMainActivityRef, this);
    }

    @Override
    public void onTaskCompleted(boolean isRefreshEnabled) {
        try {
            mRecyclerView.getAdapter().notifyDataSetChanged();
            if (! isRefreshEnabled) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * set empty view on empty data TODO move
     */
    private class EmptyDataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onChanged() {
            boolean isEmptyData = mRecyclerView.getAdapter().getItemCount() == 0;
            mEmptyLinkListView.setVisibility(isEmptyData ? View.VISIBLE : View.GONE);
            mEmptyLinkListView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    mBookmarkProviderSingleton.addByDefaultBrowser();
                }
            });
        }

        public void onItemRangeInserted(int positionStart, int itemCount) {
        }

        public void onItemRangeRemoved(int positionStart, int itemCount) {
        }
    }
}