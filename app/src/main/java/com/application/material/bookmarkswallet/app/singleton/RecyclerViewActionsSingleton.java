package com.application.material.bookmarkswallet.app.singleton;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Browser;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.Toast;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.realm.BookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.adapter.realm.RealmModelAdapter;
import com.application.material.bookmarkswallet.app.animators.ScrollManager;
import com.application.material.bookmarkswallet.app.fragments.BookmarkListFragment;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

import static com.application.material.bookmarkswallet.app.singleton.ActionbarSingleton.NOT_SELECTED_ITEM_POSITION;

/**
 * Created by davide on 31/03/15.
 */
public class RecyclerViewActionsSingleton {
    private static final String TAG = "RecyclerViewActionsSingleton";
    private static final String NO_TITLE_SET = "(no title)";
    private static RecyclerViewActionsSingleton mInstance;
    private static RecyclerView mRecyclerView;
    private static Activity mActivityRef;
    private static Fragment mListenerRef;
    private static ActionbarSingleton mActionbarSingleton;
    private static Fragment mFragmentRef;
//    private static View mEditUrlView;
    private static BookmarkRecyclerViewAdapter mAdapter;
    private static SwipeRefreshLayout mSwipeRefreshLayout;
    private AlertDialog mEditDialog;
    private static Realm mRealm;
    private View mEditTitleViewRef;
    private View mEditUrlViewRef;
    private static boolean mSearchOnUrlEnabled;
    private static String SEARCH_URL_MODE = "SEARCH_URL_MODE";
    private static String BOOKMARKS_WALLET_SHAREDPREF = "BOOKMARKS_WALLET_SHAREDPREF";
    private View mAdsView;
    private int mAdsOffset;
    private BookmarksProviderAsyncTask mBookmarksProviderAsyncTask;
    private static boolean mBookmarkSyncByProvider = false;


    public enum BrowserEnum { DEFAULT, CHROME, FIREFOX };

    public RecyclerViewActionsSingleton() {
    }

    public static RecyclerViewActionsSingleton getInstance(SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView,
                                                           Activity activityRef,
                                                           Fragment listenerRef) {
        initReferences(swipeRefreshLayout, recyclerView, activityRef, listenerRef);
        if(mInstance == null) {
            mInstance = new RecyclerViewActionsSingleton();
        }
        return mInstance;
    }

    public static RecyclerViewActionsSingleton getInstance(Activity activityRef) {
        mActivityRef = activityRef;
        if(mInstance == null) {
            mInstance = new RecyclerViewActionsSingleton();
        }
        return mInstance;
    }

    public static void initReferences(SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView,
                                      Activity activityRef, Fragment fragmentRef) {
        mSwipeRefreshLayout = swipeRefreshLayout;
        mRecyclerView = recyclerView;
        mActivityRef = activityRef;
        mListenerRef = fragmentRef;
        mFragmentRef = fragmentRef;
        mActionbarSingleton = ActionbarSingleton.getInstance(mActivityRef);
        updateAdapterRef();
        mRealm = Realm.getInstance(mActivityRef);

        mSearchOnUrlEnabled = mActivityRef
                .getSharedPreferences(BOOKMARKS_WALLET_SHAREDPREF, 0)
                .getBoolean(SEARCH_URL_MODE, false);
    }

    private static BookmarkRecyclerViewAdapter updateAdapterRef() {
        return mAdapter = (BookmarkRecyclerViewAdapter)
                mRecyclerView.getAdapter();
    }

    public void cancelAsyncTask() {
        if (mBookmarksProviderAsyncTask != null &&
                mBookmarksProviderAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
            mBookmarksProviderAsyncTask.cancel(true);
        }
    }
    /**
     * TODO refactor name
     */
    public void undoEditBookmark() {
        updateAdapterRef();
        mAdapter.notifyDataSetChanged();
        mActionbarSingleton.setEditItemPos(NOT_SELECTED_ITEM_POSITION);
        mActionbarSingleton.setTitle(null);
        mActionbarSingleton.changeActionbar(false);
//        ScrollManager.runTranslateAnimationWrapper(mAdsView, ScrollManager.Direction.UP, 0);
        ScrollManager.runTranslateAnimation(mAdsView, 0, new DecelerateInterpolator(3));
        mActivityRef.invalidateOptionsMenu();
        showClipboardLinkButtonWrapper();
        showSlidingPanelWrapper();
    }

    /**
     * TODO refactor name
     * @param position
     */
    public void selectBookmarkEditMenu(int position) {
        updateAdapterRef();
        mActionbarSingleton.setEditItemPos(position);
        mActionbarSingleton.setTitle("1");
        mActionbarSingleton.changeActionbar(true);
        int translateY = ScrollManager.getTranslateY(mAdsView, ScrollManager.Direction.DOWN, mAdsOffset);
        ScrollManager.runTranslateAnimation(mAdsView, translateY, new DecelerateInterpolator(3));

        mAdapter.notifyDataSetChanged();
        mActivityRef.invalidateOptionsMenu();
        hideClipboardLinkButtonWrapper();
        hideSlidingPanelWrapper();
    }

    //todo refactor in editUrlDialog
    public void editLinkDialog(Bookmark bookmark) {
        try {
            View editBookmarkView = mActivityRef.getLayoutInflater().
                    inflate(R.layout.dialog_edit_bookmark_layout, null);
            ((EditText) editBookmarkView.findViewById(R.id.editBookmarkUrlDialoglId)).
                    setText(bookmark.getUrl());
            ((EditText) editBookmarkView.findViewById(R.id.editBookamrkTitleDialoglId)).
                    setText(Bookmark.Utils.getBookmarkNameWrapper(bookmark.getName()));

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivityRef, R.style.CustomLollipopDialogStyle);
            mEditDialog = builder.
                    setTitle("Edit bookmark").
                    setView(editBookmarkView).
                    setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveEditLinkDialog();
                        }
                    }).
                    create();
            mEditDialog.show();

            mEditTitleViewRef = editBookmarkView.findViewById(R.id.editBookamrkTitleDialoglId);
            mEditUrlViewRef = editBookmarkView.findViewById(R.id.editBookmarkUrlDialoglId);
//            editBookmarkView.findViewById(R.id.saveEditUrlDialogId).
//                    setOnClickListener((View.OnClickListener) mListenerRef);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveEditLinkDialog() {
        String modifiedUrl = ((EditText) mEditUrlViewRef).getText().toString();
        String modifiedTitle = ((EditText) mEditTitleViewRef).getText().toString();
        mEditDialog.dismiss();

        Bookmark bookmark = getSelectedItemFromAdapter();

        mRealm.beginTransaction();

        if(! modifiedTitle.trim().equals("")) {
            bookmark.setName(modifiedTitle);
        }

        if(! modifiedUrl.trim().equals("")) {
            bookmark.setUrl(modifiedUrl);
        }
        bookmark.setLastUpdate(Bookmark.Utils.getTodayTimestamp());

        mRealm.commitTransaction();

        mRecyclerView.getAdapter()
                .notifyItemChanged(mActionbarSingleton.getEditItemPos());
        mActivityRef.onBackPressed();
    }

    //todo move to static class
    public void addBookmarkWithInfo(String url) throws MalformedURLException {
        mSwipeRefreshLayout.setRefreshing(true);
        //TODO refactor it :)
        if(! url.contains("http://") &&
                ! url.contains("https://")) {
            //trying with http
            url = "http://" + url;
        }

        new AsyncTask<URL, Integer, Boolean>() {
            private String bookmarkUrl = null;
            private String bookmarkTitle = null;
            private String iconUrl = null;
            @Override
            protected Boolean doInBackground(URL... linkUrlArray) {
                bookmarkUrl = linkUrlArray[0].toString();
                Document doc;
                try {
                    doc = Jsoup.connect(bookmarkUrl).get();
                    bookmarkTitle = doc.title();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }

                //trying to get iconUrl
                try {
                    org.jsoup.nodes.Element elem = doc.head().select("link[href~=.*\\.ico]").first();
                    iconUrl = elem.attr("abs:href");
                    Log.d(TAG, " - " + iconUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
            }

            @Override
            protected void onPostExecute(Boolean isBookmarkInfoRetrieved) {
                if (bookmarkTitle == null ||
                        bookmarkTitle.trim().equals("")) {
                    bookmarkTitle = NO_TITLE_SET;
                }
                try {
                    addBookmarkIconByUrl(iconUrl, bookmarkUrl, bookmarkTitle);
                } catch (MalformedURLException e) {
                    //CHECK out what u need
                    mSwipeRefreshLayout.setRefreshing(false);
                    addBookmark(bookmarkUrl, null, bookmarkTitle);
                    e.printStackTrace();
                }
            }

        }.execute(new URL(url));
    }

    public void addBookmarkIconByUrl(String iconUrl,
                                     final String bookmarkUrl,
                                     final String bookmarkTitle) throws MalformedURLException {
        //                mSwipeRefreshLayout.setRefreshing(false);
        if(iconUrl == null) {
            //CHECK out what u need
            mSwipeRefreshLayout.setRefreshing(false);
            addBookmark(bookmarkUrl, null, bookmarkTitle);
            return;
        }

        //TRYING GETTING IMAGE
        new AsyncTask<URL, Integer, byte[]>() {
            @Override
            protected byte[] doInBackground(URL... linkUrlArray) {
                return downloadBytesArrayByUrl(linkUrlArray[0]);
            }

            @Override
            protected void onPostExecute(byte[] iconByteArray) {
                mSwipeRefreshLayout.setRefreshing(false);

                //CHECK out what u need
                addBookmark(bookmarkUrl, iconByteArray, bookmarkTitle);
            }
        }.execute(new URL(iconUrl));
    }

    public void addBookmark(String url, byte[] iconBlob, String title) {
//        title = title == null ? "" : title;
//        Link link = new Link(-1, null, null, title, url, -1, Link.getTodayTimestamp());
//        mDbConnector.insertLink(link);

//        ((LinkRecyclerViewAdapter) mRecyclerView.getAdapter()).add(link);
//        ((BookmarkRecyclerViewAdapter) mRecyclerView.getAdapter()).updateDataset();
        //UPDATE DATASET REF
        mRecyclerView.scrollToPosition(0);
        addOrmObject(mRealm, title, null, iconBlob, url);
        setAdapter();
        mRecyclerView.getAdapter().notifyItemInserted(0);
        updateAdapterRef();
    }

    public void deleteBookmarksList() {
        mRealm.beginTransaction();
        mRealm.where(Bookmark.class).findAll().clear();
        mRealm.commitTransaction();
    }

    public RealmResults<Bookmark> getBookmarksList() {
        Realm realm = Realm.getInstance(mActivityRef);
        return realm.where(Bookmark.class).findAll();
    }

    public void addBookmarksByProvider(final BrowserEnum[] browserList) {
        mSwipeRefreshLayout.setRefreshing(true);
        mBookmarksProviderAsyncTask = new BookmarksProviderAsyncTask(browserList);
        mBookmarksProviderAsyncTask.execute();
    }

    private void addBookmarksByProviderJob(Uri bookmarksUri) throws Exception {
        ContentResolver cr = mActivityRef.getContentResolver();
        Realm realm = Realm.getInstance(mActivityRef);
        String[] projection = {
                Browser.BookmarkColumns.CREATED,
                Browser.BookmarkColumns.FAVICON,
                Browser.BookmarkColumns.TITLE,
                Browser.BookmarkColumns.URL
        };
        Cursor cursor = cr.query(bookmarksUri, projection, null, null, null);
        int urlId = cursor.getColumnIndex(Browser.BookmarkColumns.URL);
        int titleId = cursor.getColumnIndex(Browser.BookmarkColumns.TITLE);
        int faviconId = cursor.getColumnIndex(Browser.BookmarkColumns.FAVICON);
        //                    long timestamp = cursor.getColumnIndex(Browser.BookmarkColumns.CREATED);
        int cnt = 0;
        if (cursor.moveToFirst()) {
            do {
                if (mBookmarksProviderAsyncTask.isCancelled()) {
                    return;
                }
                mBookmarksProviderAsyncTask.doProgress(cnt);
                Log.e(TAG, "hey " + cursor.getString(urlId) + " # of imported: " + cnt);
                byte[] blobIcon = cursor.getBlob(faviconId);

                //add item on realm
                addOrmObject(realm, cursor.getString(titleId), null, blobIcon, cursor.getString(urlId));
                cnt ++;
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

    public boolean addOrmObject(Realm realm, String title, String iconPath, byte[] blobIcon, String url) {
        if(url == null) {
            return false;
        }

        realm.beginTransaction();
        Bookmark bookmark = realm.createObject(Bookmark.class);
        bookmark.setId(UUID.randomUUID().getLeastSignificantBits());
        bookmark.setName(title == null ? "" : title);
        if(iconPath != null) {
            bookmark.setIconPath(iconPath);
        }
        if(blobIcon != null) {
            bookmark.setBlobIcon(blobIcon);
        }
        bookmark.setUrl(url);
        bookmark.setTimestamp(Bookmark.Utils.getTodayTimestamp());
        bookmark.setLastUpdate(Bookmark.Utils.getTodayTimestamp());
        realm.commitTransaction();
        return true;
    }

    public void deleteBookmark(int position) {
        BookmarkRecyclerViewAdapter adapter = updateAdapterRef();
        mRealm.beginTransaction();
        adapter.getItem(position).removeFromRealm();
        mRealm.commitTransaction();
        adapter.notifyItemRemoved(position);
//        mAdapter.remove(position);
    }

    public void setDeletedItemPosition(int deletedItemPosition) {
        updateAdapterRef();
//        mAdapter.setSelectedItemPosition(deletedItemPosition);
    }

    public void onSwipeAction(int[] reverseSortedPositions) {
        int position = reverseSortedPositions[0];
        setDeletedItemPosition(position);
        deleteBookmark(position);

    }

    public void setAdapter() {
        try {
            RealmResults realmResults = mRealm.where(Bookmark.class).findAll();
            realmResults.sort("timestamp");

            RealmModelAdapter realmModelAdapter = new RealmModelAdapter(mActivityRef, realmResults, true);
            ((BookmarkRecyclerViewAdapter) mRecyclerView.getAdapter())
                    .setRealmBaseAdapter(realmModelAdapter);
            mRecyclerView.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAdapterByDataItems(RealmResults<Bookmark> realmResults) {
        try {
            realmResults.sort("timestamp");
            RealmModelAdapter realmModelAdapter = new RealmModelAdapter(mActivityRef, realmResults, true);
            ((BookmarkRecyclerViewAdapter) mRecyclerView.getAdapter())
                    .setRealmBaseAdapter(realmModelAdapter);
            mRecyclerView.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RealmResults<Bookmark> filterResults(String filter) {
        RealmQuery<Bookmark> query = mRealm.where(Bookmark.class);
        RealmResults<Bookmark> filteredList = query
                .contains("name", filter).or()
                .contains("url", filter)
                .findAll();

        return filteredList;
    }

    public void openLinkOnBrowser(String linkUrl) {
        try {
            if(! checkURL(linkUrl)) {
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

    private boolean checkURL(String linkUrl) {
        return true;
    }

    private void toggleClipboardLinkButtonWrapper() {
        try {
            ((BookmarkListFragment) mFragmentRef).toggleClipboardLinkButton(-1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showClipboardLinkButtonWrapper() {
        try {
            ((BookmarkListFragment) mFragmentRef).showClipboardButton();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideClipboardLinkButtonWrapper() {
        try {
            ((BookmarkListFragment) mFragmentRef).hideClipboardButton();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void hideSlidingPanelWrapper() {
        try {
            ((BookmarkListFragment) mFragmentRef).hideSlidingPanel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSlidingPanelWrapper() {
        try {
            ((BookmarkListFragment) mFragmentRef).showSlidingPanel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void hideSoftKeyboard(EditText editText) {
//        InputMethodManager imm = (InputMethodManager) mActivityRef.
//                getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
//    }


    public Intent getIntentForEditBookmark(Bookmark bookmark) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, Bookmark.Utils.stringify(bookmark));
        shareIntent.setType("text/plain");
        return shareIntent;
    }

    public BookmarkRecyclerViewAdapter getAdapter() {
        return ((BookmarkRecyclerViewAdapter) mRecyclerView.getAdapter());
    }

    public Bookmark getSelectedItemFromAdapter() {
        return ((Bookmark) ((BookmarkRecyclerViewAdapter) mRecyclerView.getAdapter())
                .getItem(mActionbarSingleton.getEditItemPos()));
    }


    private byte[] downloadBytesArrayByUrl(URL url) {
        byte[] byteArray;
        try {
            URLConnection conn = url.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int byteRead;
            while((byteRead = bis.read()) != -1) {
                baos.write(byteRead);
            }
            byteArray = baos.toByteArray();
            baos.close();
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e(TAG, "Error getting the image from server : " + e.getMessage().toString());
            return null;
        }
        return byteArray;
    }


    public void setSearchOnUrlEnabled(boolean searchOnUrlEnabled) {
        SharedPreferences sharedPref = mActivityRef
                .getSharedPreferences(BOOKMARKS_WALLET_SHAREDPREF, 0);

        sharedPref.edit().putBoolean(SEARCH_URL_MODE, searchOnUrlEnabled).apply();
        mSearchOnUrlEnabled = searchOnUrlEnabled;
    }

    public boolean isSearchOnUrlEnabled() {
        return mSearchOnUrlEnabled;
    }

    public void setAdsView(View view, int panelHeight) {
        mAdsView = view;
        mAdsOffset = panelHeight;
    }

    public boolean isBookmarkSyncByProvider() {
        return mBookmarkSyncByProvider;
    }

    public void setBookmarkSyncByProvider(boolean value) {
        mBookmarkSyncByProvider = value;
    }

    public class BookmarksProviderAsyncTask extends AsyncTask<URL, Integer, Boolean> {

        private final Integer N_OCCURENCES = 30;
        private final BrowserEnum[] browserList;

        public BookmarksProviderAsyncTask(BrowserEnum[] list) {
            browserList = list;
        }

        @Override
        protected Boolean doInBackground(URL... params) {
            mBookmarkSyncByProvider = true;
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
            publishProgress(count);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
//            every ten occurence
            if (values[0] % N_OCCURENCES == 0) {
                updateAdapterRef();
                setAdapter();
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mSwipeRefreshLayout.setRefreshing(false);
            mBookmarkSyncByProvider = false;
            updateAdapterRef();
            setAdapter();
        }
    }

}
