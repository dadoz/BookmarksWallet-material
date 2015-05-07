package com.application.material.bookmarkswallet.app.singleton;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Browser;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.realm.BookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.adapter.realm.RealmModelAdapter;
import com.application.material.bookmarkswallet.app.fragments.BookmarkListFragment;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.touchListener.SwipeDismissRecyclerViewTouchListener;
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

import static com.application.material.bookmarkswallet.app.singleton.ActionBarHandlerSingleton.NOT_SELECTED_ITEM_POSITION;

/**
 * Created by davide on 31/03/15.
 */
public class RecyclerViewActionsSingleton implements View.OnClickListener {
    private static final String TAG = "RecyclerViewActionsSingleton";
    private static RecyclerViewActionsSingleton mInstance;
    private static RecyclerView mRecyclerView;
    private static Activity mActivityRef;
    private static Fragment mListenerRef;
    private static ActionBarHandlerSingleton mActionBarHandlerSingleton;
    private static Fragment mFragmentRef;
//    private static View mEditUrlView;
    private static BookmarkRecyclerViewAdapter mAdapter;
    private static SwipeDismissRecyclerViewTouchListener mTouchListener;
    private static SwipeRefreshLayout mSwipeRefreshLayout;
    private AlertDialog mEditDialog;
    private static Realm mRealm;
    private View mEditTitleViewRef;
    private View mEditUrlViewRef;

    public RecyclerViewActionsSingleton() {
    }

    public static RecyclerViewActionsSingleton getInstance(SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView,
                                                           Activity activityRef,
                                                           Fragment listenerRef,
                                                           SwipeDismissRecyclerViewTouchListener touchListener) {
        initReferences(swipeRefreshLayout, recyclerView, activityRef, listenerRef, touchListener);
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
                                      Activity activityRef, Fragment listenerRef,
                                      SwipeDismissRecyclerViewTouchListener touchListener) {
        mSwipeRefreshLayout = swipeRefreshLayout;
        mRecyclerView = recyclerView;
        mActivityRef = activityRef;
        mListenerRef = listenerRef;
        mFragmentRef = listenerRef;
        mTouchListener = touchListener;
        mActionBarHandlerSingleton = ActionBarHandlerSingleton.getInstance(mActivityRef);
//        mEditUrlView = mActivityRef.getLayoutInflater().
//                inflate(R.layout.dialog_edit_url_layout, null);
        updateAdapterRef();
        mRealm = Realm.getInstance(mActivityRef);
    }

    private static BookmarkRecyclerViewAdapter updateAdapterRef() {
        return mAdapter = (BookmarkRecyclerViewAdapter)
                mRecyclerView.getAdapter();
    }

    /**
     * TODO refactor name
     */
    public void undoEditBookmark() {
        try {
            updateAdapterRef();
            mAdapter.notifyDataSetChanged();
//            int position = mActionBarHandlerSingleton.getEditItemPos();
//            mAdapter.notifyItemChanged(position);
            mActionBarHandlerSingleton.setEditItemPos(NOT_SELECTED_ITEM_POSITION);
            mActionBarHandlerSingleton.setTitle(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mActivityRef.invalidateOptionsMenu();

        //set on item click - swipe listener
        mRecyclerView.setOnTouchListener(mTouchListener);
//        mRecyclerView.addOnItemTouchListener((RecyclerView.OnItemTouchListener) mListenerRef);
        animateButton(false);
    }

    /**
     * TODO refactor name
     * @param position
     */
    public void selectBookmarkEditMenu(int position) {
        mAdapter = getAdapter();
        mActionBarHandlerSingleton.setEditItemPos(position);
        mActionBarHandlerSingleton.setTitle("Edit link");
        mActionBarHandlerSingleton.toggleActionBar(true, true, true, R.id.infoOuterButtonId);

//        BookmarkRecyclerViewAdapter.ViewHolder holder =
//                (BookmarkRecyclerViewAdapter.ViewHolder) mRecyclerView.
//                        findViewHolderForPosition(position);
        //position isnt stored //TODO
//        holder.itemView.setPressed(false);
        mAdapter.notifyItemChanged(position); //to change background color on view
        mActivityRef.invalidateOptionsMenu();

        mRecyclerView.setOnTouchListener(null);
//        mRecyclerView.removeOnItemTouchListener((RecyclerView.OnItemTouchListener) mListenerRef);
        animateButton(true);
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

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivityRef);
            mEditDialog = builder.
                    setTitle("Edit").
                    setView(editBookmarkView).
                    create();
            mEditDialog.show();

            mEditTitleViewRef = editBookmarkView.findViewById(R.id.editBookamrkTitleDialoglId);
            mEditUrlViewRef = editBookmarkView.findViewById(R.id.editBookmarkUrlDialoglId);
            editBookmarkView.findViewById(R.id.saveEditUrlDialogId).
                    setOnClickListener((View.OnClickListener) mListenerRef);
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
        mRealm.commitTransaction();

        mRecyclerView.getAdapter()
                .notifyItemChanged(mActionBarHandlerSingleton.getEditItemPos());
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
        title = title == null ? "" : title;
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

    public void setBookmarksByProvider() {
        mSwipeRefreshLayout.setRefreshing(true);
        new AsyncTask<URL, String, Boolean>() {

            @Override
            protected Boolean doInBackground(URL... params) {
                try {
                    ContentResolver cr = mActivityRef.getContentResolver();
                    Realm realm = Realm.getInstance(mActivityRef);
                    String[] projection = {
                            Browser.BookmarkColumns.CREATED,
                            Browser.BookmarkColumns.FAVICON,
                            Browser.BookmarkColumns.TITLE,
                            Browser.BookmarkColumns.URL
                    };
                    Cursor cursor = cr.query(Browser.BOOKMARKS_URI, projection, null, null, null);
                    int urlId = cursor.getColumnIndex(Browser.BookmarkColumns.URL);
                    int titleId = cursor.getColumnIndex(Browser.BookmarkColumns.TITLE);
                    int faviconId = cursor.getColumnIndex(Browser.BookmarkColumns.FAVICON);
//                    long timestamp = cursor.getColumnIndex(Browser.BookmarkColumns.CREATED);

                    if(cursor.moveToFirst()) {
                        do {
                            Log.e(TAG, "hey " + cursor.getString(urlId));
                            byte[] blobIcon = cursor.getBlob(faviconId);

                            //add item on realm
                            addOrmObject(realm, cursor.getString(titleId), null, blobIcon, cursor.getString(urlId));
                        } while(cursor.moveToNext());

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                return null;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                mSwipeRefreshLayout.setRefreshing(false);
//                mRecyclerView.getAdapter().notifyItemInserted(0);
                updateAdapterRef();
                setAdapter();
            }
        }.execute();
    }

    public boolean addOrmObject(Realm realm, String title, String iconPath, byte[] blobIcon, String url) {
        if(url == null) {
            return false;
        }

        realm.beginTransaction();
        Bookmark bookmark = realm.createObject(Bookmark.class);
        bookmark.setId(UUID.randomUUID().getLeastSignificantBits());
        bookmark.setName(title);
        if(iconPath != null) {
            bookmark.setIconPath(iconPath);
        }
        if(blobIcon != null) {
            bookmark.setBlobIcon(blobIcon);
        }
        bookmark.setUrl(url);
        bookmark.setTimestamp(Bookmark.Utils.getTodayTimestamp());
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

    private void animateButton(boolean animate) {
        try {
            ((BookmarkListFragment) mFragmentRef).toggleAddLinkButton(animate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveEditUrlDialogId:
                saveEditLinkDialog();
                break;
//            case R.id.editUrlLabelId:
//                String url = (String) v.getTag();
//                editLinkDialog(url);
//                break;
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
                .getItem(mActionBarHandlerSingleton.getEditItemPos()));
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


}
