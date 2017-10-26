package com.application.material.bookmarkswallet.app.strategies;

import android.content.Context;
import android.view.View;

import com.application.material.bookmarkswallet.app.helpers.OnExportResultCallback;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeInterface;

import java.lang.ref.WeakReference;
import java.util.List;

public class ExportStrategy {
    private static WeakReference<Context> context;
    private static BaseExport exportInstance;
    private static ExportStrategy instance;
    private static View view;
    public enum ExportTypeEnum { CSV, HTML}

    /**
     *
     * @param list
     */
    public void createFile(List<TreeNodeInterface> list) {
        exportInstance.createFile(list);
    }

    /**
     *
     * @param listener
     */
    public void createFileAsync(OnExportResultCallback listener) {
        WeakReference<OnExportResultCallback> lst = new WeakReference<>(listener);
        exportInstance.createFileAsync(lst);
    }

    /**
     *
     *  TODO follow sm pattern
     * @return
     */
    public static ExportStrategy getInstance(WeakReference<Context> ctx, View v) {
        instance = instance == null ? new ExportStrategy() : instance;
        context =  ctx;
        view = v;
        return instance;
    }

    /**
     *
     * @return
     */
    public static ExportStrategy getInstance(WeakReference<Context> ctx) {
        instance = instance == null ? new ExportStrategy() : instance;
        context =  ctx;
        return instance;
    }


    /**
     *
     * @param type
     * @return
     */
    public void setExportStrategy(ExportTypeEnum type) {
        if (type.name().compareTo(ExportTypeEnum.CSV.name()) == 0) {
            exportInstance = new CSVExport(context, view);
        } else if (type.name().compareTo(ExportTypeEnum.HTML.name()) == 0) {
            exportInstance = new HtmlExport(context, view);
        } else {
            //default case
            exportInstance = new HtmlExport(context, view);
        }
    }
}
