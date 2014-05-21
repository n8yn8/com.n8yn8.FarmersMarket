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

public class MarketDbController {
	
	private static final String TAG = "MarketDbController";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
	
	private static final String LOGCAT = null;
	public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Markets.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String LOCATION_TYPE = " REAL";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE " + FeedEntry.TABLE_NAME_M + " (" +
        FeedEntry._ID + " INTEGER PRIMARY KEY," +
        FeedEntry.COLUMN_NAME_Lat + LOCATION_TYPE + COMMA_SEP +
        FeedEntry.COLUMN_NAME_Lng + LOCATION_TYPE + COMMA_SEP +
        FeedEntry.COLUMN_NAME_Market + TEXT_TYPE + COMMA_SEP + 
        FeedEntry.COLUMN_NAME_Days + TEXT_TYPE + COMMA_SEP +
        FeedEntry.COLUMN_NAME_Open + TEXT_TYPE + COMMA_SEP +
        FeedEntry.COLUMN_NAME_Close + TEXT_TYPE +" )";
    private static final String[] projection = {
    	    FeedEntry._ID,
    	    FeedEntry.COLUMN_NAME_Lat,
    	    FeedEntry.COLUMN_NAME_Lng,
    	    FeedEntry.COLUMN_NAME_Market,
    	    FeedEntry.COLUMN_NAME_Days,
    	    FeedEntry.COLUMN_NAME_Open,
    	    FeedEntry.COLUMN_NAME_Close
    	    };

    private static final String SQL_DELETE_ENTRIES =
        "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME_M;
    
    private final Context mCtx;
    
    private static class DatabaseHelper extends SQLiteOpenHelper{

    	DatabaseHelper(Context applicationcontext) {
    		super(applicationcontext, Environment.getExternalStorageDirectory()+ File.separator + "FM" + File.separator + DATABASE_NAME, null, DATABASE_VERSION);
    		//Log.d(LOGCAT,DATABASE_NAME + " Created");
    	}

    	@Override
    	public void onCreate(SQLiteDatabase db) {
    		db.execSQL(SQL_CREATE_ENTRIES);
    		//Log.d(LOGCAT,"markets Created");
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
    public MarketDbController(Context ctx) {
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
    public MarketDbController open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        //Log.v(TAG, "Database opened.");
        return this;
    }

    public void close() {
    	//Log.v(TAG, "Database closed.");
        mDbHelper.close();
    }
    
    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the note
     * @param body the body of the note
     * @return rowId or -1 if failed
     */
	public long insertMarket(Double latitude, Double longitude, String title, String days, String open, String close) {
		
		ContentValues values = new ContentValues();
		values.put(FeedEntry.COLUMN_NAME_Lat, latitude);
		values.put(FeedEntry.COLUMN_NAME_Lng, longitude);
		values.put(FeedEntry.COLUMN_NAME_Market, title);
		values.put(FeedEntry.COLUMN_NAME_Days, days);
		values.put(FeedEntry.COLUMN_NAME_Open, open);
		values.put(FeedEntry.COLUMN_NAME_Close, close);
		return mDb.insert(
		         FeedEntry.TABLE_NAME_M,
		         FeedEntry.COLUMN_NAME_NULLABLE,
		         values);
	}
	
	/**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteMarket(long rowId) {

        return mDb.delete(FeedEntry.TABLE_NAME_M, FeedEntry._ID + "=" + rowId, null) > 0;
    }
	
    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor getAllMarkets() {

        return mDb.query(FeedEntry.TABLE_NAME_M, projection, null, null, null, null, null);
    }
    
    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor getMarket(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, FeedEntry.TABLE_NAME_M, projection, FeedEntry._ID + "=" + rowId, null,
                    null, null, null, null);
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
    public boolean updateMarket(long rowId, Double latitude, Double longitude, String title, String days, String open, String close) {
    	ContentValues values = new ContentValues();
		values.put(FeedEntry.COLUMN_NAME_Lat, latitude);
		values.put(FeedEntry.COLUMN_NAME_Lng, longitude);
		values.put(FeedEntry.COLUMN_NAME_Market, title);
		values.put(FeedEntry.COLUMN_NAME_Days, days);
		values.put(FeedEntry.COLUMN_NAME_Open, open);
		values.put(FeedEntry.COLUMN_NAME_Close, close);		

        return mDb.update(FeedEntry.TABLE_NAME_M, values, FeedEntry._ID + "=" + rowId, null) > 0;
    }
}
