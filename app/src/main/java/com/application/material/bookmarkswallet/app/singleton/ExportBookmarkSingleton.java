package com.application.material.bookmarkswallet.app.singleton;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.exportFeature.CSVExportParser;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.pnikosis.materialishprogress.ProgressWheel;
import io.realm.RealmResults;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by davide on 31/03/15.
 */
public class ExportBookmarkSingleton implements DialogInterface.OnShowListener {
    private static ExportBookmarkSingleton  mInstance;
    private static Fragment mListenerRef;
    private static Activity mActivityRef;
    private static RecyclerViewActionsSingleton mRvActionsSingleton;
    private AlertDialog mExportDialog;
    private RealmResults<Bookmark> mItems;
    private View mExportBookmarksDialogView;
    private ProgressWheel mProgressWheel;

    public ExportBookmarkSingleton() {
    }

    public static ExportBookmarkSingleton getInstance(Fragment listenerRef, Activity activityRef) {
        mListenerRef = listenerRef;
        mActivityRef = activityRef;
        mRvActionsSingleton = RecyclerViewActionsSingleton.getInstance(mActivityRef);
        return mInstance == null ? mInstance = new ExportBookmarkSingleton() : mInstance;
    }

    public void exportAction() {
        mExportBookmarksDialogView = mActivityRef.getLayoutInflater().
                inflate(R.layout.dialog_export_bookmarks_layout, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivityRef);
        mExportDialog = builder
                .setTitle("Bookmarks export!")
                .setView(mExportBookmarksDialogView)
                .setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("EXPORT", null)
                .create();
        mExportDialog.setOnShowListener(this);
        mExportDialog.show();
    }


    public void exportBookmarks() {
        new AsyncTask<Integer, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(Integer... params) {
                mProgressWheel = (ProgressWheel) mExportBookmarksDialogView.findViewById(R.id.progressWheelId);
                final RealmResults<Bookmark> list = mRvActionsSingleton.getBookmarksList();
                return CSVExportParser.writeFile(list);
            }

            protected void onProgressUpdate(Integer... progress) {
                mProgressWheel.spin();
            }

            protected void onPostExecute(Boolean result) {
                mProgressWheel.stopSpinning();
                if (! result) {
                    setErrorExportView();
                    mExportDialog.dismiss();
                    return;
                }
                setSuccessExportView();
            }
        }.execute(0);
    }

    private void setErrorExportView() {
        Toast.makeText(mActivityRef, "Error to export bookmarks! Please contact us!", Toast.LENGTH_SHORT).show();
    }

    private void setSuccessExportView() {
        View exportSuccessIcon = mExportBookmarksDialogView.findViewById(R.id.exportSuccessIconId);
        TextView exportInfoText = (TextView) mExportBookmarksDialogView.findViewById(R.id.exportInfoTextId);
        exportInfoText.setText("All bookmarks exported with Success!\nCheckout DOWNLOAD folder.");
        mExportDialog.getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.GONE);
        exportSuccessIcon.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(mActivityRef, R.anim.card_flip_left_out);
        exportSuccessIcon.startAnimation(animation);
    }

    @Override
    public void onShow(DialogInterface dialog) {
        Button positiveButton = mExportDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportBookmarks();
            }
        });
    }
}
