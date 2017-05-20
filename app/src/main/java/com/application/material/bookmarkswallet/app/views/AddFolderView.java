package com.application.material.bookmarkswallet.app.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.application.material.bookmarkswallet.app.R;

import java.lang.ref.WeakReference;

/**
 * Created by davide on 17/05/2017.
 */

public class AddFolderView extends LinearLayout implements View.OnClickListener, TextWatcher {
    private View addFolderButton;
    private EditText addFolderEditText;
    private WeakReference<AddFolderCallbacks> lst;
    private boolean isVisible = false;
    private ImageView closeAddFolderButton;

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
        updateVisibility();
        addFolderButton = findViewById(R.id.addFolderButtonId);
        addFolderEditText = (EditText) findViewById(R.id.addFolderEditTextId);
        closeAddFolderButton = (ImageView) findViewById(R.id.closeAddFolderButtonId);
        addFolderButton.setOnClickListener(this);
        closeAddFolderButton.setOnClickListener(this);
        addFolderEditText.addTextChangedListener(this);
    }

    public void setListener(WeakReference<AddFolderCallbacks> lst) {
        this.lst = lst;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addFolderButtonId:
                String folderName = addFolderEditText.getText().toString();
                setVisibleAndUpdate(false);
                if (lst != null && lst.get() != null &&
                        !folderName.equals(""))
                    lst.get().addFolderActionCb(folderName);
                break;
            case R.id.closeAddFolderButtonId:
                setVisibleAndUpdate(false);
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

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
    public void setVisibleAndUpdate(boolean visible) {
        isVisible = visible;
        updateVisibility();
    }

    public void updateVisibility() {
        setVisibility(isVisible ? VISIBLE : GONE);
        if (lst != null && lst.get() != null)
            lst.get().onUpdatedVisibility(isVisible);
    }

    public interface AddFolderCallbacks {
        void addFolderActionCb(String v);
        void onUpdatedVisibility(boolean isVisible);
    }
}
