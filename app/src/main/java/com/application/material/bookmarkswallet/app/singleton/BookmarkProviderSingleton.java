//package com.application.material.bookmarkswallet.app.singleton;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.AsyncTask;
//
//import com.application.material.bookmarkswallet.app.fragments.OnTaskCompleted;
//
///**
// * Created by davide on 10/08/15.
// */
//public class BookmarkProviderSingleton {
//    private static BookmarkProviderSingleton mInstance;
//    private static OnTaskCompleted mListener;
//    private String TAG = "BookmarksProviderSingleton";
//    private static Activity mActivityRef;
//    private static ActionsSingleton mBookmarkActionSingleton;
//
//    public enum BrowserEnum { DEFAULT, CHROME, FIREFOX }
//
//    public BookmarkProviderSingleton() {
//    }
//
//    public static BookmarkProviderSingleton getInstance(Activity activity, OnTaskCompleted listener) {
//        mActivityRef = activity;
//        mBookmarkActionSingleton = ActionsSingleton.getInstance(mActivityRef);
//        mListener = listener;
//        return mInstance == null ?
//                mInstance = new BookmarkProviderSingleton() :
//                mInstance;
//    }
//
//    /**
//     *
//     * @return
//     */
//    public Uri getChromeUriBrowser() {
//        String chromePackage = "com.android.chrome";
//        Uri chromeUri = Uri.parse("content://com.android.chrome.browser/bookmarks");
//        mActivityRef.grantUriPermission(chromePackage, chromeUri,
//                Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        return chromeUri;
//    }
//
//    /**
//     *
//     * @return
//     */
//    public Uri getFirefoxUriBrowser() {
//        Uri firefoxUri = Uri.parse("content://org.mozilla.firefox.db.browser/bookmarks");
//        String firefoxPackage = "org.mozilla.firefox";
//        mActivityRef.grantUriPermission(firefoxPackage, firefoxUri,
//                Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        return firefoxUri;
//    }
//
//}
