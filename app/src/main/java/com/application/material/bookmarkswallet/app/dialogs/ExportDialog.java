package com.application.material.bookmarkswallet.app.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.strategies.ExportStrategy;
import com.application.material.bookmarkswallet.app.utlis.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ExportDialog implements DialogInterface.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private final WeakReference<Context> ctx;
    private final ArrayList<Bookmark> exportBookmarkList;
    private final View view;
    private CheckBox htmlCheckbox;
    private CheckBox csvCheckbox;

    public ExportDialog(WeakReference<Context> context, View v, ArrayList<Bookmark> list) {
        ctx = context;
        exportBookmarkList = list;
        view = v;
    }

    /**
     *
     */
    public void dialogHandler() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx.get(), R.style.CustomLollipopDialogStyle);
        AlertDialog dialog = builder
                .setTitle("Bookmarks export!")
                .setView(R.layout.dialog_export_bookmarks_layout)
                .setNegativeButton(ctx.get().getString(android.R.string.cancel), this)
                .setPositiveButton(ctx.get().getString(android.R.string.yes), this)
                .create();
        dialog.show();

        initDialogListeners(dialog);
    }

    private void initDialogListeners(AlertDialog dialog) {
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
     *
     */
    private void handlePositiveButton() {
        boolean result = ExportStrategy
                .setExportStrategy(csvCheckbox.isChecked() ?
                        ExportStrategy.ExportTypeEnum.CSV : ExportStrategy.ExportTypeEnum.HTML)
                .createFile(exportBookmarkList);

        if (result) {
            successUI();
            return;
        }
        errorUI();
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
}