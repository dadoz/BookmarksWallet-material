package com.application.material.bookmarkswallet.app.dialogs;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.helpers.OnExportResultCallback;
import com.application.material.bookmarkswallet.app.utlis.Utils;

import java.lang.ref.WeakReference;

public class ExportDialog implements DialogInterface.OnClickListener,
        CompoundButton.OnCheckedChangeListener, OnExportResultCallback, View.OnClickListener {
    private final WeakReference<Context> ctx;
    private final View view;
    private CheckBox htmlCheckbox;
    private CheckBox csvCheckbox;

    public ExportDialog(WeakReference<Context> context, View v) {
        ctx = context;
        view = v;
    }

    /**
     *
     */
    public void dialogHandler() {
        AlertDialog dialog = new AlertDialog.Builder(ctx.get(), R.style.CustomLollipopDialogStyle)
                .setTitle(ctx.get().getString(R.string.export_dialog_title))
                .setView(R.layout.export_bookmarks_checkboxes_layout)
                .setNegativeButton(ctx.get().getString(android.R.string.cancel), this)
                .setPositiveButton(ctx.get().getString(android.R.string.yes), this)
                .create();
        dialog.show();

        initDialogListeners(dialog);
    }

    /**
     *
     * @param dialog
     */
    private void initDialogListeners(AlertDialog dialog) {
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(ctx.get(), R.color.grey_400));
        csvCheckbox = (CheckBox) dialog.findViewById(R.id.exportCSVCheckboxId);
        htmlCheckbox = (CheckBox) dialog.findViewById(R.id.exportHTMLCheckboxId);
        csvCheckbox.setOnCheckedChangeListener(this);
        htmlCheckbox.setOnCheckedChangeListener(this);
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case AlertDialog.BUTTON_POSITIVE:
                handlePositiveButton();
                break;
            case AlertDialog.BUTTON_NEGATIVE:
                dialog.dismiss();
                break;
        }

    }

    /**
     * TODO async
     */
    private void handlePositiveButton() {
//        if (csvCheckbox.isChecked()) {
//            ((CSVExport) ExportStrategy
//                    .setExportStrategy(ExportStrategy.ExportTypeEnum.CSV))
//                    .createFileAsync(new WeakReference<OnExportResultCallback>(this));
//            return;
//        }
//        ((HtmlExport) ExportStrategy
//                .setExportStrategy(ExportStrategy.ExportTypeEnum.HTML))
//                .createFileAsync(new WeakReference<OnExportResultCallback>(this));
    }

    /**
     *
     */
    private void errorUI() {
        Utils.setSnackbar(view, ctx, ctx.get().getString(R.string.downloaded_with_error), true, null, null);
    }

    /**
     *
     */
    private void successUI() {
        Utils.setSnackbar(view, ctx, ctx.get().getString(R.string.downloaded_with_success), false,
                ctx.get().getString(R.string.open), new WeakReference<>(this));
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean active) {
        if (compoundButton.getId() == R.id.exportCSVCheckboxId) {
            htmlCheckbox.setChecked(!active);
        }
        if (compoundButton.getId() == R.id.exportHTMLCheckboxId) {
            csvCheckbox.setChecked(!active);
        }
    }

    @Override
    public void onExportResultSuccess() {
        successUI();
    }

    @Override
    public void onExportResultError(String message) {
        errorUI();
    }

    @Override
    public void onClick(View view) {
        //TODO switch on ids
        if (ctx.get() != null)
            ctx.get().startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
    }
}