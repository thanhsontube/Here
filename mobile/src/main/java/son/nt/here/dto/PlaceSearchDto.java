package son.nt.here.dto;

/**
 * Created by Sonnt on 5/1/15.
 */
public class PlaceSearchDto {

    public String placeId;
    public String placeDescription;

    public PlaceSearchDto(String placeId, String placeDescription) {
        this.placeId = placeId;
        this.placeDescription = placeDescription;
    }
}
