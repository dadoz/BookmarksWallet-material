package com.application.material.bookmarkswallet.app.strategies;

import android.content.Context;
import android.os.Environment;
import android.view.View;

import com.application.material.bookmarkswallet.app.helpers.OnExportResultCallback;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.utlis.Utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class CSVExport extends BaseExport {

    public CSVExport(WeakReference<Context> ctx, View view) {
        super(ctx, view);
    }

    @Override
    public void createFileAsync(final WeakReference<OnExportResultCallback> listener) {
        super.createFileAsync(listener);
    }

    /**
     * @return
     */
    @Override
    public boolean createFile(List<Bookmark> list) {
        try {
            File path = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(path, EXPORT_FILE_NAME + CSV_EXTENSION);
            path.mkdir();
            file.createNewFile();
            if (file.exists()) {
                FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                CSVPrinter csvPrinter = new CSVPrinter(osw, CSVFormat.DEFAULT);
                csvPrinter.printRecord(prepareListToCSV(list));
                csvPrinter.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     *
     * @param list
     * @return
     */
    public static ArrayList<String> prepareListToCSV(List<Bookmark> list) throws Exception {
        ArrayList<String> listTmp = new ArrayList<>();
        for (Bookmark item : list) {
            listTmp.add(item.getUrl() + ';' + item.getTimestamp() + ';' +
                    Utils.getNameByBookmark(item) + ';' );
        }

        return listTmp;
    }
}