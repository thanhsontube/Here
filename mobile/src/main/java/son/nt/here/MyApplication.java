package son.nt.here;

import android.app.Application;

import son.nt.here.server.ReverseLatLngApi;
import son.nt.here.utils.SDKLocationProvider;

/**
 * Created by Sonnt on 4/17/15.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SDKLocationProvider.getInstance().initialize(getApplicationContext());
        ResourceManager.createInstance(getApplicationContext());
        ReverseLatLngApi.createInstance(getApplicationContext());
    }
}
