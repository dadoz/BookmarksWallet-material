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
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.LinkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.dbAdapter.DbConnector;
import com.application.material.bookmarkswallet.app.fragments.LinksListFragment;
import com.application.material.bookmarkswallet.app.models.Link;
import com.application.material.bookmarkswallet.app.touchListener.SwipeDismissRecyclerViewTouchListener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

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


    public void addLinkRetrivingUrlInfo(String url) throws MalformedURLException {
        mSwipeRefreshLayout.setRefreshing(true);
        if(! url.contains("http://") &&
                ! url.contains("https://")) {
            //trying with http
            url = "http://" + url;
        }

        new LinkUrlInfoAsyncTask().execute(new URL(url));
    }

    public void addLink(String url, String title) {
        //TODO fix title == null
        title = title == null ? "" : title;

        Link link = new Link(-1, null, null, title, url, -1, Link.getTodayTimestamp());
        ((LinkRecyclerViewAdapter) mRecyclerView.getAdapter()).add(link);
        mDbConnector.insertLink(link);
        mRecyclerView.scrollToPosition(0);
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

    public ArrayList<Link> getBookmarksByProvider() {
        //TODO asyncTask
        ArrayList<Link> bookmarkList = new ArrayList<Link>();
        try {
            ContentResolver cr = mActivityRef.getContentResolver();
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
            long timestamp = cursor.getColumnIndex(Browser.BookmarkColumns.CREATED);

            if(cursor.moveToFirst()) {
                do {
                    Log.e(TAG, "hey " + cursor.getString(urlId));
                    byte[] blobIcon = cursor.getBlob(faviconId);
                    Bitmap favicon = null;
                    if(blobIcon != null) {
                        favicon = BitmapFactory.decodeByteArray(blobIcon, 0, blobIcon.length);
                    }
                    bookmarkList.add(new Link(-1, null, favicon, cursor.getString(titleId), cursor.getString(urlId), -1, Link.getTodayTimestamp()));
                } while(cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bookmarkList;
    }

    private void hideSoftKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) mActivityRef.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public ArrayList<Link> getLinkListMockup() {
        ArrayList<Link> linksDataList = new ArrayList<Link>();
        ArrayList<String> linksUrlArray = new ArrayList<String>();
        boolean deletedLinkFlag = false;

        linksUrlArray.add("heavy metal1");
        linksUrlArray.add("pop1");
        linksUrlArray.add("underground");
        linksUrlArray.add("heavy metal");
        linksUrlArray.add("underground");
        linksUrlArray.add("underground");
        linksUrlArray.add("heavy metal");
        linksUrlArray.add("underground");
        linksUrlArray.add("hey_ure_fkin_my_shitty_dog_are_u_sure_u_want_to_cose_ure_crazy");
        linksUrlArray.add("bla1");
        linksUrlArray.add("link2");
        linksUrlArray.add("bla1");
        linksUrlArray.add("link2");
        String linkUrl = "http://www.google.it";
        int userId = 0;
        for(int i = 0; i < linksUrlArray.size(); i ++) {
            linksDataList.add(new Link(i, "ic_launcher", null, linksUrlArray.get(i), linkUrl, userId, 0));
        }
        return linksDataList;
    }

    private class LinkUrlInfoAsyncTask extends AsyncTask<URL, Integer, String> {
        private String linkUrl = null;
        @Override
        protected String doInBackground(URL... linkUrlArray) {
            try {
                linkUrl = linkUrlArray[0].toString();
                Document doc = Jsoup.connect(linkUrl).get();
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
            addLink(linkUrl, linkUrlTitle);
        }

    }

}
