package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.content.Context;
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
import com.application.material.bookmarkswallet.app.singleton.*;
import com.application.material.bookmarkswallet.app.singleton.search.SearchHandlerSingleton;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.application.material.bookmarkswallet.app.observer.BookmarkListObserver;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by davide on 04/08/15.
 */
public class BookmarkListFragment extends Fragment
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
    @Bind(R.id.emptySearchResultLayoutId)
    View mEmptySearchResultLayout;

    private MainActivity mMainActivityRef;
    private Realm mRealm;
    private SearchHandlerSingleton mSearchHandlerSingleton;
    private ActionbarSingleton mActionbarSingleton;
    private BookmarkActionSingleton mBookmarkActionSingleton;
    private View mView;
    private StatusSingleton mStatusSingleton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof OnChangeFragmentWrapperInterface)) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }
        mMainActivityRef =  (MainActivity) context;
        initSingletonInstances();
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        mView = inflater.inflate(R.layout.fragment_bookmark_list_layout,
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
        mSearchHandlerSingleton.initSearchView(menu);
        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        Utils.animateFabIn(mAddBookmarkFab);
        mStatusSingleton.setSearchMode(true);
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        mEmptySearchResultLayout.setVisibility(View.GONE); //PATCH
        Utils.animateFabOut(mAddBookmarkFab);
        mStatusSingleton.unsetStatus();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBookmarkFabId:
                mBookmarkActionSingleton.addBookmarkAction(this);
                break;
            default:
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
                mStatusSingleton.unsetStatus();
                handleSetting();
                return true;
            case R.id.action_export:
                Toast.makeText(mMainActivityRef, "Feature will come soon!",
                        Toast.LENGTH_SHORT).show();
//                exportBookmarksSingleton.exportAction();
                return true;
            case R.id.action_terms_and_licences:
                handleTermsAndLicences();
                return true;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Utils.ADD_BOOKMARK_ACTIVITY_REQ_CODE) {
            notifyDataChanged();
            initSingletonInstances();
        }
    }
    /**
     * init view on recyclerView - setup adapter and other stuff
     * connected to main fragment app
     */
    private void onInitView() {
        initPullToRefresh();
        initActionbar();
        initRecyclerView();
        mAddBookmarkFab.setOnClickListener(this);
//        setNotSyncBookmarks();
    }

    /**
     *
     */
    private void initActionbar() {
        mActionbarSingleton.initActionBar();
        initTitle();
    }

    /**
     * init title - set
     */
    private void initTitle() {
        mActionbarSingleton.setTitle(null);
    }

    /**
     * init view on recyclerView - setup adapter and other stuff
     * connected to main fragment app
     */
    private void initRecyclerView() {
        BookmarkRecyclerViewAdapter recyclerViewAdapter =
                new BookmarkRecyclerViewAdapter(mMainActivityRef, this);
        registerDataObserver(recyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mMainActivityRef));
        mRecyclerView.setAdapter(recyclerViewAdapter);
        setRealmAdapter(recyclerViewAdapter);
        mSearchHandlerSingleton.setAdapter(recyclerViewAdapter);

        mRecyclerView.setItemAnimator(null); //WTF
        mRecyclerView.setHasFixedSize(true);
    }

    /**
     *
     * @param recyclerViewAdapter
     */
    private void registerDataObserver(BookmarkRecyclerViewAdapter recyclerViewAdapter) {
        BookmarkListObserver observer = new BookmarkListObserver(
                mRecyclerView,
                mEmptyLinkListView,
                mEmptySearchResultLayout,
                mSwipeRefreshLayout,
                mSearchHandlerSingleton);
        recyclerViewAdapter.registerAdapterDataObserver(observer);
    }

    /**
     * Realm io function to handle adapter and get
     * data from db
     */
    public void setRealmAdapter(BookmarkRecyclerViewAdapter recyclerViewAdapter) {
        try {
            RealmResults realmResults = mRealm.where(Bookmark.class).findAll();
            RealmModelAdapter realmModelAdapter = new RealmModelAdapter(mMainActivityRef, realmResults);
            recyclerViewAdapter
                    .setRealmBaseAdapter(realmModelAdapter);
            recyclerViewAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * pull to refresh init
     */
    private void initPullToRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout
                .setColorSchemeResources(R.color.blue_grey_700,
                        R.color.yellow_400);
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
     * handle setting option - open up a new activity with all preferences available
     */
    private void handleSetting() {
        mActionbarSingleton.updateActionBar(true);
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
     * notify data changed
     */
    public void notifyDataChanged() {
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }
    /**
     * init singleton instances
     */
    private void initSingletonInstances() {
        mRealm = Realm.getInstance(new RealmConfiguration.Builder(mMainActivityRef).build());
        mStatusSingleton = StatusSingleton.getInstance();
        mActionbarSingleton = ActionbarSingleton.getInstance(mMainActivityRef);
        mBookmarkActionSingleton = BookmarkActionSingleton.getInstance(mMainActivityRef);
        mSearchHandlerSingleton = SearchHandlerSingleton.getInstance(mMainActivityRef, mRealm);
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

    @Override
    public void onTaskCompleted(byte[] data) {
        return;
    }

    @Override
    public void onTaskCompleted(String url) {
    }
}