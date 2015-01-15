package com.application.material.bookmarkswallet.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.application.material.bookmarkswallet.app.fragments.ExportBookmarksFragment;
import com.application.material.bookmarkswallet.app.fragments.LinksListFragment;

/**
 * Created by davide on 15/01/15.
 */
public class BaseFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private String coffeeMachineId;

    public BaseFragmentPagerAdapter(FragmentManager fm, String coffeeMachineId) {
        super(fm);
        this.coffeeMachineId = coffeeMachineId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new LinksListFragment();
            case 1:
                return new ExportBookmarksFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}

