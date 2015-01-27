package com.application.material.bookmarkswallet.app.dbAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdapter {
    public static final String TAG = "DatabaseAdapter_TAG";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "BookmarksWallet";
    public static final String LINKS_TABLE_NAME = "Links";

    public static final String ROWID_KEY = "_id";
    public static final String LINK_ORDER_IN_LIST_KEY = "order_in_list";
    public static final String LINK_NAME_KEY = "name";
    public static final String ICON_PATH_KEY = "icon_path";
    public static final String LINK_URL_KEY = "url";
    public static final String LINK_USER_ID_KEY = "user_id";
    public static final String LINK_DELETED_STATUS_KEY = "deleted_status";

    public static final String DATABASE_CREATE =
            "create table " + LINKS_TABLE_NAME + "(" +
                    ROWID_KEY + " integer primary key," +
                    LINK_ORDER_IN_LIST_KEY + " integer," +
                    LINK_NAME_KEY + " text not null," +
                    ICON_PATH_KEY + " text not null," +
                    LINK_URL_KEY + " text not null," +
                    LINK_USER_ID_KEY + " text not null," +
                    LINK_DELETED_STATUS_KEY + " text not null" +
                    ");";
    private static SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    public DbAdapter(Context ctx) {
        dbHelper = new DatabaseHelper(ctx);
    }

    public DbAdapter open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    //METHOD IMPLEMENTED
    public void dropDbTable() {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
            db.execSQL("VACUUM");
        } catch (Exception e) {
            Log.d(TAG, "Failed to do : " + e.getMessage());
        }
    }

    public long insertLink(int linkId, String linkOrderInList,
                           String linkName, String iconPath,
                           String linkUrl, String linksUserId,
                           boolean linkDeletedStatus) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(ROWID_KEY, linkId);
        initialValues.put(LINK_ORDER_IN_LIST_KEY, linkOrderInList);
        initialValues.put(LINK_NAME_KEY, linkName);
        initialValues.put(ICON_PATH_KEY, iconPath);
        initialValues.put(LINK_URL_KEY, linkUrl);
        initialValues.put(LINK_USER_ID_KEY, linksUserId);
        initialValues.put(LINK_DELETED_STATUS_KEY, linkDeletedStatus);
        return db.insert(LINKS_TABLE_NAME, null, initialValues);
    }

    public void deleteLinks() {
        db.delete(LINKS_TABLE_NAME, null, null);
    }

    public void deleteLinkById(int dbRowId) {
        //TODO check if dbRowId is int
        db.delete(LINKS_TABLE_NAME, ROWID_KEY + " = " + dbRowId, null);
    }

    public Cursor getLinks() {
        return db.query(LINKS_TABLE_NAME, new String[]{
                ROWID_KEY, LINK_ORDER_IN_LIST_KEY,
                LINK_NAME_KEY, ICON_PATH_KEY,
                LINK_URL_KEY, LINK_USER_ID_KEY,
                LINK_DELETED_STATUS_KEY
        }, null, null, null, null, null);
    }

    public Cursor getLinkById(long rowId) throws SQLException {
        Cursor mCursor =
                db.query(true, LINKS_TABLE_NAME, new String[]{
                                LINK_ORDER_IN_LIST_KEY, LINK_NAME_KEY,
                                ICON_PATH_KEY, LINK_URL_KEY,
                                LINK_USER_ID_KEY, LINK_DELETED_STATUS_KEY
                        }, ROWID_KEY + " = " + rowId,
                        null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean updateLink(long rowId, String linkOrderInList,
                              String linkName, String iconPath, String linkUrl,
                              String linksUserId, boolean linkDeletedStatus) {
        ContentValues values = new ContentValues();
        values.put(LINK_ORDER_IN_LIST_KEY, linkOrderInList);
        values.put(LINK_NAME_KEY, linkName);
        values.put(ICON_PATH_KEY, iconPath);
        values.put(LINK_URL_KEY, linkUrl);
        values.put(LINK_USER_ID_KEY, linksUserId);
        values.put(LINK_DELETED_STATUS_KEY, linkDeletedStatus);

        return db.update(LINKS_TABLE_NAME, values,
                ROWID_KEY + " = " + rowId, null) > 0;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version" + oldVersion
                    + " to "
                    + newVersion + ", wich will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
            onCreate(db);
        }
    }
}
