package son.nt.here;

/**
 * Created by Sonnt on 4/30/15.
 */
public class MsConst {
    public static enum PlaceType {
        NONE (0),
        FOOD (1),
        CAFE (2),
        HOME (3),
        OFFICE (4),
        OTHERS (5);

        int type;
        private PlaceType (int type) {
            this.type = type;
        }

        public static int getValue (PlaceType placeType) {
            for (PlaceType p : PlaceType.values()) {
                if (p == placeType) {
                    return p.type;
                }
            }
            return -1;
        }
    }

    public static final String KEY_GIFT_CODE = "KEY_GIFT_CODE";

    //http://maps.googleapis.com/maps/api/geocode/json?latlng=10.792986,106.670004&sensor=true

    //http://maps.googleapis.com/maps/api/directions/json?origin="+ startLat +","+ startLong +"&destination="+ endLat +","+ endLong +"&sensor=false
    //http://maps.googleapis.com/maps/api/directions/json?origin=40.712783%2C-74.005943&destination=40.703968742341544%2C-74.01009242981672&sensor=true
}
