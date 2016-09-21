package com.application.material.bookmarkswallet.app.manager;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.BookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.animator.AnimatorBuilder;
import com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.utlis.ConnectionUtils;
import com.application.material.bookmarkswallet.app.utlis.RealmUtils;
import com.application.material.bookmarkswallet.app.utlis.Utils;

import java.lang.ref.WeakReference;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class SearchManager implements Filterable, SearchView.OnQueryTextListener {
    private static WeakReference<Context> context;
    private static Realm mRealm;
    private static SearchManager instance;
    private String mFilterString;
    private MenuItem searchItem;
    private BookmarkRecyclerViewAdapter adapter;

    public SearchManager() {
    }

    /**
     * 
     * @param ctx
     * @param realm
     * @return
     */
    public static SearchManager getInstance(WeakReference<Context> ctx, Realm realm) {
        context = ctx;
        mRealm = realm;
        return instance == null ?
                instance = new SearchManager() :
                instance;
    }

    /**
     *
     * @param query
     * @return
     */
    public static boolean search(String query) {
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
     *
     * @param adpt
     */
    public void setAdapter(BookmarkRecyclerViewAdapter adpt) {
        adapter = adpt;
    }

    /**
     * @param menu
     */
    public void initSearchView(Menu menu) {
        try {
            searchItem = menu.findItem(R.id.action_search);
            android.app.SearchManager searchManager = (android.app.SearchManager) context.get()
                    .getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) searchItem.getActionView();
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(((Activity) context.get()).getComponentName()));
            searchView.setOnQueryTextListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * collapse search view
     */
    public void collapseSearchView() {
        if (searchItem != null) {
            searchItem.collapseActionView();
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
     * @param list
     */
    private void updateDataSet(RealmResults<Bookmark> list) {
        adapter.updateData(list);
        adapter.notifyDataSetChanged();
    }

    /**
     * TODO move in filter class
     * set empty search result view
     */
    private RealmResults getFilteredList(String filterString,
                                         boolean isCaseSensitive) {
        boolean isSearchOnUrlEnabled = Utils.getSearchOnUrlEnabledFromSharedPref(context);

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
     *
     * @param views
     */
    public void handleMenuItemActionCollapsedLayout(@NonNull View[] views) {
        collapseViews(views[0], views[1], true);
        views[2].setVisibility(View.VISIBLE);
        views[3].setVisibility(View.GONE); //TODO PATCH
    }

    /**
     *
     * @param views
     */
    public void handleMenuItemActionExpandLayout(@NonNull View[] views) {
        collapseViews(views[0], views[1], false);
        views[2].setVisibility(View.GONE);
    }


    /**
     *
     * @param fab
     * @param cardview
     * @param collapsing
     */
    private void collapseViews(View fab, final View cardview, final boolean collapsing) {
        Animator fabAnimator = collapsing ?
                AnimatorBuilder.getInstance(context)
                        .buildHideAnimator(fab, false) :
                AnimatorBuilder.getInstance(context)
                        .buildShowAnimator(fab, false);
        Animator cardviewAnimator = collapsing ?
                AnimatorBuilder.getInstance(context)
                        .getYTranslation(cardview, -cardview.getMeasuredHeight(), 0, 0):
                AnimatorBuilder.getInstance(context)
                        .getYTranslation(cardview, 0, -cardview.getMeasuredHeight(), 0);


        AnimatorSet temp = new AnimatorSet();
        temp.playTogether(fabAnimator, cardviewAnimator);
        cardviewAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (collapsing) {
                    cardview.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!collapsing) {
                    cardview.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        temp.start();
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
                        updateDataSet(list);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

}
