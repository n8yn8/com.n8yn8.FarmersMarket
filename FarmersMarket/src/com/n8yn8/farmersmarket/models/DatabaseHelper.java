package com.n8yn8.farmersmarket.models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;

public class DatabaseHelper extends SQLiteOpenHelper {

	// Logcat tag
	private static final String TAG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "FarmersMarkets.db";

	// Table Names
	private static final String TABLE_ITEMS = "items";
	private static final String TABLE_VENDORS = "vendors";
	private static final String TABLE_MARKETS = "markets";
	private static final String TABLE_VENDORS_AT_MARKETS = "vendors_at_markets";

	// Common column names
	private static final String KEY_ID = "id";
	//private static final String KEY_CREATED_AT = "created_at";

	// ITEMS Table - column names
	public static final String KEY_ITEM_NAME = "item_name";
	public static final String KEY_TYPE = "type";
	private static final String KEY_PRICE = "price";
	private static final String KEY_UNIT = "unit";
	private static final String KEY_START_DATE = "start";
	private static final String KEY_END_DATE = "end";
	private static final String KEY_ADDED_TO_GROCERIES = "added";
	private static final String KEY_PHOTO = "photo";

	// VENDORS Table - column names
	public static final String KEY_VENDOR_NAME = "vendor_name";

	// MARKETS Table - column names
	private static final String KEY_MARKET_NAME = "market_name";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_LONGITUDE = "longitude";
	private static final String KEY_DAYS = "days";
	private static final String KEY_OPEN = "open";
	private static final String KEY_CLOSE = "close";

	// VENDORS_AT_MARKETS Table - column names
	private static final String KEY_VENDOR_ID = "vendor_id";
	private static final String KEY_MARKET_ID = "market_id";

	// Table Create Statements
	// Items table create statement
	private static final String CREATE_TABLE_ITEMS = "CREATE TABLE "
			+ TABLE_ITEMS 
			+ "(" + KEY_ID + " INTEGER PRIMARY KEY," 
			+ KEY_ITEM_NAME + " TEXT," 
			+ KEY_TYPE + " TEXT," 
			+ KEY_PRICE + " TEXT,"
			+ KEY_UNIT + " TEXT,"
			+ KEY_VENDOR_ID + " INTEGER,"
			+ KEY_VENDOR_NAME + " TEXT,"
			+ KEY_START_DATE + " TEXT,"
			+ KEY_END_DATE + " TEXT,"
			+ KEY_ADDED_TO_GROCERIES + " TEXT,"
			+ KEY_PHOTO + " TEXT" + ")";
	//+ KEY_CREATED_AT + " DATETIME" + ")";

	// Vendors table create statement
	private static final String CREATE_TABLE_VENDORS = "CREATE TABLE " + TABLE_VENDORS
			+ "(" + KEY_ID + " INTEGER PRIMARY KEY," 
			+ KEY_VENDOR_NAME + " TEXT" + ")";

	// Markets table create statement
	private static final String CREATE_TABLE_MARKETS = "CREATE TABLE " + TABLE_MARKETS
			+ "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_MARKET_NAME + " TEXT,"
			+ KEY_LATITUDE + " REAL,"
			+ KEY_LONGITUDE + " REAL,"
			+ KEY_DAYS + " TEXT,"
			+ KEY_OPEN + " TEXT,"
			+ KEY_CLOSE + " TEXT" + ")";

	// vendors_at_markets table create statement
	private static final String CREATE_TABLE_VENDORS_AT_MARKETS = "CREATE TABLE "
			+ TABLE_VENDORS_AT_MARKETS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_VENDOR_ID + " INTEGER," + KEY_MARKET_ID + " INTEGER" + ")";

	public DatabaseHelper(Context context) {
		super(context, Environment.getExternalStorageDirectory()+ File.separator + "FM" + File.separator + DATABASE_NAME, null, DATABASE_VERSION);
		Log.i(TAG, "constructor");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "onCreate");

		// creating required tables
		db.execSQL(CREATE_TABLE_ITEMS);
		db.execSQL(CREATE_TABLE_VENDORS);
		db.execSQL(CREATE_TABLE_MARKETS);
		db.execSQL(CREATE_TABLE_VENDORS_AT_MARKETS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "onUpgrade");
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VENDORS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKETS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VENDORS_AT_MARKETS);

		// create new tables
		onCreate(db);
	}

	/*
	 * Creating a market
	 */
	public long createMarket(Market market, long[] vendor_ids) {
		Log.i(TAG, "createMarket(Market "+market.getName()+", long[] vendor_ids)");
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_MARKET_NAME, market.getName());
		values.put(KEY_LATITUDE, market.getLatitude());
		values.put(KEY_LONGITUDE, market.getLongitude());
		values.put(KEY_DAYS, market.getDays());
		values.put(KEY_OPEN, market.getOpen());
		values.put(KEY_CLOSE, market.getClose());

		// insert row
		long market_id = db.insert(TABLE_MARKETS, null, values);

		// assigning vendors to Market
		for (long vendor_id : vendor_ids) {
			createVendorAtMarket(market_id, vendor_id);
		}

		return market_id;
	}

	public long createMarket(Market market) {
		Log.i(TAG, "createMarket(Market "+market.getName());
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_MARKET_NAME, market.getName());
		values.put(KEY_LATITUDE, market.getLatitude());
		values.put(KEY_LONGITUDE, market.getLongitude());
		values.put(KEY_DAYS, market.getDays());
		values.put(KEY_OPEN, market.getOpen());
		values.put(KEY_CLOSE, market.getClose());

		// insert row
		long market_id = db.insert(TABLE_MARKETS, null, values);

		return market_id;
	}

	/*
	 * get single market
	 */
	public Market getMarket(long market_id) {
		Log.i(TAG, "getMarket(long "+market_id+")");
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_MARKETS + " WHERE "
				+ KEY_ID + " = " + market_id;

		Log.v(TAG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Market market = new Market();
		market.set_ID(c.getInt(c.getColumnIndex(KEY_ID)));
		market.setName(c.getString(c.getColumnIndex(KEY_MARKET_NAME)));
		market.setLatitude(c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
		market.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
		market.setDays(c.getString(c.getColumnIndex(KEY_DAYS)));
		market.setOpen(c.getString(c.getColumnIndex(KEY_OPEN)));
		market.setClose(c.getString(c.getColumnIndex(KEY_CLOSE)));

		return market;
	}

	/*
	 * getting all markets
	 * */
	public List<Market> getAllMarkets() {
		Log.i(TAG, "getAllMarkets()");
		List<Market> markets = new ArrayList<Market>();
		String selectQuery = "SELECT  * FROM " + TABLE_MARKETS + " ORDER BY " + KEY_MARKET_NAME;

		Log.v(TAG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Market market = new Market();
				market.set_ID(c.getInt(c.getColumnIndex(KEY_ID)));
				market.setName(c.getString(c.getColumnIndex(KEY_MARKET_NAME)));
				market.setLatitude(c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
				market.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
				market.setDays(c.getString(c.getColumnIndex(KEY_DAYS)));
				market.setOpen(c.getString(c.getColumnIndex(KEY_OPEN)));
				market.setClose(c.getString(c.getColumnIndex(KEY_CLOSE)));

				// adding to Market list
				markets.add(market);
			} while (c.moveToNext());
		}

		return markets;
	}

	/*
	 * getting all markets
	 * */
	public List<String> getAllMarketNames() {
		Log.i(TAG, "getAllMarketNames()");
		List<String> marketNames = new ArrayList<String>();
		String selectQuery = "SELECT  * FROM " + TABLE_MARKETS;

		Log.v(TAG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			marketNames.add("Choose a market");
			do {
				// adding to Market list
				marketNames.add(c.getString(c.getColumnIndex(KEY_MARKET_NAME)));
			} while (c.moveToNext());
		} else
			marketNames.add("Add a new Market first");

		return marketNames;
	}

	/*
	 * getting all Markets under single vendor
	 * */
	public List<Market> getAllMarketsByVendor(String vendor_name) {
		Log.i(TAG, "getAllMarketsByVendor()");
		List<Market> Markets = new ArrayList<Market>();

		String selectQuery = "SELECT  * FROM " + TABLE_MARKETS + " td, "
				+ TABLE_VENDORS + " tg, " + TABLE_VENDORS_AT_MARKETS + " tt WHERE tg."
				+ KEY_VENDOR_NAME + " = '" + vendor_name + "'" + " AND tg." + KEY_ID
				+ " = " + "tt." + KEY_VENDOR_ID + " AND td." + KEY_ID + " = "
				+ "tt." + KEY_MARKET_ID;

		Log.v(TAG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Market market = new Market();
				market.set_ID(c.getInt(c.getColumnIndex(KEY_ID)));
				market.setName(c.getString(c.getColumnIndex(KEY_MARKET_NAME)));
				market.setLatitude(c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
				market.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
				market.setDays(c.getString(c.getColumnIndex(KEY_DAYS)));
				market.setOpen(c.getString(c.getColumnIndex(KEY_OPEN)));
				market.setClose(c.getString(c.getColumnIndex(KEY_CLOSE)));

				// adding to Market list
				Markets.add(market);
			} while (c.moveToNext());
		}

		return Markets;
	}



	/**
	 * getting market count
	 */
	public int getMarketCount() {
		Log.i(TAG, "getMarketCount()");
		String countQuery = "SELECT  * FROM " + TABLE_MARKETS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		// return count
		return count;
	}

	/*
	 * Updating a market
	 */
	public int updateMarket(Market market) {
		Log.i(TAG, "updateMarket()");
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_MARKET_NAME, market.getName());
		values.put(KEY_LATITUDE, market.getLatitude());
		values.put(KEY_LONGITUDE, market.getLongitude());
		values.put(KEY_DAYS, market.getDays());
		values.put(KEY_OPEN, market.getOpen());
		values.put(KEY_CLOSE, market.getClose());

		// updating row
		return db.update(TABLE_MARKETS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(market.get_ID()) });
	}

	/*
	 * Deleting a market
	 */
	public void deleteMarket(long market_id) {
		Log.i(TAG, "deleteMarket()");
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_MARKETS, KEY_ID + " = ?",
				new String[] { String.valueOf(market_id) });
	}

	private Market constructMarket(Cursor c) {
		Log.i(TAG, "constructMarket()");

		Market market = new Market();
		market.set_ID(c.getInt(c.getColumnIndex(KEY_ID)));
		market.setName(c.getString(c.getColumnIndex(KEY_MARKET_NAME)));
		market.setLatitude(c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
		market.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
		market.setDays(c.getString(c.getColumnIndex(KEY_DAYS)));
		market.setOpen(c.getString(c.getColumnIndex(KEY_OPEN)));
		market.setClose(c.getString(c.getColumnIndex(KEY_CLOSE)));

		return market;
	}

	/*
	 * Creating vendor
	 */
	public long createVendor(Vendor vendor, ArrayList<Long> market_ids) {
		Log.i(TAG, "createVendor()");
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_VENDOR_NAME, vendor.getName());

		// insert row
		long vendor_id = db.insert(TABLE_VENDORS, null, values);

		// assigning vendors to Market
		for (long market_id : market_ids) {
			createVendorAtMarket(market_id, vendor_id);
		}

		return vendor_id;
	}

	/**
	 * getting all vendors
	 * */
	public List<Vendor> getAllVendors() {
		Log.i(TAG, "getAllVendors()");
		List<Vendor> vendors = new ArrayList<Vendor>();
		String selectQuery = "SELECT  * FROM " + TABLE_VENDORS + " ORDER BY " + KEY_VENDOR_NAME;

		Log.v(TAG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			Vendor choose = new Vendor("Choose a vendor");
			vendors.add(choose);
			do {
				Vendor vendor = new Vendor();
				vendor.setId(c.getInt((c.getColumnIndex(KEY_ID))));
				vendor.setName(c.getString(c.getColumnIndex(KEY_VENDOR_NAME)));

				// adding to vendors list
				vendors.add(vendor);
			} while (c.moveToNext());
		}
		return vendors;
	}

	/*
	 * getting all Markets under single vendor
	 * */
	public List<Vendor> getAllVendorsAtMarket(long market_id) {
		Log.i(TAG, "getAllVendorsAtMarket()");
		List<Vendor> vendors = new ArrayList<Vendor>();

		String selectQuery = "SELECT  * FROM " + TABLE_VENDORS + " td, "
				+ TABLE_MARKETS + " tg, " + TABLE_VENDORS_AT_MARKETS + " tt WHERE tg."
				+ KEY_ID + " = '" + market_id + "'" + " AND tg." + KEY_ID
				+ " = " + "tt." + KEY_MARKET_ID + " AND td." + KEY_ID + " = "
				+ "tt." + KEY_VENDOR_ID;

		Log.v(TAG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Vendor vendor = new Vendor();
				vendor.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				vendor.setName(c.getString(c.getColumnIndex(KEY_VENDOR_NAME)));
				Log.v(TAG, "getAllVendorsAtMarket vendor = "+vendor.getName());
				// adding to Market list
				vendors.add(vendor);
			} while (c.moveToNext());
		}

		return vendors;
	}

	/*
	 * getting all Markets under single vendor
	 * */
	public List<Market> getAllMarketsForVendor(long vendor_id, String vendor_name) {
		Log.i(TAG, "getAllMarketsForVendor()");
		List<Market> markets = new ArrayList<Market>();

		String selectQuery = "SELECT  * FROM " + TABLE_MARKETS;

		String secondSelectQuery;

		Log.v(TAG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Market market = new Market();
				market.set_ID(c.getInt(c.getColumnIndex(KEY_ID)));
				market.setName(c.getString(c.getColumnIndex(KEY_MARKET_NAME)));
				market.setLatitude(c.getDouble(c.getColumnIndex(KEY_LATITUDE)));
				market.setLongitude(c.getDouble(c.getColumnIndex(KEY_LONGITUDE)));
				market.setDays(c.getString(c.getColumnIndex(KEY_DAYS)));
				market.setOpen(c.getString(c.getColumnIndex(KEY_OPEN)));
				market.setClose(c.getString(c.getColumnIndex(KEY_CLOSE)));

				secondSelectQuery = "SELECT  * FROM " + TABLE_VENDORS_AT_MARKETS + " WHERE "
						+ KEY_MARKET_ID + " = " + market.get_ID() + " AND " + KEY_VENDOR_ID + " = " + vendor_id;
				Cursor c2 = db.rawQuery(secondSelectQuery, null);
				if (c2.moveToFirst()){
					do {
						market.setSelected(true);
					} while (c2.moveToNext());
				}

				Log.v(TAG, "getAllMarketsForVendor vendor = "+market.getName());
				// adding to Market list
				markets.add(market);
			} while (c.moveToNext());
		}

		return markets;
	}

	/*
	 * get single vendor
	 */
	public Vendor getVendor(long vendor_id) {
		Log.i(TAG, "getVendor()");
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_VENDORS + " WHERE "
				+ KEY_ID + " = " + vendor_id;

		Log.v(TAG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Vendor vendor = new Vendor();
		vendor.setId(c.getInt(c.getColumnIndex(KEY_ID)));
		vendor.setName(c.getString(c.getColumnIndex(KEY_VENDOR_NAME)));

		return vendor;
	}

	/*
	 * Updating a vendor
	 */
	public int updateVendor(Vendor vendor) {
		Log.i(TAG, "updateVendor()");
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_VENDOR_NAME, vendor.getName());

		// updating row
		return db.update(TABLE_VENDORS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(vendor.getId()) });
	}

	/*
	 * Deleting a vendor
	 */
	public void deleteVendor(long vendor_id) {
		Log.i(TAG, "deleteVendor()");
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_VENDORS, KEY_ID + " = ?",
				new String[] { String.valueOf(vendor_id) });
	}


	/*
	 * Creating vendors_at_market
	 */
	public long createVendorAtMarket(long market_id, long vendor_id) {
		Log.i(TAG, "createVendorAtMarket()");
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_MARKET_ID, market_id);
		values.put(KEY_VENDOR_ID, vendor_id);

		long id = db.insert(TABLE_VENDORS_AT_MARKETS, null, values);

		return id;
	}

	/*
	 * Updating a market vendor
	 */
	public int updateVendorAtMarket(long id, long market_id) {
		Log.i(TAG, "updateVendorAtMarket()");
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_MARKET_ID, market_id);

		// updating row
		return db.update(TABLE_MARKETS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(id) });
	}

	public long createItem(Item item) {
		Log.i(TAG, "createItem()");
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ITEM_NAME, item.getName());
		values.put(KEY_TYPE, item.getType());
		values.put(KEY_PRICE, item.getPrice());
		values.put(KEY_UNIT, item.getUnit());
		values.put(KEY_VENDOR_ID, item.getVendorId());
		values.put(KEY_VENDOR_NAME, item.getVendorName());
		values.put(KEY_START_DATE, item.getSeasonStart());
		values.put(KEY_END_DATE, item.getSeasonEnd());
		values.put(KEY_ADDED_TO_GROCERIES, item.getAdded());
		values.put(KEY_PHOTO, item.getPhoto());

		// insert row
		long item_id = db.insert(TABLE_ITEMS, null, values);

		return item_id;
	}

	/*
	 * get single item
	 */
	public Item getItem(long item_id) {
		Log.i(TAG, "getItem()");
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_ITEMS + " WHERE "
				+ KEY_ID + " = " + item_id;

		Log.v(TAG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		if (c != null)
			c.moveToFirst();

		Item item = new Item();
		item.set_ID(c.getInt(c.getColumnIndex(KEY_ID)));
		item.setName(c.getString(c.getColumnIndex(KEY_ITEM_NAME)));
		item.setType(c.getString(c.getColumnIndex(KEY_TYPE)));
		item.setPrice(c.getString(c.getColumnIndex(KEY_PRICE)));
		item.setUnit(c.getString(c.getColumnIndex(KEY_UNIT)));
		item.setVendorId(c.getLong(c.getColumnIndex(KEY_VENDOR_ID)));
		item.setVendorName(c.getString(c.getColumnIndex(KEY_VENDOR_NAME)));
		item.setSeasonStart(c.getString(c.getColumnIndex(KEY_START_DATE)));
		item.setSeasonEnd(c.getString(c.getColumnIndex(KEY_END_DATE)));
		item.setAdded(c.getString(c.getColumnIndex(KEY_ADDED_TO_GROCERIES)));
		item.setPhoto(c.getString(c.getColumnIndex(KEY_PHOTO)));

		return item;
	}

	/*
	 * getting all items
	 * */
	public List<Item> getAllItems() {
		Log.i(TAG, "getAllItems()");
		List<Item> items = new ArrayList<Item>();
		String selectQuery = "SELECT  * FROM " + TABLE_ITEMS;

		Log.v(TAG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Item item = new Item();
				item.set_ID(c.getInt(c.getColumnIndex(KEY_ID)));
				item.setName(c.getString(c.getColumnIndex(KEY_ITEM_NAME)));
				item.setType(c.getString(c.getColumnIndex(KEY_TYPE)));
				item.setPrice(c.getString(c.getColumnIndex(KEY_PRICE)));
				item.setUnit(c.getString(c.getColumnIndex(KEY_UNIT)));
				item.setVendorId(c.getLong(c.getColumnIndex(KEY_VENDOR_ID)));
				item.setVendorName(c.getString(c.getColumnIndex(KEY_VENDOR_NAME)));
				item.setSeasonStart(c.getString(c.getColumnIndex(KEY_START_DATE)));
				item.setSeasonEnd(c.getString(c.getColumnIndex(KEY_END_DATE)));
				item.setAdded(c.getString(c.getColumnIndex(KEY_ADDED_TO_GROCERIES)));
				item.setPhoto(c.getString(c.getColumnIndex(KEY_PHOTO)));

				// adding to Market list
				items.add(item);
			} while (c.moveToNext());
		} /*else {
			Item item = new Item();
			item.setName("Add new Item first");
			items.add(item);
		}*/

		return items;
	}
	
	public List<Item> getItemsAtMarket(long market_id) {
		Log.i(TAG, "getItemsAtMarket()");
		SQLiteDatabase db = this.getReadableDatabase();
		List<Vendor> vendors= getAllVendorsAtMarket(market_id);
		List<Item> items = new ArrayList<Item>();
		for (int i = 0; i < vendors.size(); i++){
			long vendor_id = vendors.get(i).getId();
			items.addAll(getItemsAtVendor(vendor_id));
		}
		
		return items;
	}
	
	public List<Item> getItemsAtMarketByCategory(long market_id, String category) {
		Log.i(TAG, "getItemsAtMarketByCategory");
		SQLiteDatabase db = this.getReadableDatabase();
		List<Item> items = new ArrayList<Item>();
		
		String selectQuery = 
				"SELECT  * FROM " 
		+ TABLE_ITEMS + ", " + TABLE_VENDORS + ", " + TABLE_MARKETS + ", " + TABLE_VENDORS_AT_MARKETS
		+ " WHERE " 
		+ "vendors_at_markets.market_id = "+ market_id + " AND "
		+ "vendors_at_markets.vendor_id = vendors_at_markets.market_id AND "
		+ TABLE_ITEMS+".vendor_id = vendors_at_markets.vendor_id AND "
		+ TABLE_ITEMS+".type = \"" + category + "\"";
		// item.category = category
		// item.vendorId  vendorAtMarket.vendorId
		// vendorAtMarket.vendorId = vendorAtMarket.marketId
		// vendorAtMarket.marketId = market.id
		
		Log.v(TAG, selectQuery);
		
		Cursor c = db.rawQuery(selectQuery, null);
		
		// looping through all rows and adding to list
				if (c.moveToFirst()) {
					do {
						Item item = new Item();
						item.set_ID(c.getInt(c.getColumnIndex(KEY_ID)));
						item.setName(c.getString(c.getColumnIndex(KEY_ITEM_NAME)));
						item.setType(c.getString(c.getColumnIndex(KEY_TYPE)));
						item.setPrice(c.getString(c.getColumnIndex(KEY_PRICE)));
						item.setUnit(c.getString(c.getColumnIndex(KEY_UNIT)));
						item.setVendorId(c.getLong(c.getColumnIndex(KEY_VENDOR_ID)));
						item.setVendorName(c.getString(c.getColumnIndex(KEY_VENDOR_NAME)));
						item.setSeasonStart(c.getString(c.getColumnIndex(KEY_START_DATE)));
						item.setSeasonEnd(c.getString(c.getColumnIndex(KEY_END_DATE)));
						item.setAdded(c.getString(c.getColumnIndex(KEY_ADDED_TO_GROCERIES)));
						item.setPhoto(c.getString(c.getColumnIndex(KEY_PHOTO)));

						// adding to Items list
						items.add(item);
						Log.d(TAG, "Item added to list = " + item.getName());
					} while (c.moveToNext());
				}
		
		return items;
	}
	
	public List<Item> getItemsAtVendor(long vendor_id) {
		Log.i(TAG, "getItemsOf()");
		SQLiteDatabase db = this.getReadableDatabase();
		List<Item> items = new ArrayList<Item>();

		String selectQuery = "SELECT  * FROM " + TABLE_ITEMS + " WHERE "
				+ KEY_VENDOR_ID + " = " + vendor_id;

		Log.v(TAG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Item item = new Item();
				item.set_ID(c.getInt(c.getColumnIndex(KEY_ID)));
				item.setName(c.getString(c.getColumnIndex(KEY_ITEM_NAME)));
				item.setType(c.getString(c.getColumnIndex(KEY_TYPE)));
				item.setPrice(c.getString(c.getColumnIndex(KEY_PRICE)));
				item.setUnit(c.getString(c.getColumnIndex(KEY_UNIT)));
				item.setVendorId(c.getLong(c.getColumnIndex(KEY_VENDOR_ID)));
				item.setVendorName(c.getString(c.getColumnIndex(KEY_VENDOR_NAME)));
				item.setSeasonStart(c.getString(c.getColumnIndex(KEY_START_DATE)));
				item.setSeasonEnd(c.getString(c.getColumnIndex(KEY_END_DATE)));
				item.setAdded(c.getString(c.getColumnIndex(KEY_ADDED_TO_GROCERIES)));
				item.setPhoto(c.getString(c.getColumnIndex(KEY_PHOTO)));

				// adding to Items list
				items.add(item);
			} while (c.moveToNext());
		} /*else {
			Item item = new Item();
			item.setName("Add new Item first");
			items.add(item);
		}*/

		return items;
	}

	public List<Item> getItemsOf(String looking_for, String key) {
		Log.i(TAG, "getItemsOf()");
		SQLiteDatabase db = this.getReadableDatabase();
		List<Item> items = new ArrayList<Item>();

		String selectQuery = "SELECT  * FROM " + TABLE_ITEMS + " WHERE "
				+ key + " = \"" + looking_for + "\"";

		Log.v(TAG, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				Item item = new Item();
				item.set_ID(c.getInt(c.getColumnIndex(KEY_ID)));
				item.setName(c.getString(c.getColumnIndex(KEY_ITEM_NAME)));
				item.setType(c.getString(c.getColumnIndex(KEY_TYPE)));
				item.setPrice(c.getString(c.getColumnIndex(KEY_PRICE)));
				item.setUnit(c.getString(c.getColumnIndex(KEY_UNIT)));
				item.setVendorId(c.getLong(c.getColumnIndex(KEY_VENDOR_ID)));
				item.setVendorName(c.getString(c.getColumnIndex(KEY_VENDOR_NAME)));
				item.setSeasonStart(c.getString(c.getColumnIndex(KEY_START_DATE)));
				item.setSeasonEnd(c.getString(c.getColumnIndex(KEY_END_DATE)));
				item.setAdded(c.getString(c.getColumnIndex(KEY_ADDED_TO_GROCERIES)));
				item.setPhoto(c.getString(c.getColumnIndex(KEY_PHOTO)));

				// adding to Items list
				items.add(item);
			} while (c.moveToNext());
		} /*else {
			Item item = new Item();
			item.setName("Add new Item first");
			item.setAdded("no");
			items.add(item);
		}*/

		return items;
	}
	
	

	public SparseArray<Group> getGroceriesByType(List<String> categories){
		Log.i(TAG, "getItemsOf");
		
		SQLiteDatabase db = this.getReadableDatabase();
		SparseArray<Group> groups = new SparseArray<Group>();

		int groupID = 0;
		for(int i = 1; i < categories.size(); i++){
			String category = categories.get(i);
			Log.v(TAG, "category = " + category + ", column = " + KEY_TYPE + ", sorted by = " +KEY_ITEM_NAME);
			
			String sortOrder = KEY_ITEM_NAME + " DESC";
			String selection = KEY_TYPE + " LIKE ?";
			String[] selectionArgs = { category };
			String[] projection = {
					KEY_ID, //0
					KEY_ITEM_NAME, //1
					KEY_TYPE, //2
					KEY_PRICE, //3
					KEY_UNIT, //4
					KEY_VENDOR_ID, //5
					KEY_VENDOR_NAME, //6
					KEY_START_DATE, //7
					KEY_END_DATE, //8
					KEY_ADDED_TO_GROCERIES, //9
					KEY_PHOTO //10
			};

			Cursor cursor = db.query(TABLE_ITEMS,projection,selection,selectionArgs,null,null,sortOrder);

			if (cursor.moveToFirst()) {
				Log.i(TAG, "cursor moved to first");
				//Add children to the group.
				Group group = new Group(category);
				do {
					//Include items in the group if they are added to the grocery list.
					if (cursor.getString(9).equals("yes")) {
						group.children.add(new Item(
								cursor.getLong(0), 
								cursor.getString(1), 
								cursor.getString(2), 
								cursor.getString(3), 
								cursor.getString(4), 
								cursor.getLong(5),
								cursor.getString(6), 
								cursor.getString(7), 
								cursor.getString(8),
								cursor.getString(9),
								cursor.getString(10)));
						Log.v(TAG, "item retrieved = " +cursor.getString(1));
					} else {
						Log.v(TAG, "skipping item = " + cursor.getString(1));
					}
				} while (cursor.moveToNext());
				//Include the group if it contains children.
				if(!group.childrenEmpty()){
					group.updateGroupCount();
					groups.put(groupID, group);
					groupID++;
				}
			}
		}

		return groups;
	}
	
	public SparseArray<Group> getGroceriesByVendor(List<Vendor> vendors){
		Log.i(TAG, "getItemsOf");
		
		SQLiteDatabase db = this.getReadableDatabase();
		SparseArray<Group> groups = new SparseArray<Group>();

		int groupID = 0;
		for(int i = 1; i < vendors.size(); i++){
			Vendor vendor = vendors.get(i);
			String selectQuery = "SELECT  * FROM " + TABLE_ITEMS + " WHERE "
					+ KEY_VENDOR_ID + " = " + vendor.getId();

			Log.v(TAG, selectQuery);

			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				Log.i(TAG, "cursor moved to first");
				//Add children to the group.
				Group group = new Group(vendor.getName());
				do {
					//Include items in the group if they are added to the grocery list.
					if (cursor.getString(9).equals("yes")) {
						group.children.add(new Item(
								cursor.getLong(0), 
								cursor.getString(1), 
								cursor.getString(2), 
								cursor.getString(3), 
								cursor.getString(4), 
								cursor.getLong(5),
								cursor.getString(6), 
								cursor.getString(7), 
								cursor.getString(8),
								cursor.getString(9),
								cursor.getString(10)));
						Log.v(TAG, "item retrieved = " +cursor.getString(1));
					} else {
						Log.v(TAG, "skipping item = " + cursor.getString(1));
					}
				} while (cursor.moveToNext());
				//Include the group if it contains children.
				if(!group.childrenEmpty()){
					group.updateGroupCount();
					groups.put(groupID, group);
					groupID++;
				}
			}
		}

		return groups;
	}


	/*
	 * Updating an item
	 */
	public int updateItem(Item item) {
		Log.i(TAG, "updateItem()");
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ITEM_NAME, item.getName());
		values.put(KEY_TYPE, item.getType());
		values.put(KEY_PRICE, item.getPrice());
		values.put(KEY_UNIT, item.getUnit());
		values.put(KEY_VENDOR_ID, item.getVendorId());
		values.put(KEY_VENDOR_NAME, item.getVendorName());
		values.put(KEY_START_DATE, item.getSeasonStart());
		values.put(KEY_END_DATE, item.getSeasonEnd());
		values.put(KEY_ADDED_TO_GROCERIES, item.getAdded());
		values.put(KEY_PHOTO, item.getPhoto());

		// updating row
		return db.update(TABLE_ITEMS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(item.get_ID()) });
	}

	public boolean updateItem(long rowId, String added) {
		Log.i(TAG, "updateItem");
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_ADDED_TO_GROCERIES, added);
		return db.update(TABLE_ITEMS, values, KEY_ID + "=" + rowId, null) > 0;
	}

	/*
	 * Deleting a market
	 */
	public void deleteItem(long item_id) {
		Log.i(TAG, "deleteItem()");
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_ITEMS, KEY_ID + " = ?",
				new String[] { String.valueOf(item_id) });
	}

	// closing database
	public void closeDB() {
		Log.i(TAG, "closeDB()");
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

}