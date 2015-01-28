package com.application.material.bookmarkswallet.app.dbAdapter;

import android.content.Context;
import android.database.Cursor;
import com.application.material.bookmarkswallet.app.dbAdapter.sample.Utils;
import com.application.material.bookmarkswallet.app.models.Link;

import java.util.ArrayList;

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

    public static boolean getBooleanByInt(int value) {
        try {
            if (value == 1)
                return true;
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    @Override
    public boolean insertLink(Link linkObj) {
        if (linkObj == null) {
            return false;
        }

        dbAdapter.open();
        dbAdapter.insertLink(linkObj.getLinkId(), EMPTY_LINK_LIST, linkObj.getLinkName(), linkObj.getIconPath(),
                linkObj.getLinkUrl(), Integer.toString(linkObj.getUserId()), linkObj.isLinkDeleted());
        dbAdapter.close();
        return true;
    }

    /**
     * INSERT ROW in dbAdapter - overloading insert function
     */
    @Override
    public boolean insertLink(int linkId, int linkOrderInList,
                              String linkName, String iconPath,
                              String linkUrl, int linksUserId) {
        if (linkUrl == null) {
            return false;
        }

        dbAdapter.open();
        dbAdapter.insertLink(linkId, Integer.toString(linkOrderInList), linkName, iconPath,
                linkUrl, Integer.toString(linksUserId), false);

        dbAdapter.close();
        return true;
    }

    /**
     * GET ALL ROWS from dbAdapter*
     */
    @Override
    public ArrayList<Link> getLinkList() {
        boolean emptyDb = true;
        ArrayList<Link> linkList = new ArrayList<Link>();
        dbAdapter.open();

        Cursor c = dbAdapter.getLinks();
        if (c.moveToFirst()) {
            emptyDb = false;
            do {
                //TODO add c.getInt(1) in Link obj - linkOrderInList
                //TODO to be fixed inconPath pos 3 in dbAdapter but must be in pos 2
//        		public Link(int linkId,String linkIconPath,String linkName,String linkUrl,int userId,String delIcon,boolean linkDeleted){

                if (!getBooleanByInt(c.getInt(6)))
                    linkList.add(new Link(c.getInt(0), c.getString(3), c.getString(2), c.getString(4),
                            c.getInt(5), null, getBooleanByInt(c.getInt(6))));
            } while (c.moveToNext());
        }

        dbAdapter.close();
        if (emptyDb)
            return null;
        return linkList;
    }

    /**
     * GET ALL ROWS from dbAdapter*
     */
    @Override
    public ArrayList<Link> getLinkListTest() {
        boolean emptyDb = true;
        ArrayList<Link> linkList = new ArrayList<Link>();
        dbAdapter.open();

        Cursor c = dbAdapter.getLinks();
        if (c.moveToFirst()) {
            emptyDb = false;
            do {
                //TODO add c.getInt(1) in Link obj - linkOrderInList
                //TODO to be fixed inconPath pos 3 in dbAdapter but must be in pos 2
//        		public Link(int linkId,String linkIconPath,String linkName,String linkUrl,int userId,String delIcon,boolean linkDeleted){

                linkList.add(new Link(c.getInt(0), c.getString(3), c.getString(2), c.getString(4),
                        c.getInt(5), null, getBooleanByInt(c.getInt(6))));
            } while (c.moveToNext());
        }

        dbAdapter.close();
        if (emptyDb)
            return null;
        return linkList;
    }

    /**
     * GET ONE ROW from dbAdapter*
     */
    @Override
    public Link getLinkById(int linkId) {
        Link linkObj = null;
        dbAdapter.open();

        Cursor c = dbAdapter.getLinkById(linkId);
        if (c.moveToFirst()) {
            linkObj = new Link(c.getInt(0), c.getString(2),
                    c.getString(3), c.getString(4), c.getInt(5),
                    null, getBooleanByInt(c.getInt(6)));
        }

        dbAdapter.close();
        return linkObj;
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
    public boolean deleteLinkByObject(Link linkObj) {
        dbAdapter.open();
        //add delete function
        dbAdapter.close();
        return true;
    }

    @Override
    public void updateLinkByObject(Link linkObj) {
        dbAdapter.open();
        //TODO not sure if linkId is the same as rowId
        long rowId = linkObj.getLinkId();
        dbAdapter.updateLink(rowId, Integer.toString(linkObj.getLinkOrderInList()), linkObj.getLinkName(),
                linkObj.getIconPath(), linkObj.getLinkUrl(), Integer.toString(linkObj.getUserId()),
                linkObj.isLinkDeleted());
        dbAdapter.close();
    }
}