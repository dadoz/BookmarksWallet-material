package com.application.material.bookmarkswallet.app.presenter;

import android.animation.Animator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.animator.AnimatorBuilder;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by davide on 23/10/15.
 */
public class AddBookmarkUrlEditTextPresenter implements UrlEditTextInterface, TextWatcher {
    private final WeakReference<EditText> titleField;
    private final WeakReference<EditText> urlField;
    private final FloatingActionButton clipboardFab;
    private final FloatingActionButton addFab;
    private final AnimatorBuilder animatorBuilder;
    private boolean animate = true;
    private static int HIDDEN_VIEW_Y;
    @Bind(R.id.noBookmarkPreviewLayoutId)
    View noBookmarkPreviewLayout;
    @Bind(R.id.addInfoTitleTextInputId)
    View addInfoTitleTextInput;
    @Bind(R.id.addInfoIconLayoutId)
    View addInfoIconLayout;

    /**
     *
     * @param urlField
     * @param titleField
     * @param addFab
     * @param clipboardFab
     * @param view
     * @return
     */
    public static AddBookmarkUrlEditTextPresenter init(
            @NonNull WeakReference<EditText> urlField, @NonNull WeakReference<EditText> titleField,
            @NonNull FloatingActionButton addFab, @NonNull FloatingActionButton clipboardFab,
            @NonNull AnimatorBuilder animatorBuilder, @NonNull View view) {
        return new AddBookmarkUrlEditTextPresenter(urlField, titleField,
                addFab, clipboardFab, animatorBuilder, view);
    }

    private AddBookmarkUrlEditTextPresenter(WeakReference<EditText> urlField,
                                            WeakReference<EditText> titleField,
                                            FloatingActionButton addFab,
                                            FloatingActionButton clipboardFab,
                                            AnimatorBuilder animatorBuilder,
                                            View view) {
        ButterKnife.bind(this, view);
        this.urlField = urlField;
        this.titleField = titleField;
        this.addFab = addFab;
        this.clipboardFab = clipboardFab;
        this.animatorBuilder = animatorBuilder;
        initPresenter();
    }

    /**
     * init field
     */
    public void initPresenter() {
        HIDDEN_VIEW_Y = -1000; //TODO calculate view height
        addInfoIconLayout.setTranslationY(HIDDEN_VIEW_Y);
        addInfoTitleTextInput.setTranslationY(HIDDEN_VIEW_Y);
        urlField.get().addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        int iconDelay = 100;
        if (isEmptyString(s)) {
            animate = true;
            showPasteClipboardFab(true);
            initNoBookmarkPreviewAnimator(true).start();
            initAddInfoBookmarkAnimator(false, addInfoIconLayout, iconDelay).start();
            initAddInfoBookmarkAnimator(false, addInfoTitleTextInput, 0).start();
            titleField.get().setText("");
            return;
        }

        //animate only once
        if (!animate) {
            return;
        }
        showPasteClipboardFab(false);
        initNoBookmarkPreviewAnimator(false).start();
        initAddInfoBookmarkAnimator(true, addInfoIconLayout, iconDelay).start();
        initAddInfoBookmarkAnimator(true, addInfoTitleTextInput, 0).start();
        animate = false;
    }

    /**
     *
     * @param emptyString
     * @return
     */
    private Animator initAddInfoBookmarkAnimator(boolean emptyString, View view, int delay) {
        return emptyString ?
                animatorBuilder.getYTranslation(view, HIDDEN_VIEW_Y, 0, delay) :
                animatorBuilder.getYTranslation(view, 0, HIDDEN_VIEW_Y, 0);
    }

    /**
     *
     * @param emptyString
     * @return
     */
    private Animator initNoBookmarkPreviewAnimator(boolean emptyString) {
        return emptyString ?
                animatorBuilder.buildHideAnimator(noBookmarkPreviewLayout, true) :
                animatorBuilder.buildShowAnimator(noBookmarkPreviewLayout, false);
    }

    /**
     *
     * @param isShowing
     */
    private void showPasteClipboardFab(boolean isShowing) {
        if (isShowing) {
            clipboardFab.show();
            addFab.hide();
            return;
        }
        clipboardFab.hide();
        addFab.show();

    }

    /**
     *
     * @param s
     * @return
     */
    private static boolean isEmptyString(Editable s) {
        return s.length() == 0;
    }
}
