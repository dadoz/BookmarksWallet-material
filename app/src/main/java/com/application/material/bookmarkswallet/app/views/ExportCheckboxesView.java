package com.application.material.bookmarkswallet.app.views;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.application.material.bookmarkswallet.app.R;

/**
 * Created by davide on 18/06/2017.
 */

public class ExportCheckboxesView extends FrameLayout {
    public ExportCheckboxesView(@NonNull Context context) {
        super(context);
        initView();
    }

    public ExportCheckboxesView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ExportCheckboxesView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.export_bookmarks_checkboxes_layout, this);
    }

}
