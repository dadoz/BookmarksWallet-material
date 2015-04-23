package com.application.material.bookmarkswallet.app.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.fragments.ImportBookmarksCardviewFragment;
import com.application.material.bookmarkswallet.app.fragments.BookmarkLinksListFragment;

/**
 * Created by davide on 15/01/15.
 */
public class BaseFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private String TAG = "SamplePagerAdapter";
    private final Activity mainActivityRef;
    private int maxPageCounter;

    public BaseFragmentPagerAdapter(FragmentManager fm, Activity activityRef, int n) {
        super(fm);
        this.mainActivityRef = activityRef;
        maxPageCounter = n;
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
                return new BookmarkLinksListFragment();
            case 1:
                return new ImportBookmarksCardviewFragment();
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
        switch (position) {
            case 0:
                return mainActivityRef.getResources().getString(R.string.links_tab_name);
            case 1:
                return mainActivityRef.getResources().getString(R.string.import_export_tab_name);
            default:
                return "generic tab";
        }
    }

    /**
     * @return the number of pages to display
     */
    @Override
    public int getCount() {
        return maxPageCounter;
    }


    /**
     * Destroy the item from the {@link android.support.v4.view.ViewPager}. In our case this is
     * simply removing the {@link android.view.View}.
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        Log.i(TAG, "destroyItem() [position: " + position + "]");
    }

}

