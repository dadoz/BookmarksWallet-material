package com.application.material.bookmarkswallet.app.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.*;

import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.utlis.Utils;

import java.lang.ref.WeakReference;

public class BookmarkActionHelper {
    private final WeakReference<Context> context;
    private String TAG = "BookmarkActionHelper";

    public BookmarkActionHelper(Context context) {
        this.context = new WeakReference<>(context);
    }

    /**y
     * open browser by intent (opening on bookmark url)
     * @param url
     */
    public void openLinkOnBrowser(String url, View view) {
        try {
            if (!Utils.isValidUrl(url)) {
                showErrorMessage(context.get().getString(R.string.wrong_url), view);
                return;
            }
            context.get().startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(Utils.buildUrl(url, true))));
        } catch (Exception e) {
            showErrorMessage(context.get().getString(R.string.cannot_load_url), view);
        }
    }

    /**
     * add bookmark action
     * @param fragment
     */
    public void addBookmarkAction(WeakReference<Fragment> fragment) {
        Intent intent = new Intent(context.get(), AddBookmarkActivity.class);
        fragment.get().startActivityForResult(intent, Utils.ADD_BOOKMARK_ACTIVITY_REQ_CODE);
    }



    /**
     *
     * @param message
     */
    private void showErrorMessage(String message, View view) {
        if (view != null) {
            message = (message == null) ? "Ops! Something went wrong!" : message;
            Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
            snackbar.getView()
                    .setBackgroundColor(ContextCompat.getColor(context.get(), R.color.red_500));
            snackbar.show();
        }
    }

    /**
     * @param adapter
     */
//    public void deleteAction(final BookmarkRecyclerViewAdapter adapter) {
//        RealmUtils.deleteListFromRealm(mRealm, adapter.getSelectedItemList());
//        adapter.notifyRemovedSelectedItems(); //NEVER TRIGGERED
//    }

    /**
     *
     * @param bookmark
     * @return
     */
//    private Intent getSharingBookmarkIntent(TreeNodeInterface bookmark) {
//        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//        shareIntent.putExtra(Intent.EXTRA_TEXT, ""); //Bookmark.Utils.stringify(bookmark));
//        shareIntent.setType("text/plain");
//        return shareIntent;
//    }

    /**
     *
     * @param adapter
     */
//    public void shareAction(BookmarkRecyclerViewAdapter adapter) {
//        Intent intent = getSharingBookmarkIntent(adapter.getSelectedItem());
//        context.get().startActivity(Intent.createChooser(intent, context.get().getString(R.string.share_to)));
//    }
    /**
     *
     * @param adapter
     */
//    public void selectAllAction(BookmarkRecyclerViewAdapter adapter) {
//        adapter.setSelectedAllItemPos(displayNodeView.getFiles());
//        adapter.notifyDataSetChanged();
//    }
}
