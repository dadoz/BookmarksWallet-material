package com.lib.davidelm.filetreevisitorlibrary.views;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.lib.davidelm.filetreevisitorlibrary.R;

/**
 * Created by davide on 07/06/2017.
 */

public class ExclusiveSelectionButton extends AppCompatImageView {
    private static Drawable checkCircleGrey;
    private static Drawable checkCircle;

    public ExclusiveSelectionButton(Context context) {
        super(context);
        initView();
    }

    public ExclusiveSelectionButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ExclusiveSelectionButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        setImageDrawable(checkCircleGrey);
        checkCircle = ContextCompat.getDrawable(getContext(), R.mipmap.ic_check_circle);
        checkCircleGrey = ContextCompat.getDrawable(getContext(), R.mipmap.ic_check_circle_grey);
//        checkCircleGrey.setColorFilter(ContextCompat.getColor(getContext(), android.R.color.background_dark),
//                        PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void setSelected(boolean select) {
        super.setSelected(select);
        setImageDrawable(select ? checkCircle : checkCircleGrey);
    }
}
