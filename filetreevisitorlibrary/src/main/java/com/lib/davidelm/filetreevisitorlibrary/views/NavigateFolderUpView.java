package com.lib.davidelm.filetreevisitorlibrary.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.lib.davidelm.filetreevisitorlibrary.R;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

import java.lang.ref.WeakReference;


/**
 * Created by davide on 21/05/2017.
 */

public class NavigateFolderUpView extends LinearLayout {
    String TAG = "NavigateFolderUpView";
    private WeakReference<OnClickListener> lst;

    public NavigateFolderUpView(Context context) {
        super(context);
        initView();
    }

    public NavigateFolderUpView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public NavigateFolderUpView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public NavigateFolderUpView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.navigate_folder_up_view, this);
    }

    public void setLst(OnClickListener lst) {
        setOnClickListener(lst);
    }
}
