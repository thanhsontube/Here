package son.nt.here.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import son.nt.here.dto.MyPlaceDto;
import son.nt.here.notification.NotiUtils;
import son.nt.here.utils.Logger;
import son.nt.here.utils.SDKLocationProvider;

public class HereService extends Service {
    public static final String TAG = "HereService";
    public static final long DELAYS_UPDATE = 1000 * 60 * 30; //30 minutes

    private GoogleApiClient googleApiClient;
    private boolean isConnected = false;
    private UpdateCurrentPlace updateCurrentPlace;
    private Handler handler = new Handler();

    private LocalBinder localBinder = new LocalBinder();
    private IService mListener;
    private LatLng lastKnowLocation;
    public interface IService {
        void onMyLocation(ArrayList<MyPlaceDto> arrayList);
        void onFirstStart(LatLng latLng);
    }
    public void registerListener (IService callback) {
        this.mListener = callback;
    }
    public void unRegisterListener() {
        this.mListener = null;
    }

    public static Intent getIntentService(Context context) {
        return new Intent(context, HereService.class);
    }

    public class LocalBinder extends Binder {
        public HereService getService() {
            return HereService.this;
        }
    }

    public HereService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.debug(TAG, ">>>" + "HereService onCreate");
        initGoogleApiClient();
    }

    @Override
    public IBinder onBind(Intent intent) {
        initGoogleApiClient();
        return localBinder;
    }

    public void initGoogleApiClient() {
        if (googleApiClient != null) {
            return;
        }
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Logger.debug(TAG, ">>>" + "onConnected");
                        isConnected = true;
                        updateCurrentPlace = new UpdateCurrentPlace(HereService.this);
                        handler.post(runnableUpdate);
                        startGMSUpdate();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        isConnected = false;
                        handler.removeCallbacks(runnableUpdate);

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        isConnected = false;
                        Logger.debug(TAG, ">>>" + "onConnectionFailed");
                        handler.removeCallbacks(runnableUpdate);

                    }
                })

                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
        googleApiClient.connect();
    }

    /**
     * this function will called every time location changed.
     */

    public void startGMSUpdate() {

        LocationRequest locationRequest = LocationRequest.create() //
                .setPriority(LocationRequest.PRIORITY_NO_POWER) //
                .setInterval(SDKLocationProvider.LOCATION_UPDATE_INTERVAL) //
                .setFastestInterval(SDKLocationProvider.LOCATION_UPDATE_FASTEST_INTERVAL) //
                .setSmallestDisplacement(SDKLocationProvider.LOCATION_UPDATE_SMALLEST_DISPLACEMENT);
        if (!googleApiClient.isConnected()) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lastKnowLocation = new LatLng(location.getLatitude(), location.getLongitude());
                updateCurrentPlace.update();

            }
        });
    }

    private Runnable runnableUpdate = new Runnable() {
        @Override
        public void run() {
            updateCurrentPlace.update();
            handler.postDelayed(this,DELAYS_UPDATE);
        }
    };

    @Override
    public void onDestroy() {
        Logger.debug(TAG, ">>>" + "onDestroy");
        super.onDestroy();
        if (googleApiClient != null && isConnected) {
            googleApiClient.disconnect();
        }
        handler.removeCallbacks(runnableUpdate);
    }

    public void reversePlaceId (String placeId) {
        Logger.debug(TAG, ">>>" + "reversePlaceId:" + placeId);
        if (!googleApiClient.isConnected()) {
            return;
        }
        Places.GeoDataApi.getPlaceById(googleApiClient, placeId).setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(PlaceBuffer places) {
                if (!places.getStatus().isSuccess()) {
                    Logger.error(TAG, ">>>" + " Places.GeoDataApi.getPlaceById ERROR");
                }

                for (Place place : places) {
                    Logger.debug(TAG, ">>>" + "Name:" + place.getName() + ";address:" + place.getAddress());
                }

            }
        });
    }



    private static final class UpdateCurrentPlace {
        private WeakReference<HereService> hereServiceWeakReference;

        public UpdateCurrentPlace(HereService hereService) {
            this.hereServiceWeakReference = new WeakReference<HereService>(hereService);
        }

        public void update() {
            final HereService listener = hereServiceWeakReference.get();
            if (listener == null || listener.googleApiClient == null || !listener.isConnected) {
                return;
            }

            Places.PlaceDetectionApi.getCurrentPlace(listener.googleApiClient, null).setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(PlaceLikelihoodBuffer placeLikelihoods) {
                    if (!placeLikelihoods.getStatus().isSuccess()) {
                        return;
                    }
                    LatLng latLng = null;
                    if (placeLikelihoods != null) {
                        Place place = placeLikelihoods.get(0).getPlace();
                        MyPlaceDto myPlaceDto = new MyPlaceDto();
                        latLng = place.getLatLng();
                        myPlaceDto.favTitle = (String) place.getName();
                        myPlaceDto.formatted_address = (String) place.getAddress();
                        NotiUtils.showNotification(hereServiceWeakReference.get().getApplicationContext(), myPlaceDto);
                    }
                    ArrayList<MyPlaceDto> arrayList = new ArrayList<>();
                    MyPlaceDto myPlaceDto;
                    Place place;
                    for (PlaceLikelihood p: placeLikelihoods) {
                        place = p.getPlace();
                        if (place != null) {
                            myPlaceDto = new MyPlaceDto();
                            myPlaceDto.addPlaces(place);
                            arrayList.add(myPlaceDto);
                        }
                    }
                    if (listener != null && listener.mListener != null) {
                        listener.mListener.onFirstStart(listener.lastKnowLocation);
                        listener.mListener.onMyLocation(arrayList);
                    }
                    placeLikelihoods.release();
                }
            });
        }


    }

    public LatLng getLastKnowLocation() {
        return lastKnowLocation;
    }
}
