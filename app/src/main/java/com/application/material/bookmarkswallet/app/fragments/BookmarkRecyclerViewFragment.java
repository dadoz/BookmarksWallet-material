package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.*;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.application.material.bookmarkswallet.app.MainActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.realm.BookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.adapter.realm.RealmModelAdapter;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.recyclerView.RecyclerViewCustom;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by davide on 04/08/15.
 */
public class BookmarkRecyclerViewFragment extends Fragment
        implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    public static final String FRAG_TAG = "LinksListFragment";
    @Bind(R.id.clipboardFloatingButtonId)
    FloatingActionButton mAddBookmarkFab;
    @Bind(R.id.mainContainerViewId)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.linksListId)
    RecyclerViewCustom mRecyclerView;

    private MainActivity mMainActivityRef;
    private Realm mRealm;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof OnChangeFragmentWrapperInterface)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }
        mMainActivityRef =  (MainActivity) activity;
        mRealm = Realm.getInstance(mMainActivityRef);
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        View view = inflater.inflate(R.layout.bookmark_recycler_view_layout,
                container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        onInitView();

        return view;
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

    }

    /**
     * init view on recyclerView - setup adapter and other stuff
     * connected to main fragment app
     */
    private void onInitView() {
        initPullToRefresh();
        initTitle();

//        mItems = getBookmarksList();
        onInitRecyclerView();

        mAddBookmarkFab.setOnClickListener(this);

        setSearchMode();
        setNotSyncBookmarks();
    }

    /**
     * init view on recyclerView - setup adapter and other stuff
     * connected to main fragment app
     */
    private void onInitRecyclerView() {
        BookmarkRecyclerViewAdapter recyclerViewAdapter =
                new BookmarkRecyclerViewAdapter(mMainActivityRef, mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mMainActivityRef));
        mRecyclerView.setAdapter(recyclerViewAdapter);
        setRealmAdapter(recyclerViewAdapter);

        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onRefresh() {

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
     * private function
     */

    private void setSearchMode() {
//        if (mActionbarSingleton.isSearchMode()) {
//            mAdsView.setVisibility(View.GONE);
//            mClipboardFloatingButton.hide(false);
//        }
    }

    private void initTitle() {
//        mActionbarSingleton.setTitle(null);
//        mActionbarSingleton.setDisplayHomeEnabled(false);
    }

    private void initPullToRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout
                .setColorSchemeResources(android.R.color.holo_red_light,
                        android.R.color.holo_orange_light, android.R.color.holo_blue_bright,
                        android.R.color.holo_green_light);
    }

    private void setNotSyncBookmarks() {
//        if (rvActionsSingleton.getSyncStatus() == CANCELED) {
//            rvActionsSingleton.setBookmarksNotSyncView(true);
//        }
    }
}