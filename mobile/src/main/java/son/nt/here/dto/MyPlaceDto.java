package son.nt.here.dto;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import son.nt.here.MsConst;

/**
 * Created by Sonnt on 5/4/15.
 */
public class MyPlaceDto implements Place, Serializable {

    public String id;
    public String formatted_address;
    public String place_id;
    public String name;
    public LatLngBounds viewPorts;
    public String phoneNumber;
    public String priceLevel;
    public String status = "FAIL";

    public String street_number; //207
    public String streetName; //route = le van sy
    public String country; //vn
    public String city; //HCM
    public String district; //Phu Nhuan
    public String subLv1; //p14
    public String postal_code;
    public double lat;
    public double lng;
    public String title;
    public String description;
    public List<String> listImages = new ArrayList<>();
    public List<MsConst.PlaceType> placeTypes;

    public MyPlaceDto() {
        street_number = "";
        streetName = "";
        country = "";
        city = "";
        district = "";
        subLv1 = "";
        postal_code = "";
        formatted_address = "";
    }

    public static MyPlaceDto create (JsonObject jsonObject) {

        MyPlaceDto dto = new MyPlaceDto();

        if (!jsonObject.get("status").getAsString().equals("OK")) {
            return dto;
        }
        dto.status = "OK";

        JsonObject mainJ = jsonObject.getAsJsonArray("results").get(0).getAsJsonObject();


        dto.formatted_address = mainJ.get("formatted_address").getAsString();
        dto.place_id = mainJ.get("place_id").getAsString();

        //lat lng
        dto.lat = mainJ.get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lat").getAsDouble();
        dto.lng = mainJ.get("geometry").getAsJsonObject().get("location").getAsJsonObject().get("lng").getAsDouble();

        //address_components
        JsonArray jComponents = mainJ.getAsJsonArray("address_components");

        if(jComponents == null || jComponents.size() == 0) {
            return dto;
        }

        //country
        JsonObject jChild;
        for (int i = 0; i < jComponents.size(); i++) {
            jChild = jComponents.get(i).getAsJsonObject();
            String types = jChild.get("types").getAsJsonArray().get(0).getAsString();
            Log.v("", ">>>" + "types:" + types);
            if (types.equals("street_number")) {
                dto.street_number = jChild.get("long_name").getAsString();
            } else if (types.equals("route")) {
                dto.streetName = jChild.get("long_name").getAsString();
            } else if (types.equals("sublocality_level_1")) {
                dto.subLv1 = jChild.get("long_name").getAsString();
            } else if (types.equals("administrative_area_level_2")) {
                dto.district = jChild.get("long_name").getAsString();
            } else if (types.equals("administrative_area_level_1")) {
                dto.city = jChild.get("long_name").getAsString();
            } else if (types.equals("country")) {
                dto.country = jChild.get("long_name").getAsString();
            } else if (types.equals("postal_code")) {
                dto.postal_code = jChild.get("long_name").getAsString();
            }
        }
        return dto;
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

    public String getListImages () {
        if (listImages == null || listImages.size() == 0) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String s: listImages) {
            stringBuilder.append(s);
            stringBuilder.append(";");
        }
        return stringBuilder.toString();
    }
}
