package son.nt.here.db;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import son.nt.here.dto.MyPlaceDto;
import son.nt.here.utils.DbUtils;

/**
 * Created by Sonnt on 6/2/15.
 */
public class LoadFavouritesModel extends AsyncTask<Void, Void, List<MyPlaceDto>> {

    private final Context mContext;

    private final IOnLoadFavoritesListener mListener;

    private MyData db;

    public LoadFavouritesModel(final Context context, final IOnLoadFavoritesListener listener)
    {
        super();

        this.mContext = context.getApplicationContext();
        this.mListener = listener;
        db = new MyData(context);
    }
    @Override
    protected List<MyPlaceDto> doInBackground(Void... params) {
        List<MyPlaceDto> list = new ArrayList<>();
        MyPlaceDto dto;
        Cursor cursor = db.getFavorites();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                dto = new MyPlaceDto();
                dto.favTitle = cursor.getString(cursor.getColumnIndex("title"));
                dto.formatted_address = cursor.getString(cursor.getColumnIndex("address"));
                dto.favNotes = cursor.getString(cursor.getColumnIndex("notes"));
                list.add(dto);

            } while (cursor.moveToNext());
        }

        DbUtils.close(db.db, cursor);
        return list;
    }

    @Override
    protected void onPostExecute(final List<MyPlaceDto> result)
    {
        this.mListener.onLoadFavorites(result);
    }

    /**
     * Callback interface on completion of this model
     */
    public interface IOnLoadFavoritesListener
    {
        void onLoadFavorites(final List<MyPlaceDto> bookings);
    }

}
