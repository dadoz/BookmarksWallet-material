package com.application.material.bookmarkswallet.app.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.application.material.bookmarkswallet.app.R;

/**
 * Created by davide on 15/01/15.
 */
public class BasePagerAdapter extends PagerAdapter {
    private String TAG = "SamplePagerAdapter";
    private final Activity mainActivityRef;
    private int maxPageCounter;

    public BasePagerAdapter(Activity activityRef, int n) {
        this.mainActivityRef = activityRef;
        maxPageCounter = n;
    }
    /**
     * @return the number of pages to display
     */
    @Override
    public int getCount() {
        return maxPageCounter;
    }

    /**
     * @return true if the value returned from
     *         {@link #instantiateItem(android.view.ViewGroup, int)} is the same object
     *         as the {@link android.view.View} added to the {@link android.support.v4.view.ViewPager}.
     */
    @Override
    public boolean isViewFromObject(View view, Object o) {
        return o == view;
    }

    // BEGIN_INCLUDE (pageradapter_getpagetitle)
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
        return "Item " + (position + 1);
    }

    // END_INCLUDE (pageradapter_getpagetitle)

    /**
     * Instantiate the {@link View} which should be displayed at
     * {@code position}. Here we inflate a layout from the apps resources
     * and then change the text view to signify the position.
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // Inflate a new layout from our resources
        View view = null;
        switch (position) {
            case 0:

                view = mainActivityRef.getLayoutInflater().inflate(R.layout.links_list_layout,
                        container, false);
                break;
            case 1:
                view = mainActivityRef.getLayoutInflater().inflate(R.layout.add_bookmark_fragment,
                        container, false);
        }

        // Add the newly created View to the ViewPager
        container.addView(view);

        // Retrieve a TextView from the inflated View, and update it's text
//        TextView title = (TextView) view.findViewById(R.id.item_title);
//        title.setText(String.valueOf(position + 1));

        Log.i(TAG, "instantiateItem() [position: " + position + "]");

        // Return the View
        return view;
    }

    /**
     * Destroy the item from the {@link android.support.v4.view.ViewPager}. In our case this is
     * simply removing the {@link View}.
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        Log.i(TAG, "destroyItem() [position: " + position + "]");
    }
}


