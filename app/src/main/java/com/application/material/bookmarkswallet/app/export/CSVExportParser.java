package com.application.material.bookmarkswallet.app.export;

import android.os.Environment;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import io.realm.RealmResults;

import java.io.*;

/**
 * Created by davide on 26/01/15.
 */
public class CSVExportParser {
    public static final String EXPORT_FILE_NAME = "bookmarksExport.csv";

    public CSVExportParser() {
    }

    public static boolean writeFile(RealmResults<Bookmark> data) {
        try {
//            String dir = PrivateApplicationDirSingleton.getDir(ctx).getPath();
            String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            File file =  new File(dir, EXPORT_FILE_NAME);
            file.createNewFile();

            if(file.exists()) {
                FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
//                CSVPrinter csvPrinter = new CSVPrinter(osw, CSVFormat.DEFAULT);
//                csvPrinter.printRecords(data); //how to parse custom object into csv ?
//                csvPrinter.close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

}
