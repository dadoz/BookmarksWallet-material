package com.application.material.bookmarkswallet.app.dbAdapter_old;

import android.content.Context;
import android.database.Cursor;
import com.application.material.bookmarkswallet.app.dbAdapter_old.sample.Utils;
import com.application.material.bookmarkswallet.app.models.Bookmark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by davide on 14/01/15.
 */
public class DbConnector implements BookmarksDataInterface {
    private static final String TAG = "linksParserJSONData_TAG";
    private static String EMPTY_LINK_LIST = "EMPTY";
    private static DbConnector mInstance;
    private final DbAdapter dbAdapter;

    public DbConnector(Context ctx) {
        dbAdapter = new DbAdapter(ctx);
    }

    public static DbConnector getInstance(Context ctx) {
        if (mInstance == null) {
            return mInstance = new DbConnector(ctx);
        }

        return mInstance;
    }

    /**
     * GET ALL ROWS from dbAdapter*
     */
    public static int countRows(DbAdapter dbAdapter) {
        dbAdapter.open();

        Cursor c = dbAdapter.getLinks();
        int count = c.getCount();

        dbAdapter.close();
        return count;
    }

    /**
     * GET ONE ROW from dbAdapter*
     */
    public static int getMaxOnLinkIdWrappLocalDb(DbAdapter dbAdapter) {
        dbAdapter.open();
        int linkId;
        boolean emptyDb = true;
//        Cursor mCursor = dbAdapter.getMaxOnLinkId();
        Cursor mCursor = null;
        linkId = Utils.EMPTY_LINKID;
        if (mCursor != null) {
            emptyDb = false;
            mCursor.moveToFirst();
            if (linkId < mCursor.getInt(0))
                linkId = mCursor.getInt(0);
        }
        dbAdapter.close();

        if (emptyDb)
            return 1;
        return linkId;
    }

/*    public static boolean getBooleanByInt(int value) {
        try {
            if (value == 1)
                return true;
        } catch (Exception e) {
            return false;
        }
        return false;
    }*/

    @Override
    public boolean insertLink(Bookmark bookmarkObj) {
        if (bookmarkObj == null) {
            return false;
        }

        dbAdapter.open();
        long result = dbAdapter.insertLink(bookmarkObj.getName(), bookmarkObj.getUrl(), bookmarkObj.getIconPath(),
                bookmarkObj.getBlobIcon(), Integer.toString(bookmarkObj.getUserId()), bookmarkObj.getTimestamp());
        dbAdapter.close();
        return result != -1;
    }

    public boolean insertLinkList(ArrayList<Bookmark> bookmarkList) {
        if (bookmarkList == null) {
            return false;
        }

        dbAdapter.open();
        long result = -1;
        for(Bookmark bookmarkObj : bookmarkList) {
            result = dbAdapter.insertLink(bookmarkObj.getName(), bookmarkObj.getUrl(), bookmarkObj.getIconPath(),
                    bookmarkObj.getBlobIcon(), Integer.toString(bookmarkObj.getUserId()), bookmarkObj.getTimestamp());
        }
        dbAdapter.close();
        return result != -1;
    }

    /**
     * GET ALL ROWS from dbAdapter*
     */
    @Override
    public ArrayList<Bookmark> getLinkList() {
        ArrayList<Bookmark> bookmarkList = new ArrayList<Bookmark>();
        dbAdapter.open();

        Cursor c = dbAdapter.getLinks();
        if (c.moveToFirst()) {
            do {
//                bookmarkList.add(new Bookmark(c.getInt(0), c.getString(3), c.getBlob(7), c.getString(2), c.getString(4), c.getInt(5), c.getLong(6)));
            } while (c.moveToNext());
        }

        dbAdapter.close();
        sortByTimestamp(bookmarkList);
        return bookmarkList;
//        return linkList.size() == 0 ? linkList : linkList;
    }

    private void sortByTimestamp(ArrayList<Bookmark> bookmarkList) {
        Collections.sort(bookmarkList, new Comparator<Bookmark>() {
            @Override
            public int compare(Bookmark lhs, Bookmark rhs) {
                long res = rhs.getTimestamp() - lhs.getTimestamp();
                return (int) res;
            }
        });
    }

    /**
     * GET ALL ROWS from dbAdapter*
     */
    @Override
    public ArrayList<Bookmark> getLinkListTest() {
        boolean emptyDb = true;
        ArrayList<Bookmark> bookmarkList = new ArrayList<Bookmark>();
        dbAdapter.open();

        Cursor c = dbAdapter.getLinks();
        if (c.moveToFirst()) {
            emptyDb = false;
            do {
                //TODO add c.getInt(1) in Link obj - linkOrderInList
                //TODO to be fixed inconPath pos 3 in dbAdapter but must be in pos 2
//        		public Link(int linkId,String linkIconPath,String linkName,String linkUrl,int userId,String delIcon,boolean linkDeleted){

//                bookmarkList.add(new Bookmark(c.getInt(0), c.getString(3), c.getBlob(7), c.getString(2), c.getString(4),
//                        c.getInt(5), c.getLong(6)));
            } while (c.moveToNext());
        }

        dbAdapter.close();
        if (emptyDb)
            return null;
        return bookmarkList;
    }

    /**
     * GET ONE ROW from dbAdapter*
     */
    @Override
    public Bookmark getLinkById(int linkId) {
        Bookmark bookmarkObj = null;
        dbAdapter.open();

        Cursor c = dbAdapter.getLinkById(linkId);
        if (c.moveToFirst()) {
//            bookmarkObj = new Bookmark(c.getInt(0), c.getString(3), c.getBlob(7),
//                    c.getString(2), c.getString(4), c.getInt(5), c.getLong(6));
        }


        dbAdapter.close();
        return bookmarkObj;
    }

    /**
     * GET ONE ROW from dbAdapter*
     */
    @Override
    public boolean deleteAllLinks() {
        dbAdapter.open();
        dbAdapter.dropDbTable();
        dbAdapter.deleteLinks();
        dbAdapter.close();
        return true;
    }

    //UTILS

    /**
     * GET ONE ROW from dbAdapter*
     */
    @Override
    public boolean deleteLinkById(int linkId) {
        dbAdapter.open();
        dbAdapter.deleteLinkById(linkId);
        dbAdapter.close();
        return true;
    }

    /**
     * GET ONE ROW from dbAdapter*
     */
    @Override
    public boolean deleteLinkByObject(Bookmark bookmarkObj) {
        dbAdapter.open();
        //add delete function
        dbAdapter.close();
        return true;
    }

    @Override
    public void updateLinkByObject(Bookmark bookmarkObj) {
        dbAdapter.open();
        //TODO not sure if linkId is the same as rowId
        long rowId = bookmarkObj.getId();
        dbAdapter.updateLink(rowId, null, bookmarkObj.getName(),
                bookmarkObj.getIconPath(), bookmarkObj.getBlobIcon(), bookmarkObj.getUrl(),
                Integer.toString(bookmarkObj.getUserId()), bookmarkObj.getTimestamp());
        dbAdapter.close();
    }
}