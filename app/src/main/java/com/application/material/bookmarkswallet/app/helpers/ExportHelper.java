package com.application.material.bookmarkswallet.app.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.dialogs.ExportDialog;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.utlis.RealmUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import io.realm.Realm;

/**
 *
 */
public abstract class ExportHelper {
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 99;
    public static final String EXPORT_FILE_NAME = "materialBookmarks_export_date";
    public static final String CSV_EXTENSION = ".csv";
    public static final String HTML_EXTENSION = ".html";

    private final WeakReference<Context> context;
    private final View view;

    /**
     *
     * @param ctx
     */
    public ExportHelper(WeakReference<Context> ctx, View v) {
        context = ctx;
        view = v;
    }

    /**
     *
     * @param list
     * @return
     */
    public abstract boolean createFile(ArrayList<Bookmark> list);

    /**
     *
     */
    public void checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(context.get(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context.get(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions((Activity) context.get(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                return;
            }
        }

        //implement request
        export(RealmUtils.getResultsList(Realm.getDefaultInstance()));
    }

    /**
     *
     */
    public void handleRequestPermissionSuccess() {
        Toast.makeText(context.get(), context.get().getString(R.string.accept),
                Toast.LENGTH_SHORT).show();
        export(RealmUtils.getResultsList(Realm.getDefaultInstance()));
    }

    /**
     *
     */
    public void handleRequestPermissionDeny() {
        Toast.makeText(context.get(), context.get().getString(R.string.decline),
                Toast.LENGTH_SHORT).show();
    }


    /**
     *
     * @param list
     * @return
     */
    public boolean export(ArrayList<Bookmark> list) {
        new ExportDialog(context, view, list).dialogHandler();
        return false;
    }
}