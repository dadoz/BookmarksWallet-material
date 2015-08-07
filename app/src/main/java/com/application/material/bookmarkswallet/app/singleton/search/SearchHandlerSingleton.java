package com.application.material.bookmarkswallet.app.singleton.search;

import android.app.Activity;
import android.view.Menu;

/**
 * Created by davide on 04/08/15.
 */
public class SearchHandlerSingleton {

    private static SearchHandlerSingleton mInstance;
    private static Activity mActivityRef;

    public SearchHandlerSingleton() {
    }

    /**
     *
     * @param activity
     * @return
     */
    public static SearchHandlerSingleton getInstance(Activity activity) {
        mActivityRef = activity;
        return mInstance == null ?
                mInstance = new SearchHandlerSingleton() :
                mInstance;
    }
    
    /**
     * TODO refactoring it
     * @param menu
     */
    public void handleSearch(Menu menu) {
//        //SEARCH ITEM
//        mSearchItem = menu.findItem(R.id.action_search);
//        SearchManager searchManager = (SearchManager) mActivityRef.getSystemService(Context.SEARCH_SERVICE);
//
//        SearchView searchView = null;
//        if (mSearchItem != null) {
//            if (mActionbarSingleton.isSearchMode()) {
//                mSearchItem.expandActionView();
//            }
//
//            searchView = (SearchView) mSearchItem.getActionView();
//            MenuItemCompat.setOnActionExpandListener(mSearchItem,
//                    new MenuItemCompat.OnActionExpandListener() {
//                        @Override
//                        public boolean onMenuItemActionExpand(MenuItem item) {
//                            mActionbarSingleton.setSearchMode(true);
////                            ((BookmarkRecyclerViewAdapter) mRecyclerView.getAdapter())
////                                    .setSearchMode(true);
//                            mRecyclerView.getAdapter().notifyDataSetChanged();
//                            int startDelay = 600;
//                            hideClipboardButton(startDelay);
////                            mSlidingLayerLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
////                            mAdsView.setVisibility(View.GONE);
//                            return true;
//                        }
//
//                        @Override
//                        public boolean onMenuItemActionCollapse(MenuItem item) {
//                            mActionbarSingleton.setSearchMode(false);
////                            ((BookmarkRecyclerViewAdapter) mRecyclerView.getAdapter())
////                                    .setSearchMode(false);
//                            rvActionsSingleton.setAdapter();
////                            mAdsView.setVisibility(View.VISIBLE);
//                            showClipboardButton();
//                            return true;
//                        }
//                    });
//        }
//
//        if (searchView != null) {
//            searchView.setSearchableInfo(searchManager
//                    .getSearchableInfo(mActivityRef.getComponentName()));
//            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                public boolean onQueryTextChange(String newText) {
//                    if(newText.trim().toLowerCase().equals("")) {
//                        rvActionsSingleton.setAdapter();
//                        return true;
//                    }
//
//                    getFilter().filter(newText);
//                    return true;
//                }
//
//                public boolean onQueryTextSubmit(String query) {
//                    return false;
//                }
//            });
//        }
    }
}
