package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.bookmarkswallet.app.MainActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.old.BaseFragmentPagerAdapter;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnChangeFragmentWrapperInterface;
import com.application.material.bookmarkswallet.app.fragments.interfaces.OnInitActionBarInterface;
import com.nispok.views.SlidingTabLayout;

/**
 * Created by davide on 14/01/15.
 */
public class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment_TAG";
    public static final String FRAG_TAG = "BaseFragment";
    private MainActivity mainActivityRef;
    @InjectView(R.id.viewpager)
    ViewPager viewPager;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (! (activity instanceof OnChangeFragmentWrapperInterface)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLoadViewHandlerInterface");
        }
        if (! (activity instanceof OnInitActionBarInterface)) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnInitActionBarInterface");
        }
        mainActivityRef = (MainActivity) activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        View baseFragment = inflater.inflate(R.layout.base_fragment_layout,
                container, false);
        ButterKnife.inject(this, baseFragment);

        Toolbar toolbar = (Toolbar) baseFragment.findViewById(R.id.toolbarId);

//        mainActivityRef.initActionBarWithCustomView(toolbar);

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) mainActivityRef.
                getSupportActionBar().getCustomView().findViewById(R.id.sliding_tabs);

        int[] colors = {getResources().getColor(R.color.material_mustard_yellow)};
        slidingTabLayout.setDividerColors(colors);
        slidingTabLayout.setSelectedIndicatorColors(colors);
        viewPager.setAdapter(new BaseFragmentPagerAdapter(mainActivityRef.getSupportFragmentManager(), mainActivityRef, 2));
        slidingTabLayout.setViewPager(viewPager);
        viewPager.setOnTouchListener(null);

//		setHasOptionsMenu(true);
        return baseFragment;
    }

//    public void notifyToggleEditView(boolean isSelecting) {
//        //LinkList frag index
//        int linkListFragmentIndex = 0;
//        try {
//
//            LinksListFragment fragment = (LinksListFragment)
//                    ((BaseFragmentPagerAdapter) viewPager.getAdapter())
//                            .getItem(linkListFragmentIndex);
//
//            if (fragment != null) {
////                fragment.toggleEditView(isSelecting);
//                return;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Log.e(TAG, "cannot toggle edit view");
//    }
}