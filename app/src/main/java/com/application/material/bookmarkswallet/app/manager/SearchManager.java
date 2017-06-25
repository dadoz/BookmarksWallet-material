package com.application.material.bookmarkswallet.app.manager;

import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Filter;
import android.widget.Filterable;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.lang.ref.WeakReference;

public class SearchManager implements Filterable,
        MaterialSearchView.OnQueryTextListener, MaterialSearchView.SearchViewListener {
    private static SearchManager instance;
    private MenuItem searchItem;
    private WeakReference<SearchManagerCallbackInterface> listener;
    private MaterialSearchView searchView;

    public SearchManager() {
    }

    /**
     * 
     * @return
     */
    public static SearchManager getInstance() {
        return instance == null ?
                instance = new SearchManager() :
                instance;
    }

    public void setListener(SearchManagerCallbackInterface listener) {
        this.listener = new WeakReference<>(listener);
    }

    /**
     *
     * @param query
     * @return
     */
    public static boolean isSearchValid(String query) {
            return (Utils.isValidUrl(query)); //&& pingUrl(query);
    }

    /**
     * TODO require too much time -.- (do in bckgrnd)
     * @param ip
     * @return
     */
    private static boolean pingUrl(String ip) {
        try {
            Process p = Runtime.getRuntime().exec("ping -c 1 -t 10 " + ip);
            p.waitFor();
            return p.exitValue() == 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * @param menu
     * @param view
     */
    public void initSearchView(Menu menu, @NonNull MaterialSearchView view) {
        try {
            searchView = view;
            searchItem = menu.findItem(R.id.action_search);
            searchView.setMenuItem(searchItem);
            searchView.setOnQueryTextListener(this);
            searchView.setOnSearchViewListener(this);
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

//    /**
//     *  @param views
//     *
//     */
//    public void handleMenuItemActionCollapsedLayout(@NonNull View[] views) {
//        ((FloatingActionsMenu) views[0]).collapse();
//        AnimatorBuilder.getInstance(context).collapseViews(views[0], true);
//    }
//
//    /**
//     *  @param views
//     *
//     */
//    public void handleMenuItemActionExpandLayout(@NonNull View[] views) {
//        ((FloatingActionsMenu) views[0]).collapse();
//        AnimatorBuilder.getInstance(context).collapseViews(views[0], false);
//    }

    @Override
    public void onSearchViewShown() {
        StatusManager.getInstance().setSearchActionbarMode(true);
        if (listener.get() != null)
            listener.get().onOpenSearchView();
    }

    @Override
    public void onSearchViewClosed() {
        StatusManager.getInstance().unsetStatus();
        if (listener.get() != null)
            listener.get().onCloseSearchView();
    }

    /**
     *
     * @return
     */
    public MaterialSearchView getSearchView() {
        return searchView;
    }


    /**
     * filter class handled by search
     */
    private class LinkFilter extends Filter {
        private boolean mCaseSensitive = false;

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
        protected void publishResults(final CharSequence query, FilterResults results) {
            listener.get().searchBy(query.toString().trim().toLowerCase(), mCaseSensitive);
//            //TODO add callback
//            ((Activity) context.get()).runOnUiThread(() -> {
//                try {
//                    String searchValue = ((String) constraint).trim().toLowerCase();
////                    RealmResults list = searchValue.equals("") ?
////                            RealmUtils.getResults(mRealm) : getFilteredList(searchValue, mCaseSensitive);
////                    listener.get().searchBy(searchValue, mCaseSensitive);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
        }

    }

    /**
     *
     */
    public interface SearchManagerCallbackInterface {
        void onOpenSearchView();
        void onCloseSearchView();
        void searchBy(String searchValue, boolean mCaseSensitive);
    }
}
