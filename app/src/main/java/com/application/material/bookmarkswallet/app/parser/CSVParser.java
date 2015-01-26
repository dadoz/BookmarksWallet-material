package com.application.material.bookmarkswallet.app.parser;

import android.os.Environment;
import com.application.material.bookmarkswallet.app.models.Link;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by davide on 26/01/15.
 */
public class CSVParser {
    public static final String EXPORT_FILE_NAME = "bookmarksExport.csv";

    public CSVParser() {
    }

    public static boolean writeFile(ArrayList<Link> data) {
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

}
