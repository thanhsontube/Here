package son.nt.here.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Sonnt on 5/18/15.
 */
public class DbUtils {
    public static void close (SQLiteDatabase db) {
        if (db!= null) {
            db.close();
        }
    }

    public static void close (SQLiteDatabase db, Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
        close(db);
    }
}
