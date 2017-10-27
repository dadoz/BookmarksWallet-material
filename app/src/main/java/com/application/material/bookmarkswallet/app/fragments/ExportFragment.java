package com.application.material.bookmarkswallet.app.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.helpers.OnExportResultCallback;
import com.application.material.bookmarkswallet.app.strategies.ExportStrategy;
import com.application.material.bookmarkswallet.app.views.ExportCheckboxesView;
import com.application.material.bookmarkswallet.app.views.ExportInfoView;
import com.lib.davidelm.filetreevisitorlibrary.manager.NodeListManager;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

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
public class ExportFragment extends BaseFragment implements ExportCheckboxesView.OnExportClickListener,
        OnExportResultCallback, EasyPermissions.PermissionCallbacks {
    public static String FRAG_TAG = "ExportFragment";
    private Unbinder unbinder;
    @BindView(R.id.exportCheckboxesViewId)
    ExportCheckboxesView exportCheckboxesView;
    @BindView(R.id.rippedFrameLayoutId)
    View rippedFrameLayout;
    @BindView(R.id.exportSuccessTextId)
    View exportSuccessText;
    @BindView(R.id.exportSuccessImageId)
    View exportSuccessImage;
    @BindView(R.id.exportBookmarksInfoLayoutId)
    ExportInfoView exportInfoView;
//    @BindView(R.id.exportCardviewButtonId)
//    View exportCardviewButton;
    private ExportStrategy exportStrategy;
    private List<TreeNodeInterface> bookmarksList;

    {
        layoutId = R.layout.fragment_export_layout;
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
        //get bookmarks from view
        bookmarksList = NodeListManager.getInstance(getContext()).getNodeList();
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
        exportCheckboxesView.setExportListener(this);
        exportInfoView.setTotBookmarkToBeExported(bookmarksList != null ? bookmarksList.size() : 0);
    }

    @Override
    public void onExportClick(View v) {
        requestWriteExternalStoragePermissions();
    }

    @AfterPermissionGranted(MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE)
    private void requestWriteExternalStoragePermissions() {
        if (EasyPermissions.hasPermissions(getContext(), WRITE_EXTERNAL_STORAGE)) {
            handleExportAction();
            return;
        }

        EasyPermissions.requestPermissions(this, getString(R.string.handle_permission),
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        handleExportAction();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        onExportResultError("no permission granted");
    }

    @Override
    public void onExportResultSuccess() {
        revealEffect();
    }

    @Override
    public void onExportResultError(String message) {
        if (getView() != null)
            Snackbar.make(getView(), "Oh Snap, " + message, Snackbar.LENGTH_SHORT).show();
    }


    /**
     * TODO move in presenter
     * handle export action
     */
    private void handleExportAction() {

        //check empty
        if (bookmarksList.size() == 0) {
            onExportResultError("empty list");
            return;
        }

        //handle bookmarks export
        try {
            exportStrategy.setExportStrategy(exportCheckboxesView.getStatus());
            exportStrategy.createFile(bookmarksList);
            onExportResultSuccess();
        } catch (Exception e) {
            onExportResultError(e.getMessage());
        }
    }
    /**
     * todo move in a presenter
     */
    public void revealEffect() {
        // get the center for the clipping circle
        int cx = rippedFrameLayout.getWidth() / 2;
        int cy = rippedFrameLayout.getHeight() / 2;

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

        // create the animator for this view (the start radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(rippedFrameLayout, cx, cy, 0, finalRadius);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationEnd(animation);
                exportSuccessText.setVisibility(View.GONE);
                exportSuccessImage.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                exportSuccessText.setVisibility(View.VISIBLE);
                exportSuccessImage.setVisibility(View.VISIBLE);
            }
        });

        //start
        // make the view visible and start the animation
        rippedFrameLayout.setVisibility(View.VISIBLE);
        anim.start();
    }


}
