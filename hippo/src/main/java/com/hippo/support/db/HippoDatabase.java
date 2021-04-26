package com.hippo.support.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hippo.support.model.SupportDataList;
import com.hippo.utils.HippoLog;
import com.google.gson.Gson;

import java.util.Map;

/**
 * Created by gurmail on 29/03/18.
 */

public class HippoDatabase {

    private static HippoDatabase INSTANCE;
    private static final String DATABASE_NAME = "hippo_database";
    private static final int DATABASE_VERSION = 1;
    private DbHelper dbHelper;
    private SQLiteDatabase database;


    private static final String TABLE_SUPPORT_DATA = "table_support_data";
    private static final String SUPPORT_CATEGORY = "support_category";
    private static final String SUPPORT_CATEGORY_NAME = "support_category_name";
    private static final String SUPPORT_DATA = "support_data";

    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            createTable(sqLiteDatabase);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            onCreate(sqLiteDatabase);
        }
    }

    private static void createTable(SQLiteDatabase database) {
        database.execSQL(" CREATE TABLE IF NOT EXISTS " + TABLE_SUPPORT_DATA + " ("
                + SUPPORT_CATEGORY + " INTEGER, "
                + SUPPORT_CATEGORY_NAME + " TEXT, "
                + SUPPORT_DATA + " TEXT"
                + ");");
    }

    public static HippoDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new HippoDatabase(context);
        } else if (!INSTANCE.database.isOpen()) {
            INSTANCE = null;
            INSTANCE = new HippoDatabase(context);
        }
        return INSTANCE;
    }

    private HippoDatabase(Context context) {
        dbHelper = new DbHelper(context);
        database = dbHelper.getWritableDatabase();
        createTable(database);
    }

    public void close() {
        try {
            database.close();
            dbHelper.close();
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertSupportData(int category, String supportData) {
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put(SUPPORT_CATEGORY, category);
            contentValues.put(SUPPORT_DATA, supportData);
            database.insert(TABLE_SUPPORT_DATA, null, contentValues);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void insertUpdateSupportData(Map<String, SupportDataList> itemData){
        /*try {
            if(itemData == null)
                return;

            Gson gson = new Gson();

            for (Map.Entry<String, SupportDataList> supportData : itemData.entrySet()) {
                String jsonString = gson.toJson(supportData.getValue());
                int category = Integer.parseInt(supportData.getKey());
                if (TextUtils.isEmpty(jsonString))
                    return;

                ContentValues contentValues = new ContentValues();
                contentValues.put(SUPPORT_CATEGORY, category);
                contentValues.put(SUPPORT_DATA, jsonString);
                int rowsAffected = database.update(TABLE_SUPPORT_DATA, contentValues, SUPPORT_CATEGORY + "=" + category, null);
                if (rowsAffected == 0) {
                    insertSupportData(category, jsonString);
                }
            }
            Thread.sleep(100);
        } catch(Exception e){
            e.printStackTrace();
        }*/

        deleteSupportData();

        if(itemData == null)
            return;

        // Begin the transaction
        database.beginTransaction();
        Gson gson = new Gson();

        try{
            for (Map.Entry<String, SupportDataList> jsonString : itemData.entrySet()) {
                ContentValues contentValues=new ContentValues();
                contentValues.put(SUPPORT_CATEGORY, Integer.parseInt(jsonString.getKey()));
                contentValues.put(SUPPORT_CATEGORY_NAME, jsonString.getValue().getCategoryName().toLowerCase().trim());
                contentValues.put(SUPPORT_DATA, gson.toJson(jsonString.getValue()));
                database.insert(TABLE_SUPPORT_DATA,null, contentValues);

            }
            database.setTransactionSuccessful();
        } catch(Exception e){
            HippoLog.e("Error in transaction", ""+e.toString());
        } finally {
            database.endTransaction();
        }

    }

    public SupportDataList getSupportDataItems(int category){
        SupportDataList menu = new SupportDataList();
        try{
            String[] columns = new String[] { SUPPORT_DATA };
            Cursor cursor = database.query(TABLE_SUPPORT_DATA, columns, SUPPORT_CATEGORY + "=" + category,
                    null, null, null, null);

            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                String data = cursor.getString(cursor.getColumnIndex(SUPPORT_DATA));

                Gson gson = new Gson();
                menu = gson.fromJson(data, SupportDataList.class);
            } else {
                menu = null;
            }

        } catch(Exception e){
            e.printStackTrace();
            menu = null;
        }

        return menu;
    }

    public SupportDataList getSupportDataItems(String categoryName){
        SupportDataList menu = new SupportDataList();
        try{
            String[] columns = new String[] { SUPPORT_DATA };
            Cursor cursor = database.query(TABLE_SUPPORT_DATA, columns, SUPPORT_CATEGORY_NAME + "=" + "'"+categoryName+"'",
                    null, null, null, null);

            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                String data = cursor.getString(cursor.getColumnIndex(SUPPORT_DATA));

                Gson gson = new Gson();
                menu = gson.fromJson(data, SupportDataList.class);
            } else {
                menu = null;
            }

        } catch(Exception e){
            e.printStackTrace();
            menu = null;
        }

        return menu;
    }


    public SupportDataList getSupportDataListLike(String categoryName) {
        SupportDataList menu = new SupportDataList();
        try{
            String[] columns = new String[] { SUPPORT_DATA };
            Cursor cursor = database.query(true, TABLE_SUPPORT_DATA, columns, SUPPORT_CATEGORY_NAME + " LIKE ?",
                    new String[] {"%"+ categoryName+ "%" }, null, null, null, null);

            if(cursor.getCount() > 0){
                cursor.moveToFirst();
                String data = cursor.getString(cursor.getColumnIndex(SUPPORT_DATA));

                Gson gson = new Gson();
                menu = gson.fromJson(data, SupportDataList.class);
            } else {
                menu = null;
            }

        } catch(Exception e){
            e.printStackTrace();
            menu = null;
        }

        return menu;
    }

    public void deleteSupportData(){
        database.delete(TABLE_SUPPORT_DATA, null, null);
    }

}

