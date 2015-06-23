package com.application.material.bookmarkswallet.app.models;

import android.view.View;

/**
 * Created by davide on 21/06/15.
 */
public class Setting {

    private final boolean mSwitchCheck;
    private String mLabel;
    private int mSwitchVisibility;

    public Setting(String label, int switchVisibility, boolean switchCheck) {
        this.mLabel = label;
        this.mSwitchVisibility = switchVisibility;
        this.mSwitchCheck = switchCheck;
    }

    public String getLabel() {
        return mLabel;
    }

    public boolean isSwitchVisible() {
        return mSwitchVisibility == View.VISIBLE;
    }

    public boolean isSwitchCheck() {
        return mSwitchCheck;
    }
}
