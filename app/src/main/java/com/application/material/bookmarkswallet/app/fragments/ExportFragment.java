package com.application.material.bookmarkswallet.app.fragments;

import android.os.Bundle;
import android.view.View;

import com.application.material.bookmarkswallet.app.R;
/**
 * Created by davide on 12/06/2017.
 */
public class ExportFragment extends BaseFragment {
    public static String FRAG_TAG = "ExportFragment";

    {
        layoutId = R.layout.export_cardviews_layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInitView();
    }

    /**
     * init view
     */
    private void onInitView() {
    }

}
