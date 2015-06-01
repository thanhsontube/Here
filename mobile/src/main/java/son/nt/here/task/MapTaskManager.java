package son.nt.here.task;

import com.google.android.gms.maps.GoogleMap;

import java.util.List;

import son.nt.here.dto.FavDto;

/**
 * Created by Sonnt on 4/30/15.
 */
public class MapTaskManager {
    public GoogleMap mMap;
    public List<FavDto> listFavourites;

    public void load (MapTask mapTask) {
        mapTask.execute(this);
    }

    public void cancel(MapTask mapTask) {
        mapTask.cancel();
    }

    public void loadGeoCode (GeoCodeTask task) {
        task.execute(this);
    }

    public void cancelGeoCode (GeoCodeTask task) {
        task.cancel();
    }


}
