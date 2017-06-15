package com.application.material.bookmarkswallet.app.navigationDrawer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.application.material.bookmarkswallet.app.fragments.SettingsFragment;

/**
 * Created by davide on 13/06/2017.
 */

class ActivityUtils {

    /**
     * handle static
     * @param supportFragmentManager
     * @return
     */
    public static Fragment findLastFragment(FragmentManager supportFragmentManager) {
        try {
            int index = supportFragmentManager.getBackStackEntryCount() -1;
            String tag = supportFragmentManager.getBackStackEntryAt(index).getName();
            return supportFragmentManager.findFragmentByTag(tag);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param fragmentManager
     * @param fragment
     * @return
     */
    public static boolean isSameFrag(FragmentManager fragmentManager, Fragment fragment) {
        Fragment lastFrag = findLastFragment(fragmentManager);
        return lastFrag != null &&
                lastFrag.getClass().equals(fragment.getClass());
    }
}
