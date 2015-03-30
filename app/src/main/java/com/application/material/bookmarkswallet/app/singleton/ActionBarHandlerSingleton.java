package com.application.material.bookmarkswallet.app.singleton;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.animators.ScrollManager;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeActionbarLayoutAction;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnInitActionBarInterface;
import com.getbase.floatingactionbutton.FloatingActionButton;

/**
 * Created by davide on 18/03/15.
 */
public class ActionBarHandlerSingleton implements OnInitActionBarInterface,
        OnChangeActionbarLayoutAction {

    private static final String TAG = "ActionBarHandlerSingleton";
    private static Activity mActivtyRef;
    private static ActionBarHandlerSingleton mSingletonRef;
    private static ScrollManager scrollManager;
    private View actionbarInfoActionView;
    private View actionbarAddBookmarkActionView;
    private boolean isChangeColor;
    private boolean isBackOverridden;
    private View infoView;
    private boolean editMode;

    private ActionBarHandlerSingleton() {
    }

    public static ActionBarHandlerSingleton getInstance(Activity activityRef) {
        mActivtyRef = activityRef;
        scrollManager = new ScrollManager();
        return mSingletonRef == null ?
                mSingletonRef = new ActionBarHandlerSingleton() : mSingletonRef;
    }

    @Override
    public void setActivtyRef(Activity activtyRef) {
        mActivtyRef = activtyRef;
    }

    @Override
    public void initActionBarWithCustomView(Toolbar toolbar) {
    }

    public void initActionBar() {
        android.support.v7.app.ActionBar actionBar = setActionBar();
        try {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowCustomEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private android.support.v7.app.ActionBar setActionBar() {
        final Toolbar toolbar = (Toolbar) mActivtyRef.findViewById(R.id.toolbarId);
        ((ActionBarActivity) mActivtyRef).setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar =
                ((ActionBarActivity) mActivtyRef).getSupportActionBar();
        return actionBar;
    }

    public void initActionBar(final RecyclerView recyclerView, final FloatingActionButton fab) {
        android.support.v7.app.ActionBar actionBar = setActionBar(recyclerView, fab);
        try {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowCustomEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private android.support.v7.app.ActionBar setActionBar(final RecyclerView recyclerView, final FloatingActionButton fab) {
        final Toolbar toolbar = (Toolbar) mActivtyRef.findViewById(R.id.toolbarId);
        toolbar.post(new Runnable() {
            @Override public void run() {
                scrollManager.attach(recyclerView);
                if(actionbarInfoActionView != null) {
                    scrollManager.addView(actionbarInfoActionView, ScrollManager.Direction.UP);
                }
                scrollManager.addView(fab, ScrollManager.Direction.DOWN);
                scrollManager.setInitialOffset(toolbar.getHeight());
            }
        });

        ((ActionBarActivity) mActivtyRef).setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar =
                ((ActionBarActivity) mActivtyRef).getSupportActionBar();
        return actionBar;
    }

    private android.support.v7.app.ActionBar getActionBar() {
        return ((ActionBarActivity) mActivtyRef).getSupportActionBar();

    }

    @Override
    public boolean setTitle(String title) {
        try {
            String appName = mActivtyRef.getResources().getString(R.string.app_name);
            getActionBar().setTitle(title == null ? appName : title);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void toggleActionBar(String title, boolean isBack, boolean isColor, int layoutId) {
        isBackOverridden = isBack;
        isChangeColor = isColor;
        toggleActionBar(title);
        toggleLayoutByActionMenu(layoutId);
    }

    @Override
    public void toggleActionBar(String title, boolean isBack, boolean isColor) {
        isBackOverridden = isBack;
        isChangeColor = isColor;
        toggleActionBar(title);
    }

    @Override
    public void toggleActionBar(String title) {
        android.support.v7.app.ActionBar actionBar = getActionBar();

        setTitle(title);
        boolean isHomeUpEnabled = title != null;
        actionBar.setDisplayHomeAsUpEnabled(isHomeUpEnabled);
        actionBar.setDisplayShowHomeEnabled(isHomeUpEnabled);
        actionBar.setBackgroundDrawable(mActivtyRef.getResources().
                getDrawable(isChangeColor ?
                        R.color.material_blue_grey :
                        R.color.material_mustard_yellow));
    }

    @Override
    public void setViewOnActionMenu(View view, int layoutId) {
        switch (layoutId) {
            case R.id.infoButtonLayoutId:
                actionbarInfoActionView = view;
                break;
            case R.id.addBookmarkLayoutId:
                actionbarAddBookmarkActionView = view;
                break;
        }
    }
    @Override
    public void setViewOnActionMenu(View view, int layoutId, View.OnClickListener listener) {
        switch (layoutId) {
            case R.id.infoButtonLayoutId:
                actionbarInfoActionView = view;
                actionbarInfoActionView.setOnClickListener(listener);
                break;
            case R.id.addBookmarkLayoutId:
                actionbarAddBookmarkActionView = view;
                actionbarAddBookmarkActionView.setOnClickListener(listener);
                break;
        }
    }

    @Override
    public void toggleLayoutByActionMenu(int layoutId) {
        switch (layoutId) {
            case R.id.infoButtonLayoutId:
                int visibility = actionbarInfoActionView.getVisibility();
                actionbarInfoActionView.setVisibility(visibility == View.VISIBLE ?
                        View.GONE : View.VISIBLE);
                break;
            case R.id.addBookmarkLayoutId:
                visibility = actionbarAddBookmarkActionView.getVisibility();
                actionbarAddBookmarkActionView.setVisibility(visibility == View.VISIBLE ?
                        View.GONE : View.VISIBLE);
                break;
        }

    }

    @Override
    public void showLayoutByActionMenu(int layoutId) {
        switch (layoutId) {
            case R.id.infoButtonLayoutId:
                actionbarInfoActionView.setVisibility(View.VISIBLE);
                break;
            case R.id.addBookmarkLayoutId:
                actionbarAddBookmarkActionView.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Override
    public void hideLayoutByActionMenu(int layoutId) {
        switch (layoutId) {
            case R.id.infoButtonLayoutId:
                actionbarInfoActionView.setVisibility(View.GONE);
                break;
            case R.id.addBookmarkLayoutId:
                actionbarAddBookmarkActionView.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void toggleInnerLayoutByActionMenu(int layoutId) {
        switch (layoutId) {
            case R.id.infoButtonLayoutId:
                View innerView = actionbarInfoActionView.findViewById(R.id.actionbarInfoInnerLayoutId);
                View outerView = actionbarInfoActionView.findViewById(R.id.actionbarInfoOuterLayoutId);
                //inner template
                innerView.setVisibility(innerView.getVisibility() == View.VISIBLE ?
                        View.GONE : View.VISIBLE);

                //outer template
                outerView.setVisibility(outerView.getVisibility() == View.VISIBLE ?
                        View.GONE : View.VISIBLE);
                break;
            case R.id.addBookmarkLayoutId:
//                visibility = actionbarAddBookmarkActionView.getVisibility();
//                actionbarAddBookmarkActionView.setVisibility(visibility == View.VISIBLE ?
//                        View.GONE : View.VISIBLE);
                break;
        }

    }


    @Override
    public boolean getOverrideBackPressed() {
        return isBackOverridden;
    }

    @Override
    public void setOverrideBackPressed(boolean value) {
        isBackOverridden = value;
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

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }
}
