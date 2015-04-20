package com.application.material.bookmarkswallet.app.singleton;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import com.application.material.bookmarkswallet.app.adapter.LinkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.dbAdapter_old.DbConnector;
import com.application.material.bookmarkswallet.app.fragments.LinksListFragment;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.touchListener.SwipeDismissRecyclerViewTouchListener;
import io.realm.Realm;
import io.realm.RealmResults;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

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
    private static DbConnector mDbConnector;
    private static Fragment mFragmentRef;
    private static View mEditUrlView;
    private static LinkRecyclerViewAdapter mAdapter;
    private static SwipeDismissRecyclerViewTouchListener mTouchListener;
    private static SwipeRefreshLayout mSwipeRefreshLayout;
    private AlertDialog mEditDialog;
    private static Realm mRealm;

    public RecyclerViewActionsSingleton() {
    }

    public static RecyclerViewActionsSingleton getInstance(SwipeRefreshLayout swipeRefreshLayout, RecyclerView recyclerView,
                                                           Activity activityRef,
                                                           Fragment listenerRef,
                                                           DbConnector dbConnector,
                                                           SwipeDismissRecyclerViewTouchListener touchListener) {
        initReferences(swipeRefreshLayout, recyclerView, activityRef, listenerRef, dbConnector, touchListener);
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
                                      DbConnector dbConnector,
                                      SwipeDismissRecyclerViewTouchListener touchListener) {
        mSwipeRefreshLayout = swipeRefreshLayout;
        mRecyclerView = recyclerView;
        mActivityRef = activityRef;
        mListenerRef = listenerRef;
        mFragmentRef = listenerRef;
        mDbConnector = dbConnector;
        mTouchListener = touchListener;
        mActionBarHandlerSingleton = ActionBarHandlerSingleton.getInstance(mActivityRef);
        mEditUrlView = mActivityRef.getLayoutInflater().
                inflate(R.layout.dialog_edit_url_layout, null);
        mAdapter = (LinkRecyclerViewAdapter)
                mRecyclerView.getAdapter();
        mRealm = Realm.getInstance(mActivityRef);
    }

    public void setAdapterRef(LinkRecyclerViewAdapter adapterRef) {
        mAdapter = adapterRef;
    }
    public boolean saveEditLink() {
        boolean isSaved = false;
        int position = mAdapter.getSelectedItemPosition();

        mRecyclerView.setOnTouchListener(mTouchListener);
        LinkRecyclerViewAdapter.ViewHolder holder =
                (LinkRecyclerViewAdapter.ViewHolder) mRecyclerView.
                        findViewHolderForPosition(position);
        if(holder != null) {
            mAdapter.update(position, holder.getEditLinkName(), holder.getEditUrlName());
            hideSoftKeyboard(holder.getEditLinkView());
            isSaved = true;
        }

        mAdapter.deselectedItemPosition();
        mAdapter.notifyDataSetChanged();

        mActionBarHandlerSingleton.setTitle(null);
        mActionBarHandlerSingleton.toggleActionBar(false, false, false, R.id.infoOuterButtonId);

        mActivityRef.invalidateOptionsMenu();
        mRecyclerView.addOnItemTouchListener((RecyclerView.OnItemTouchListener) mListenerRef);
        animateButton(false);

        Toast.makeText(mActivityRef, isSaved ? "save" : "Error on saving bookmarks...", Toast.LENGTH_SHORT).show();
        return isSaved;
    }


    public void undoEditLink() {
        int position = mAdapter.getSelectedItemPosition();

        mRecyclerView.setOnTouchListener(mTouchListener);
        LinkRecyclerViewAdapter.ViewHolder holder =
                (LinkRecyclerViewAdapter.ViewHolder) mRecyclerView.
                        findViewHolderForPosition(position);
        if(holder != null) {
            hideSoftKeyboard(holder.getEditLinkView());
        }

        Toast.makeText(mActivityRef, "undo edit", Toast.LENGTH_SHORT).show();
        ((LinkRecyclerViewAdapter) mRecyclerView.getAdapter()).deselectedItemPosition();
        (mRecyclerView.getAdapter()).notifyDataSetChanged();
        mActivityRef.invalidateOptionsMenu();
        mRecyclerView.addOnItemTouchListener((RecyclerView.OnItemTouchListener) mListenerRef);
        animateButton(false);
    }

    public void editLink(int position) {
        Toast.makeText(mActivityRef, "edit" + position, Toast.LENGTH_SHORT).show();

        mActionBarHandlerSingleton.setEditMode(true);
        mActionBarHandlerSingleton.setTitle("Edit link");
        mActionBarHandlerSingleton.toggleActionBar(true, true, true, R.id.infoOuterButtonId);

        ((LinkRecyclerViewAdapter) mRecyclerView.getAdapter()).setSelectedItemPosition(position);
        mRecyclerView.getAdapter().notifyDataSetChanged();
        mActivityRef.invalidateOptionsMenu();
        mRecyclerView.removeOnItemTouchListener((RecyclerView.OnItemTouchListener) mListenerRef);
        animateButton(true);
    }
    

    //todo refactor in editUrlDialog
    public void editLinkDialog(String url) {
        ((EditText) mEditUrlView.findViewById(R.id.editLinkUrDialoglId)).
                setText(url);
        if(mEditDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivityRef);
            mEditDialog = builder.
                    setTitle("Edit Url").
                    setView(mEditUrlView).
                    create();
        }
        mEditDialog.show();

        mEditUrlView.findViewById(R.id.saveEditUrlDialogId).
                setOnClickListener((View.OnClickListener) mListenerRef);
    }

    public void saveEditLinkDialog() {
        mEditDialog.dismiss();
        String modifiedUrl = ((EditText) mEditUrlView.findViewById(R.id.editLinkUrDialoglId)).
                getText().toString();

        int position = mAdapter.getSelectedItemPosition();
        LinkRecyclerViewAdapter.ViewHolder holder =
                (LinkRecyclerViewAdapter.ViewHolder) mRecyclerView.
                        findViewHolderForPosition(position);

        holder.setEditUrlName(modifiedUrl);
    }


    public void addBookmarkWithInfo(String url) throws MalformedURLException {
        mSwipeRefreshLayout.setRefreshing(true);
        if(! url.contains("http://") &&
                ! url.contains("https://")) {
            //trying with http
            url = "http://" + url;
        }

        new AsyncTask<URL, Integer, String>() {
            private String linkUrl = null;
            @Override
            protected String doInBackground(URL... linkUrlArray) {
                try {
                    linkUrl = linkUrlArray[0].toString();
                    Document doc = Jsoup.connect(linkUrl).get();
                    org.jsoup.nodes.Element elem = doc.head().select("link[href~=.*\\.ico|png]").first();
                    String iconUrl = elem.attr("href");
                    Log.d(TAG, " - " + iconUrl);
                    return doc.title();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {

            }

            @Override
            protected void onPostExecute(String linkUrlTitle) {
                mSwipeRefreshLayout.setRefreshing(false);
                //CHECK out what u need
                addBookmark(linkUrl, linkUrlTitle);
            }

        }.execute(new URL(url));
    }

    public void addBookmark(String url, String title) {
//        title = title == null ? "" : title;
//        Link link = new Link(-1, null, null, title, url, -1, Link.getTodayTimestamp());
//        mDbConnector.insertLink(link);

//        ((LinkRecyclerViewAdapter) mRecyclerView.getAdapter()).add(link);
        title = title == null ? "" : title;
        addOrmObject(mRealm, title, null, title);
        mRecyclerView.scrollToPosition(0);
        ((LinkRecyclerViewAdapter) mRecyclerView.getAdapter()).updateDataset();

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

                            addOrmObject(realm, cursor.getString(titleId), blobIcon, cursor.getString(urlId));
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
                ((LinkRecyclerViewAdapter) mRecyclerView.getAdapter()).updateDataset();
            }
        }.execute();
    }


    public void addOrmObject(Realm realm, String title, byte[] blobIcon, String url) {
        realm.beginTransaction();
        Bookmark bookmark = realm.createObject(Bookmark.class);
        bookmark.setId(UUID.randomUUID().getLeastSignificantBits());
        bookmark.setName(title);
        if(blobIcon != null) {
            bookmark.setBlobIcon(blobIcon);
        }
        bookmark.setUrl(url);
        bookmark.setTimestamp(Bookmark.Utils.getTodayTimestamp());
//      bookmarkList.add(new Bookmark(-1, null, blobIcon, cursor.getString(titleId), cursor.getString(urlId), -1, Bookmark.getTodayTimestamp()));
        realm.commitTransaction();
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
            ((LinksListFragment) mFragmentRef).toggleAddLinkButton(animate);
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
            case R.id.editUrlLabelId:
                String url = (String) v.getTag();
                editLinkDialog(url);
                break;
        }

    }


    private void hideSoftKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) mActivityRef.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


}
