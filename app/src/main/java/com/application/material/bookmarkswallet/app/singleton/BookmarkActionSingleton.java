package com.application.material.bookmarkswallet.app.singleton;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.Toast;
import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.actionMode.EditBookmarkActionMode;
import com.application.material.bookmarkswallet.app.adapter.realm.BookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.adapter.realm.RealmRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import io.realm.Realm;

import java.util.UUID;

/**
 * Created by davide on 04/08/15.
 */
public class BookmarkActionSingleton {
    private static BookmarkActionSingleton mInstance;
    private static Activity mActivityRef;
    private static Realm mRealm;
    private static StatusSingleton mStatusSingleton;
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
        mStatusSingleton = StatusSingleton.getInstance();
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
        fragment.startActivityForResult(intent, Utils.ADD_BOOKMARK_ACTIVITY_REQ_CODE);
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
        int pos = getBookmarkPosByView(v, recyclerView);
        mActivityRef.startActionMode(new EditBookmarkActionMode(mActivityRef, v, recyclerView));
        mStatusSingleton.setEditMode(pos);
        recyclerView.getAdapter().notifyItemChanged(pos);
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

    /**
     * TODO move into realm class
     */
    public boolean addOrmObject(Realm realm, String title, String iconPath, byte[] blobIcon, String url) {
        boolean result = false;
        try {
            if (url == null) {
                return false;
            }
            realm.beginTransaction();
            Bookmark bookmark = realm.createObject(Bookmark.class);
            bookmark.setId(UUID.randomUUID().getLeastSignificantBits());
            bookmark.setName(title == null ? "" : title);
            if (iconPath != null) {
                bookmark.setIconPath(iconPath);
            }
            if (blobIcon != null) {
                bookmark.setBlobIcon(blobIcon);
            }
            bookmark.setUrl(url);
            bookmark.setTimestamp(Bookmark.Utils.getTodayTimestamp());
            bookmark.setLastUpdate(Bookmark.Utils.getTodayTimestamp());
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            realm.commitTransaction();
        }
        return result;
    }

}
