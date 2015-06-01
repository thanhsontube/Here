package son.nt.here.db;

/**
 * Created by Sonnt on 5/18/15.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import son.nt.here.MsConst;
import son.nt.here.dto.FavDto;
import son.nt.here.dto.MyPlaceDto;


public class MyData {
    MyDataHelper helper;
    public SQLiteDatabase db;


    public MyData(Context context) {
        helper = new MyDataHelper(context);
        try {
            db = helper.getWritableDatabase();
        } catch (Exception e) {
            db = helper.getReadableDatabase();
        }
    }


    public boolean insertData(FavDto dto) {

        try {
            ContentValues values = new ContentValues();
            values.put("lat", String.valueOf(dto.lat));
            values.put("lng", String.valueOf(dto.lng));
            values.put("title", String.valueOf(dto.favTitle));
            values.put("address", String.valueOf(dto.formatted_address));
            values.put("notes", String.valueOf(dto.favNotes));
            values.put("isDelete", 0);
            StringBuilder placeType = new StringBuilder();
            for (MsConst.PlaceType type : dto.placeTypes) {
                placeType.append(MsConst.PlaceType.getValue(type));
                placeType.append(";");
            }
            values.put("type", placeType.toString());
            values.put("images", dto.getListImages());
            db.insertOrThrow(MyDataHelper.DATABASE_TABLE, null, values);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public void removeFav (MyPlaceDto dto) {
        ContentValues values = new ContentValues();
        values.put("isDelete", 1);
        String whereClause = "lat = ? and lng = ?";
        String[] whereArgs = new String[]{String.valueOf(dto.lat), String.valueOf(dto.lng)};
        db.update(MyDataHelper.DATABASE_TABLE,values, whereClause, whereArgs);
    }


    public Cursor getData(String tableName) {
        String whereClause = "isDelete = 0";
        return db.query(tableName, null, whereClause, null, null, null, null);
    }

    public Cursor getFavorites () {
        return getData(MyDataHelper.DATABASE_TABLE);
    }


    public void deleteRow(Cursor cursor) {
        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{cursor.getString(0)};
        db.delete(MyDataHelper.DATABASE_TABLE, whereClause, whereArgs);
    }


    public void deleteRow(String message) {
        String whereClause = "value = ?";
        String[] whereArgs = new String[]{message};
        db.delete(MyDataHelper.DATABASE_TABLE, whereClause, whereArgs);
    }


    public void deleteRow(String tableName, String message) {
        String whereClause = "value = ?";
        String[] whereArgs = new String[]{message};
        db.delete(tableName, whereClause, whereArgs);
    }


    // TABLE FAVORITE
    public boolean addFavorite(String text) {
        try {
            ContentValues values = new ContentValues();
            values.put("value", text);
            db.insertOrThrow(MyDataHelper.DATABASE_TABLE, null, values);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public boolean isFavorite(String text) {
        String whereClause = "value = ?";
        String[] whereArgs = new String[]{text};
        Cursor cursor = db.query(MyDataHelper.DATABASE_TABLE, null, whereClause, whereArgs, null, null, null);
        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }


    public void removeFavorite(String text) {
        String whereClause = "value = ?";
        String[] whereArgs = new String[]{text};
        db.delete(MyDataHelper.DATABASE_TABLE, whereClause, whereArgs);
    }


}

