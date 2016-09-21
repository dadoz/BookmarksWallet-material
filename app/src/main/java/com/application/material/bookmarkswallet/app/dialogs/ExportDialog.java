package com.application.material.bookmarkswallet.app.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.helpers.CSVExportHelper;
import com.application.material.bookmarkswallet.app.helpers.HtmlExportHelper;
import com.application.material.bookmarkswallet.app.helpers.OnExportResultCallback;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.strategies.ExportStrategy;
import com.application.material.bookmarkswallet.app.utlis.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ExportDialog implements DialogInterface.OnClickListener, CompoundButton.OnCheckedChangeListener, OnExportResultCallback {
    private final WeakReference<Context> ctx;
    private final View view;
    private CheckBox htmlCheckbox;
    private CheckBox csvCheckbox;

    public ExportDialog(WeakReference<Context> context, View v) {
        ctx = context;
//        exportBookmarkList = list;
        view = v;
    }

    /**
     *
     */
    public void dialogHandler() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx.get(), R.style.CustomLollipopDialogStyle);
        AlertDialog dialog = builder
                .setTitle(ctx.get().getString(R.string.export_dialog_title))
                .setView(R.layout.dialog_export_bookmarks_layout)
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
        if (csvCheckbox.isChecked()) {
            ((CSVExportHelper) ExportStrategy
                    .setExportStrategy(ExportStrategy.ExportTypeEnum.CSV))
                    .createFileAsync(new WeakReference<OnExportResultCallback>(this));
            return;
        }
        ((HtmlExportHelper) ExportStrategy
                .setExportStrategy(ExportStrategy.ExportTypeEnum.HTML))
                .createFileAsync(new WeakReference<OnExportResultCallback>(this));
    }

    /**
     *
     */
    private void errorUI() {
        String message = "download with error";
        Utils.setSnackbar(view, ctx, message, true);
    }

    /**
     *
     */
    private void successUI() {
        String message = "download with success";
        Utils.setSnackbar(view, ctx, message, false);
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
    public void onExportResultSuccess(String message) {
        successUI();
    }

    @Override
    public void onExportResultError(String message) {
        errorUI();
    }
}