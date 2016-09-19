package com.application.material.bookmarkswallet.app.fragments;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.actionMode.EditBookmarkActionMode;
import com.application.material.bookmarkswallet.app.adapter.BookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.strategies.ExportStrategy;
import com.application.material.bookmarkswallet.app.realm.adapter.RealmModelAdapter;
import com.application.material.bookmarkswallet.app.animator.AnimatorBuilder;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper;
import com.application.material.bookmarkswallet.app.helpers.StatusHelper;
import com.application.material.bookmarkswallet.app.manager.SearchManager;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.singleton.*;
import com.application.material.bookmarkswallet.app.utlis.RealmUtils;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.application.material.bookmarkswallet.app.observer.BookmarkListObserver;
import com.flurry.android.FlurryAgent;

import java.lang.ref.WeakReference;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper.SharedPrefKeysEnum.IMPORT_ACCOUNT_NOTIFIED;
import static com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper.SharedPrefKeysEnum.IMPORT_KEEP_NOTIFIED;

public class BookmarkListFragment extends Fragment
        implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener,
        MenuItemCompat.OnActionExpandListener, BookmarkRecyclerViewAdapter.OnActionListenerInterface {
    public static final String FRAG_TAG = "LinksListFragment";
    @Bind(R.id.addBookmarkFabId)
    FloatingActionButton mAddBookmarkFab;
    @Bind(R.id.mainContainerViewId)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.linksListId)
    RecyclerView recyclerView;
    @Bind(R.id.emptyLinkListViewId)
    View mEmptyLinkListView;
    @Bind(R.id.emptySearchResultLayoutId)
    View mEmptySearchResultLayout;
    @Bind(R.id.importFromAccountButtonId)
    View importFromAccountButton;
    @Bind(R.id.importFromKeepButtonId)
    View importFromKeepButton;
    @Bind(R.id.importFromAccountDismissButtonId)
    View importFromAccountDismissButton;
    @Bind(R.id.importFromKeepDismissButtonId)
    View importFromKeepDismissButton;
    @Bind(R.id.importFromAccountCardviewLayoutId)
    View importFromAccountCardviewLayout;
    @Bind(R.id.importFromKeepCardviewLayoutId)
    View importFromKeepCardviewLayout;

    private Realm mRealm;
    private SearchManager searchManager;
    private ActionbarSingleton mActionbarSingleton;
    private ActionsSingleton mBookmarkActionSingleton;
    private View mainView;
    private StatusHelper statusHelper;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof OnChangeFragmentWrapperInterface)) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }
        initSingletonInstances();
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
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
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        searchManager.initSearchView(menu);
        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.action_search), this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        Utils.animateFabIn(mAddBookmarkFab);
        statusHelper.setSearchMode(true);
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        mEmptySearchResultLayout.setVisibility(View.GONE); //PATCH
        Utils.animateFabOut(mAddBookmarkFab);
        statusHelper.unsetStatus();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBookmarkFabId:
                mBookmarkActionSingleton.addBookmarkAction(this);
                break;
            case R.id.importFromKeepButtonId:
                FlurryAgent.logEvent("importFromKeep", true);
                dismissCardview(v);
                break;
            case R.id.importFromAccountButtonId:
                FlurryAgent.logEvent("importFromAccount", true);
                dismissCardview(v);
                break;
            case R.id.importFromAccountDismissButtonId:
            case R.id.importFromKeepDismissButtonId:
                dismissCardview(v);
                break;
        }
    }

    /**
     *
     * @param v
     */
    private void dismissCardview(View v) {
        switch (v.getId()) {
            case R.id.importFromKeepButtonId:
            case R.id.importFromKeepDismissButtonId:
                SharedPrefHelper.getInstance(new WeakReference<>(getContext()))
                        .setValue(IMPORT_KEEP_NOTIFIED, true);
                hideViewAnimator(importFromKeepCardviewLayout);
                recyclerView.scrollToPosition(0);
                importNotificationToUser();
                break;
            case R.id.importFromAccountButtonId:
            case R.id.importFromAccountDismissButtonId:
                SharedPrefHelper.getInstance(new WeakReference<>(getContext()))
                        .setValue(IMPORT_ACCOUNT_NOTIFIED, true);
                hideViewAnimator(importFromAccountCardviewLayout);
                recyclerView.scrollToPosition(0);
                importNotificationToUser();
                break;
        }
    }

    /**
     *
     */
    private void importNotificationToUser() {
        Toast.makeText(getContext(), R.string.youll_be_notified, Toast.LENGTH_SHORT).show();
    }

    /**
     *
     * @param view
     */
    private void hideViewAnimator(final View view) {
        Animator animator = AnimatorBuilder.getInstance(new WeakReference<>(getContext()))
                .getYTranslation(view, 0, -view.getMeasuredHeight(), 0);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }


    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ((BookmarkRecyclerViewAdapter) recyclerView.getAdapter())
                        .updateData(RealmUtils.getResults(mRealm));
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                shareBookmark();
                break;
            case R.id.action_settings:
                statusHelper.unsetStatus();
                handleSetting();
                return true;
            case R.id.action_import_export:
                ExportStrategy
                        .buildInstance(new WeakReference<>(getContext()), mainView)
                        .checkAndRequestPermission();
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
        initPullToRefresh();
        initActionbar();
        initRecyclerView();
        mAddBookmarkFab.setOnClickListener(this);
//        initImportViews();
//        setNotSyncBookmarks();
    }

    /**
     *
     */
    private void initImportViews() {
        importFromKeepButton.setOnClickListener(this);
        importFromKeepDismissButton.setOnClickListener(this);
        importFromAccountButton.setOnClickListener(this);
        importFromAccountDismissButton.setOnClickListener(this);
        if ((boolean) SharedPrefHelper.getInstance(new WeakReference<>(getContext()))
                .getValue(IMPORT_KEEP_NOTIFIED, false)) {
            importFromKeepCardviewLayout.setVisibility(View.GONE);
        }
        if ((boolean) SharedPrefHelper.getInstance(new WeakReference<>(getContext()))
                .getValue(IMPORT_ACCOUNT_NOTIFIED, false)) {
            importFromAccountCardviewLayout.setVisibility(View.GONE);
        }
    }

    /**
     *
     */
    private void initActionbar() {
        mActionbarSingleton.initActionBar();
        mActionbarSingleton.setTitle(getString(R.string.bookmark_list_title));
    }

    /**
     * init view on recyclerView - setup adapter and other stuff
     * connected to main fragment app
     */
    private void initRecyclerView() {
        BookmarkRecyclerViewAdapter recyclerViewAdapter = new BookmarkRecyclerViewAdapter(getActivity(),
                new WeakReference<BookmarkRecyclerViewAdapter.OnActionListenerInterface>(this));
        registerDataObserver(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);
        setRealmAdapter(recyclerViewAdapter, RealmUtils.getResults(mRealm));
        searchManager.setAdapter(recyclerViewAdapter); //TODO what???
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
        BookmarkListObserver observer = new BookmarkListObserver(recyclerView, mEmptyLinkListView,
                mEmptySearchResultLayout, mSwipeRefreshLayout, searchManager);
        recyclerViewAdapter.registerAdapterDataObserver(observer);
    }

    /**
     * @param realmResults
     * @param recyclerViewAdapter
     * Realm io function to handle adapter and get
     * data from db
     */
    public void setRealmAdapter(BookmarkRecyclerViewAdapter recyclerViewAdapter,
                                RealmResults realmResults) {
        try {
            RealmModelAdapter realmModelAdapter = new RealmModelAdapter(getActivity(), realmResults);
            recyclerViewAdapter.setRealmBaseAdapter(realmModelAdapter);
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
     * handle setting option - open up a new activity with all preferences available
     */
    private void handleSetting() {
        mActionbarSingleton.updateActionBar(true);
        ((OnChangeFragmentWrapperInterface) getActivity())
                .changeFragment(new SettingsFragment(), null, SettingsFragment.FRAG_TAG);
    }

    /**
     * share bookmark through intent you like more
     */
    private void shareBookmark() {
        Intent intent = getIntentForEditBookmark(getSelectedItem());
        getActivity().startActivity(Intent.createChooser(intent, "share bookmark to..."));
    }

    /**
     * get bookmark obj by item pos on recycler view
     * @return
     */
    public Bookmark getSelectedItem() {
        return (((BookmarkRecyclerViewAdapter) recyclerView.getAdapter())
                .getItem(statusHelper.getEditItemPos()));
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
        mRealm = Realm.getDefaultInstance();
        statusHelper = StatusHelper.getInstance();
        mActionbarSingleton = ActionbarSingleton.getInstance(new WeakReference<>(getContext()));
        mBookmarkActionSingleton = ActionsSingleton.getInstance(new WeakReference<>(getContext()));
        searchManager = SearchManager.getInstance(new WeakReference<>(getContext()), mRealm);
    }


    @Override
    public boolean onLongItemClick(View view, int position) {
        EditBookmarkActionMode editActionMode = new EditBookmarkActionMode(new WeakReference<>(getContext()),
                position, ((BookmarkRecyclerViewAdapter) recyclerView.getAdapter()));

        getActivity().startActionMode(editActionMode);
        statusHelper.setEditMode(position);
        recyclerView.getAdapter().notifyItemChanged(position);
        return true;
    }

    @Override
    public void onItemClick(View view, int position) {
        Bookmark bookmark = ((BookmarkRecyclerViewAdapter) recyclerView.getAdapter()).getItem(position);
        mBookmarkActionSingleton.openLinkOnBrowser(bookmark.getUrl());
    }
}