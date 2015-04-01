package com.application.material.bookmarkswallet.app.singleton;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Browser;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.application.material.bookmarkswallet.app.R;
import com.application.material.bookmarkswallet.app.adapter.LinkRecyclerViewAdapter;
import com.application.material.bookmarkswallet.app.dbAdapter.DbConnector;
import com.application.material.bookmarkswallet.app.fragments.LinksListFragment;
import com.application.material.bookmarkswallet.app.models.Link;

import java.util.ArrayList;

/**
 * Created by davide on 31/03/15.
 */
public class RecyclerViewActionsSingleton {
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
    private AlertDialog mEditDialog;

    public RecyclerViewActionsSingleton() {
    }

    public static RecyclerViewActionsSingleton getInstance(RecyclerView recyclerView,
                                                           Activity activityRef,
                                                           Fragment listenerRef,
                                                           DbConnector dbConnector) {
        initReferences(recyclerView, activityRef, listenerRef, dbConnector);
        if(mInstance == null) {
            mInstance = new RecyclerViewActionsSingleton();
        }
        return mInstance;
    }

    public static void initReferences(RecyclerView recyclerView,
                               Activity activityRef, Fragment listenerRef,
                               DbConnector dbConnector) {
        mRecyclerView = recyclerView;
        mActivityRef = activityRef;
        mListenerRef = listenerRef;
        mFragmentRef = listenerRef;
        mDbConnector = dbConnector;
        mActionBarHandlerSingleton = ActionBarHandlerSingleton.getInstance(mActivityRef);
        mEditUrlView = mActivityRef.getLayoutInflater().
                inflate(R.layout.dialog_edit_url_layout, null);
        mAdapter = (LinkRecyclerViewAdapter)
                mRecyclerView.getAdapter();

    }

    public void saveEditLink() {
        int position = mAdapter.getSelectedItemPosition();
        LinkRecyclerViewAdapter.ViewHolder holder =
                (LinkRecyclerViewAdapter.ViewHolder) mRecyclerView.
                        findViewHolderForPosition(position);
        mAdapter.update(position, holder.getEditLinkName(), holder.getEditUrlName());
        Toast.makeText(mActivityRef, "save", Toast.LENGTH_SHORT).show();

        mAdapter.deselectedItemPosition();
        mAdapter.notifyDataSetChanged();

        mActionBarHandlerSingleton.toggleActionBar(null, false, false, R.id.infoButtonLayoutId);

        mActivityRef.invalidateOptionsMenu();
        mRecyclerView.addOnItemTouchListener((RecyclerView.OnItemTouchListener) mListenerRef);
        animateButton(false);
    }


    public void undoEditLink() {
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
        mActionBarHandlerSingleton.toggleActionBar("Edit link", true, true, R.id.infoButtonLayoutId);

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


    public void addLink(String url) {
        Link link = new Link(-1, null, "NEW FAKE", url, -1);
        ((LinkRecyclerViewAdapter) mRecyclerView.getAdapter()).add(link);
        mDbConnector.insertLink(link);
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


    public ArrayList<Link> getBookmarksByProvider() {
        //TODO asyncTask
        ArrayList<Link> bookmarkList = new ArrayList<Link>();
        try {
            ContentResolver cr = mActivityRef.getContentResolver();
            String[] projection = {
                    Browser.BookmarkColumns.FAVICON,
                    Browser.BookmarkColumns.TITLE,
                    Browser.BookmarkColumns.URL
            };
            Cursor cursor = cr.query(Browser.BOOKMARKS_URI, projection, null, null, null);
            int urlId = cursor.getColumnIndex(Browser.BookmarkColumns.URL);
            int titleId = cursor.getColumnIndex(Browser.BookmarkColumns.TITLE);
            int faviconId = cursor.getColumnIndex(Browser.BookmarkColumns.FAVICON);

            if(cursor.moveToFirst()) {
                do {
                    Log.e(TAG, "hey " + cursor.getString(urlId));
//					Bitmap favicon = BitmapFactory.decodeByteArray(cursor.getBlob(faviconId), 0, 0, null);
                    bookmarkList.add(new Link(-1, null, cursor.getString(titleId), cursor.getString(urlId), -1));
                } while(cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bookmarkList;
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
            linksDataList.add(new Link(i, "ic_launcher", linksUrlArray.get(i), linkUrl, userId));
        }
        return linksDataList;
    }


}
