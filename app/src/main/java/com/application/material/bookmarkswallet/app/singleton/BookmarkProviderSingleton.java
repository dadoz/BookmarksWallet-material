package com.application.material.bookmarkswallet.app.singleton;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.application.material.bookmarkswallet.app.asyncTask.BookmarkProviderAsyncTask;
import com.application.material.bookmarkswallet.app.browser.BrowserCustom;
import com.application.material.bookmarkswallet.app.fragments.OnTaskCompleted;
import io.realm.Realm;

import java.lang.ref.WeakReference;

/**
 * Created by davide on 10/08/15.
 */
public class BookmarkProviderSingleton {
    private static BookmarkProviderSingleton mInstance;
    private static OnTaskCompleted mListener;
    private BookmarkProviderAsyncTask mAsyncTask;
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
//        Intent intent = new Intent(mActivityRef, BookmarksProviderService.class);
//        intent.putExtra("DATA_TO_SERVICES", true);
//        mActivityRef.startService(intent);
        mAsyncTask = new BookmarkProviderAsyncTask(composeBookmarkUriArray(browserList),
                new WeakReference<>(mListener), new WeakReference<>(this));
        mAsyncTask.execute();
    }

    /**
     *
     * @param browserList
     * @return
     */
    private Uri[] composeBookmarkUriArray(final BrowserEnum[] browserList) {
        return new Uri[] {
                getBookmarksUriByBrowser(browserList[0]),
                getBookmarksUriByBrowser(browserList[1]),
        };
    }

    /**
     * TODO refactor it :)
     * @param bookmarksUri
     * @throws Exception
     */
    public void addBookmarksByProviderJob(Uri bookmarksUri) throws Exception {
        int cnt = 0;
        ContentResolver cr = mActivityRef.getContentResolver();
        Realm realm = Realm.getInstance(mActivityRef);
        String[] projection = BrowserCustom.HISTORY_PROJECTION;

        Cursor cursor = cr.query(bookmarksUri, projection, null, null, null);
        if (cursor.moveToFirst()) {
            do {
//                if (mAsyncTask.isCancelled()) {
//                    setSyncStatus(SyncStatusEnum.CANCELED);
//                    return;
//                }
                mAsyncTask.doProgress(cnt);
                cnt ++;

                if (cursor.getInt(cursor.getColumnIndex(BrowserCustom.BookmarkColumns.BOOKMARK)) == 1) {
                    mBookmarkActionSingleton.addOrmObject(realm,
                            cursor.getString(cursor.getColumnIndex(BrowserCustom.BookmarkColumns.TITLE)),
                            null,
                            cursor.getBlob(cursor.getColumnIndex(BrowserCustom.BookmarkColumns.FAVICON)),
                            cursor.getString(cursor.getColumnIndex(BrowserCustom.BookmarkColumns.URL)));
                }
            } while (cursor.moveToNext());

        }
    }

    /**
     * @param browser
     * @return
     */
    private Uri getBookmarksUriByBrowser(BrowserEnum browser) {
        if (browser.ordinal() == BrowserEnum.DEFAULT.ordinal()) {
            return BrowserCustom.BOOKMARKS_URI;
        } else if (browser.ordinal() == BrowserEnum.CHROME.ordinal()) {
            return getChromeUriBrowser();
        } else if (browser.ordinal() == BrowserEnum.FIREFOX.ordinal()) {
            return getFirefoxUriBrowser();
        }

        return BrowserCustom.BOOKMARKS_URI;
    }

    /**
     *
     * @return
     */
    public Uri getChromeUriBrowser() {
        String chromePackage = "com.android.chrome";
        Uri chromeUri = Uri.parse("content://com.android.chrome.browser/bookmarks");
        mActivityRef.grantUriPermission(chromePackage, chromeUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return chromeUri;
    }

    /**
     *
     * @return
     */
    public Uri getFirefoxUriBrowser() {
        Uri firefoxUri = Uri.parse("content://org.mozilla.firefox.db.browser/bookmarks");
        String firefoxPackage = "org.mozilla.firefox";
        mActivityRef.grantUriPermission(firefoxPackage, firefoxUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return firefoxUri;
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


}
