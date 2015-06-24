package son.nt.here;

import android.app.Application;

import com.parse.Parse;

import son.nt.here.server.ReverseLatLngApi;
import son.nt.here.service.HereService;
import son.nt.here.task.ReversePlaceId;
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
        ReversePlaceId.createInstance(getApplicationContext());

        //parse
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "WdGiKRhKXH6fODkQA74BOvQueafTUlWa7nh0ep1g", "kJmGDmGSow7YlrEh2vSu9HjCs62IVbRzIV4SElUy");

        startService(HereService.getIntentService(getApplicationContext()));
    }
}
