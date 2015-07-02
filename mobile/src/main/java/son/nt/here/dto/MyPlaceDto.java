package son.nt.here.dto;

import android.net.Uri;
import android.text.TextUtils;
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

/**
 * Created by Sonnt on 5/4/15.
 */
public class MyPlaceDto implements Serializable, Place {

    public String id;

    //address getting by api
    public String formatted_address;

    //address get by nearly
    public String address;

    public String place_id;
    public String name;
    public LatLngBounds viewPorts;
    public String phoneNumber;
    public String priceLevel;
    public String status = "FAIL";
    public String webUri;

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
    public List<Integer> placeTypes;
    public boolean isFav;
    public float rating;

    public String favTitle, favNotes;
    public long favUpdateTime;

    public MyPlaceDto() {
        street_number = "";
        streetName = "";
        country = "";
        city = "";
        district = "";
        subLv1 = "";
        postal_code = "";
        formatted_address = "";
        favNotes = "";
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
            if (jChild == null || !jChild.has("types") || jChild.get("types").getAsJsonArray() == null || jChild.get("types").getAsJsonArray().size() == 0) {
                return  dto;
            }
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

    @Override
    public String toString() {
        if (TextUtils.isEmpty(address)) {
            return description;
        } else {
            return name;
        }
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
        return address;
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public CharSequence getName() {
        return name;
    }

    @Override
    public LatLng getLatLng() {
        return new LatLng(lat, lng);
    }

    @Override
    public LatLngBounds getViewport() {
        return null;
    }

    @Override
    public Uri getWebsiteUri() {
        if (TextUtils.isEmpty(webUri)) {
            return null;
        }
        return Uri.parse(webUri);
    }

    @Override
    public CharSequence getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public float getRating() {
        return rating;
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

    public void addPlaces (Place place) {
        this.phoneNumber = (String) place.getPhoneNumber();
        if (place.getWebsiteUri() != null) {

            this.webUri = place.getWebsiteUri().toString();
        }
        this.placeTypes = place.getPlaceTypes();
        this.name = (String) place.getName();
        this.address = (String) place.getAddress();
        this.lat = place.getLatLng().latitude;
        this.lng = place.getLatLng().longitude;
        this.rating = place.getRating();
    }

}
