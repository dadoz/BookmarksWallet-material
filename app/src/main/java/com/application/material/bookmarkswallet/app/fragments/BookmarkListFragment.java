package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.*;
import android.support.v7.widget.SearchView;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;

import butterknife.Bind;
import butterknife.ButterKnife;
import com.application.material.bookmarkswallet.app.MainActivity;
import com.application.material.bookmarkswallet.app.adapter.realm.BookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.recyclerView.RecyclerViewCustom;
import com.application.material.bookmarkswallet.app.singleton.ActionbarSingleton;
import com.application.material.bookmarkswallet.app.singleton.ClipboardSingleton;
import com.application.material.bookmarkswallet.app.singleton.ExportBookmarkSingleton;
import com.application.material.bookmarkswallet.app.singleton.RecyclerViewActionsSingleton;
import com.application.material.bookmarkswallet.app.singleton.RecyclerViewActionsSingleton.BrowserEnum;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import java.net.MalformedURLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.application.material.bookmarkswallet.app.singleton.RecyclerViewActionsSingleton.SyncStatusEnum.CANCELED;
import static com.application.material.bookmarkswallet.app.singleton.RecyclerViewActionsSingleton.SyncStatusEnum.RUNNING;

public class BookmarkListFragment extends Fragment
        implements View.OnClickListener, Filterable,
        SwipeRefreshLayout.OnRefreshListener,
//        SlidingUpPanelLayout.PanelSlideListener,
        CompoundButton.OnCheckedChangeListener,
        View.OnLayoutChangeListener {
    private static final String TAG = "LinksListFragment_TAG";
    public static final String FRAG_TAG = "LinksListFragment";
    private MainActivity mMainActivityRef;
    @Bind(R.id.urlEditText)
    EditText mUrlEditText;
    @Bind(R.id.httpFormatCheckboxId)
    CheckBox mHttpFormatCheckbox;
    @Bind(R.id.slidingPanelDoneIconId)
    ImageView slidingPanelDoneText;
//	@Bind(R.id.slidingLayerLayoutId)
//    SlidingUpPanelLayout mSlidingLayerLayout;
    @Bind(R.id.linksListId)
    RecyclerViewCustom mRecyclerView;
    @Bind(R.id.emptyLinkListViewId)
    View emptyLinkListView;
    @Bind(R.id.emptySearchResultLayoutId)
    View mEmptySearchResultView;
    @Bind(R.id.mainContainerViewId)
    SwipeRefreshLayout mSwipeRefreshLayout;
//    @Bind(R.id.adViewId)
//    AdView mAdsView;
    @Bind(R.id.notSyncLayoutId)
    LinearLayout notSyncLayout;

    private LinearLayoutManager linearLayoutManager;
    private RealmResults<Bookmark> mItems;
    private View mLinkListView;
    private ActionbarSingleton mActionbarSingleton;
    private RecyclerViewActionsSingleton rvActionsSingleton;
    private ExportBookmarkSingleton exportBookmarksSingleton;
    private BookmarkRecyclerViewAdapter mLinkRecyclerViewAdapter;
    private static Realm mRealm;
    private ClipboardSingleton mClipboardSingleton;

    private MenuItem mSearchItem;
    private static final BrowserEnum [] mBrowserList = {BrowserEnum.CHROME, BrowserEnum.DEFAULT};

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof OnChangeFragmentWrapperInterface)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }
        mMainActivityRef =  (MainActivity) activity;
        mActionbarSingleton = ActionbarSingleton.getInstance(mMainActivityRef);
        exportBookmarksSingleton = ExportBookmarkSingleton.getInstance(this, mMainActivityRef);
        mClipboardSingleton = ClipboardSingleton.getInstance(mMainActivityRef);
        mRealm = Realm.getInstance(mMainActivityRef);

    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        mLinkListView = inflater.inflate(R.layout.bookmark_recycler_view_layout,
                container, false);
        ButterKnife.bind(this, mLinkListView);
        //load ads
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdsView.loadAd(adRequest);

        mLinkListView = inflater.inflate(R.layout.bookmark_recycler_view_layout,
                container, false);
        ButterKnife.bind(this, mLinkListView);
        setHasOptionsMenu(true);
        onInitView();
        triggerImport();
        return mLinkListView;
    }

    private void triggerImport() {
        if (mMainActivityRef.getIntent()
                .getExtras().getBoolean(Utils.IMPORT_TRIGGER)) {
//            rvActionsSingleton.addBookmarksByProvider(mBrowserList);
            mMainActivityRef.getIntent().getExtras().remove(Utils.IMPORT_TRIGGER);
        }
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
    public void onConfigurationChanged(Configuration newConfig) {
        //set refresh layout depending on isBookmarksSyncByProvider
        if (rvActionsSingleton.getSyncStatus() == CANCELED) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    private void onInitView() {
//        ArrayList<View> viewArrayList = new ArrayList<>();
//        viewArrayList.add(mAdsView);
//        viewArrayList.add(mClipboardFloatingButton);
//        viewArrayList.add(mSlidingLayerLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this); //default - if items then set onRefreshListener

//        mSlidingLayerLayout.setPanelSlideListener(this);
//        mSlidingLayerLayout.addOnLayoutChangeListener(this);
//        slidingPanelDoneText.setOnClickListener(this);

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_blue_bright,
                android.R.color.holo_green_light);
//		mActionbarSingleton.setToolbarScrollManager(mRecyclerView, viewArrayList, mSlidingLayerLayout.getPanelHeight());
        mActionbarSingleton.setTitle(null);
        mActionbarSingleton.setDisplayHomeEnabled(false);

//        rvActionsSingleton = RecyclerViewActionsSingleton
//                .getInstance(mSwipeRefreshLayout, mRecyclerView, notSyncLayout, mMainActivityRef, this);
//        rvActionsSingleton.setAdsView(mAdsView, mSlidingLayerLayout.getPanelHeight());

        mItems = rvActionsSingleton.getBookmarksList();

//        mClipboardFloatingButton.setOnClickListener(this);
//		initRecyclerView();
        rvActionsSingleton.setAdapter();
//        if (mActionbarSingleton.isEditMode()) {
//            ScrollManager.runTranslateAnimation(mAdsView, 0, new DecelerateInterpolator(3));
//            rvActionsSingleton.selectBookmarkEditMenu(mActionbarSingleton.getEditItemPos());
//        }

//        if (mActionbarSingleton.isSearchMode()) {
//            mAdsView.setVisibility(View.GONE);
//            mClipboardFloatingButton.hide(false);
//        }

        if (rvActionsSingleton.getSyncStatus() == CANCELED) {
            rvActionsSingleton.setBookmarksNotSyncView(true);
        }
    }

    private void initRecyclerView() {
        mLinkRecyclerViewAdapter =
                new BookmarkRecyclerViewAdapter(mMainActivityRef, mRecyclerView);

        linearLayoutManager = new LinearLayoutManager(mMainActivityRef);
        emptyLinkListView.findViewById(R.id.importLocalBookmarksButtonId).setOnClickListener(this);

        //set empty view
        mRecyclerView.setEmptyView(emptyLinkListView);
        mRecyclerView.setEmptySearchResultView(mEmptySearchResultView);
        mRecyclerView.setHasFixedSize(true);

        //set layout manager
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mLinkRecyclerViewAdapter);
        mRecyclerView.setItemAnimator(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //SEARCH VIEW HANDLER
//        searchViewHandler(menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void collapseSearchActionView() {
        if (mSearchItem == null) {
            return;
        }
        mSearchItem.collapseActionView();
    }

    public void searchViewHandler(Menu menu) {
        //SEARCH ITEM
        mSearchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) mMainActivityRef.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (mSearchItem != null) {
//            if (mActionbarSingleton.isSearchMode()) {
//                mSearchItem.expandActionView();
//            }

            searchView = (SearchView) mSearchItem.getActionView();
            MenuItemCompat.setOnActionExpandListener(mSearchItem,
                    new MenuItemCompat.OnActionExpandListener() {
                        @Override
                        public boolean onMenuItemActionExpand(MenuItem item) {
//                            mActionbarSingleton.setSearchMode(true);
//                            ((BookmarkRecyclerViewAdapter) mRecyclerView.getAdapter())
//                                    .setSearchMode(true);
                            mRecyclerView.getAdapter().notifyDataSetChanged();
                            int startDelay = 600;
//                            hideClipboardButton(startDelay);
//                            mSlidingLayerLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
//                            mAdsView.setVisibility(View.GONE);
                            return true;
                        }

                        @Override
                        public boolean onMenuItemActionCollapse(MenuItem item) {
//                            mActionbarSingleton.setSearchMode(false);
//                            ((BookmarkRecyclerViewAdapter) mRecyclerView.getAdapter())
//                                    .setSearchMode(false);
                            rvActionsSingleton.setAdapter();
//                            mAdsView.setVisibility(View.VISIBLE);
//                            showClipboardButton();
                            return true;
                        }
                    });
        }

        if (searchView != null) {
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(mMainActivityRef.getComponentName()));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                public boolean onQueryTextChange(String newText) {
                    if(newText.trim().toLowerCase().equals("")) {
                        rvActionsSingleton.setAdapter();
                        return true;
                    }

                    getFilter().filter(newText);
                    return true;
                }

                public boolean onQueryTextSubmit(String query) {
                    return false;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Bookmark bookmark = rvActionsSingleton.getSelectedItemFromAdapter();
                rvActionsSingleton.editLinkDialog(bookmark);
                break;
            case R.id.action_share:
                bookmark = rvActionsSingleton.getSelectedItemFromAdapter();
                Intent intent = rvActionsSingleton.getIntentForEditBookmark(bookmark);
                mMainActivityRef.startActivity(Intent.createChooser(intent, "share bookmark to..."));
                break;
            case R.id.action_settings:
//                if (mActionbarSingleton.isSearchMode()) {
//                    mSearchItem.collapseActionView();
//                }
//                if (mActionbarSingleton.isPanelExpanded()) {
//                    mSlidingLayerLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//                    return true;
//                }
//                mActionbarSingleton.changeActionbar(true);
                mMainActivityRef.changeFragment(new SettingsFragment(), null, SettingsFragment.FRAG_TAG);
                return true;
            case R.id.action_export:
                exportBookmarksSingleton.exportAction();
                return true;
            case R.id.action_terms_and_licences:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.apache.org/licenses/LICENSE-2.0"));
                startActivity(browserIntent);
                return true;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.importLocalBookmarksButtonId:
//                rvActionsSingleton.addBookmarksByProvider(mBrowserList);
                break;
            case R.id.notSyncLayoutId:
                mSwipeRefreshLayout.setRefreshing(true);
                rvActionsSingleton.deleteBookmarksList();
//                rvActionsSingleton.addBookmarksByProvider(mBrowserList);
                break;
            case R.id.addBookmarkFabId:
                if(! mClipboardSingleton.hasClipboardText()) {
                    Toast.makeText(mMainActivityRef, "no text in clipboard", Toast.LENGTH_SHORT).show();
                    break;
                }

                String bookmarkUrl = mClipboardSingleton.getTextFromClipboard();
                if(bookmarkUrl == null) {
                    break;
                }
                addLinkOnRecyclerViewWrapper(bookmarkUrl.trim());
                break;
            case R.id.slidingPanelDoneIconId:
                if (rvActionsSingleton.getSyncStatus() == RUNNING) {
                    Toast.makeText(mMainActivityRef, "Action denied. U're already importing bookmarks!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!Utils.isValidUrl(mUrlEditText.getText().toString())) {
                    Toast.makeText(mMainActivityRef, "no valid url typed in!", Toast.LENGTH_SHORT).show();
                    break;
                }

                try {
                    rvActionsSingleton.addBookmarkWithInfo(mUrlEditText.getText().toString());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

//                mSlidingLayerLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                mUrlEditText.setText("");
                break;
        }
    }


    public void addLinkOnRecyclerViewWrapper(String url) {
        try {
            rvActionsSingleton.addBookmarkWithInfo(url);
        } catch (Exception e) {
            mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(mMainActivityRef, "error on add bookmark - url not valid!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void undoEditBookmarkRecyclerViewWrapper() {
        rvActionsSingleton.undoEditBookmark();
    }

    @Override
    public Filter getFilter() {
        return new LinkFilter();
    }

    @Override
    public void onRefresh() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
//                if (! mActionbarSingleton.isSearchMode()) {
//                    rvActionsSingleton.setAdapter();
//                }
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(task, 2000);
    }

//    @Override
    public void onPanelSlide(View view, float v) {
        if (v <= 0) {
            return;
        }
//        mAdsView.setVisibility(View.GONE);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.httpFormatCheckboxId:
                mUrlEditText.setText(isChecked ? "https://" : "http://");
                mUrlEditText.setSelection(mUrlEditText.getText().length());
                break;
        }
    }

    public void hideSlidingPanel() {
//        mSlidingLayerLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
    }

    public void showSlidingPanel() {
//        mSlidingLayerLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//        if (! mActionbarSingleton.isSearchMode() &&
//            ! mActionbarSingleton.isEditMode() &&
//                bottom > ((SlidingUpPanelLayout) v).getDefaultHeight()) {
//            ((SlidingUpPanelLayout) v).setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//        }
    }

    private class LinkFilter extends Filter {
        public LinkFilter() {
        }

        @Override
        protected FilterResults performFiltering(final CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            filterResults.values = null;
            filterResults.count = 0;
            return filterResults;
        }

        @Override
        protected void publishResults(final CharSequence constraint, FilterResults results) {
            mMainActivityRef.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RealmQuery<Bookmark> query = mRealm.where(Bookmark.class);
                    RealmResults<Bookmark> filteredList;
                    String searchValue = ((String) constraint).toLowerCase();
                    boolean caseSensitive = false;

                    if (rvActionsSingleton.isSearchOnUrlEnabled()) {
                        filteredList = query
                                .contains("url", searchValue, caseSensitive).or()
                                .contains("name", searchValue, caseSensitive)
                                .findAll();
                    } else {
                        filteredList = query
                                .contains("name", searchValue, caseSensitive)
                                .findAll();
                    }

//                    Log.e(TAG, "hey" + filteredList.size());
                    if (filteredList.size() == 0) {
                        mRecyclerView.setEmptySearchResultQuery(constraint);
                    }
                    rvActionsSingleton.setAdapterByDataItems(filteredList);
                }
            });
        }
    }

}
