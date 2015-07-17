package com.application.material.bookmarkswallet.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.application.material.bookmarkswallet.app.R;

/**
 * Created by davide on 17/07/15.
 */
public class TutorialFragment extends Fragment {

    private static String ARG_SECTION_NUMBER = "ARG_SECTION_NUMBER";
    private int mSectionNumber;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TutorialFragment newInstance(int sectionNumber) {
        TutorialFragment fragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
        ViewGroup container, Bundle savedInstanceState) {
        mSectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        int layoutRes = getViewLayout();

        return inflater.inflate(layoutRes, container, false);
    }

    public int getViewLayout() {
        switch (mSectionNumber) {
            case 0:
                return R.layout.activity_main;
            case 1:
                return R.layout.activity_main;
            case 2:
                return R.layout.activity_main;
        }
        return R.layout.activity_main;
    }
}
