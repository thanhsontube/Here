package son.nt.here.task;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import son.nt.here.dto.MyPlaceDto;

/**
 * Created by Sonnt on 4/30/15.
 */
public class MapTask {

    private List<MyPlaceDto> listFavourites = new ArrayList<>();

    public MapTask() {
        listFavourites.clear();;
    }

    public MapTask addPin (MyPlaceDto dto) {
        listFavourites.add(dto);
        return this;

    }
    public MapTask addPins (List<MyPlaceDto> favs) {
        listFavourites.addAll(favs);
        return this;

    }

    protected Executor getExecutor () {

        return AsyncTask.THREAD_POOL_EXECUTOR;
    }

    void execute (MapTaskManager mapTaskManager) {
            task.executeOnExecutor(getExecutor(), mapTaskManager);
    }

    void cancel () {
        task.cancel(true);

    }
    private final AsyncTask<MapTaskManager, Void, Void> task = new AsyncTask<MapTaskManager, Void, Void>() {
        private Throwable error = null;
        private MapTaskManager mapTaskManager;

        @Override
        protected Void doInBackground(MapTaskManager... params) {
            if (isCancelled()) {
                return null;
            }
            MapTaskManager manager = params[0];
            this.mapTaskManager = manager;



            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            for (MyPlaceDto dto: listFavourites) {
                if(isCancelled()) {
                    return ;
                }
                GoogleMap mMap = mapTaskManager.mMap;
                if (mMap == null) {
                    return ;
                }
                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(dto.lat, dto.lng))
                        .title(dto.favTitle)
                        .icon(BitmapDescriptorFactory.defaultMarker());
                Marker marker = mMap.addMarker(markerOptions);

            }
        }
    };

}
