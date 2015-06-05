package son.nt.here.db;

/**
 * Created by Sonnt on 5/18/15.
 */


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyDataHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "here.db";
    //favorite
    public static final String DATABASE_TABLE = "here_table";


    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "+DATABASE_TABLE + " ("
            +"_id" + " INTEGER PRIMARY KEY,"
            + "lat text,"
            + "lng text,"
            + "title text,"
            + "address text,"
            + "notes text,"
            + "isDelete integer,"
            + "images text,"
            + "update_time long,"
            + "type text"
            + " )";





    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DATABASE_TABLE;


    public MyDataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }


    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }


}

