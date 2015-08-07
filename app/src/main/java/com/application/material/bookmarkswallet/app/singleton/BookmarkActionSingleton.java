package com.application.material.bookmarkswallet.app.singleton;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.Toast;
import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.realm.BookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.adapter.realm.RealmRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.recyclerView.RecyclerViewCustom;
import com.cocosw.bottomsheet.BottomSheet;
import io.realm.Realm;

/**
 * Created by davide on 04/08/15.
 */
public class BookmarkActionSingleton {
    private static BookmarkActionSingleton mInstance;
    private static Activity mActivityRef;
    private static Realm mRealm;
    private String TAG = "BookmarkActionSingleton";

    public BookmarkActionSingleton() {
    }

    /**
     *
     * @param activity
     * @return
     */
    public static BookmarkActionSingleton getInstance(Activity activity) {
        mActivityRef = activity;
        mRealm = Realm.getInstance(mActivityRef);
        return mInstance == null ?
                mInstance = new BookmarkActionSingleton() :
                mInstance;
    }

    /**y
     * open browser by intent (opening on bookmark url)
     * @param linkUrl
     */
    public void openLinkOnBrowser(String linkUrl) {
        try {
            if (! checkURL(linkUrl)) {
                Toast.makeText(mActivityRef, "your URL is wrong "
                        + linkUrl, Toast.LENGTH_SHORT).show();
                return;
            }

            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(linkUrl));
            mActivityRef.startActivity(browserIntent);
        } catch(Exception e) {
            Log.e(TAG, "error - " + e);
            Toast.makeText(mActivityRef, "I cant load your URL "
                    + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * url check - by regex
     * @param linkUrl
     * @return
     */
    private boolean checkURL(String linkUrl) {
        return true;
    }

    /**
     * add bookmark action
     * @param fragment
     */
    public void addBookmarkAction(Fragment fragment) {
        Intent intent = new Intent(mActivityRef, AddBookmarkActivity.class);
        fragment.startActivityForResult(intent, 0);
    }

    /**
     * handle click on recycler view
     */
    public void handleClickAction(View v, RecyclerView recyclerView) {
        Bookmark bookmark = getBookmarkByView(v, recyclerView);
        openLinkOnBrowser(bookmark.getUrl());
    }

    /**
     * handle click on recycler view
     */
    public void handleLongClickAction(final View v, final RecyclerView recyclerView) {
        mActivityRef.startActionMode(new EditBookmarkActionMode(this, v, recyclerView));
    }

    /**
     *
     * @param recyclerView
     * @param v
     * @return
     */
    private Bookmark getBookmarkByView(View v, RecyclerView recyclerView) {
        BookmarkRecyclerViewAdapter adapter =
                (BookmarkRecyclerViewAdapter) recyclerView.getAdapter();
        return (Bookmark) adapter
                .getItem(recyclerView.getChildPosition(v));
    }
    /**
     *
     * @param recyclerView
     * @param v
     * @return
     */
    private int getBookmarkPosByView(View v, RecyclerView recyclerView) {
        BookmarkRecyclerViewAdapter adapter =
                (BookmarkRecyclerViewAdapter) recyclerView.getAdapter();
        return recyclerView.getChildPosition(v);
    }

    /**
     *
     * @param v
     * @param recyclerView
     */
    public void deleteAction(View v, RecyclerView recyclerView) {
        int position = getBookmarkPosByView(v, recyclerView);
        RealmRecyclerViewAdapter adapter = (RealmRecyclerViewAdapter) recyclerView.getAdapter();

        mRealm.beginTransaction();
        adapter.getItem(position).removeFromRealm();
        mRealm.commitTransaction();
        adapter.notifyItemRemoved(position);
    }

    /**
     *
     * @param bookmark
     * @return
     */
    public Intent getIntentForEditBookmark(Bookmark bookmark) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, Bookmark.Utils.stringify(bookmark));
        shareIntent.setType("text/plain");
        return shareIntent;
    }

    /**
     *
     */
    public void shareAction(View v, RecyclerView recyclerView) {
        final Bookmark bookmark = getBookmarkByView(v, recyclerView);
        Intent intent = getIntentForEditBookmark(bookmark);
        mActivityRef.startActivity(Intent.createChooser(intent, "share bookmark to..."));
    }
}
