package com.application.material.bookmarkswallet.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper;

/**
 * Created by davide on 12/06/2017.
 */

public class BaseFragment extends Fragment {
    protected SharedPrefHelper sharedPrefHelper;
    protected int layoutId;
    public static String FRAG_TAG = "BaseFragment";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        View mainView = inflater.inflate(layoutId, container, false);
        sharedPrefHelper = SharedPrefHelper.getInstance(getContext());
        return mainView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}