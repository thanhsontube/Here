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

    public LoadFavouritesModel(final Context context, final IOnLoadFavoritesListener listener) {
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
                dto.name = cursor.getString(cursor.getColumnIndex("title"));
                dto.formatted_address = cursor.getString(cursor.getColumnIndex("address"));
                dto.favNotes = cursor.getString(cursor.getColumnIndex("notes"));
                dto.favUpdateTime = cursor.getLong(cursor.getColumnIndex("update_time"));
                dto.street_number = cursor.getString(cursor.getColumnIndex("street_number"));
                dto.streetName = cursor.getString(cursor.getColumnIndex("streetName"));
                dto.country = cursor.getString(cursor.getColumnIndex("country"));
                dto.city = cursor.getString(cursor.getColumnIndex("city"));
                dto.district = cursor.getString(cursor.getColumnIndex("district"));
                dto.subLv1 = cursor.getString(cursor.getColumnIndex("subLv1"));
                dto.postal_code = cursor.getString(cursor.getColumnIndex("postal_code"));
                dto.place_id = cursor.getString(cursor.getColumnIndex("placeId"));
                dto.lat = cursor.getDouble(cursor.getColumnIndex("lat"));
                dto.lng = cursor.getDouble(cursor.getColumnIndex("lng"));
                dto.address = cursor.getString(cursor.getColumnIndex("address_near"));

                dto.webUri = cursor.getString(cursor.getColumnIndex("web_uri"));
                dto.phoneNumber = cursor.getString(cursor.getColumnIndex("phone_number"));
                dto.rating = cursor.getFloat(cursor.getColumnIndex("rating"));
                dto.isFav = (cursor.getInt(cursor.getColumnIndex("is_fav")) == 1 ? true : false);
                list.add(dto);

            } while (cursor.moveToNext());
        }

        DbUtils.close(db.db, cursor);
        return list;
    }

    @Override
    protected void onPostExecute(final List<MyPlaceDto> result) {
        this.mListener.onLoadFavorites(result);
    }

    /**
     * Callback interface on completion of this model
     */
    public interface IOnLoadFavoritesListener {
        void onLoadFavorites(final List<MyPlaceDto> bookings);
    }

}
