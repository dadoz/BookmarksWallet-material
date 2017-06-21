package com.application.material.bookmarkswallet.app.fragments;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.application.MaterialBookmarkApplication;
import com.application.material.bookmarkswallet.app.helpers.OnExportResultCallback;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.strategies.ExportStrategy;
import com.application.material.bookmarkswallet.app.views.ExportCheckboxesView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.application.material.bookmarkswallet.app.strategies.BaseExport.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;


/**
 * Created by davide on 12/06/2017.
 */
public class ExportFragment extends BaseFragment implements View.OnClickListener, OnExportResultCallback {
    public static String FRAG_TAG = "ExportFragment";
    private Unbinder unbinder;
    @BindView(R.id.exportCheckboxesViewId)
    ExportCheckboxesView exportCheckboxesView;
    @BindView(R.id.exportCardviewButtonId)
    View exportCardviewButton;
    private ExportStrategy exportStrategy;

    {
        layoutId = R.layout.export_cardviews_layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        exportStrategy = new ExportStrategy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        View view = super.onCreateView(inflater, container, savedInstance);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null)
            unbinder.unbind();
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
        exportCardviewButton.setOnClickListener(this);
    }

    /**
     * handle export action
     */
    private void handleExportAction() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getActivity().requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        //get bookmarkList
        List<Bookmark> list = getActivity() != null ?
                ((MaterialBookmarkApplication) getActivity().getApplication()).getBookmarksList() : null;

        exportStrategy.setExportStrategy(exportCheckboxesView.getStatus());
        exportStrategy.createFile(list);
    }


    @Override
    public void onClick(View v) {
        handleExportAction();
    }

    @Override
    public void onExportResultSuccess(String message) {
        if (getView() != null)
            Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onExportResultError(String message) {
        if (getView() != null)
            Snackbar.make(getView(), "Oh Snap, " + message, Snackbar.LENGTH_SHORT).show();
    }
}
