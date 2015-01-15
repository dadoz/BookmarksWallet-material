package com.application.material.bookmarkswallet.app.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.widget.ActionMenuPresenter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.application.material.bookmarkswallet.app.MainActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.BasePagerAdapter;
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

        mainActivityRef.initActionBarWithToolbar(toolbar);
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) mainActivityRef.
                getSupportActionBar().getCustomView().findViewById(R.id.sliding_tabs);
        viewPager.setAdapter(new BasePagerAdapter(mainActivityRef, 2));
        slidingTabLayout.setViewPager(viewPager);

//		setHasOptionsMenu(true);
        return baseFragment;
    }
}