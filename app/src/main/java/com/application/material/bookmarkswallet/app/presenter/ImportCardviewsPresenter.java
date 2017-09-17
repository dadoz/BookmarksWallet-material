package com.application.material.bookmarkswallet.app.presenter;

import android.animation.Animator;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.animator.AnimationBuilder;
import com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper;
import com.flurry.android.FlurryAgent;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper.SharedPrefKeysEnum.IMPORT_ACCOUNT_NOTIFIED;
import static com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper.SharedPrefKeysEnum.IMPORT_KEEP_NOTIFIED;

@Deprecated
public class ImportCardviewsPresenter implements View.OnClickListener {
    private final WeakReference<Context> context;
    private final View view;
    @BindView(R.id.importFromAccountButtonId)
    View importFromAccountButton;
    @BindView(R.id.importFromKeepButtonId)
    View importFromKeepButton;
    @BindView(R.id.importFromAccountDismissButtonId)
    View importFromAccountDismissButton;
    @BindView(R.id.importFromKeepDismissButtonId)
    View importFromKeepDismissButton;
    @BindView(R.id.importFromAccountCardviewLayoutId)
    View importFromAccountCardviewLayout;
    @BindView(R.id.importFromKeepCardviewLayoutId)
    View importFromKeepCardviewLayout;

    public ImportCardviewsPresenter(WeakReference<Context> ctx, View v) {
        context = ctx;
        view = v;
        ButterKnife.bind(ctx.get(), view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.importFromKeepButtonId:
                FlurryAgent.logEvent("importFromKeep", true);
                dismissCardview(v);
                break;
            case R.id.importFromAccountButtonId:
                FlurryAgent.logEvent("importFromAccount", true);
                dismissCardview(v);
                break;
            case R.id.importFromAccountDismissButtonId:
            case R.id.importFromKeepDismissButtonId:
                dismissCardview(v);
                break;
        }
    }


    /**
     *
     * @param v
     */
    private void dismissCardview(View v) {
        switch (v.getId()) {
            case R.id.importFromKeepButtonId:
            case R.id.importFromKeepDismissButtonId:
                SharedPrefHelper.getInstance(context.get())
                        .setValue(IMPORT_KEEP_NOTIFIED, true);
                hideViewAnimator(importFromKeepCardviewLayout);
//                recyclerView.scrollToPosition(0);
                importNotificationToUser();
                break;
            case R.id.importFromAccountButtonId:
            case R.id.importFromAccountDismissButtonId:
                SharedPrefHelper.getInstance(context.get())
                        .setValue(IMPORT_ACCOUNT_NOTIFIED, true);
                hideViewAnimator(importFromAccountCardviewLayout);
//                recyclerView.scrollToPosition(0);
                importNotificationToUser();
                break;
        }
    }

    /**
     *
     */
    private void importNotificationToUser() {
        Toast.makeText(context.get(), R.string.youll_be_notified, Toast.LENGTH_SHORT).show();
    }

    /**
     *
     * @param view
     */
    private void hideViewAnimator(final View view) {
        Animator animator = AnimationBuilder.getInstance(context.get())
                .getYTranslation(view, 0, -view.getMeasuredHeight(), 0);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }



    /**
     *
     */
    private void initImportViews() {
        importFromKeepButton.setOnClickListener(this);
        importFromKeepDismissButton.setOnClickListener(this);
        importFromAccountButton.setOnClickListener(this);
        importFromAccountDismissButton.setOnClickListener(this);
        if ((boolean) SharedPrefHelper.getInstance(context.get())
                .getValue(IMPORT_KEEP_NOTIFIED, false)) {
            importFromKeepCardviewLayout.setVisibility(View.GONE);
        }
        if ((boolean) SharedPrefHelper.getInstance(context.get())
                .getValue(IMPORT_ACCOUNT_NOTIFIED, false)) {
            importFromAccountCardviewLayout.setVisibility(View.GONE);
        }
    }
}
