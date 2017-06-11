package com.application.material.bookmarkswallet.app.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.application.material.bookmarkswallet.app.R;

import java.lang.ref.WeakReference;

/**
 * Created by davide on 17/05/2017.
 */

public class AddFolderView extends CoordinatorLayout implements View.OnClickListener, TextWatcher {
    private View addFolderButton;
    private EditText addFolderEditText;
    private WeakReference<AddFolderCallbacks> lst;
    private ImageView closeAddFolderButton;
    private BottomSheetBehavior bottomSheetBehavior;

    public AddFolderView(Context context) {
        super(context);
        initView();
    }

    public AddFolderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AddFolderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.add_folder_layout, this);
        addFolderButton = findViewById(R.id.addFolderButtonId);
        addFolderEditText = (EditText) findViewById(R.id.addFolderEditTextId);
        closeAddFolderButton = (ImageView) findViewById(R.id.closeAddFolderButtonId);
        addFolderButton.setOnClickListener(this);
        closeAddFolderButton.setOnClickListener(this);
        addFolderEditText.addTextChangedListener(this);

        //set bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(getChildAt(0));
        bottomSheetBehavior.setBottomSheetCallback(bottoSheetCb);
        setCollapsed();
    }


    public void setListener(WeakReference<AddFolderCallbacks> lst) {
        this.lst = lst;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addFolderButtonId:
                String folderName = addFolderEditText.getText().toString();
                setCollapsed();
                if (lst != null && lst.get() != null &&
                        !folderName.equals(""))
                    lst.get().addFolderActionCb(folderName);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    public void setExpanded() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void setCollapsed() {

        bottomSheetBehavior.setPeekHeight(150);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        bottoSheetCb = null;
    }

    BottomSheetBehavior.BottomSheetCallback bottoSheetCb = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                lst.get().onAddFolderCollapsed(bottomSheet);
            if (newState == BottomSheetBehavior.STATE_EXPANDED)
                lst.get().onAddFolderExpanded(bottomSheet);
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };

    public interface AddFolderCallbacks {
        void addFolderActionCb(String v);
        void onUpdatedVisibility(boolean isVisible);

        void onAddFolderCollapsed(View bottomSheet);

        void onAddFolderExpanded(View bottomSheet);
    }
}
