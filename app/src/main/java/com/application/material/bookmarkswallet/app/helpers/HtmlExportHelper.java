package com.application.material.bookmarkswallet.app.helpers;


import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.application.material.bookmarkswallet.app.models.Bookmark;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class HtmlExportHelper extends ExportHelper {

    public HtmlExportHelper(WeakReference<Context> ctx, View view) {
        super(ctx, view);
    }

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
                osw.write("<!DOCTYPE NETSCAPE-Bookmark-file-1>\n" +
                        "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">\n" +
                        "<TITLE>Bookmarks</TITLE>\n" +
                        "<H1>Bookmarks</H1>\n" +
                        "<DL><p>\n" +
                        "<DT><H3 ADD_DATE=\"1472893318090001\">Senza etichetta</H3>\n" +
                        "<DL><p>\n");

                for (Bookmark bookmark : list) {
                    osw.write("<DT><A HREF=" + bookmark.getUrl() + " ADD_DATE=" +
                            bookmark.getTimestamp() +">" + bookmark.getName() + "</A>\n");
                }

                osw.write("</DL><p>\n" +
                        "</DL><p>");
                osw.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
