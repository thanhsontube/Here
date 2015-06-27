package son.nt.here.db;

/**
 * Created by Sonnt on 5/18/15.
 */


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyDataHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 10;
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
            + "placeId text,"
            + "isDelete integer,"
            + "images text,"
            + "street_number text,"
            + "streetName text,"
            + "country text,"
            + "city text,"
            + "district text,"
            + "subLv1 text,"
            + "postal_code text,"
            + "update_time long,"
            + "address_near text,"
            + "is_fav boolean,"
            + "web_uri text,"
            + "phone_number text,"
            + "rating real,"
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

