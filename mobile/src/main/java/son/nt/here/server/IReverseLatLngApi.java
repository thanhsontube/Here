package son.nt.here.server;


import com.google.gson.JsonObject;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Sonnt on 5/4/15.
 */
public interface IReverseLatLngApi {
    @GET("/maps/api/geocode/json")
    void reverseLatLng (@Query("latlng") String latlng, @Query("sensor") String sensor, Callback<JsonObject> callback) ;

    @GET("/maps/api/directions/json")
    void distance (@Query("origin") String origin, @Query("destination") String destination, @Query("sensor") String sensor, Callback<JsonObject> callback) ;
}
