package son.nt.here.server;

import android.content.Context;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;


import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import son.nt.here.dto.MyPlaceDto;
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
    }

    private RestAdapter getRestAdapter () {
        return new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setConverter(new GsonConverter(new GsonBuilder().serializeNulls().create()))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
    }

    public void reverseLatLng (String latLng) {
        listener.onStart();
        mService.reverseLatLng(latLng, "true", new Callback<JsonObject>() {
            @Override
            public void success(JsonObject myPlaceDto, Response response) {
                Logger.debug(TAG, ">>>" + "reverseLatLng success:" + myPlaceDto.toString());
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
}
