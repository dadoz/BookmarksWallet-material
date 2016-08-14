package com.application.material.bookmarkswallet.app.singleton;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnInitActionBarInterface;


/**
 * Created by davide on 18/03/15.
 */
public class ActionbarSingleton implements OnInitActionBarInterface {

    private static final String TAG = "ActionbarSingleton";
    public static Activity activityRef;
    private static ActionbarSingleton instanceRef;

    private ActionbarSingleton() {
    }

    public static ActionbarSingleton getInstance(Activity activity) {
        activityRef = activity;
        return instanceRef == null ?
                instanceRef = new ActionbarSingleton() : instanceRef;
    }

    /**
     * init action bar
     */
    @Override
    public void initActionBar() {
        try {
            setActionBar();
            getActionBar().setDisplayShowTitleEnabled(true);
            getActionBar().setDisplayShowCustomEnabled(false);
            setElevation(10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param elevation
     */
    public void setElevation(float elevation) {
        try {
            getActionBar().setElevation(elevation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * init title on toolbar
     * @param title
     * @return
     */
    @Override
    public boolean setTitle(String title) {
        try {
            String mainTitle = activityRef.getResources().getString(R.string.main_title);
            getActionBar().setTitle(title == null ? mainTitle : title);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * init title on toolbar
     * @param title
     * @param color
     * @return
     */
    @Override
    public boolean setTitle(String title, int color) {
        try {
            setTitle(title);
            Toolbar toolbar = (Toolbar) activityRef.findViewById(R.id.toolbarId);
            toolbar.setTitleTextColor(color);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     *
     * @param isHomeUpEnabled
     */
    @Override
    public void updateActionBar(boolean isHomeUpEnabled) {
        try {
            setDisplayHomeEnabled(isHomeUpEnabled);
            setStatusbarColor(getDefaultActionbarColor());
            setToolbarColor(getDefaultToolbarDrawableColor());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param isHomeUpEnabled
     * @param actionbarColor
     * @param toolbarColor
     */
    @Override
    public void updateActionBar(boolean isHomeUpEnabled,
                                int actionbarColor, Drawable toolbarColor) {
        try {
            setDisplayHomeEnabled(isHomeUpEnabled);
            setStatusbarColor(actionbarColor);
            setToolbarColor(toolbarColor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * set toolbar color
     */
    private void setToolbarColor(Drawable drawableColor) {
        getActionBar().setBackgroundDrawable(drawableColor);
    }

    /**
     *
     * @param isHomeUpEnabled
     */
    public void setDisplayHomeEnabled(boolean isHomeUpEnabled) {
        getActionBar().setDisplayHomeAsUpEnabled(isHomeUpEnabled);
        getActionBar().setDisplayShowHomeEnabled(isHomeUpEnabled);
    }

    /**
     *
     * @param color
     */
    private void setStatusbarColor(int color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activityRef.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        }
    }

    /**
     * set toolbar
     */
    private void setActionBar() {
        Toolbar toolbar = (Toolbar) activityRef.findViewById(R.id.toolbarId);
        ((AppCompatActivity) activityRef).setSupportActionBar(toolbar);
    }

    /**
     * get toolbar
     * @return
     */
    private android.support.v7.app.ActionBar getActionBar() {
        return ((AppCompatActivity) activityRef).getSupportActionBar();
    }

    /**
     * default toolbar color
     * @return
     */
    private Drawable getDefaultToolbarDrawableColor() {
        return activityRef.getResources()
                .getDrawable(R.color.yellow_400);
    }

    /**
     * default actionbar color
     * @return
     */
    private int getDefaultActionbarColor() {
        return activityRef.getResources().getColor(R.color.yellow_600);
    }

}
