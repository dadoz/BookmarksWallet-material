package com.application.material.bookmarkswallet.app.manager;

public class StatusManager {
    private static StatusManager mInstance;
    public enum StatusEnum {IDLE, EDIT, SYNC, SEARCH, ACTION_MENU}
    private StatusEnum currentStatus = StatusEnum.IDLE;
    private enum StatusSearchEnum {ADD_ON_SEARCH, ADD_ON_RESULT}
    private StatusSearchEnum currentStatusSearch = StatusSearchEnum.ADD_ON_SEARCH;

    private StatusManager() {
    }

    /**
     *
     * @return
     */
    public static StatusManager getInstance() {
        return mInstance == null ?
                mInstance = new StatusManager() :
                mInstance;
    }

    /**
     * get current status
     */
    public StatusEnum getCurrentStatus() {
        return this.currentStatus;
    }

    /**
     * get current status
     * @param currentStatus
     */
    public void setCurrentStatus(StatusEnum currentStatus) {
        this.currentStatus = currentStatus;
    }

    /**
     *
     */
    public void unsetStatus() {
        currentStatus = StatusEnum.IDLE;
    }

    /**
     *
     * @return
     */
    public boolean isIdleMode() {
        return currentStatus == StatusEnum.IDLE;
    }

    /**
     *
     */
    public void setSyncMode() {
        currentStatus = StatusEnum.SYNC;
    }

    /**
     *
     * @return
     */
    public boolean isSyncMode() {
        return currentStatus == StatusEnum.SEARCH;
    }

    /**
     *
     * @param searchMode
     */
    public void setSearchActionbarMode(boolean searchMode) {
        currentStatus = searchMode ? StatusEnum.SEARCH : StatusEnum.IDLE;
    }

    /**
     *
     * @return
     */
    public boolean isSearchActionbarMode() {
        return currentStatus == StatusEnum.SEARCH;
    }

    /**
     *
     * @return
     */
    public boolean isEditMode() {
        return currentStatus == StatusEnum.EDIT;
    }

    /**
     *
     * @return
     */
    public void setEditMode() {
        currentStatus = StatusEnum.EDIT;
    }

    /**
     *
     */
    public void setOnActionMenuMode() {
        currentStatus = StatusEnum.ACTION_MENU;
    }

    /**
     *
     * @return
     */
    public boolean isOnActionMenuMode() {
        return currentStatus == StatusEnum.ACTION_MENU;
    }

    /**
     *
     * @return
     */
    public boolean isOnSearchMode() {
        return currentStatusSearch == StatusSearchEnum.ADD_ON_SEARCH;
    }

    /**
     *
     * @return
     */
    public boolean isOnResultMode() {
        return currentStatusSearch == StatusSearchEnum.ADD_ON_RESULT;
    }

    /**
     *
     */
    public void setOnSearchMode() {
        currentStatusSearch = StatusSearchEnum.ADD_ON_SEARCH;
    }

    /**
     *
     */
    public void setOnResultMode() {
        currentStatusSearch = StatusSearchEnum.ADD_ON_RESULT;
    }
}
