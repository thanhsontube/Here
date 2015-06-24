package son.nt.here.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import java.lang.ref.WeakReference;

import son.nt.here.dto.MyPlaceDto;
import son.nt.here.notification.NotiUtils;
import son.nt.here.utils.Logger;
import son.nt.here.utils.SDKLocationProvider;

public class HereService extends Service {
    public static final String TAG = "HereService";

    private GoogleApiClient googleApiClient;
    private boolean isConnected = false;
    private UpdateCurrentPlace updateCurrentPlace;

    public static Intent getIntentService(Context context) {
        return new Intent(context, HereService.class);
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
        return null;
    }

    private void getLastKnowLocation() {

    }

    private void initGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Logger.debug(TAG, ">>>" + "onConnected");
                        isConnected = true;
                        updateCurrentPlace = new UpdateCurrentPlace(HereService.this);
                        updateCurrentPlace.update();
                        startGMSUpdate();

                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        isConnected = false;

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        isConnected = false;
                        Logger.debug(TAG, ">>>" + "onConnectionFailed");

                    }
                })

                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    /**
     * this function will called every time location changed.
     */

    private void startGMSUpdate() {

        LocationRequest locationRequest = LocationRequest.create() //
                .setPriority(LocationRequest.PRIORITY_NO_POWER) //
                .setInterval(SDKLocationProvider.LOCATION_UPDATE_INTERVAL) //
                .setFastestInterval(SDKLocationProvider.LOCATION_UPDATE_FASTEST_INTERVAL) //
                .setSmallestDisplacement(SDKLocationProvider.LOCATION_UPDATE_SMALLEST_DISPLACEMENT);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateCurrentPlace.update();

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (googleApiClient != null && isConnected) {
            googleApiClient.disconnect();
        }
    }

    private static final class UpdateCurrentPlace {
        private WeakReference<HereService> hereServiceWeakReference;

        public UpdateCurrentPlace(HereService hereService) {
            this.hereServiceWeakReference = new WeakReference<HereService>(hereService);
        }

        public void update() {
            HereService listener = hereServiceWeakReference.get();
            if (listener == null || listener.googleApiClient == null || !listener.isConnected) {
                return;
            }
            PendingResult<PlaceLikelihoodBuffer> placeLikelihoodPendingResult = Places.PlaceDetectionApi.getCurrentPlace(listener.googleApiClient, null);
            placeLikelihoodPendingResult.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {


                @Override
                public void onResult(PlaceLikelihoodBuffer placeLikelihoods) {
                    if (!placeLikelihoods.getStatus().isSuccess()) {
                        return;
                    }
                    if (placeLikelihoods != null) {
                        Place place = placeLikelihoods.get(0).getPlace();
                        MyPlaceDto myPlaceDto = new MyPlaceDto();
                        myPlaceDto.favTitle = (String) place.getName();
                        myPlaceDto.formatted_address = (String) place.getAddress();
                        NotiUtils.showNotification(hereServiceWeakReference.get().getApplicationContext(), myPlaceDto);
                    }

                    for (PlaceLikelihood placeLikelihood : placeLikelihoods) {
                        Place place = placeLikelihood.getPlace();
                        if (place != null) {
                            CharSequence address = place.getAddress();
                            Logger.debug(TAG, ">>>" + "Name:" + place.getName() + ";address:" + address);
                        }
                    }
                }
            });
        }
    }
}
