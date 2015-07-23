package com.application.material.bookmarkswallet.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.application.material.bookmarkswallet.app.MainActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.singleton.SharedPrefSingleton;
import com.application.material.bookmarkswallet.app.utlis.Utils;

/**
 * Created by davide on 17/07/15.
 */
public class TutorialFragment extends Fragment implements View.OnClickListener {

    private static final int LATEST_TUTORIAL_PAGE = 2;
    private static String ARG_SECTION_NUMBER = "ARG_SECTION_NUMBER";
    private int mSectionNumber;
    private Button mFinishTutorialButton;
    private View mView;
    private SharedPrefSingleton mSharedPrefSingleton;

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
        mView = inflater.inflate(getViewLayoutRes(), container, false);
        mSharedPrefSingleton = SharedPrefSingleton.getInstance(getActivity());

        onInitView();
        return mView;
    }

    private void onInitView() {
        if (mSectionNumber != LATEST_TUTORIAL_PAGE) {
            return;
        }

        mFinishTutorialButton = (Button) mView.findViewById(R.id.finishTutorialBUttonId);
        mFinishTutorialButton.setOnClickListener(this);
    }

    public int getViewLayoutRes() {
        switch (mSectionNumber) {
            case 0:
                return R.layout.tutorial_fragment_layout_1;
            case 1:
                return R.layout.tutorial_fragment_layout_2;
            case 2:
                return R.layout.tutorial_fragment_layout_3;
        }
        return R.layout.activity_main;
    }

    @Override
    public void onClick(View v) {
        mSharedPrefSingleton.setValue(SharedPrefSingleton.TUTORIAL_DONE, true);
        startActivity(new Intent(getActivity(), MainActivity.class)
                .putExtra(Utils.IMPORT_TRIGGER, true));
        getActivity().finish();
    }
}
