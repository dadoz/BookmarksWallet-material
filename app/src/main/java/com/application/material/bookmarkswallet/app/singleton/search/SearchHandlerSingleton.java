package com.application.material.bookmarkswallet.app.singleton.search;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Filter;
import android.widget.Filterable;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.realm.BookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.adapter.realm.RealmModelAdapter;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.singleton.StatusSingleton;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by davide on 04/08/15.
 */
public class SearchHandlerSingleton implements Filterable, SearchView.OnQueryTextListener {
    private static SearchHandlerSingleton mInstance;
    private static Activity mActivityRef;
    private static StatusSingleton mStatusSingleton;
    private static Realm mRealm;
    private BookmarkRecyclerViewAdapter mAdapter;
    private String mFilterString;
    private MenuItem mSearchItem;

    public SearchHandlerSingleton() {
    }

    /**
     *
     * @param activity
     * @retur
     */
    public static SearchHandlerSingleton getInstance(Activity activity, Realm realm) {
        mActivityRef = activity;
        mRealm = realm;
//        mStatusSingleton = StatusSingleton.getInstance();
        return mInstance == null ?
                mInstance = new SearchHandlerSingleton() :
                mInstance;
    }

    /**
     *
     */
    public void setAdapter(BookmarkRecyclerViewAdapter adapter) {
        mAdapter = adapter;
    }

    /**
     * @param menu
     */
    public void initSearchView(Menu menu) {
        try {
            mSearchItem = menu.findItem(R.id.action_search);
            SearchManager searchManager = (SearchManager) mActivityRef
                    .getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) mSearchItem.getActionView();
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(mActivityRef.getComponentName()));
            searchView.setOnQueryTextListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * collapse search view
     */
    public void collapseSearchView() {
        try {
            mSearchItem.collapseActionView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Filter getFilter() {
        return new LinkFilter();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        getFilter().filter(newText);
        return true;
    }

    /**
     * TODO move on realm class
     * @param filteredList
     */
    private void updateDataSet(RealmResults<Bookmark> filteredList) {
        mAdapter.getRealmBaseAdapter().updateData(filteredList);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * TODO move on realm class
     * @param
     */
    private void initDataSet() {
        RealmResults<Bookmark> result = mRealm.where(Bookmark.class).findAll();
        result.sort("timestamp", Sort.DESCENDING);
        mAdapter.getRealmBaseAdapter().updateData(result);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * TODO move in filter class
     * set empty search result view
     */
    private RealmResults getFilteredList(boolean isSearchOnUrl,
                                                      String filterString,
                                                      boolean isCaseSensitive) {
        mFilterString = filterString;
        if (isSearchOnUrl) {
            return mRealm.where(Bookmark.class)
                    .contains("url", filterString, isCaseSensitive?
                            Case.SENSITIVE : Case.INSENSITIVE)
                    .or()
                    .contains("name", filterString, isCaseSensitive?
                            Case.SENSITIVE : Case.INSENSITIVE)
                    .findAll();
        }

        return mRealm.where(Bookmark.class)
                    .contains("name", filterString, isCaseSensitive?
                            Case.SENSITIVE : Case.INSENSITIVE)
                    .findAll();
    }

    public String getFilterString() {
        return mFilterString;
    }

    /**
     * filter class handled by search
     */
    private class LinkFilter extends Filter {
        private final boolean mSearchOnUrlEnabled;
        private boolean mCaseSensitive = false;

        public LinkFilter() {
            mSearchOnUrlEnabled = false;
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
            mActivityRef.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String searchValue = ((String) constraint).trim().toLowerCase();
                        if (searchValue.equals("")) {
                            initDataSet();
                            return;
                        }

                        RealmResults filteredList = getFilteredList(mSearchOnUrlEnabled,
                                searchValue, mCaseSensitive);

//                        if (filteredList.size() == 0) -- handled by DataObserver
                        updateDataSet(filteredList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }
}
