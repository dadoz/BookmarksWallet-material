package com.application.material.bookmarkswallet.app.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.application.material.bookmarkswallet.app.R;

public class TopSnackBarView extends RelativeLayout {
    public TopSnackBarView(Context context) {
        super(context);
        init();
    }

    public TopSnackBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TopSnackBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(23)
    public TopSnackBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        inflate(getContext(), R.layout.top_snack_bar_layout, this);
    }
}
