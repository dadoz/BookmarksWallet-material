package com.application.material.bookmarkswallet.app.manager;

public class StatusManager {
    private static StatusManager instance;
    private enum StatusEnum {ADD_ON_SEARCH, ADD_ON_RESULT}
    private StatusEnum currentStatus = StatusEnum.ADD_ON_SEARCH; //todo default
    public static StatusManager getInstance() {
        return instance == null ? instance = new StatusManager() : instance;
    }

    /**
     *
     * @return
     */
    public boolean isOnSearchMode() {
        return currentStatus == StatusEnum.ADD_ON_SEARCH;
    }

    /**
     *
     * @return
     */
    public boolean isOnResultMode() {
        return currentStatus == StatusEnum.ADD_ON_RESULT;
    }

    /**
     *
     */
    public void setOnSearchMode() {
        currentStatus = StatusEnum.ADD_ON_SEARCH;
    }

    /**
     *
     */
    public void setOnResultMode() {
        currentStatus = StatusEnum.ADD_ON_RESULT;
    }
}
