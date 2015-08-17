package com.application.material.bookmarkswallet.app.singleton;

/**
 * Created by davide on 04/08/15.
 */
public class StatusSingleton {
    private static StatusSingleton mInstance;
    public static final int EDIT_POS_NOT_SET = -1;

    private enum StatusEnum {EDIT, SYNC, SEARCH, IDLE}
    private int mEditItemPos;
    private StatusEnum mCurrentStatus = StatusEnum.IDLE;

    public StatusSingleton() {
    }

    /**
     *
     * @return
     */
    public static StatusSingleton getInstance() {
        return mInstance == null ?
                mInstance = new StatusSingleton() :
                mInstance;
    }

    /**
     * get current status
     */
    StatusEnum getCurrentStatus() {
        return this.mCurrentStatus;
    }

    /**
     * get current status
     * @param mCurrentStatus
     */
    public void setCurrentStatus(StatusEnum mCurrentStatus) {
        this.mCurrentStatus = mCurrentStatus;
    }

    /**
     *
     */
    public void unsetStatus() {
        mCurrentStatus = StatusEnum.IDLE;
    }

    /**
     *
     * @return
     */
    public boolean isIdleMode() {
        return mCurrentStatus == StatusEnum.IDLE;
    }

    /**
     *
     */
    public void setSyncMode() {
        mCurrentStatus = StatusEnum.SYNC;
    }

    /**
     *
     * @return
     */
    public boolean isSyncMode() {
        return mCurrentStatus == StatusEnum.SEARCH;
    }

    /**
     *
     * @param searchMode
     */
    public void setSearchMode(boolean searchMode) {
        mCurrentStatus = searchMode ? StatusEnum.SEARCH : StatusEnum.IDLE;
    }

    /**
     *
     * @return
     */
    public boolean isSearchMode() {
        return mCurrentStatus == StatusEnum.SEARCH;
    }

    /**
     *
     * @return
     */
    public boolean isEditMode() {
        return mCurrentStatus == StatusEnum.EDIT;
    }

    /**
     *
     * @return
     */
    public void setEditMode(int pos) {
        mCurrentStatus = StatusEnum.EDIT;
        setEditItemPos(pos);
    }

    /**
     *
     * @return
     */
    public int getEditItemPos() {
        return mCurrentStatus == StatusEnum.EDIT ? mEditItemPos : EDIT_POS_NOT_SET;
    }

    /**
     *
     * @param pos
     */
    public void setEditItemPos(int pos) {
        mEditItemPos = pos;
    }
}
