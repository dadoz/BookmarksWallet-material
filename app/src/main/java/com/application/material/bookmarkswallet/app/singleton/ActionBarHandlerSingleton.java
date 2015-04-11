package com.application.material.bookmarkswallet.app.singleton;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        OnChangeActionbarLayoutAction, View.OnClickListener {

    private static final String TAG = "ActionBarHandlerSingleton";
    private static Activity mActivtyRef;
    private static ActionBarHandlerSingleton mSingletonRef;
    private static ScrollManager scrollManager;
    private View actionbarInfoActionView;
    private boolean isChangeColor;
    private boolean isBackOverridden;
    private View infoView;
    private boolean editMode;
    private Toolbar toolbar;
    private int mEditItemPos;
    private View         swipeRefreshLayout;

    private ActionBarHandlerSingleton() {
    }

    public static ActionBarHandlerSingleton getInstance(Activity activityRef) {
        mActivtyRef = activityRef;
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
        setActionBar();
        try {
            getActionBar().setDisplayShowTitleEnabled(true);
            getActionBar().setDisplayShowCustomEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*    public void initActionBar(final RecyclerView recyclerView, final FloatingActionButton fab) {
        setToolbarScrollManager(recyclerView, fab);
        try {
            getActionBar().setDisplayShowTitleEnabled(true);
            getActionBar().setDisplayShowCustomEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private void setActionBar() {
        toolbar = (Toolbar) mActivtyRef.findViewById(R.id.toolbarId);
        ((ActionBarActivity) mActivtyRef).setSupportActionBar(toolbar);
    }


    public void setToolbarScrollManager(final RecyclerView recyclerView, final FloatingActionButton fab) {
        final View infoInnerView = actionbarInfoActionView.findViewById(R.id.infoInnerLayoutId);
        scrollManager = new ScrollManager(infoInnerView);
        try {
            toolbar.post(new Runnable() {
                @Override public void run() {
                    scrollManager.attach(recyclerView);
                    if(actionbarInfoActionView != null) {
                        scrollManager.addViewNoDown(infoInnerView, ScrollManager.Direction.UP);
                    }
                    scrollManager.addView(fab, ScrollManager.Direction.DOWN);
                    scrollManager.setInitialOffset(toolbar.getHeight() + infoInnerView.getHeight());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

//        ((ActionBarActivity) mActivtyRef).setSupportActionBar(toolbar);
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
    public void toggleActionBar(boolean isHomeUpEnabled, boolean isBack, boolean isColor, int layoutId) {
        isBackOverridden = isBack;
        isChangeColor = isColor;
        toggleActionBar(isHomeUpEnabled);
        toggleLayoutByActionMenu(layoutId);
    }

    @Override
    public void toggleActionBar(boolean isHomeUpEnabled, boolean isBack, boolean isColor) {
        isBackOverridden = isBack;
        isChangeColor = isColor;
        toggleActionBar(isHomeUpEnabled);
    }

    @Override
    public void toggleActionBar(boolean isHomeUpEnabled) {

        try {
//            setTitle(title);
//            boolean isHomeUpEnabled = title != null;
            setDisplayHomeEnabled(isHomeUpEnabled);
            getActionBar().setBackgroundDrawable(mActivtyRef.getResources().
                    getDrawable(isChangeColor ?
                            R.color.material_blue_grey :
                            R.color.material_mustard_yellow));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDisplayHomeEnabled(boolean isHomeUpEnabled) {
        getActionBar().setDisplayHomeAsUpEnabled(isHomeUpEnabled);
        getActionBar().setDisplayShowHomeEnabled(isHomeUpEnabled);
    }

    @Override
    public void setViewOnActionMenu(View mainView, View view, int layoutId) {
        switch (layoutId) {
            case R.id.actionbarInfoLayoutId:
                        swipeRefreshLayout = mainView;
                actionbarInfoActionView = view;
                break;
        }
    }

    @Override
    public void setViewOnActionMenu(View mainView, View view, int layoutId, View.OnClickListener listener) {
        switch (layoutId) {
            case R.id.actionbarInfoLayoutId:
                swipeRefreshLayout = mainView;
                actionbarInfoActionView = view;
                actionbarInfoActionView.
                        findViewById(R.id.infoOuterButtonId).
                        setOnClickListener(this);
                break;
        }
    }

    @Override
    public void toggleLayoutByActionMenu(int layoutId) {
        switch (layoutId) {
            case R.id.infoOuterButtonId:
                int visibility = actionbarInfoActionView.getVisibility();
                actionbarInfoActionView.setVisibility(visibility == View.VISIBLE ?
                        View.GONE : View.VISIBLE);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)         swipeRefreshLayout.getLayoutParams();
                float height = mActivtyRef.getResources().getDimension(R.dimen.actionbar_infoaction_height);
                params.setMargins(0,
                        visibility == View.VISIBLE ? 0 : (int) height, 0 ,0);
                break;
        }

    }

    @Override
    public void showLayoutByActionMenu(int layoutId) {
        switch (layoutId) {
            case R.id.actionbarInfoLayoutId:
                //animation
                actionbarInfoActionView.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Override
    public void hideLayoutByActionMenu(int layoutId) {
        switch (layoutId) {
            case R.id.actionbarInfoLayoutId:
                //animation
                actionbarInfoActionView.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void toggleInnerLayoutByActionMenu(int layoutId) {
        switch (layoutId) {
            case R.id.infoOuterButtonId:
                View innerView = actionbarInfoActionView.findViewById(R.id.infoInnerLayoutId);
                View outerView = actionbarInfoActionView.findViewById(R.id.infoOuterLayoutId);
                colorizeIcon((ImageView) actionbarInfoActionView.findViewById(R.id.refreshInfoIconId));

                //animation
                innerView.setVisibility(innerView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                int mustardYellow = mActivtyRef.getResources().getColor(R.color.material_mustard_yellow);
                outerView.setBackgroundColor(innerView.getVisibility() == View.VISIBLE ? Color.WHITE : mustardYellow);
                actionbarInfoActionView.findViewById(R.id.refreshInfoIconId).
                        setVisibility(innerView.getVisibility() == View.VISIBLE ?
                                View.VISIBLE : View.GONE);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.infoOuterButtonId:
                toggleInnerLayoutByActionMenu(v.getId());
                break;
        }

    }

    private void colorizeIcon(ImageView imgStatus) {
        // Get the Image container object
        Drawable d = imgStatus.getDrawable();
//                Drawable d = mActivtyRef.getResources().getDrawable(R.drawable.ic_refresh_black_36dp);
        int iconColor = mActivtyRef.getResources().getColor(R.color.material_red);
        d.setColorFilter(iconColor, PorterDuff.Mode.SRC_ATOP);
        imgStatus.setImageDrawable(d);
    }

    public int getEditItemPos() {
        return mEditItemPos;
    }

    public void setEditItemPos(int editItemPos) {
        this.mEditItemPos = editItemPos;
    }
}
