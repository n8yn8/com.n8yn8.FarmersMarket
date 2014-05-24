package com.n8yn8.farmersmarket;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.n8yn8.farmersmarket.Contract.FeedEntry;

public class ItemDbController {

	private static final String TAG = "ItemDbController";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	public static final String FILE_DIR = "here";

	private static final String LOGCAT = null;
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "Items.db";
	private static final String INTERNAL = DATABASE_NAME;
	//private static final String EXTERNAL = "Environment.getExternalStorageDirectory()+ File.separator + "FM" + File.separator + DATABASE_NAME";
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_ENTRIES =
			"CREATE TABLE " + FeedEntry.TABLE_NAME_I + " (" +
					FeedEntry._ID + " INTEGER PRIMARY KEY," +
					FeedEntry.COLUMN_NAME_Item + TEXT_TYPE + COMMA_SEP + 
					FeedEntry.COLUMN_NAME_Type + TEXT_TYPE + COMMA_SEP + 
					FeedEntry.COLUMN_NAME_Price + TEXT_TYPE + COMMA_SEP +
					FeedEntry.COLUMN_NAME_Unit + TEXT_TYPE + COMMA_SEP +
					FeedEntry.COLUMN_NAME_Vendor + TEXT_TYPE + COMMA_SEP +
					FeedEntry.COLUMN_NAME_Start + TEXT_TYPE + COMMA_SEP +
					FeedEntry.COLUMN_NAME_End + TEXT_TYPE + COMMA_SEP +
					FeedEntry.COLUMN_NAME_Added + TEXT_TYPE + COMMA_SEP +
					FeedEntry.COLUMN_NAME_Photo + TEXT_TYPE +" )";

	private static final String[] projection = {
		FeedEntry._ID,
		FeedEntry.COLUMN_NAME_Item,
		FeedEntry.COLUMN_NAME_Type,
		FeedEntry.COLUMN_NAME_Price,
		FeedEntry.COLUMN_NAME_Unit,
		FeedEntry.COLUMN_NAME_Vendor,
		FeedEntry.COLUMN_NAME_Start,
		FeedEntry.COLUMN_NAME_End,
		FeedEntry.COLUMN_NAME_Added,
		FeedEntry.COLUMN_NAME_Photo
	};

	private static final String SQL_DELETE_ENTRIES =
			"DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME_I;

	private static Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper{

		DatabaseHelper(Context applicationcontext) {
			super(applicationcontext, Environment.getExternalStorageDirectory()+ File.separator + "FM" + File.separator + DATABASE_NAME, null, DATABASE_VERSION);
			Log.d(LOGCAT,DATABASE_NAME + " Created");
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_ENTRIES);
			Log.d(LOGCAT,"items Created");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int version_old, int current_version) {
			db.execSQL(SQL_DELETE_ENTRIES);
			onCreate(db);
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx the Context within which to work
	 */
	public ItemDbController(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the notes database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException if the database could be neither opened or created
	 */
	public ItemDbController open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		Log.d(TAG,"items openned");
		return this;
	}

	public void close() {
		mDbHelper.close();
		Log.d(TAG,"items closed");
	}

	public long insertItem(String item, String type, String price, String unit, String vendor, String start, String end, String added, String photo) {
		ContentValues values = new ContentValues();
		values.put(FeedEntry.COLUMN_NAME_Item, item);
		values.put(FeedEntry.COLUMN_NAME_Type, type);
		values.put(FeedEntry.COLUMN_NAME_Price, price);
		values.put(FeedEntry.COLUMN_NAME_Unit, unit);
		values.put(FeedEntry.COLUMN_NAME_Vendor, vendor);
		values.put(FeedEntry.COLUMN_NAME_Start, start);
		values.put(FeedEntry.COLUMN_NAME_End, end);
		values.put(FeedEntry.COLUMN_NAME_Added, added);
		values.put(FeedEntry.COLUMN_NAME_Photo, photo);
		return mDb.insert(FeedEntry.TABLE_NAME_I, FeedEntry.COLUMN_NAME_NULLABLE, values);
	}

	/**
	 * Delete the note with the given rowId
	 * 
	 * @param rowId id of note to delete
	 * @return true if deleted, false otherwise
	 */
	public void deleteItem(long rowId) {

		String deleteQuery = "DELETE FROM "+FeedEntry.TABLE_NAME_I+" where "+FeedEntry._ID+"='"+ rowId +"'";
		Log.d("query",deleteQuery);		
		mDb.execSQL(deleteQuery);

		//return mDb.delete(FeedEntry.TABLE_NAME_I, FeedEntry._ID + "=" + rowId, null) > 0;
	}

	/**
	 * Return a Cursor over the list of all items in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor getAllItems() {

		return mDb.query(FeedEntry.TABLE_NAME_I,projection,null,null,null,null,null);
	}

	/**
	 * Return a Cursor over the list of items of the given column in the database
	 * 
	 * @param category of the items to retrieve in column
	 * @return Cursor positioned to matching items, if found
	 * @throws SQLException if list could not be found/retrieved
	 */
	public Cursor getItemsOf(String category, String column, String sortBy){
		String sortOrder = sortBy + " DESC";
		String selection = column + " LIKE ?";
		String[] selectionArgs = { category };

		return mDb.query(FeedEntry.TABLE_NAME_I,projection,selection,selectionArgs,null,null,sortOrder);

	}
	
	/**
	 * Return a Cursor over the list of items of the given column in the database
	 * 
	 * @param category of the items to retrieve in column
	 * @return Cursor positioned to matching items, if found
	 * @throws SQLException if list could not be found/retrieved
	 */
	public Cursor getItemsOfAdded(String category, String column, String sortBy){
		String sortOrder = sortBy + " DESC";
		String selection = column + " LIKE ?";
		String[] selectionArgs = {category};

		return mDb.query(FeedEntry.TABLE_NAME_I,projection,selection,selectionArgs,null,null,sortOrder);

	}

	/**
	 * Return a Cursor positioned at the note that matches the given rowId
	 * 
	 * @param rowId id of note to retrieve
	 * @return Cursor positioned to matching note, if found
	 * @throws SQLException if note could not be found/retrieved
	 */
	public Cursor getItem(long rowId) throws SQLException {

		Cursor mCursor =
				mDb.query(true, FeedEntry.TABLE_NAME_I, projection, FeedEntry._ID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	/**
	 * Update the note using the details provided. The note to be updated is
	 * specified using the rowId, and it is altered to use the title and body
	 * values passed in
	 * 
	 * @param rowId id of note to update
	 * @param title value to set note title to
	 * @param body value to set note body to
	 * @return true if the note was successfully updated, false otherwise
	 */
	public boolean updateItem(long rowId, String item, String type, String price, String unit, String vendor, String start, String end, String added, String photo) {
		ContentValues values = new ContentValues();
		values.put(FeedEntry.COLUMN_NAME_Item, item);
		values.put(FeedEntry.COLUMN_NAME_Type, type);
		values.put(FeedEntry.COLUMN_NAME_Price, price);
		values.put(FeedEntry.COLUMN_NAME_Unit, unit);
		values.put(FeedEntry.COLUMN_NAME_Vendor, vendor);
		values.put(FeedEntry.COLUMN_NAME_Start, start);
		values.put(FeedEntry.COLUMN_NAME_End, end);
		values.put(FeedEntry.COLUMN_NAME_Added, added);
		values.put(FeedEntry.COLUMN_NAME_Photo, photo);

		return mDb.update(FeedEntry.TABLE_NAME_I, values, FeedEntry._ID + "=" + rowId, null) > 0;
	}
	
	public boolean updateItem(long rowId, String added) {
		ContentValues values = new ContentValues();
		values.put(FeedEntry.COLUMN_NAME_Added, added);
		return mDb.update(FeedEntry.TABLE_NAME_I, values, FeedEntry._ID + "=" + rowId, null) > 0;
	}

	public boolean added(long rowId, String added){
		ContentValues values = new ContentValues();
		values.put(FeedEntry.COLUMN_NAME_Added, added);
		return mDb.update(FeedEntry.TABLE_NAME_I, values, FeedEntry._ID + "=" + rowId, null) > 0;
	}
}
