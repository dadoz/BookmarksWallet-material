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
import com.application.material.bookmarkswallet.app.adapter.realm.BookmarkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.adapter.realm.RealmModelAdapter;
import com.application.material.bookmarkswallet.app.fragments.BookmarkListFragment;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import com.application.material.bookmarkswallet.app.touchListener.SwipeDismissRecyclerViewTouchListener;
import io.realm.Realm;
import io.realm.RealmResults;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.MalformedURLException;
import java.net.URL;
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
    private static View mEditUrlView;
    private static BookmarkRecyclerViewAdapter mAdapter;
    private static SwipeDismissRecyclerViewTouchListener mTouchListener;
    private static SwipeRefreshLayout mSwipeRefreshLayout;
    private AlertDialog mEditDialog;
    private static Realm mRealm;

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
        mEditUrlView = mActivityRef.getLayoutInflater().
                inflate(R.layout.dialog_edit_url_layout, null);
        setAdapter();
        mRealm = Realm.getInstance(mActivityRef);
    }

    private static BookmarkRecyclerViewAdapter setAdapter() {
        return mAdapter = (BookmarkRecyclerViewAdapter)
                mRecyclerView.getAdapter();
    }

    public void setAdapterRef(BookmarkRecyclerViewAdapter adapterRef) {
        mAdapter = adapterRef;
    }

    public boolean saveEditLink() {
        boolean isSaved = false;
        try {
            setAdapter();
/*            int position = mAdapter.getSelectedItemPosition();

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
            mAdapter.notifyDataSetChanged();*/



        } catch (Exception e) {
            e.printStackTrace();
        }
        mActionBarHandlerSingleton.setTitle(null);
        mActionBarHandlerSingleton.toggleActionBar(false, false, false, R.id.infoOuterButtonId);

        mActivityRef.invalidateOptionsMenu();
        mRecyclerView.addOnItemTouchListener((RecyclerView.OnItemTouchListener) mListenerRef);
        animateButton(false);

        Toast.makeText(mActivityRef, isSaved ? "save" : "Error on saving bookmarks...", Toast.LENGTH_SHORT).show();
        return isSaved;
    }


    public void undoEditLink() {
        try {
            setAdapter();
            int position = mActionBarHandlerSingleton.getEditItemPos();
            mAdapter.notifyItemChanged(position);
            mActionBarHandlerSingleton.setEditItemPos(NOT_SELECTED_ITEM_POSITION);
            mActionBarHandlerSingleton.setTitle(null);
/*            int position = mAdapter.getSelectedItemPosition();

            mRecyclerView.setOnTouchListener(mTouchListener);
            LinkRecyclerViewAdapter.ViewHolder holder =
                    (LinkRecyclerViewAdapter.ViewHolder) mRecyclerView.
                            findViewHolderForPosition(position);
            if(holder != null) {
                hideSoftKeyboard(holder.getEditLinkView());
            }

            Toast.makeText(mActivityRef, "undo edit", Toast.LENGTH_SHORT).show();
            mAdapter.deselectedItemPosition();
            mAdapter.notifyDataSetChanged();
*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        mActivityRef.invalidateOptionsMenu();
        mRecyclerView.addOnItemTouchListener((RecyclerView.OnItemTouchListener) mListenerRef);
        animateButton(false);
    }

    public void selectBookmarkEditMenu(int position) {
        mAdapter = getAdapter();
        Toast.makeText(mActivityRef, "edit" + position, Toast.LENGTH_SHORT).show();

        mActionBarHandlerSingleton.setEditItemPos(position);
        mActionBarHandlerSingleton.setTitle("Edit link");
        mActionBarHandlerSingleton.toggleActionBar(true, true, true, R.id.infoOuterButtonId);

//        ((BookmarkRecyclerViewAdapter) mRecyclerView.getAdapter()).setSelectedItemPosition(position);
        BookmarkRecyclerViewAdapter.ViewHolder holder =
                (BookmarkRecyclerViewAdapter.ViewHolder) mRecyclerView.
                        findViewHolderForPosition(position);
        holder.itemView.setPressed(false);
        mAdapter.notifyItemChanged(position); //to change background color on view
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
/*        mEditDialog.dismiss();
        String modifiedUrl = ((EditText) mEditUrlView.findViewById(R.id.editLinkUrDialoglId)).
                getText().toString();

        int position = mAdapter.getSelectedItemPosition();
        LinkRecyclerViewAdapter.ViewHolder holder =
                (LinkRecyclerViewAdapter.ViewHolder) mRecyclerView.
                        findViewHolderForPosition(position);

        holder.setEditUrlName(modifiedUrl);*/
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
            private String iconUrl = null;
            @Override
            protected String doInBackground(URL... linkUrlArray) {
                try {
                    linkUrl = linkUrlArray[0].toString();
                    Document doc = Jsoup.connect(linkUrl).get();
                    org.jsoup.nodes.Element elem = doc.head().select("link[href~=.*\\.ico|png]").first();
                    iconUrl = elem.attr("href");
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
                addBookmark(linkUrl, iconUrl, null, linkUrlTitle);
            }

        }.execute(new URL(url));
    }

    public void addBookmark(String url, String iconPath, byte[] iconBlob, String title) {
//        title = title == null ? "" : title;
//        Link link = new Link(-1, null, null, title, url, -1, Link.getTodayTimestamp());
//        mDbConnector.insertLink(link);

//        ((LinkRecyclerViewAdapter) mRecyclerView.getAdapter()).add(link);
//        ((BookmarkRecyclerViewAdapter) mRecyclerView.getAdapter()).updateDataset();
        //UPDATE DATASET REF
        mRecyclerView.scrollToPosition(0);
        title = title == null ? "" : title;
        addOrmObject(mRealm, title, iconPath, iconBlob, url);
        update();
        mRecyclerView.getAdapter().notifyItemInserted(0);
        setAdapter();
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
                setAdapter();
                update();
            }
        }.execute();
    }

    public void addOrmObject(Realm realm, String title, String iconPath, byte[] blobIcon, String url) {
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
    }

    public void deleteBookmark(int position) {
        BookmarkRecyclerViewAdapter adapter = setAdapter();
        mRealm.beginTransaction();
        adapter.getItem(position).removeFromRealm();
        mRealm.commitTransaction();
        adapter.notifyItemRemoved(position);
//        mAdapter.remove(position);
    }

    public void setDeletedItemPosition(int deletedItemPosition) {
        setAdapter();
//        mAdapter.setSelectedItemPosition(deletedItemPosition);
    }

    public void onSwipeAction(int[] reverseSortedPositions) {
        int position = reverseSortedPositions[0];
        setDeletedItemPosition(position);
        deleteBookmark(position);

    }

    public void update() {
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


    public Intent getIntentForEditBookmark(Bookmark bookmark) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra("BOOKMARK_EXTRA", Bookmark.Utils.stringify(bookmark));
        return shareIntent;
    }

    public BookmarkRecyclerViewAdapter getAdapter() {
        return ((BookmarkRecyclerViewAdapter) mRecyclerView.getAdapter());
    }
}
