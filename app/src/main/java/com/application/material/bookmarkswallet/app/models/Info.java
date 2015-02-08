package com.application.material.bookmarkswallet.app.models;

/**
 * Created by davide on 07/02/15.
 */
public class Info extends BookmarkCardview{
    private String period;
    private int counter;

    public Info(CardviewTypeEnum type, String title, String period, int counter) {
        super(type, title);
        this.period = period;
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }
}
