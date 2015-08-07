package com.application.material.bookmarkswallet.app.singleton;

import android.app.Activity;

/**
 * Created by davide on 04/08/15.
 */
public class StateSingleton {

    private static StateSingleton mInstance;

    public StateSingleton() {
    }

    /**
     *
     * @return
     */
    public static StateSingleton getInstance() {
        return mInstance == null ?
                mInstance = new StateSingleton() :
                mInstance;
    }

    /**
     *
     * @param searchMode
     */
    public void setSearchMode(boolean searchMode) {
//        this.mSearchMode = searchMode;
    }

    /**
     *
     * @return
     */
    public boolean isSearchMode() {
//        return mSearchMode;
        return false;
    }

    /**
     *
     * @return
     */
    public boolean isEditMode() {
//        return mEditItemPos != NOT_SELECTED_ITEM_POSITION;
        return false;
    }

    /**
     *
     * @return
     */
    public int getEditItemPos() {
//        return mEditItemPos;
        return 0;
    }

    /**
     *
     * @param editItemPos
     */
    public void setEditItemPos(int editItemPos) {
//        this.mEditItemPos = editItemPos;
    }

    private void setSearchMode() {
//        if (mActionbarSingleton.isSearchMode()) {
//            mAdsView.setVisibility(View.GONE);
//            mClipboardFloatingButton.hide(false);
//        }
    }

}
