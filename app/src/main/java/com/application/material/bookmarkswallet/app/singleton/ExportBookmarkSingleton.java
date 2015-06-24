package com.application.material.bookmarkswallet.app.singleton;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.exportFeature.CSVExportParser;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import io.realm.RealmResults;

import java.util.ArrayList;

/**
 * Created by davide on 31/03/15.
 */
public class ExportBookmarkSingleton {
    private static ExportBookmarkSingleton  mInstance;
    private static View mExportBookmarksRevealView;
    private static Fragment mListenerRef;
    private static Activity mActivityRef;
    private AlertDialog mExportDialog;

    public static ExportBookmarkSingleton getInstance(Fragment listenerRef, Activity activityRef) {
        mListenerRef = listenerRef;
        mActivityRef = activityRef;
        mExportBookmarksRevealView = mActivityRef.getLayoutInflater().
                inflate(R.layout.dialog_export_bookmarks_layout, null);
        if(mInstance == null) {
            mInstance = new ExportBookmarkSingleton();
        }
        return mInstance;
    }

    public ExportBookmarkSingleton() {
    }

    public void exportAction(final RealmResults<Bookmark> mItems) {
        mExportBookmarksRevealView.findViewById(R.id.exportConfirmButtonDialogId).
                setOnClickListener((View.OnClickListener) mListenerRef);
//        mExportBookmarksRevealView.findViewById(R.id.dismissExportButtonDialogId).
//                setOnClickListener((View.OnClickListener) mListenerRef);

        if (mExportDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivityRef);
            mExportDialog = builder.
                    setTitle("Bookmarks export!").
                    setView(mExportBookmarksRevealView).
                    setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).
                    setPositiveButton("EXPORT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            exportBookmarks(mItems);
                        }
                    }).
                    create();
        }
        mExportDialog.show();
    }

    public void exportBookmarks(RealmResults<Bookmark> items) {
        boolean isFileCreated = CSVExportParser.writeFile(items);
        if (isFileCreated) {
            setSuccessExportView();
        }
    }

    private void setSuccessExportView() {
        View view = mExportBookmarksRevealView.findViewById(R.id.exportFrameLayoutId);
        view.setBackgroundColor(mActivityRef.getResources().getColor(R.color.material_green));
        if (Build.VERSION.SDK_INT >= 21) {
            ViewAnimationUtils.createCircularReveal(view,
                    view.getWidth() / 2, view.getHeight() / 2, 0, view.getHeight()).start();
        }

        (view.findViewById(R.id.exportInfoTextId)).setVisibility(View.GONE);
        (view.findViewById(R.id.exportSuccessTextId)).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.exportSuccessTextId)).
                append(CSVExportParser.EXPORT_FILE_NAME);

//            ((TextView) view.findViewById(R.id.dismissExportButtonDialogId)).
//                    setTextColor(mActivityRef.getResources().getColor(R.color.white));

        (view.findViewById(R.id.exportConfirmButtonDialogId)).setOnClickListener(null);
//            ActionbarSingleton.setColorFilter(((ImageView) view
//                    .findViewById(R.id.exportConfirmButtonDialogId)).getDrawable(),
//                    R.color.material_violet_500, mActivityRef);
        ((ImageView) view.findViewById(R.id.exportConfirmButtonDialogId)).setImageDrawable(
                mActivityRef.getResources().getDrawable(R.drawable.ic_check_circle_white_48dp));

    }

    public void dismissExportDialog() {
        if(mExportDialog != null) {
            mExportDialog.dismiss();
        }
    }
}
