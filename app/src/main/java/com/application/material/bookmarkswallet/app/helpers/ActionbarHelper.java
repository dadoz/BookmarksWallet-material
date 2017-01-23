package com.application.material.bookmarkswallet.app.helpers;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
public class ActionbarHelper {

    static final float elevation = 10f;

    public static void setElevationOnVIew(@NonNull View appBarLayout, boolean isSet) {
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(appBarLayout,
                    "elevation", isSet ? elevation : 0f));
            appBarLayout.setStateListAnimator(stateListAnimator);
        }
    }

    public static void setDefaultHomeEnambled(@NonNull Activity activity, boolean isEnabled) {
        if (activity.getActionBar() != null) {
            activity.getActionBar().setDisplayHomeAsUpEnabled(isEnabled);
            activity.getActionBar().setDisplayShowHomeEnabled(isEnabled);
        }

    }

//    /**
//     * init action bar
//     */
//    @Override
//    public void initActionBar() {
//        try {
//            setActionBar();
////            getActionBar().setElevation(4.0f);
//            getActionBar().setDisplayHomeAsUpEnabled(false);
//            getActionBar().setDisplayShowTitleEnabled(true);
//            getActionBar().setDisplayShowCustomEnabled(false);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     *
//     * @param elevation
//     */
//    public void setElevation(float elevation) {
//        try {
////            getActionBar().setElevation(elevation);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * init title on toolbar
//     * @param title
//     * @return
//     */
//    @Override
//    public boolean setTitle(String title) {
//        try {
//            String mainTitle = context.get().getResources().getString(R.string.main_title);
//            getActionBar().setTitle(title == null ? mainTitle : title);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     *
//     * @param isHomeUpEnabled
//     */
//    @Override
//    public void updateActionBar(boolean isHomeUpEnabled) {
//        try {
//            setDisplayHomeEnabled(isHomeUpEnabled);
////            setStatusbarColor(getDefaultActionbarColor());
////            setToolbarColor(getDefaultToolbarDrawableColor());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * set toolbar color
//     */
//    private void setToolbarColor(Drawable drawableColor) {
//        getActionBar().setBackgroundDrawable(drawableColor);
//    }
//
//    /**
//     *
//     * @param isHomeUpEnabled
//     */
//    public void setDisplayHomeEnabled(boolean isHomeUpEnabled) {
//        if (getActionBar() != null) {
//            getActionBar().setDisplayHomeAsUpEnabled(isHomeUpEnabled);
//            getActionBar().setDisplayShowHomeEnabled(isHomeUpEnabled);
//        }
//    }
//
//    /**
//     *
//     * @param color
//     */
//    public void setStatusbarColor(int color) {
//        if (Build.VERSION.SDK_INT >= 21) {
//            Window window = ((Activity) context.get()).getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setStatusBarColor(color);
//        }
//    }
//
//    /**
//     * set toolbar
//     */
//    private void setActionBar() {
//        Toolbar toolbar = (Toolbar) ((Activity) context.get()).findViewById(R.id.toolbarId);
//        ((AppCompatActivity) context.get()).setSupportActionBar(toolbar);
//    }
//
//    /**
//     * get toolbar
//     * @return
//     */
//    private android.support.v7.app.ActionBar getActionBar() {
//        return ((AppCompatActivity) context.get()).getSupportActionBar();
//    }
//
//    /**
//     * default toolbar color
//     * @return
//     */
//    private Drawable getDefaultToolbarDrawableColor() {
//        return ContextCompat
//                .getDrawable(context.get(), R.color.yellow_400);
//    }
//
//    /**
//     * default actionbar color
//     * @return
//     */
//    private int getDefaultActionbarColor() {
//        return ContextCompat
//                .getColor(context.get(), R.color.yellow_600);
//    }

}
