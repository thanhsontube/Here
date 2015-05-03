package son.nt.here.dto;

import com.google.gson.JsonObject;

/**
 * Created by Sonnt on 5/4/15.
 */
public class DistanceDto {
    public String distance;
    public String duration;
    public String status = "FAIL";
    public static DistanceDto create (JsonObject jsonObject) {

        JsonObject jo = jsonObject.getAsJsonArray("routes").get(0).getAsJsonObject().getAsJsonArray("legs").get(0)
                .getAsJsonObject();

        String distance = jo.get("distance").getAsJsonObject().get("text").getAsString();
        String duration = jo.get("duration").getAsJsonObject().get("text").getAsString();

        DistanceDto distanceDto = new DistanceDto();
        distanceDto.distance = distance;
        distanceDto.duration = duration;
        distanceDto.status = "OK";
        return distanceDto;
    }
}
