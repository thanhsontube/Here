package son.nt.here.server;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;


import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import son.nt.here.dto.DistanceDto;
import son.nt.here.dto.MyPlaceDto;
import son.nt.here.utils.EventBus;
import son.nt.here.utils.Logger;

/**
 * Created by Sonnt on 5/4/15.
 */
public class ReverseLatLngApi {

    public static final String BASE_URL = "http://maps.googleapis.com";
    private static final String TAG = "ReverseLatLngApi";
    public static ReverseLatLngApi instance = null;
    IReverseLatLngApi mService;
    RestAdapter restAdapter;

    public static void createInstance (Context context) {
        instance = new ReverseLatLngApi(context);

    }

    public static ReverseLatLngApi getInstance () {
        return instance;
    }

    public ReverseLatLngApi (Context context) {
        init();
    }

    private void init() {
        restAdapter = getRestAdapter();
        mService = restAdapter.create(IReverseLatLngApi.class);
        EventBus.register(this);
    }

    private RestAdapter getRestAdapter () {
        return new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setConverter(new GsonConverter(new GsonBuilder().serializeNulls().create()))
//                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .build();
    }

    public void reverseLatLng (String latLng) {
        listener.onStart();
        mService.reverseLatLng(latLng, "true", new Callback<JsonObject>() {
            @Override
            public void success(JsonObject myPlaceDto, Response response) {
                listener.onSuccess(MyPlaceDto.create(myPlaceDto));

            }

            @Override
            public void failure(RetrofitError error) {
                Logger.debug(TAG, ">>>" + "reverseLatLng failure:" + error.toString());
                listener.onFailure(new Exception(error.toString()));
            }
        });
    }

    IApiListener listener;

    public interface IApiListener {
        void onStart();
        void onSuccess (MyPlaceDto myPlaceDto);
        void onFailure (Throwable error);
    }

    public void setCallbackListener (IApiListener callback) {
        listener = callback;
    }

    public void distance (LatLng origin, LatLng destination) {
        if (origin == null || destination == null) {
            return;
        }
        String sFormat = "%s,%s";
        String sOrigin = String.format(sFormat, String.valueOf(origin.latitude), String.valueOf(origin.longitude));
        String sDestination = String.format(sFormat, String.valueOf(destination.latitude), String.valueOf(destination.longitude));

        mService.distance(sOrigin, sDestination, "true", new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                DistanceDto distanceDto = DistanceDto.create(jsonObject);

                EventBus.post(distanceDto);
            }

            @Override
            public void failure(RetrofitError error) {
                EventBus.post(new DistanceDto());
            }
        });
    }
}
