package com.application.material.bookmarkswallet.app.dbAdapter;

//import android.os.Bundle;
//import android.app.Activity;
//import android.view.Menu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdapter {
	public static final String DATABASE_NAME="BookmarksWalletDb_tmp";
	public static final int DATABASE_VERSION=2;
	public static final String LINKS_TABLE_NAME="LinksTable_tmp2";

	public static final String TAG="DatabaseAdapter_TAG";
	public static final String ROWID_KEY="_id";
	public static final String LINK_ORDER_IN_LIST_KEY="linkOrderInList";
	public static final String LINK_NAME_KEY="linkName";
	public static final String ICON_PATH_KEY="iconPath";
	public static final String LINK_URL_KEY="linkUrl";
	public static final String LINK_USER_ID_KEY="linksUserId";
	public static final String LINK_DELETED_STATUS_KEY="linkDeletedStatus";

	public static final String LOG_TABLE_NAME="LogTable_tmp";

	public static final String LOG_ACTION_KEY="action";
	public static final String LOG_MODEL_KEY="model";
	public static final String LOG_MODEL_ID_KEY="modelId";

	public static final String DATABASE_CREATE=
			"create table "+LINKS_TABLE_NAME+"("+ ROWID_KEY+" integer primary key,"
			+LINK_ORDER_IN_LIST_KEY+ " integer,"
			+LINK_NAME_KEY+" text not null,"
			+ICON_PATH_KEY+" text not null,"
			+LINK_URL_KEY+" text not null,"
			+LINK_USER_ID_KEY+" text not null,"
			+LINK_DELETED_STATUS_KEY+" text not null);";

	public static final String LOG_DB_CREATE=
			"create table "+LOG_TABLE_NAME+"(_id integer primary key autoincrement,"
			+LOG_ACTION_KEY+ " text not null,"
			+LOG_MODEL_KEY+" text not null,"
			+LOG_MODEL_ID_KEY+" integer);";

	private final Context context;

	private DatabaseHelper DBHelper;
	private static SQLiteDatabase db;

	public DbAdapter(Context ctx){
		this.context=ctx;
		DBHelper=new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context){
			super(context,DATABASE_NAME,null,DATABASE_VERSION);			
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(DATABASE_CREATE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			Log.w(TAG, "Upgrading database from version" + oldVersion
					+ " to "
					+ newVersion + ", wich will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS "+DATABASE_NAME);
			onCreate(db);
		}
	}
	
	//open the database
	public DbAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
		
	}
	
	//close the database
	public void close(){
		DBHelper.close();
	}

	//insert a title into the database --- row of a db
	public long insertLink(int linkId,String linkOrderInList, String linkName, String iconPath,
			String linkUrl,String linksUserId,boolean linkDeletedStatus){
		
		ContentValues initialValues = new ContentValues();
		initialValues.put(ROWID_KEY, linkId);
		initialValues.put(LINK_ORDER_IN_LIST_KEY, linkOrderInList);
		initialValues.put(LINK_NAME_KEY, linkName);
		initialValues.put(ICON_PATH_KEY, iconPath);
		initialValues.put(LINK_URL_KEY, linkUrl);
		initialValues.put(LINK_USER_ID_KEY, linksUserId);
		initialValues.put(LINK_DELETED_STATUS_KEY, linkDeletedStatus);

		return db.insert(LINKS_TABLE_NAME,null, initialValues);

	}
	//delete all rows
	public void deleteLinks(){
		//delete all db table
		db.delete(LINKS_TABLE_NAME, null, null);
	}
	
	//delete row by rowId
	public void deleteLinkById(int dbRowId){
		//TODO check if dbRowId is int
		db.delete(LINKS_TABLE_NAME, ROWID_KEY + "=" + dbRowId, null);
	}

    //update title
    public boolean fakeDeleteLinkById(long rowId){
    	
    	boolean linkDeletedStatus=true;
    	ContentValues initialValues = new ContentValues();
    	initialValues.put(LINK_DELETED_STATUS_KEY, linkDeletedStatus);

        return db.update(LINKS_TABLE_NAME, initialValues, 
                         ROWID_KEY + "=" + rowId, null) > 0;
    }

	public void dropDbTable(){
		try {
			  db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
			  db.execSQL("VACUUM");
		}catch (Exception e){
			Log.d(TAG, "Failed to do : " + e.getMessage());
		}	
	}   
	//retrieve all titles
    public Cursor getLinks(){
        return db.query(LINKS_TABLE_NAME, new String[] {
        		ROWID_KEY,
        		LINK_ORDER_IN_LIST_KEY,
   				LINK_NAME_KEY,
   				ICON_PATH_KEY,		
        		LINK_URL_KEY,
        		LINK_USER_ID_KEY,
        		LINK_DELETED_STATUS_KEY},
        		null, 
                null, 
                null, 
                null, 
                null);
    }

    //retrieve a particular title
    public Cursor getLinkById(long rowId) throws SQLException {
        Cursor mCursor =
                db.query(true, LINKS_TABLE_NAME, new String[] {
                		LINK_ORDER_IN_LIST_KEY,
           				LINK_NAME_KEY,
           				ICON_PATH_KEY,		
                		LINK_URL_KEY,
                		LINK_USER_ID_KEY,
                		LINK_DELETED_STATUS_KEY},
                		ROWID_KEY + "=" + rowId, 
                		null,
                		null, 
                		null, 
                		null, 
                		null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //update title
    public boolean updateLink(long rowId,String linkOrderInList, String linkName, String iconPath,
    		String linkUrl,String linksUserId,boolean linkDeletedStatus){
    	
    	ContentValues initialValues = new ContentValues();
    	initialValues.put(LINK_ORDER_IN_LIST_KEY, linkOrderInList);
    	initialValues.put(LINK_NAME_KEY, linkName);
    	initialValues.put(ICON_PATH_KEY, iconPath);
    	initialValues.put(LINK_URL_KEY, linkUrl);
    	initialValues.put(LINK_USER_ID_KEY, linksUserId);
    	initialValues.put(LINK_DELETED_STATUS_KEY, linkDeletedStatus);
    	
        return db.update(LINKS_TABLE_NAME, initialValues, 
                         ROWID_KEY + "=" + rowId, null) > 0;
    }

    /***get max LinkId**/
	public Cursor getMaxOnLinkId() {
		// TODO Auto-generated method stub
		try {
//			 SQLiteDatabase db=this.getReadableDatabase();
			return db.rawQuery("SELECT MAX("+ROWID_KEY+") FROM "+LOG_TABLE_NAME,new String[] {});
		}catch (Exception e){
			Log.d(TAG, "Failed to do : " + e.getMessage());
		}	
		return null;
}   

    
    
    
    
    
    
    
    
    
    
}
