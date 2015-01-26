package com.application.material.bookmarkswallet.app.parser;

import android.content.Context;
import android.os.Environment;
import com.application.material.bookmarkswallet.app.singleton.PrivateApplicationDirSingleton;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by davide on 26/01/15.
 */
public class CSVParser {
    private static String EXPORT_FILE_NAME = "bookmarksExport.csv";

    public CSVParser() {
    }

    public static void writeFile(Object data) {
        try {
//            String dir = PrivateApplicationDirSingleton.getDir(ctx).getPath();
            String dir = Environment.getDownloadCacheDirectory().getPath();
            File file =  new File(dir, EXPORT_FILE_NAME);
            FileWriter fw = new FileWriter(file);
            CSVPrinter csvPrinter = new CSVPrinter(fw, CSVFormat.DEFAULT);
            csvPrinter.print(data);
            csvPrinter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
