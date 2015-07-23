package com.application.material.bookmarkswallet.app.adapter;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.fragments.TutorialFragment;

/**
 * Created by davide on 15/01/15.
 */
public class TutorialPagerAdapter extends FragmentPagerAdapter implements OnPageChangeListener {
    private final int mTotalItems;
    private String TAG = "SamplePagerAdapter";
    private final Activity mMainActivityRef;

    public TutorialPagerAdapter(FragmentManager fm, Activity activityRef, int totalItems) {
        super(fm);
        mMainActivityRef = activityRef;
        mTotalItems = totalItems;
    }

    /**
     * Instantiate the {@link View} which should be displayed at
     * {@code position}. Here we inflate a layout from the apps resources
     * and then change the text view to signify the position.
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return TutorialFragment.newInstance(position);
            case 1:
                return TutorialFragment.newInstance(position);
            case 2:
                return TutorialFragment.newInstance(position);
        }
        return null;
    }

    /**
     * Return the title of the item at {@code position}. This is important
     * as what this method returns is what is displayed in the
     * {@link com.nispok.views.SlidingTabLayout}.
     * <p>
     * Here we construct one using the position value, but for real
     * application the title should refer to the item's contents.
     */
    @Override
    public CharSequence getPageTitle(int position) {
        Resources res = mMainActivityRef.getResources();
        switch (position) {
            case 0:
                return res.getString(R.string.links_tab_name);
            case 1:
                return res.getString(R.string.import_export_tab_name);
            case 2:
                return res.getString(R.string.import_export_tab_name);
            default:
                return "generic tab";
        }
    }

    /**
     * @return the number of pages to display
     */
    @Override
    public int getCount() {
        return mTotalItems;
    }

    /**
     *
     * @param position
     * @param positionOffset
     * @param positionOffsetPixels
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     *
     * @param position
     */
    @Override
    public void onPageSelected(int position) {

    }

    /**
     *
     * @param state
     */
    @Override
    public void onPageScrollStateChanged(int state) {

    }

}

