package com.application.material.bookmarkswallet.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class UploadBookmarkFragment extends Fragment implements
        View.OnClickListener, AddBookmarkActivity.OnHandleBackPressed {
    private Unbinder unbinder;

    @Override
    public void onSaveInstanceState(Bundle savedInstance) {
        super.onSaveInstanceState(savedInstance);
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        View mainView = inflater.inflate(R.layout.fragment_upload_bookmark_layout, container, false);
        unbinder = ButterKnife.bind(this, mainView);
        return mainView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInitView(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     *
     * @param savedInstanceState
     */
    private void onInitView(Bundle savedInstanceState) {
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//        }
    }

    @Override
    public boolean handleBackPressed() {
//        if (statusManager.isOnResultMode()) {
//            statusManager.setOnSearchMode();
//            getActivity().getSupportFragmentManager().popBackStack();
//            return true;
//        }
        return false;
    }
}
