package com.application.material.bookmarkswallet.app.helpers;

import android.content.Context;
import android.os.Environment;
import android.view.View;

import com.application.material.bookmarkswallet.app.models.Bookmark;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class CSVExportHelper  extends ExportHelper {

    public CSVExportHelper(WeakReference<Context> ctx, View view) {
        super(ctx, view);
    }

    /**
     * @return
     */
    @Override
    public boolean createFile(ArrayList<Bookmark> list) {
        try {
            File path = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(path, EXPORT_FILE_NAME);
            path.mkdir();
            file.createNewFile();
            if (file.exists()) {
                FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                CSVPrinter csvPrinter = new CSVPrinter(osw, CSVFormat.DEFAULT);
                csvPrinter.printRecords(list);
                csvPrinter.close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

}