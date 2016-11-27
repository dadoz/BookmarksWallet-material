package com.application.material.bookmarkswallet.app.models;

import android.view.View;

import com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper;

public class Setting {

    private final boolean mSwitchCheck;
    private final String mDescription;
    private final SharedPrefHelper.SharedPrefKeysEnum type;
    private String mLabel;
    private int mSwitchVisibility;

    /**
     *
     * @param label
     * @param description
     * @param type
     * @param switchVisibility
     * @param switchCheck
     */
    public Setting(String label, String description, SharedPrefHelper.SharedPrefKeysEnum type,
                   int switchVisibility, boolean switchCheck) {
        this.mLabel = label;
        this.mDescription = description;
        this.mSwitchVisibility = switchVisibility;
        this.mSwitchCheck = switchCheck;
        this.type = type;
    }

    /**
     *
     * @return
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     *
     * @return
     */
    public String getLabel() {
        return mLabel;
    }

    /**
     *
     * @return
     */
    public boolean isSwitchVisible() {
        return mSwitchVisibility == View.VISIBLE;
    }

    /**
     *
     * @return
     */
    public boolean isSwitchCheck() {
        return mSwitchCheck;
    }

    /**
     *
     * @return
     */
    public SharedPrefHelper.SharedPrefKeysEnum getType() {
        return type;
    }
}
