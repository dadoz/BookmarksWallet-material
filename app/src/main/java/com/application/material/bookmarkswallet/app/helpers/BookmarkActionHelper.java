package com.application.material.bookmarkswallet.app.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.*;

import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.BookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.utlis.RealmUtils;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import io.realm.Realm;

import java.lang.ref.WeakReference;

public class BookmarkActionHelper {
    private static BookmarkActionHelper mInstance;
    private static WeakReference<Context> context;
    private static Realm mRealm;
    private String TAG = "BookmarkActionHelper";

    private BookmarkActionHelper() {
        mRealm = Realm.getDefaultInstance();
    }

    /**
     *
     * @param ctx
     * @return
     */
    public static BookmarkActionHelper getInstance(WeakReference<Context> ctx) {
        context = ctx;
        return mInstance == null ?
                mInstance = new BookmarkActionHelper() :
                mInstance;
    }

    /**y
     * open browser by intent (opening on bookmark url)
     * @param url
     */
    public void openLinkOnBrowser(String url) {
        try {
            if (!Utils.isValidUrl(url)) {
                showErrorMessage(context.get().getString(R.string.wrong_url));
                return;
            }
            context.get().startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(Utils.buildUrl(url, true))));
        } catch (Exception e) {
            showErrorMessage(context.get().getString(R.string.cannot_load_url));
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
     * @param adapter
     */
    public void deleteAction(final BookmarkRecyclerViewAdapter adapter) {
        RealmUtils.deleteListFromRealm(mRealm, adapter.getSelectedItemList());
        adapter.notifyRemovedSelectedItems(); //NEVER TRIGGERED
    }

    /**
     *
     * @param bookmark
     * @return
     */
    private Intent getSharingBookmarkIntent(Bookmark bookmark) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, Bookmark.Utils.stringify(bookmark));
        shareIntent.setType("text/plain");
        return shareIntent;
    }

    /**
     *
     * @param adapter
     */
    public void shareAction(BookmarkRecyclerViewAdapter adapter) {
        Intent intent = getSharingBookmarkIntent(adapter.getSelectedItem());
        context.get().startActivity(Intent.createChooser(intent, context.get().getString(R.string.share_to)));
    }

    /**
     *
     * @param message
     */
    private void showErrorMessage(String message) {
        View mainView = ((Activity) context.get()).getWindow().getDecorView()
                .getRootView().findViewById(R.id.mainContainerViewId);
        if (mainView != null) {
            message = (message == null) ? "Ops! Something went wrong!" : message;
            Snackbar snackbar = Snackbar.make(mainView, message, Snackbar.LENGTH_LONG);
            snackbar.getView()
                    .setBackgroundColor(ContextCompat.getColor(context.get(), R.color.red_500));
            snackbar.show();
        }
    }

    /**
     *
     * @param adapter
     */
    public void selectAllAction(BookmarkRecyclerViewAdapter adapter) {
        adapter.setSelectedAllItemPos();
        adapter.notifyDataSetChanged();
    }
}
