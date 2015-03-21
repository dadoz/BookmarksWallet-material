package com.application.material.bookmarkswallet.app.fragments.interfaces;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

/**
 * Created by davide on 07/11/14.
 */
public interface OnInitActionBarInterface {
    public void initActionBarWithCustomView(Toolbar toolbar);
    public void initActionBar();

    public void initToggleSettings(boolean isFragment, boolean isColor);
    public void toggleActionBar(String title);
    public boolean isChangeFragment();
    public void setIsChangeFragment(boolean value);
    public boolean isChangeColor();
    public void setIsChangeColor(boolean value);


}
