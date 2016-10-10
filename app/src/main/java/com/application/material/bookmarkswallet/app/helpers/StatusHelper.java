package com.application.material.bookmarkswallet.app.helpers;

public class StatusHelper {
    private static StatusHelper mInstance;
    public enum StatusEnum {EDIT, SYNC, SEARCH, IDLE}
    private StatusEnum mCurrentStatus = StatusEnum.IDLE;

    private StatusHelper() {
    }

    /**
     *
     * @return
     */
    public static StatusHelper getInstance() {
        return mInstance == null ?
                mInstance = new StatusHelper() :
                mInstance;
    }

    /**
     * get current status
     */
    public StatusEnum getCurrentStatus() {
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
    public void setEditMode() {
        mCurrentStatus = StatusEnum.EDIT;
    }

    /**
     *
     * @return
     */
//    public int getEditItemPos() {
//        return mCurrentStatus == StatusEnum.EDIT ? mEditItemPos : EDIT_POS_NOT_SET;
//    }

    /**
     *
     * @param pos
     */
//    public void setEditItemPos(int pos) {
//        mEditItemPos = pos;
//    }

}
