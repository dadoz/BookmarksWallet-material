package com.application.material.bookmarkswallet.app.utlis;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.fragments.BookmarkListFragment;
import com.application.material.bookmarkswallet.app.BaseActivity.OnBackPressedHandlerInterface;

/**
 * Created by davide on 13/06/2017.
 */

public class ActivityUtils {

    /**
     * handle static
     * @param supportFragmentManager
     * @return
     */
    public static Fragment findLastFragment(FragmentManager supportFragmentManager) {
        try {
            if (supportFragmentManager.getBackStackEntryCount() == 0)
                return null;

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

    /**
     * handling back pressed
     * @return
     */
    public static OnBackPressedHandlerInterface getBackPressedHandler(FragmentManager fragmentManager) {
        Fragment lastFrag = findLastFragment(fragmentManager);
        if (lastFrag == null || lastFrag instanceof BookmarkListFragment) {
            Fragment frag = fragmentManager.findFragmentByTag(BookmarkListFragment.FRAG_TAG);
            return frag != null && frag instanceof OnBackPressedHandlerInterface ?
                    ((OnBackPressedHandlerInterface) frag) : null;
        }
        return null;
    }

    /**
     * TODO move smwhere
     * init fragment function
     */
    public static void onChangeFragment(FragmentManager fragmentManager, Fragment frag, String tag) {
        boolean isSameFrag = isSameFrag(fragmentManager, frag);
        frag = isSameFrag ? findLastFragment(fragmentManager) : frag;

        if (frag instanceof BookmarkListFragment)
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainerFrameLayoutId, frag, tag);

        if (!isSameFrag &&
                !(frag instanceof BookmarkListFragment))
            transaction.addToBackStack(tag);
        transaction.commit();
    }


}
