package son.nt.here.dto;

import android.net.Uri;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Locale;

/**
 * Created by Sonnt on 5/4/15.
 */
public class MyPlaceDto implements Place {

    public String id;
    public String address;
    public String name;
    public Double lat;
    public Double lng;
    public LatLngBounds viewPorts;
    public String phoneNumber;
    public String priceLevel;
    public String status;

    public static MyPlaceDto create (JsonObject jsonObject) {
        //TODO convert here
//        JsonParser jsonParser = new JsonParser();
//        jsonParser.parse(jsonObject.)
//        String status = jsonObject.get

        JsonElement jsonElement = jsonObject.get("status");
        String status = jsonElement.getAsString();
        MyPlaceDto myPlaceDto = new MyPlaceDto();
        myPlaceDto.status = status;

        if(!status.equalsIgnoreCase("OK")) {
            return myPlaceDto;
        }
        JsonArray jsonArray = jsonObject.getAsJsonArray("results");
        JsonObject mainJ = jsonArray.get(0).getAsJsonObject();

        String address = mainJ.get("formatted_address").getAsString();
        myPlaceDto.address = address;

        return myPlaceDto;
    }


    @Override
    public String getId() {
        return null;
    }

    @Override
    public List<Integer> getPlaceTypes() {
        return null;
    }

    @Override
    public CharSequence getAddress() {
        return null;
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public CharSequence getName() {
        return null;
    }

    @Override
    public LatLng getLatLng() {
        return null;
    }

    @Override
    public LatLngBounds getViewport() {
        return null;
    }

    @Override
    public Uri getWebsiteUri() {
        return null;
    }

    @Override
    public CharSequence getPhoneNumber() {
        return null;
    }

    @Override
    public boolean zzsT() {
        return false;
    }

    @Override
    public float getRating() {
        return 0;
    }

    @Override
    public int getPriceLevel() {
        return 0;
    }

    @Override
    public Place freeze() {
        return null;
    }

    @Override
    public boolean isDataValid() {
        return false;
    }
}
