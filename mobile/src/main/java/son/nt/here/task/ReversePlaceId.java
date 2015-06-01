package son.nt.here.task;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import son.nt.here.utils.Logger;

/**
 * Created by Sonnt on 5/3/15.
 */
public class ReversePlaceId {
    public static final String TAG = "ReversePlaceId";

    static  ReversePlaceId instance = null;
    private Context context;
    private GoogleApiClient googleApiClient;
    public boolean isCancel = false;

    public static ReversePlaceId getInstance() {
        return instance;
    }
    public static void createInstance (Context context) {
        instance = new ReversePlaceId(context);
    }



    public ReversePlaceId (Context context) {
        this.context = context;
        initGeoApi();
        googleApiClient.connect();
    }

    private void initGeoApi() {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        Logger.debug(TAG, ">>>" + " ReversePlaceId onConnected");
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })

                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Logger.error(TAG, ">>>" + "ReversePlaceId onConnectionFailed");

                    }
                })
                .build();
    }

    public void reverse (final String placeId) {
        isCancel = false;
        if(isCancel) {
            return;
        }
        PendingResult<PlaceBuffer> pendingResult = Places.GeoDataApi.getPlaceById(googleApiClient, placeId);
        pendingResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(PlaceBuffer places) {
                if (!places.getStatus().isSuccess()) {
                    if (!isCancel) {
                        listener.onFail(new Exception("reverse fail with placeId:" + placeId));
                    }
                }
                Place place = places.get(0);
                if(!isCancel) {
                    listener.onSuccess(place);
                }
                places.release();
            }
        });

    }

    public void setListener (IReversePlaceIdListener callback) {
        this.listener = callback;
    }
    IReversePlaceIdListener listener;
    public interface IReversePlaceIdListener {
        void onSuccess (Place place);
        void onFail(Throwable error);
    }
}
