package com.application.material.bookmarkswallet.app.manager;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.animator.AnimatorBuilder;
import com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.utlis.RealmUtils;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.lang.ref.WeakReference;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class SearchManager implements Filterable,
        MaterialSearchView.OnQueryTextListener, MaterialSearchView.SearchViewListener {
    private static WeakReference<Context> context;
    private static Realm mRealm;
    private static SearchManager instance;
    private String mFilterString;
    private MenuItem searchItem;
    private static WeakReference<SearchManagerCallbackInterface> listener;
    private View addNewFab;
    private MaterialSearchView searchView;

    public SearchManager() {
    }

    /**
     * 
     * @param ctx
     * @param realm
     * @return
     */
    public static SearchManager getInstance(WeakReference<Context> ctx, Realm realm,
                                            WeakReference<SearchManagerCallbackInterface> lst) {
        context = ctx;
        mRealm = realm;
        listener = lst;
        return instance == null ?
                instance = new SearchManager() :
                instance;
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
     * @param views
     */
    public void initSearchView(Menu menu, @NonNull View[] views) {
        try {
            searchView = (MaterialSearchView) views[0];
            addNewFab = views[1];
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

    /**
     * TODO move in filter class
     * set empty search result view
     */
    private RealmResults getFilteredList(String filterString,
                                         boolean isCaseSensitive) {
        boolean isSearchOnUrlEnabled = (boolean) SharedPrefHelper.getInstance(context)
                .getValue(SharedPrefHelper.SharedPrefKeysEnum.SEARCH_URL_MODE, false);

        mFilterString = filterString;
        RealmQuery<Bookmark> query = mRealm.where(Bookmark.class);
        if (isSearchOnUrlEnabled) {
            query
                    .contains(Bookmark.urlField, filterString, isCaseSensitive?
                            Case.SENSITIVE : Case.INSENSITIVE)
                    .or();
        }
        return query
                .contains(Bookmark.nameField, filterString, isCaseSensitive?
                        Case.SENSITIVE : Case.INSENSITIVE)
                .findAll();
    }

    /**
     *
     * @return
     */
    public String getFilterString() {
        return mFilterString;
    }

    /**
     *  @param views
     *
     */
    public void handleMenuItemActionCollapsedLayout(@NonNull View[] views) {
        //TODO leak - weak please
        ((FloatingActionsMenu) views[0]).collapse();
        AnimatorBuilder.getInstance(context).collapseViews(views[0], true);
    }

    /**
     *  @param views
     *
     */
    public void handleMenuItemActionExpandLayout(@NonNull View[] views) {
        //TODO leak - weak please
        ((FloatingActionsMenu) views[0]).collapse();
        AnimatorBuilder.getInstance(context).collapseViews(views[0], false);
    }

    @Override
    public void onSearchViewShown() {
        handleMenuItemActionExpandLayout(new View[] {addNewFab});
        StatusManager.getInstance().setSearchActionbarMode(true);
        if (listener.get() != null)
            listener.get().onOpenSearchView();
    }

    @Override
    public void onSearchViewClosed() {
        handleMenuItemActionCollapsedLayout(new View[] {addNewFab});
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
        protected void publishResults(final CharSequence constraint, FilterResults results) {
            //TODO add callback
            ((Activity) context.get()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String searchValue = ((String) constraint).trim().toLowerCase();
                        RealmResults list = searchValue.equals("") ?
                                RealmUtils.getResults(mRealm) : getFilteredList(searchValue, mCaseSensitive);
                        listener.get().updateSearchDataList(list);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    /**
     *
     */
    public interface SearchManagerCallbackInterface {
        void updateSearchDataList(RealmResults list);
        void onOpenSearchView();
        void onCloseSearchView();
    }
}
