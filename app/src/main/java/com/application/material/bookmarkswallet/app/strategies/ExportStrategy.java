package com.application.material.bookmarkswallet.app.strategies;

import android.content.Context;
import android.view.View;

import com.application.material.bookmarkswallet.app.helpers.CSVExportHelper;
import com.application.material.bookmarkswallet.app.helpers.ExportHelper;
import com.application.material.bookmarkswallet.app.helpers.HtmlExportHelper;

import java.lang.ref.WeakReference;

public class ExportStrategy {
    private static WeakReference<Context> context;
    private static ExportTypeEnum defaultType = ExportStrategy.ExportTypeEnum.HTML;
    private static ExportHelper exportInstance;
    private static ExportStrategy instance;
    private static View view;

    /**
     *
     */
    public enum ExportTypeEnum { CSV, HTML}

    /**
     *
     *  TODO follow sm pattern
     * @return
     */
    public static ExportHelper buildInstance(WeakReference<Context> ctx, View v) {
        instance = new ExportStrategy();
        context =  ctx;
        view = v;
        return setExportStrategy(defaultType);
    }

    /**
     *
     * @param type
     * @return
     */
    public static ExportHelper setExportStrategy(ExportTypeEnum type) {
        return exportInstance = getExportHelper(type);
    }

    /**
     *
     * @return
     */
    public static ExportHelper getInstance(WeakReference<Context> ctx) {
        context =  ctx;
        return instance.getExportInstance();
    }

    /**
     *
     * @return
     */
    private ExportHelper getExportInstance() {
        return exportInstance;
    }


    /**
     *
     * @param type
     * @return
     */
    private static ExportHelper getExportHelper(ExportTypeEnum type) {
        if (type.name().compareTo(ExportTypeEnum.CSV.name()) == 0) {
            return new CSVExportHelper(context, view);
        } else if (type.name().compareTo(ExportTypeEnum.HTML.name()) == 0) {
            return new HtmlExportHelper(context, view);
        }
        return new CSVExportHelper(context, view);
    }

}
