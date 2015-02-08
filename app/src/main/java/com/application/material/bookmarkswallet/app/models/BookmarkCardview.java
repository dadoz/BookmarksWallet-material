package com.application.material.bookmarkswallet.app.models;

/**
 * Created by davide on 07/02/15.
 */
public class BookmarkCardview {

    public enum CardviewTypeEnum { INFO_CARDVIEW, IMPORT_CARDVIEW }
    private final CardviewTypeEnum type;
    private String title;

    public BookmarkCardview(CardviewTypeEnum type, String title) {
        this.title = title;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CardviewTypeEnum getType() {
        return type;
    }

}
