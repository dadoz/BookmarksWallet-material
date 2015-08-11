package com.application.material.bookmarkswallet.app.singleton;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Browser;
import android.util.Log;
import com.application.material.bookmarkswallet.app.fragments.OnTaskCompleted;
import io.realm.Realm;

import java.net.URL;

/**
 * Created by davide on 10/08/15.
 */
public class BookmarkProviderSingleton {
    private static BookmarkProviderSingleton mInstance;
    private static OnTaskCompleted mListener;
    private BookmarksProviderAsyncTask mAsyncTask;
    private String TAG = "BookmarksProviderSingleton";
    private static Activity mActivityRef;
    private static BookmarkActionSingleton mBookmarkActionSingleton;

    public enum BrowserEnum { DEFAULT, CHROME, FIREFOX }

    public BookmarkProviderSingleton() {
    }

    public static BookmarkProviderSingleton getInstance(Activity activity, OnTaskCompleted listener) {
        mActivityRef = activity;
        mBookmarkActionSingleton = BookmarkActionSingleton.getInstance(mActivityRef);
        mListener = listener;
        return mInstance == null ?
                mInstance = new BookmarkProviderSingleton() :
                mInstance;
    }

    /**
     * add by chrome
     */
    public void addByChrome() {
        BrowserEnum[] browserList = {BrowserEnum.CHROME};
        addByBrowserList(browserList);
    }

    /**
     * add default list
     */
    public void addByDefaultBrowser() {
        BrowserEnum[] browserList = {BrowserEnum.CHROME, BrowserEnum.DEFAULT};
        addByBrowserList(browserList);
    }

    /**
     * method to add bookmarks and start a new async task
     * @param browserList
     */
    public void addByBrowserList(final BrowserEnum[] browserList) {
        mAsyncTask = new BookmarksProviderAsyncTask(browserList);
        mAsyncTask.execute();
    }

    /**
     * cancel current async task
     */
    public void cancelAsyncTask() {
        if (mAsyncTask != null &&
                mAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            mAsyncTask.cancel(true);
        }
    }

    /**
     * TODO refactor it :)
     * @param bookmarksUri
     * @throws Exception
     */
    private void addBookmarksByProviderJob(Uri bookmarksUri) throws Exception {
        ContentResolver cr = mActivityRef.getContentResolver();
        Realm realm = Realm.getInstance(mActivityRef);
        String[] projection = {
                Browser.BookmarkColumns.CREATED,
                Browser.BookmarkColumns.FAVICON,
                Browser.BookmarkColumns.TITLE,
                Browser.BookmarkColumns.URL,
                Browser.BookmarkColumns.BOOKMARK
        };

        Cursor cursor = cr.query(bookmarksUri, projection, null, null, null);
        int urlId = cursor.getColumnIndex(Browser.BookmarkColumns.URL);
        int titleId = cursor.getColumnIndex(Browser.BookmarkColumns.TITLE);
        int faviconId = cursor.getColumnIndex(Browser.BookmarkColumns.FAVICON);
        int bookmarkId = cursor.getColumnIndex(Browser.BookmarkColumns.BOOKMARK);
        int cnt = 0;

        if (cursor.moveToFirst()) {
            do {
                if (mAsyncTask.isCancelled()) {
//                    setSyncStatus(SyncStatusEnum.CANCELED);
                    return;
                }
                mAsyncTask.doProgress(cnt);
                Log.e(TAG, "hey " + cursor.getString(urlId) + " # of imported: " + cnt);
                cnt ++;

                //add item on realm
                if (cursor.getInt(bookmarkId) == 1) {
                    byte[] blobIcon = cursor.getBlob(faviconId);
                    mBookmarkActionSingleton.addOrmObject(realm, cursor.getString(titleId), null, blobIcon, cursor.getString(urlId));
                }
            } while (cursor.moveToNext());

        }
    }

    private Uri getBookmarksUriByBrowser(BrowserEnum browser) {
        if (browser.ordinal() == BrowserEnum.DEFAULT.ordinal()) {
            return Browser.BOOKMARKS_URI;
        } else if (browser.ordinal() == BrowserEnum.CHROME.ordinal()) {
            String chromePackage = "com.android.chrome";
            Uri chromeUri = Uri.parse("content://com.android.chrome.browser/bookmarks");
            mActivityRef.grantUriPermission(chromePackage, chromeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            return chromeUri;
        } else if (browser.ordinal() == BrowserEnum.FIREFOX.ordinal()) {
            Uri firefoxUri = Uri.parse("content://org.mozilla.firefox.db.browser/bookmarks");
            String firefoxPackage = "org.mozilla.firefox";
            mActivityRef.grantUriPermission(firefoxPackage, firefoxUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            return firefoxUri;
        }

        return Browser.BOOKMARKS_URI;
    }

    /**
     * TODO move on service :O
     */
    public class BookmarksProviderAsyncTask extends AsyncTask<URL, Integer, Boolean> {

        private final Integer N_OCCURENCES = 30;
        private final BrowserEnum[] browserList;
        private final Integer[] params = new Integer[1];

        public BookmarksProviderAsyncTask(BrowserEnum[] list) {
            browserList = list;
        }

        @Override
        protected Boolean doInBackground(URL... params) {
//            setSyncStatus(SyncStatusEnum.RUNNING);
            try {
                Uri bookmarksUri = getBookmarksUriByBrowser(browserList[0]);
                addBookmarksByProviderJob(bookmarksUri);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Uri bookmarksUri = getBookmarksUriByBrowser(browserList[1]);
                    addBookmarksByProviderJob(bookmarksUri);
                    publishProgress();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
            return null;
        }

        public void doProgress(int count) {
            params[0] = count;
            publishProgress(params);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (values.length != 0 &&
                    values[0] % N_OCCURENCES == 0) {
                updateAdapter(true);
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
//            mSwipeRefreshLayout.setRefreshing(false);
            //updateAdapter
            updateAdapter(false);
//            setSyncStatus(SyncStatusEnum.DONE);
        }
    }

    /**
     * update adapter to set data on interface
     */
    private void updateAdapter(boolean isRefreshingEnabled) {
        mListener.onTaskCompleted(isRefreshingEnabled);
    }
}
