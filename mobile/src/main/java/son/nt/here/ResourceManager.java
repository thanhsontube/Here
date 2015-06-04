package son.nt.here;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import son.nt.here.db.MyData;

/**
 * Created by Sonnt on 5/3/15.
 */
public class ResourceManager {
    private static  ResourceManager instance;
    private Context context;

    public GoogleApiClient mGoogleApiClient;
    public boolean isGoogleApiClientConnected = false;
    private MyData data;

    public ResourceManager(Context context) {
        this.context = context;
        init();

    }

    public MyData getData() {
        return data;
    }

    public static void createInstance(Context context) {
        instance = new ResourceManager(context);
    }

    public static ResourceManager getInstance () {
        return instance;
    }

    private void init () {
        data = new MyData(context);
        rebuildGoogleApiClient();
    }

    private void rebuildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        isGoogleApiClientConnected = true;
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        isGoogleApiClientConnected = false;
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        isGoogleApiClientConnected = false;
                    }
                })
                .build();
    }
}
