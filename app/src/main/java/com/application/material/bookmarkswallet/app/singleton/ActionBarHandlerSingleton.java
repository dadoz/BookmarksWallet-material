package com.application.material.bookmarkswallet.app.singleton;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeActionbarLayoutAction;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnInitActionBarInterface;

/**
 * Created by davide on 18/03/15.
 */
public class ActionBarHandlerSingleton implements OnInitActionBarInterface,
        OnChangeActionbarLayoutAction {

    private static Activity mActivtyRef;
    private static ActionBarHandlerSingleton mSingletonRef;
    private final View actionbarInfoActionView;
    private final View actionbarAddBookmarkInnerView;
    private boolean isChangeColor;
    private boolean isChangeFragment;
    private View infoView;

    private ActionBarHandlerSingleton() {
        actionbarInfoActionView = mActivtyRef.getLayoutInflater().
                inflate(R.layout.actionbar_info_action_layout, null);
        actionbarAddBookmarkInnerView = mActivtyRef.getLayoutInflater().
                inflate(R.layout.actionbar_add_bookmark_inner_layout, null);
    }

    public static ActionBarHandlerSingleton getInstance(Activity activityRef) {
        mActivtyRef = activityRef;
        return mSingletonRef == null ?
                mSingletonRef = new ActionBarHandlerSingleton() : mSingletonRef;
    }

    @Override
    public void initActionBarWithCustomView(Toolbar toolbar) {
    }

    public void initActionBar() {
        android.support.v7.app.ActionBar actionBar = setActionBar();
        try {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.actionbar_link_list_layout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private android.support.v7.app.ActionBar setActionBar() {
        Toolbar toolbar = (Toolbar) mActivtyRef.findViewById(R.id.toolbarId);
        ((ActionBarActivity) mActivtyRef).setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar =
                ((ActionBarActivity) mActivtyRef).getSupportActionBar();
        return actionBar;
    }

    private android.support.v7.app.ActionBar getActionBar() {
        return ((ActionBarActivity) mActivtyRef).getSupportActionBar();

    }

    private boolean setTitle(String title) {
        try {
            ((TextView) getActionBar().getCustomView().
                    findViewById(R.id.actionBarCustomTitleId)).
                    setText(title == null ? "Bookmark Wallet" : title);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void initToggleSettings(boolean isFragment, boolean isColor) {
        isChangeFragment = isFragment;
        isChangeColor = isColor;
    }

    @Override
    public void toggleActionBar(String title) {
        android.support.v7.app.ActionBar actionBar = getActionBar();

        setTitle(title);
        boolean isHomeUpEnabled = title != null &&
                ! title.equals("Add bookmark") &&
                ! title.equals("Info");
        actionBar.setDisplayHomeAsUpEnabled(isHomeUpEnabled);
        actionBar.setDisplayShowHomeEnabled(isHomeUpEnabled);
        actionBar.setBackgroundDrawable(mActivtyRef.getResources().
                getDrawable(isChangeColor ?
                        R.color.material_blue_grey :
                        R.color.material_mustard_yellow));

        if(infoView != null) {
            infoView.setVisibility(title == null && ! isChangeColor ? View.VISIBLE : View.GONE);
        }

        if(isChangeColor) {
            hideLayoutByMenuAction();
        }
    }

    @Override
    public View setDefaultActionMenu(int layoutId, View.OnClickListener listenerRef) {
//        View view = getLayoutInflater().inflate(layoutId, null);
        //TODO refactor it
//        view.findViewById(R.id.actionbarExportActionIconId).setOnClickListener(listenerRef);
//        view.findViewById(R.id.actionbarImportActionIconId).setOnClickListener(listenerRef);
//        view.findViewById(R.id.actionbarInfoActionIconId).setOnClickListener(listenerRef);
//        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
//        if(actionBar != null) {
//            ((ViewGroup) actionBar.getCustomView()).addView(view);
//        }
//        return view;
        return null;
    }

    @Override
    public void showDefaultActionMenu(View actionMenu) {

    }

    @Override
    public void showLayoutByMenuAction(int actionId) {
        View view = null;
        String title = null;
        switch (actionId) {
            case R.id.infoButtonLayoutId:
                title = "Info";
                view = actionbarInfoActionView;

                break;
            case R.id.addLinkButtonId:
                title = "Add bookmark";
                view = actionbarAddBookmarkInnerView;
                break;
        }

        if(infoView != null) {
            infoView.setVisibility(View.GONE);
        }

        if(getActionBar() != null &&
                view != null) {

            toggleActionBar(title);
            if (view.getParent() == null) {
                ((ViewGroup) getActionBar().getCustomView()).addView(view);
            }
        }
    }

    @Override
    public void hideLayoutByMenuAction() {
        //remove view if I'll find it
        View view = getActionBar().getCustomView().
                findViewById(R.id.infoLayoutId);
            ((ViewGroup) getActionBar().getCustomView()).removeView(view);

        view = getActionBar().getCustomView().
                findViewById(R.id.addBookmarkLayoutId);
        ((ViewGroup) getActionBar().getCustomView()).removeView(view);
    }

    @Override
    public boolean isChangeFragment() {
        return isChangeFragment;
    }

    @Override
    public void setIsChangeFragment(boolean value) {
        isChangeFragment = value;
    }

    @Override
    public boolean isChangeColor() {
        return isChangeColor;
    }

    @Override
    public void setIsChangeColor(boolean value) {
        isChangeColor = value;
    }

    public void setInfoView(View infoView) {
        this.infoView = infoView;
    }

    public View getInfoView() {
        return infoView;
    }
}
