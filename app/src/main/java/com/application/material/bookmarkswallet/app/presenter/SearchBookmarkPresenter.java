package com.application.material.bookmarkswallet.app.presenter;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.animator.AnimatorBuilder;

import java.lang.ref.WeakReference;

public class SearchBookmarkPresenter implements TextWatcher {
    private static SearchBookmarkPresenter instance;
    private EditText urlEditTextView;
    private View clipboardFabButton;
    private View searchButton;
    private TextInputLayout urlTextInputLayoutView;

    /**
     *
     * @return
     */
    public static SearchBookmarkPresenter getInstance() {
        return instance == null ? instance = new SearchBookmarkPresenter() : instance;
    }

    /**
     *
     * @param viewArray
     */
    public void init(@NonNull  View[] viewArray) {
        urlEditTextView = (EditText) viewArray[0];
        urlTextInputLayoutView = (TextInputLayout) viewArray[3];
        clipboardFabButton = viewArray[1];
        searchButton = viewArray[2];
        urlEditTextView.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        showErrorOnUrlEditText(false);
        boolean isQueryEmpty = charSequence.length() == 0;
        clipboardFabButton.setVisibility(isQueryEmpty ? View.VISIBLE : View.GONE);
        searchButton.setVisibility(isQueryEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    /**
     *
     * @param showError
     */
    public void showErrorOnUrlEditText(boolean showError) {
        urlTextInputLayoutView.setError(null);
        if (showError) {
            urlTextInputLayoutView.setError("wrong error");
        }
    }
}
