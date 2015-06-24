package com.application.material.bookmarkswallet.app.singleton;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.animators.ScrollManager;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnInitActionBarInterface;

import java.util.List;

/**
 * Created by davide on 18/03/15.
 */
public class ActionbarSingleton implements OnInitActionBarInterface {

    private static final String TAG = "ActionbarSingleton";
    private static Activity mActivityRef;
    private static ActionbarSingleton mSingletonRef;
    private static ScrollManager scrollManager;
    private Toolbar toolbar;

    public static int NOT_SELECTED_ITEM_POSITION = -1;
    private int mEditItemPos = NOT_SELECTED_ITEM_POSITION;
    private boolean mSearchMode;
    private boolean mPanelExpanded;

    //LAYOUT MANAGER TYPE
//    public enum LayoutManagerTypeEnum { GRID, LIST };
//    private LayoutManagerTypeEnum layoutManagerType = LayoutManagerTypeEnum.LIST;

    private ActionbarSingleton() {
    }

    public static ActionbarSingleton getInstance(Activity activityRef) {
        mActivityRef = activityRef;
        return mSingletonRef == null ?
                mSingletonRef = new ActionbarSingleton() : mSingletonRef;
    }

    public void setActivtyRef(Activity activtyRef) {
        mActivityRef = activtyRef;
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

    private void setActionBar() {
        toolbar = (Toolbar) mActivityRef.findViewById(R.id.toolbarId);
        ((AppCompatActivity) mActivityRef).setSupportActionBar(toolbar);
    }


    public void setToolbarScrollManager(final RecyclerView recyclerView, final List<View> viewList) {
        scrollManager = new ScrollManager(mActivityRef);
        try {
            toolbar.post(new Runnable() {
                @Override public void run() {
                    scrollManager.attach(recyclerView);
//                    if(actionbarInfoActionView != null) {
//                        scrollManager.addViewNoDown(infoInnerView, ScrollManager.Direction.UP);
//                    }
//                    scrollManager.setInitialOffset(toolbar.getHeight() + infoInnerView.getHeight());
                    scrollManager.addView(viewList.get(0), ScrollManager.Direction.DOWN);
                    scrollManager.addView(viewList.get(1), ScrollManager.Direction.DOWN);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

//        ((ActionBarActivity) mActivityRef).setSupportActionBar(toolbar);
    }

    private android.support.v7.app.ActionBar getActionBar() {
        return ((AppCompatActivity) mActivityRef).getSupportActionBar();

    }

    public boolean setTitle(String title) {
        try {
            String appName = mActivityRef.getResources().getString(R.string.app_name);
            getActionBar().setTitle(title == null ? appName : title);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    @Override
    public void changeActionbar(boolean isHomeUpEnabled) {
        try {
            setDisplayHomeEnabled(isHomeUpEnabled);
            getActionBar().setBackgroundDrawable(mActivityRef.getResources().
                    getDrawable(isEditMode() ?
                            R.color.material_mustard_yellow_300 :
                            R.color.material_mustard_yellow));
            setStatusBarColor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }

        Window window = mActivityRef.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int color = mActivityRef.getResources()
                .getColor(isEditMode() ? R.color.material_mustard_yellow :
                        R.color.material_mustard_yellow_700);
        window.setStatusBarColor(color);
    }

    public void setDisplayHomeEnabled(boolean isHomeUpEnabled) {
        getActionBar().setDisplayHomeAsUpEnabled(isHomeUpEnabled);
        getActionBar().setDisplayShowHomeEnabled(isHomeUpEnabled);
    }


    public boolean isEditMode() {
        return mEditItemPos != NOT_SELECTED_ITEM_POSITION;
//        return editMode;
    }

    public int getEditItemPos() {
        return mEditItemPos;
    }

    public void setEditItemPos(int editItemPos) {
        this.mEditItemPos = editItemPos;
    }

    //TODO move out
    public void setColorFilter(Drawable drawable, int color) {
        drawable.setColorFilter(mActivityRef.getResources()
                        .getColor(color),
                PorterDuff.Mode.SRC_IN);
    }

    public void setColorResourceFilter(Drawable drawable, int colorResource) {
        if (drawable == null) {
            return;
        }
        drawable.setColorFilter(colorResource,
                PorterDuff.Mode.SRC_IN);
    }

    public void hideSoftKeyboard(EditText editText) {
        if(editText == null) {
            return;
        }

        ((InputMethodManager) mActivityRef
                .getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void setSearchMode(boolean searchMode) {
        this.mSearchMode = searchMode;
    }

    public boolean isSearchMode() {
        return mSearchMode;
    }

    public boolean isPanelExpanded() {
        return mPanelExpanded;
    }

    public void setPanelExpanded(boolean panelExpanded) {
        this.mPanelExpanded = panelExpanded;
    }


/*
    public void setInfoView(View infoView) {
        this.infoView = infoView;
    }

    public View getInfoView() {
        return infoView;
    }


    public static void setColorFilter(Drawable drawable, int color, Activity activityRef) {
        drawable.setColorFilter(activityRef.getResources()
                        .getColor(color),
                PorterDuff.Mode.SRC_IN);
    }

    private void colorizeIcon(ImageView imgStatus) {
        // Get the Image container object
        Drawable d = imgStatus.getDrawable();
//                Drawable d = mActivityRef.getResources().getDrawable(R.drawable.ic_refresh_black_36dp);
        int iconColor = mActivityRef.getResources().getColor(R.color.material_red);
        d.setColorFilter(iconColor, PorterDuff.Mode.SRC_ATOP);
        imgStatus.setImageDrawable(d);
    }

    public void setLayoutManagerType(LayoutManagerTypeEnum type) {
        layoutManagerType = type;
    }

    public boolean isLayoutManagerGrid() {
        return layoutManagerType == LayoutManagerTypeEnum.GRID;
    }

    public boolean isLayoutManagerList() {
        return layoutManagerType == LayoutManagerTypeEnum.LIST;
    }

    @Override
    public void toggleLayoutByActionMenu(int layoutId) {
        switch (layoutId) {
            case R.id.infoOuterButtonId:
                int visibility = actionbarInfoActionView.getVisibility();
                actionbarInfoActionView.setVisibility(visibility == View.VISIBLE ?
                        View.GONE : View.VISIBLE);

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                        swipeRefreshLayout.getLayoutParams();
                float height = mActivityRef.getResources().getDimension(R.dimen.actionbar_infoaction_height);
                params.setMargins(0,
                        visibility == View.VISIBLE ? 0 : (int) height, 0 ,0);
                break;
        }

    }*/
/*
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
    }*/

//    @Override
//    public void toggleInnerLayoutByActionMenu(int layoutId) {
//        switch (layoutId) {
//            case R.id.action_info:
//                View innerView = actionbarInfoActionView.findViewById(R.id.infoInnerLayoutId);
//                View outerView = actionbarInfoActionView.findViewById(R.id.infoOuterLayoutId);
//                colorizeIcon((ImageView) actionbarInfoActionView.findViewById(R.id.refreshInfoIconId));
//
//                animation
//                innerView.setVisibility(innerView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
//                outerView.setBackgroundColor(innerView.getVisibility() == View.VISIBLE ? Color.WHITE : mustardYellow);
//                outerView.setBackgroundColor(Color.WHITE);
//                actionbarInfoActionView.findViewById(R.id.refreshInfoIconId).
//                        setVisibility(innerView.getVisibility() == View.VISIBLE ?
//                                View.VISIBLE : View.GONE);
//                break;
//        }
//
//    }

//    @Override
//    public boolean getOverrideBackPressed() {
//        return mOverrideBack;
//    }
//
//    @Override
//    public void setOverrideBackPressed(boolean value) {
//        mOverrideBack = value;
//    }


}
