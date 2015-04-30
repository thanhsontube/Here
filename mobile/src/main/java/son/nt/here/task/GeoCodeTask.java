package son.nt.here.task;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.Executor;

import son.nt.here.dto.GeocodeDto;

/**
 * Created by Sonnt on 4/30/15.
 */
public abstract class GeoCodeTask {

    public abstract void onStart();
    public abstract void onSucceed (GeocodeDto dto);
    public abstract void onFailed (Throwable error);

    private WeakReference<Context> wContext = new WeakReference<Context>(null);
    private LatLng location;
    public GeoCodeTask (Context context, LatLng latLng) {
        this.wContext = new WeakReference<Context>(context);
        this.location = latLng;
    }

    AsyncTask<MapTaskManager, Void, GeocodeDto> task = new AsyncTask<MapTaskManager, Void, GeocodeDto>() {
        private Throwable error = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(isCancelled()) {
                return;
            }
            onStart();
        }

        @Override
        protected GeocodeDto doInBackground(MapTaskManager... params) {
            if(isCancelled()) {
                return null;
            }
            Context context = wContext.get();
            if (wContext == null) {
                cancel(true);
                return null;
            }
            Geocoder geocoder = new Geocoder(context);
            double latitude = location.latitude;
            double longitude = location.longitude;

            List<Address> addresses = null;
            String addressText="";

            try {
                addresses = geocoder.getFromLocation(latitude, longitude,1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(addresses != null && addresses.size() > 0 ){
                Address address = addresses.get(0);

                addressText = String.format("%s, %s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getLocality(),
                        address.getCountryName());


                addressText = address.toString();
            }

            return new GeocodeDto(addressText);
        }

        @Override
        protected void onPostExecute(GeocodeDto geocodeDto) {
            super.onPostExecute(geocodeDto);

            if(isCancelled()) {
                return;
            }
            if (error != null) {
                onFailed(error);
                return;
            }
            onSucceed(geocodeDto);
        }
    };

    void cancel () {
        task.cancel(true);

    }

    void execute (MapTaskManager mapTaskManager) {
        task.executeOnExecutor(getExecutor(), mapTaskManager);
    }

    private Executor getExecutor() {
        return AsyncTask.THREAD_POOL_EXECUTOR;
    }
}
