package com.application.material.bookmarkswallet.app.recyclerView;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by davide on 06/05/15.
 */
public class CustomSwipeRefreshLayout extends SwipeRefreshLayout {
    private static final String TAG = "CustomSwipeRefreshLayout";
    private View mDownView;

    public CustomSwipeRefreshLayout(Context context) {
        super(context);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent (MotionEvent ev) {
        Log.e(TAG, "hey touch event intercept " + ev.getAction());
        if(ev.getAction() == MotionEvent.ACTION_DOWN) {
            Log.e(TAG, "ACTION DOWN");
        }

        if(ev.getAction() == MotionEvent.ACTION_MOVE) {
            if(mDownView != null) {
                mDownView.setPressed(false);
            }
            Log.e(TAG, "ACTION_MOVE");
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setDownView(View view) {
        mDownView = view;
    }
}
