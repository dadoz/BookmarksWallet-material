package com.application.material.bookmarkswallet.app.presenter;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.animator.AnimatorBuilder;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddBookmarkUrlEditTextPresenter implements UrlEditTextInterface,
        TextWatcher, LayoutTransition.TransitionListener {
    private final WeakReference<EditText> titleField;
    private final WeakReference<EditText> urlField;
    private final AnimatorBuilder animatorBuilder;

//    @BindView(R.id.noBookmarkPreviewLayoutId)
//    View noBookmarkPreviewLayout;
//    @BindView(R.id.iconImageViewId)
//    View iconImageView;
    @BindView(R.id.addBookmarkFabId)
    FloatingActionButton addFab;
    @BindView(R.id.pasteClipboardFabId)
    FloatingActionButton clipboardFab;
    @BindView(R.id.addBookmarkMainContainerLayoutId)
    ViewGroup parentView;
    private boolean alreadyAnimated = false;

    /**
     *
     * @param urlField
     * @param titleField
     * @param view
     * @return
     */
    public static AddBookmarkUrlEditTextPresenter init(
            @NonNull WeakReference<EditText> urlField, @NonNull WeakReference<EditText> titleField,
            @NonNull AnimatorBuilder animatorBuilder, @NonNull View view) {
        return new AddBookmarkUrlEditTextPresenter(urlField, titleField,
                animatorBuilder, view);
    }

    /**
     *
     * @param urlField
     * @param titleField
     * @param animatorBuilder
     * @param view
     */
    private AddBookmarkUrlEditTextPresenter(WeakReference<EditText> urlField,
                                            WeakReference<EditText> titleField,
                                            AnimatorBuilder animatorBuilder,
                                            View view) {
        ButterKnife.bind(this, view);
        this.urlField = urlField;
        this.titleField = titleField;
        this.animatorBuilder = animatorBuilder;
        initPresenter();
    }

    /**
     * init field
     */
    public void initPresenter() {
//        parentView.removeView(addInfoTitleTextInput);
        parentView.getLayoutTransition().addTransitionListener(this);
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
        boolean emptyString = isEmptyString(s);
        if (!emptyString &&
            alreadyAnimated) {
            return;
        }
        alreadyAnimated = !emptyString;
//        initHideShowAnimator(noBookmarkPreviewLayout, !emptyString).start();
//        initHideShowAnimator(iconImageView, emptyString).start();
//        animateOnRemoveView(addInfoTitleTextInput, emptyString);
        if (emptyString) {
            titleField.get().setText("");
        }
    }

    /**
     *
     * @param view
     * @param removing
     */
    private void animateOnRemoveView(View view, boolean removing) {
        if (removing) {
            parentView.removeView(view);
            return;
        }
        parentView.addView(view);
    }

    /**
     *
     * @param emptyString
     * @return
     */
//    private Animator initAddInfoBookmarkAnimator(boolean emptyString, View view, int delay) {
//        return emptyString ?
//                animatorBuilder.getYTranslation(view, HIDDEN_VIEW_Y, 0, delay) :
//                animatorBuilder.getYTranslation(view, 0, HIDDEN_VIEW_Y, 0);
//    }

    /**
     *
     * @param showing
     * @return
     */
    private Animator initHideShowAnimator(View view, boolean showing) {
        return showing ?
                animatorBuilder.buildShowAnimator(view, true) :
                animatorBuilder.buildHideAnimator(view, true);
    }

    /**
     *
     * @param showing
     */
    private void showPasteClipboardFab(boolean showing) {
        if (showing) {
            clipboardFab.show();
            return;
        }
//        addIconImageFab.show();
        addFab.show();
    }

    /**
     *
     */
    private void hideAllActionFabs() {
//        addIconImageFab.hide();
        addFab.hide();
        clipboardFab.hide();
    }

    /**
     *
     * @param s
     * @return
     */
    private static boolean isEmptyString(Editable s) {
        return s.length() == 0;
    }


    @Override
    public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
        hideAllActionFabs();
    }

    @Override
    public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
        switch (transitionType) {
            case LayoutTransition.APPEARING:
                showPasteClipboardFab(false);
                break;
            case LayoutTransition.CHANGE_DISAPPEARING:
                showPasteClipboardFab(true);
                break;
        }
    }
}
