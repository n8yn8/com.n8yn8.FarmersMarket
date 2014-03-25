package com.n8yn8.farmersmarket;

import android.provider.BaseColumns;

public final class Contract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public Contract() {}

    /* Inner class that defines the table contents */
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME_M = "markets";
        public static final String COLUMN_NAME_Lat = "latitude";        
        public static final String COLUMN_NAME_Lng = "longitude";
        public static final String COLUMN_NAME_Market = "market";
        public static final String COLUMN_NAME_Days = "daysopen";
        public static final String COLUMN_NAME_Open = "open";
        public static final String COLUMN_NAME_Close = "close";
        public static final String COLUMN_NAME_NULLABLE = "NULL";
        public static final String TABLE_NAME_I = "items";
        public static final String COLUMN_NAME_Item = "item";
        public static final String COLUMN_NAME_Type = "type";
        public static final String COLUMN_NAME_Price = "price";
        public static final String COLUMN_NAME_Unit = "unit";
        public static final String COLUMN_NAME_Start = "start";
        public static final String COLUMN_NAME_End = "end";
        public static final String COLUMN_NAME_Added = "added";
        public static final String COLUMN_NAME_Photo = "photo";
        public static final String TABLE_NAME_V = "vendors";
        public static final String COLUMN_NAME_Vendor = "vendor";
        public static final String COLUMN_NAME_Organic = "organic";
    }
}