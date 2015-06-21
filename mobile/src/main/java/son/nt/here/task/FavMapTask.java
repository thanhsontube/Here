package son.nt.here.task;

import android.os.Handler;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import son.nt.here.dto.MyPlaceDto;

/**
 * Created by Sonnt on 6/21/15.
 */
public class FavMapTask implements Runnable {

    private final WeakReference<GoogleMap> map;
    private List<MyPlaceDto> listFavs = new ArrayList<>();
    private Handler mHandle = new Handler();
    public FavMapTask(GoogleMap googleMap) {
        this.map = new WeakReference<GoogleMap>(googleMap);
    }

    public void setListFav (List<MyPlaceDto> list) {
        this.listFavs.addAll(list);

    }

    public void execute (){
        mHandle.postDelayed(this, 50);

    }

    public void stop() {
        mHandle.removeCallbacks(this);
    }


    @Override
    public void run() {
        final MarkerOptions markerOptions = new MarkerOptions();
        final GoogleMap googleMap = map.get();
        if (googleMap == null) {
            return;
        }

        BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker();

        for (MyPlaceDto dto : listFavs) {
            markerOptions.position(new LatLng(dto.lat, dto.lng))
                    .title(dto.favTitle)
                    .icon(icon)
            ;
            googleMap.addMarker(markerOptions);

        }

    }




}
