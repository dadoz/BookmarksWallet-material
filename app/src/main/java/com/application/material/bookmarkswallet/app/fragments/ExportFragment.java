package com.application.material.bookmarkswallet.app.fragments;

import android.content.Context;
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
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
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
        layoutId = R.layout.export_layout;
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

    @Override
    public void onClick(View v) {
        requestWriteExternalStoragePermissions();
    }

    @AfterPermissionGranted(MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
    private void requestWriteExternalStoragePermissions() {
        if (EasyPermissions.hasPermissions(getContext(), WRITE_EXTERNAL_STORAGE)) {
            handleExportAction();
            return;
        }

        EasyPermissions.requestPermissions(getActivity(), getString(R.string.action_rename),
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE);
    }

    /**
     * TODO move in presenter
     * handle export action
     */
    private void handleExportAction() {
        try {
            //get bookmarkList
            List<Bookmark> list = getActivity() != null ?
                    ((MaterialBookmarkApplication) getActivity().getApplication()).getBookmarksList() : null;

            exportStrategy.setExportStrategy(exportCheckboxesView.getStatus());
            exportStrategy.createFile(list);
            onExportResultSuccess(getString(R.string.export_bookmarks_success));
        } catch (Exception e) {
            onExportResultError(e.getMessage());
        }
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
