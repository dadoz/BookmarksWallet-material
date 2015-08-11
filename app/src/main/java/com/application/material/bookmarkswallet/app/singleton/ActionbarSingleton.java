package com.application.material.bookmarkswallet.app.singleton;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnInitActionBarInterface;


/**
 * Created by davide on 18/03/15.
 */
public class ActionbarSingleton implements OnInitActionBarInterface {

    private static final String TAG = "ActionbarSingleton";
    private static Activity mActivityRef;
    private static ActionbarSingleton mSingletonRef;
//    private static ScrollManager scrollManager;
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


//    public void setToolbarScrollManager(final RecyclerView recyclerView, final List<View> viewList, int adsOffsetHeight) {
//        scrollManager = new ScrollManager(mActivityRef, adsOffsetHeight);
//        try {
//            toolbar.post(new Runnable() {
//                @Override public void run() {
//                    scrollManager.attach(recyclerView);
//                    for (View view : viewList) {
//                        scrollManager.addView(view, ScrollManager.Direction.DOWN);
//                    }
//                }
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private android.support.v7.app.ActionBar getActionBar() {
        return ((AppCompatActivity) mActivityRef).getSupportActionBar();

    }

    public boolean setTitle(String title) {
        try {
            String mainTitle = mActivityRef.getResources().getString(R.string.main_title);
            getActionBar().setTitle(title == null ? mainTitle : title);
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
                    getDrawable(R.color.yellow_400));
//            setStatusBarColor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT > 21) {
            Window window = mActivityRef.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int color = mActivityRef.getResources()
                    .getColor(isEditMode() ? R.color.material_mustard_yellow :
                            R.color.material_mustard_yellow_700);
            window.setStatusBarColor(color);
        }
    }

    /**
     * change background color
     * @param toolbarColorRes
     * @param statusbarColorRes
     */
    public void setBackgroundColor(int toolbarColorRes, int statusbarColorRes) {
        getActionBar().setBackgroundDrawable(mActivityRef.getResources().
                getDrawable(toolbarColorRes));
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = mActivityRef.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int color = mActivityRef.getResources()
                    .getColor(statusbarColorRes);
            window.setStatusBarColor(color);
        }
    }

    public void setDisplayHomeEnabled(boolean isHomeUpEnabled) {
        getActionBar().setDisplayHomeAsUpEnabled(isHomeUpEnabled);
        getActionBar().setDisplayShowHomeEnabled(isHomeUpEnabled);
    }

    public void setSearchMode(boolean searchMode) {
        this.mSearchMode = searchMode;
    }

    public boolean isSearchMode() {
        return mSearchMode;
    }

    public boolean isEditMode() {
        return mEditItemPos != NOT_SELECTED_ITEM_POSITION;
    }

    public int getEditItemPos() {
        return mEditItemPos;
    }

    public void setEditItemPos(int editItemPos) {
        this.mEditItemPos = editItemPos;
    }

    public void hideSoftKeyboard(EditText editText) {
        if(editText == null) {
            return;
        }

        ((InputMethodManager) mActivityRef
                .getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


    public boolean isPanelExpanded() {
        return mPanelExpanded;
    }

    public void setPanelExpanded(boolean panelExpanded) {
        this.mPanelExpanded = panelExpanded;
    }
}
