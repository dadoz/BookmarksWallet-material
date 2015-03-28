package com.application.material.bookmarkswallet.app.singleton;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeActionbarLayoutAction;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnInitActionBarInterface;

/**
 * Created by davide on 18/03/15.
 */
public class ActionBarHandlerSingleton implements OnInitActionBarInterface,
        OnChangeActionbarLayoutAction {

    private static final String TAG = "ActionBarHandlerSingleton";
    private static Activity mActivtyRef;
    private static ActionBarHandlerSingleton mSingletonRef;
    private View actionbarInfoActionView;
    private boolean isChangeColor;
    private boolean isChangeFragment;
    private View infoView;

    private ActionBarHandlerSingleton() {
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
            actionbarInfoActionView = getActionBar().getCustomView().findViewById(R.id.infoLayoutId);
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

//    @Override
//    public View setDefaultActionMenu(int layoutId, View.OnClickListener listenerRef) {
//        return null;
//    }

//    @Override
//    public void showDefaultActionMenu(View actionMenu) {
//    }

    @Override
    public void showLayoutByMenuAction(int actionId) {
        View view = actionbarInfoActionView;
        String title = "Info";

        toggleActionBar(title);
        actionbarInfoActionView.setVisibility(View.VISIBLE);

//        if(getActionBar() != null &&
//                view != null) {
//
//            toggleActionBar(title);
//            if (view.getParent() == null) {
//                ((ViewGroup) getActionBar().getCustomView()).addView(view);
//            }
//        }
    }

    @Override
    public void hideLayoutByMenuAction() {
        //remove view if I'll find it
        actionbarInfoActionView.setVisibility(View.GONE);
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

/*    private void setListenerOnLayoutTransition() {
        final int[] animCounter = {4};
        View animatedLayout = getActionBar().getCustomView().findViewById(R.id.actionbarLinkListLayoutId);
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.addTransitionListener(new LayoutTransition.TransitionListener() {

            @Override
            public void endTransition(LayoutTransition arg0, ViewGroup arg1,
                                      View arg2, int arg3) {
                animCounter[0] --;
                if(animCounter[0] == 0) {
                    toggleActionBar(null);
                    animCounter[0] = 4;
                }
            }

            @Override
            public void startTransition(LayoutTransition transition,
                                        ViewGroup container, View view, int transitionType) {
                Log.e(TAG, "start transition");

            }});
        ((LinearLayout) animatedLayout).setLayoutTransition(layoutTransition);
    }*/
}
