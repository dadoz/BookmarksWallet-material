package com.application.material.bookmarkswallet.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.helpers.SharedPrefHelper;

import java.lang.ref.WeakReference;

/**
 * Created by davide on 12/06/2017.
 */

public class BaseFragment extends Fragment implements AddBookmarkActivity.OnHandleBackPressed {
    protected SharedPrefHelper sharedPrefHelper;
    protected int layoutId;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!(this instanceof AddBookmarkActivity.OnHandleBackPressed))
            throw new UnsupportedOperationException("OnHandleBackPressed not implemented");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        View mainView = inflater.inflate(layoutId, container, false);
        sharedPrefHelper = SharedPrefHelper.getInstance(new WeakReference<>(getContext()));
        return mainView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public static String FRAG_TAG = "BaseFragment";

    @Override
    public boolean handleBackPressed() {
        return true;
    }
}