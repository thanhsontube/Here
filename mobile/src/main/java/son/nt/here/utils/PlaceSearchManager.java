package son.nt.here.utils;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Sonnt on 4/30/15.
 */
public class PlaceSearchManager {

    public GoogleApiClient mGoogleApiClient;



    public void load (TsPlace task) {
        task.execute(this);
    }

    public void cancel(TsPlace task) {
        task.cancel();
    }


}
