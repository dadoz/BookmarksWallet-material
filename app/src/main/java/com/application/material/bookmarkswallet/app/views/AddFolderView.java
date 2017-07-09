package com.application.material.bookmarkswallet.app.views;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.utlis.Utils;

import java.lang.ref.WeakReference;

/**
 * Created by davide on 17/05/2017.
 */

public class AddFolderView extends CoordinatorLayout implements View.OnClickListener, TextWatcher {
    private View addFolderButton;
    private TextInputLayout addFolderTextInputLayout;
    private WeakReference<AddFolderCallbacks> lst;
    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheetContainerLayout;

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
        bottomSheetContainerLayout = findViewById(R.id.bottomSheetContainerLayoutId);
        addFolderButton = findViewById(R.id.addFolderButtonId);
        addFolderTextInputLayout = (TextInputLayout) findViewById(R.id.addFolderTextInputLayoutId);
        addFolderButton.setOnClickListener(this);
        bottomSheetContainerLayout.setOnClickListener(this);
        addFolderTextInputLayout.getEditText().addTextChangedListener(this);

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
            case R.id.bottomSheetContainerLayoutId:
                toggle();
                break;
            case R.id.addFolderButtonId:
                add();
                break;
        }
    }

    /**
     *
     */
    private void add() {
        String folderName = addFolderTextInputLayout.getEditText().getText().toString();
        //check empty
        if (folderName.equals("")) {
            showError();
            return;
        }

        setCollapsed(true);
        if (lst != null && lst.get() != null) {
            lst.get().addFolderActionCb(folderName);
        }
    }

    /**
     *
     */
    private void toggle() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            setExpanded();
            if (lst != null && lst.get() != null)
                lst.get().onAddFolderExpanded(bottomSheetContainerLayout);
            return;
        }

        setCollapsed(true);
        if (lst != null && lst.get() != null)
            lst.get().onAddFolderCollapsed(bottomSheetContainerLayout);
    }

    /**
     *
     */
    private void showError() {
        addFolderTextInputLayout.setError(getContext().getString(R.string.empty_folder));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        addFolderTextInputLayout.setError(null);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * set expanded
     */
    public void setExpanded() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    /**
     * set collapsed
     */
    public void setCollapsed() {
        setCollapsed(false);
    }
    /**
     * set collapsed
     */
    public void setCollapsed(boolean hasDelay) {
        Utils.hideKeyboard(getContext());
        addFolderTextInputLayout.getEditText().setText("");
        new Handler().postDelayed(() -> {
            bottomSheetBehavior.setPeekHeight(150);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }, hasDelay ? 100 : 0);

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

    public void hide() {
        setVisibility(GONE);
    }

    public interface AddFolderCallbacks {
        void addFolderActionCb(String v);

        void onAddFolderCollapsed(View bottomSheet);

        void onAddFolderExpanded(View bottomSheet);
    }
}
