package com.application.material.bookmarkswallet.app.singleton;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.*;
import android.widget.Toast;
import com.application.material.bookmarkswallet.app.AddBookmarkActivity;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.BookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.helpers.StatusHelper;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.utlis.Utils;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class ActionsSingleton {
    private static ActionsSingleton mInstance;
    private static WeakReference<Context> context;
    private static Realm mRealm;
    private static StatusHelper mStatusSingleton;
    private String TAG = "ActionsSingleton";

    public ActionsSingleton() {
    }

    /**
     *
     * @param ctx
     * @return
     */
    public static ActionsSingleton getInstance(WeakReference<Context> ctx) {
        context = ctx;
        mRealm = Realm.getInstance(new RealmConfiguration.Builder(ctx.get()).build());
        mStatusSingleton = StatusHelper.getInstance();
        return mInstance == null ?
                mInstance = new ActionsSingleton() :
                mInstance;
    }

    /**y
     * open browser by intent (opening on bookmark url)
     * @param linkUrl
     */
    public void openLinkOnBrowser(String linkUrl) {
        try {
            if (! checkURL(linkUrl)) {
                Toast.makeText(context.get(), "your URL is wrong " + linkUrl,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(linkUrl));
            context.get().startActivity(browserIntent);
        } catch(Exception e) {
            Log.e(TAG, "error - " + e);
            Toast.makeText(context.get(), "I cant load your URL "
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
        Intent intent = new Intent(context.get(), AddBookmarkActivity.class);
        fragment.startActivityForResult(intent, Utils.ADD_BOOKMARK_ACTIVITY_REQ_CODE);
    }

//    /**
//     * handle click on recycler view
//     */
//    public void handleClickAction(View v, RecyclerView recyclerView) {
//        Bookmark bookmark = getBookmarkByView(v, recyclerView);
//        openLinkOnBrowser(bookmark.getUrl());
//    }
//
//    /**
//     * handle click on recycler view
//     */
//    public void handleLongClickAction(final View view, final RecyclerView recyclerView) {
//        int pos = getBookmarkPosByView(view, recyclerView);
//        ((Activity) context.get()).startActionMode(new EditBookmarkActionMode(context, view, recyclerView));
//        mStatusSingleton.setEditMode(pos);
//        recyclerView.getAdapter().notifyItemChanged(pos);
//    }

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
                .getItem(recyclerView.getChildLayoutPosition(v));
    }
    /**
     *
     * @param recyclerView
     * @param v
     * @return
     */
    private int getBookmarkPosByView(View v, RecyclerView recyclerView) {
        return recyclerView.getChildLayoutPosition(v);
    }

    /**
     *
     * @param position
     * @param adapter
     */
    public void deleteAction(BookmarkRecyclerViewAdapter adapter, int position) {
//        int position = getBookmarkPosByView(v, recyclerView);
//        Log.e("TAG", "" + position);
//        BookmarkRecyclerViewAdapter adapter = (BookmarkRecyclerViewAdapter) recyclerView.getAdapter();

        mRealm.beginTransaction();
        adapter.getItem(position).deleteFromRealm();
        mRealm.commitTransaction();
        adapter.notifyItemRemoved(position);
        adapter.notifyDataSetChanged();
    }

    /**
     * delete all bookmarks stored
     */
    public void deleteAllAction() {
        mRealm.beginTransaction();
        mRealm.where(Bookmark.class).findAll().clear();
        mRealm.commitTransaction();
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
    public void shareAction(Bookmark bookmark) {
        Intent intent = getIntentForEditBookmark(bookmark);
        context.get().startActivity(Intent.createChooser(intent, context.get().getString(R.string.share_to)));
    }

    /**
     * TODO move into realm class
     */
    public boolean addOrmObject(Realm realm, String title, String iconPath, byte[] blobIcon, String url) {
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
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            realm.cancelTransaction();
            e.printStackTrace();
        }
        return false;
    }

}
