package com.application.material.bookmarkswallet.app.importFeatures;

import android.os.Environment;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created by davide on 28/01/15.
 */
public class ChromeManual {

    public static final String EXPORT_FILE_NAME = "bookmarksExport.csv";

    private static boolean readFile(ArrayList<Bookmark> data) {
        try {
//            String dir = PrivateApplicationDirSingleton.getDir(ctx).getPath();
            String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            File file =  new File(dir, EXPORT_FILE_NAME);
            file.createNewFile();

            if(file.exists()) {
                FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                CSVPrinter csvPrinter = new CSVPrinter(osw, CSVFormat.DEFAULT);
                csvPrinter.printRecords(data); //how to parse custom object into csv ?
                csvPrinter.close();
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private ArrayList<Bookmark> parseData(String data) {
        return null;
    }
}
