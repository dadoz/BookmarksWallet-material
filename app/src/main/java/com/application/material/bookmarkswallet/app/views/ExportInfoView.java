package com.application.material.bookmarkswallet.app.views;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.application.material.bookmarkswallet.app.R;

/**
 * Created by davide on 18/06/2017.
 */

public class ExportInfoView extends RelativeLayout {
    public ExportInfoView(@NonNull Context context) {
        super(context);
        initView();
    }

    public ExportInfoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ExportInfoView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.export_bookmarks_info_layout, this);
    }

}
