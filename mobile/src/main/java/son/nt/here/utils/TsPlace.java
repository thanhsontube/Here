package son.nt.here.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import son.nt.here.dto.PlaceSearchDto;

/**
 * Created by Sonnt on 5/1/15.
 */
public abstract class  TsPlace {

    private static final String TAG = "onConnected";
    private static TsPlace instance = null;
    private Context context;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    LatLngBounds mBounds = BOUNDS_GREATER_SYDNEY;
    AutocompleteFilter mPlaceFilter = null;

    //map API
    private GoogleApiClient mGoogleApiClient;

    public TsPlace(Context context) {
        this.context = context;
        rebuildGoogleApiClient();
//        if (mGoogleApiClient != null) {
//            mGoogleApiClient.connect();
//        }
    }

    public TsPlace(Context context, String place) {
        this.context = context;
        this.place = place;
        rebuildGoogleApiClient();
//        if (mGoogleApiClient != null) {
//            mGoogleApiClient.connect();
//        }
    }

//    public static void createInstance(Context context) {
//        instance = new TsPlace(context);
//    }

    public static TsPlace getInstance() {
        return instance;
    }

    private void rebuildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(onConnectionFailedListener)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    private GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            Logger.debug(TAG, ">>>" + "GoogleApiClient onConnected");
//            if(isWait == true) {
//                task.executeOnExecutor(getExecutor(), place);
//            }


        }

        @Override
        public void onConnectionSuspended(int i) {
            Logger.debug(TAG, ">>>" + "GoogleApiClient onConnectionSuspended");


        }
    };

    GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Logger.error(TAG, ">>>" + "GoogleApiClient onConnectionFailed");

        }
    };


    private AsyncTask<PlaceSearchManager, Void, List<PlaceSearchDto>> task = new AsyncTask<PlaceSearchManager, Void, List<PlaceSearchDto>>() {


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
        protected List<PlaceSearchDto> doInBackground(PlaceSearchManager... params) {
            Logger.debug(TAG, ">>>" + "doInBackground");
            if (isCancelled()) {
                return null;
            }


            // Submit the query to the autocomplete API and retrieve a PendingResult that will
            // contain the results when the query completes.
            GoogleApiClient mGoogleApiClient = params[0].mGoogleApiClient;
            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
                            .getAutocompletePredictions(mGoogleApiClient, place,
                                    mBounds, mPlaceFilter);

            // This method should have been called off the main UI thread. Block and wait for at most 60s
            // for a result from the API.
            AutocompletePredictionBuffer autocompletePredictions = results
                    .await(60, TimeUnit.SECONDS);

            // Confirm that the query completed successfully, otherwise return null
            final com.google.android.gms.common.api.Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {

                Logger.debug(TAG, ">>>Error getting autocomplete prediction API call: " + status.toString());
                autocompletePredictions.release();
                error = new Exception("Error getting autocomplete prediction API call: \" + status.toString()");
                return null;
            }

            Logger.debug(TAG, ">>>Query completed. Received " + autocompletePredictions.getCount()
                    + " predictions.");

            // Copy the results into our own data structure, because we can't hold onto the buffer.
            // AutocompletePrediction objects encapsulate the API response (place ID and description).

            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
            List<PlaceSearchDto> resultList = new ArrayList<>();
            while (iterator.hasNext()) {
                AutocompletePrediction prediction = iterator.next();
                // Get the details of this prediction and copy it into a new PlaceAutocomplete object.
                resultList.add(new PlaceSearchDto(prediction.getPlaceId(),
                        prediction.getDescription()));

            }

            // Release the buffer now that all data has been copied.
            autocompletePredictions.release();

            return resultList;
        }

        @Override
        protected void onPostExecute(List<PlaceSearchDto> placeSearchDtos) {
            super.onPostExecute(placeSearchDtos);
            if (isCancelled()) {
                return;
            }

            if(error != null) {
                onFailed(error);
            } else {
                onSucceed(placeSearchDtos);
            }
        }
    };

    boolean isWait = false;
    private String place;

//    public void search (String place) {
//        this.place = place;
//        if(mGoogleApiClient.isConnected()) {
//            task.executeOnExecutor(getExecutor(), place);
//            isWait = false;
//        } else {
//            isWait = true;
//        }
//
//    }

    private Executor getExecutor () {
        return AsyncTask.THREAD_POOL_EXECUTOR;
    }

    public abstract void onStart();
    public abstract void onSucceed (List<PlaceSearchDto> listPlaceSearch);
    public abstract void onFailed (Throwable error);
    public abstract void onReservePlaceIdOK (Place place);

    void cancel() {
        task.cancel(true);
    }

//    void execute (String place) {
//        this.place = place;
//        if(mGoogleApiClient.isConnected()) {
//            task.executeOnExecutor(getExecutor(), place);
//            isWait = false;
//        } else {
//            isWait = true;
//        }
//    }

    void execute (PlaceSearchManager mng) {
        task.executeOnExecutor(getExecutor(), mng);
//        if(mGoogleApiClient.isConnected()) {
//            isWait = false;
//        } else {
//            isWait = true;
//        }
    }

    void reservePlaceId (PlaceSearchManager mng, final String placeId) {
        PendingResult<PlaceBuffer> pendingResult = Places.GeoDataApi.getPlaceById(mng.mGoogleApiClient, placeId);
        pendingResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(PlaceBuffer places) {
                if (!places.getStatus().isSuccess()) {
                    onFailed(new Exception("reverse fail with placeId:" + placeId));
                }
                Place place = places.get(0);
                onReservePlaceIdOK(place);
                places.release();
            }
        });
    }


}
