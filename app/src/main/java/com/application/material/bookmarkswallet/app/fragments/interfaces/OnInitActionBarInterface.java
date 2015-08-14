package com.application.material.bookmarkswallet.app.fragments.interfaces;

import android.graphics.drawable.Drawable;

/**
 * Created by davide on 07/11/14.
 */
public interface OnInitActionBarInterface {
    public void initActionBar();
    public boolean setTitle(String title);
    public boolean setTitle(String title, int color);
    public void udpateActionbar(boolean isHomeUpEnabled);
    public void udpateActionbar(boolean isHomeUpEnabled,
                                   int actionbarColor, Drawable toolbarColor);
}
