package com.application.material.bookmarkswallet.app.fragments.interfaces;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

/**
 * Created by davide on 07/11/14.
 */
public interface OnInitActionBarInterface {
//    public void initActionBarWithCustomView(Toolbar toolbar);
    public void initActionBar();
    public void setActivtyRef(Activity activtyRef);
    public boolean setTitle(String title);

    public void toggleActionBar(boolean isHomeUpEnabled);
    public void toggleActionBar(boolean isHomeUpEnabled, boolean isFragment, boolean isColor);
    public void toggleActionBar(boolean isHomeUpEnabled, boolean isFragment, boolean isColor, int layoutId);

    public boolean getOverrideBackPressed();
    public void setOverrideBackPressed(boolean value);
    public boolean isChangeColor();
    public void setIsChangeColor(boolean value);


}
