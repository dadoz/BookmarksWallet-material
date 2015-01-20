package com.application.material.bookmarkswallet.app.animators;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;

/**
 * Created by davide on 20/01/15.
 */
public class CustomDefaultAnimator extends DefaultItemAnimator {
    public CustomDefaultAnimator() {
        super();
    }

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        return false;
    }
}