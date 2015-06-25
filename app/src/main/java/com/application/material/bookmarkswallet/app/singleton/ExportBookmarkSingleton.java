package com.application.material.bookmarkswallet.app.singleton;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
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
import io.realm.RealmResults;

import java.util.ArrayList;

/**
 * Created by davide on 31/03/15.
 */
public class ExportBookmarkSingleton implements DialogInterface.OnShowListener {
    private static ExportBookmarkSingleton  mInstance;
    private static Fragment mListenerRef;
    private static Activity mActivityRef;
    private AlertDialog mExportDialog;
    private RealmResults<Bookmark> mItems;
    private View mExportBookmarksDialogView;

    public ExportBookmarkSingleton() {
    }

    public static ExportBookmarkSingleton getInstance(Fragment listenerRef, Activity activityRef) {
        mListenerRef = listenerRef;
        mActivityRef = activityRef;
        return mInstance == null ? mInstance = new ExportBookmarkSingleton() : mInstance;
    }

    public void exportAction(final RealmResults<Bookmark> data) {
        mItems = data;
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

    public void exportBookmarks(RealmResults<Bookmark> items) {
        boolean isFileCreated = CSVExportParser.writeFile(items);
        if (! isFileCreated) {
            setErrorExportView();
            mExportDialog.dismiss();
            return;
        }
        setSuccessExportView();
    }

    private void setErrorExportView() {
        Toast.makeText(mActivityRef, "Error to export bookmarks! Please contact us!", Toast.LENGTH_SHORT).show();
    }

    private void setSuccessExportView() {
        View exportSuccessIcon = mExportBookmarksDialogView.findViewById(R.id.exportSuccessIconId);
        TextView exportInfoText = (TextView) mExportBookmarksDialogView.findViewById(R.id.exportInfoTextId);
        exportInfoText.setText("Eureka:\n bookmarks exported with Success!Checkout on Download folder.");
        mExportDialog.getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.GONE);
        exportSuccessIcon.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(mActivityRef, R.anim.card_flip_left_out);
        exportSuccessIcon.startAnimation(animation);

/*        View view = mExportBookmarksRevealView.findViewById(R.id.exportFrameLayoutId);
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
*/
    }

    public void dismissExportDialog() {
        if(mExportDialog != null) {
            mExportDialog.dismiss();
        }
    }

    @Override
    public void onShow(DialogInterface dialog) {
        Button positiveButton = mExportDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportBookmarks(mItems);
            }
        });
    }
}
