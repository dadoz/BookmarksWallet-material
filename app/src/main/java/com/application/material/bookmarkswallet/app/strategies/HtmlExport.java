package com.application.material.bookmarkswallet.app.strategies;


import android.content.Context;
import android.os.Environment;
import android.view.View;

import com.application.material.bookmarkswallet.app.helpers.OnExportResultCallback;
import com.application.material.bookmarkswallet.app.utlis.TreeNodeContentUtils;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import com.lib.davidelm.filetreevisitorlibrary.models.TreeNodeRealm;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.util.List;

public class HtmlExport extends BaseExport {

    public HtmlExport(WeakReference<Context> ctx, View view) {
        super(ctx, view);
    }

    @Override
    public void createFileAsync(final WeakReference<OnExportResultCallback> listener) {
        super.createFileAsync(listener);
    }

    @Override
    public boolean createFile(List<?> list) {
        try {
            File path = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(path, EXPORT_FILE_NAME + HTML_EXTENSION);
            path.mkdir();
            file.createNewFile();

            if (file.exists()) {
                FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                osw.write("<!DOCTYPE NETSCAPE-Bookmark-file-1>\n" +
                        "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">\n" +
                        "<TITLE>Material Bookmarks - minimal EXPORT</TITLE>\n" +
                        "<H1>Bookmarks</H1>\n" +
                        "<DL><p>\n" +
                        "<DT><H3 ADD_DATE=\"1472893318090001\">Bookmark list</H3>\n" +
                        "<DL><p>\n");

                for (Object item : list) {
                    osw.write("<DT><A HREF=" +
                            Utils.buildUrl(((TreeNodeRealm) item).getNodeContent().getDescription(), false) +
                            " ADD_DATE=" +
                            ((TreeNodeRealm) item).getNodeContent().getFileUri() + ">" +
                            TreeNodeContentUtils.getBookmarkName(((TreeNodeRealm) item)
                                    .getNodeContent().getName()) +
                            "</A>\n");
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
