package son.nt.here.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.otto.Produce;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SDKLocationProvider implements GoogleApiClient.ConnectionCallbacks,
                GoogleApiClient.OnConnectionFailedListener, android.location.LocationListener,
                com.google.android.gms.location.LocationListener
{
    private static final String TAG                                   = SDKLocationProvider.class
                                                                                             .getSimpleName();
    private static SDKLocationProvider INSTANCE                              = new SDKLocationProvider();
    private Context mApplicationContext;

    private static final long          LOCATION_UPDATE_FASTEST_INTERVAL      = TimeUnit.MINUTES.toMillis(1);
    private static final long          LOCATION_UPDATE_INTERVAL              = TimeUnit.MINUTES.toMillis(20);
    private static final int           LOCATION_UPDATE_SMALLEST_DISPLACEMENT = 100;

    // Result
    private Location mLastKnownLocation;

    // New GMS API
    private GoogleApiClient mGoogleApiClient;

    // Legacy Location API
    private LocationManager mLocationManager;

    private SDKLocationProvider()
    {
        // Enforce Singleton

        // Register the Producer getLastKnownLocation()
        EventBus.register(this);
    }

    public static SDKLocationProvider getInstance()
    {
        return SDKLocationProvider.INSTANCE;
    }

    /**
     * Connect to location services (GMS is preferred, with a fallback to LocationManager). First 
     * get the last known location and then register for passive updates. We use passive updates as 
     * GMaps will automatically try and get the best location. We listen for their results and post 
     * to the EventBus for all subscribers. 
     * @param context
     */
    public void initialize(final Context context)
    {
        Logger.debug(SDKLocationProvider.TAG, "init()");

        // Only use app context
        this.mApplicationContext = context.getApplicationContext();

        // First Check which API to use
        final int serviceAvailability = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.mApplicationContext);
        if (serviceAvailability == ConnectionResult.SUCCESS)
        {
            // If GMS is available, connect and wait for the callback to get the last location and register for passive updates
            this.mGoogleApiClient = new GoogleApiClient.Builder(this.mApplicationContext).addApi(LocationServices.API)
                            .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
            this.mGoogleApiClient.connect();
        }
        else
        {
            // Fallback to LocationManager if GMS is unavailable. We can directly get the last location and register for updates
            this.mLocationManager = (LocationManager) this.mApplicationContext
                            .getSystemService(Context.LOCATION_SERVICE);

            if (this.mLocationManager != null)
            {
                this.obtainLocationManagerLastLocation();
                this.startLocationManagerUpdate();
            }
        }
    }

    /**
     * Call this to change the value of {@code mLastKnownLocation} and send it out on the EventBus
     *  
     * @param location
     */
    public void updateLastKnownLocation(Location location)
    {
        Logger.debug(SDKLocationProvider.TAG, "Last location updated to " + location.toString());
        this.mLastKnownLocation = location;
        EventBus.post(this.mLastKnownLocation);
    }

    @Produce
    public Location getLastKnownLocation()
    {
        return mLastKnownLocation;
    }

    // ----- NEW GMS API SECTION START ----- //

    private void obtainGMSLastLocation()
    {
        Logger.debug(SDKLocationProvider.TAG, "Obtaining GMS Last Location");
        final Location location = LocationServices.FusedLocationApi.getLastLocation(this.mGoogleApiClient);
        if (location != null)
        {
            this.updateLastKnownLocation(location);
        }
    }

    private void startGMSUpdate()
    {
        Logger.debug(SDKLocationProvider.TAG, "Subscribing to GMS Location Update");

        LocationRequest locationRequest = LocationRequest.create() //
                        .setPriority(LocationRequest.PRIORITY_NO_POWER) //
                        .setInterval(SDKLocationProvider.LOCATION_UPDATE_INTERVAL) //
                        .setFastestInterval(SDKLocationProvider.LOCATION_UPDATE_FASTEST_INTERVAL) //
                        .setSmallestDisplacement(SDKLocationProvider.LOCATION_UPDATE_SMALLEST_DISPLACEMENT);

        Logger.debug(SDKLocationProvider.TAG, "startGMSUpdate - Starting new Request");
        LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionFailed(final ConnectionResult result)
    {
        // Do nothing
        Logger.debug(SDKLocationProvider.TAG, "onConnectionFailed()");
    }

    @SuppressWarnings("nls")
    @Override
    public void onConnected(final Bundle connectionHint)
    {
        Logger.debug(SDKLocationProvider.TAG, "onConnected()");
        this.obtainGMSLastLocation();
        this.startGMSUpdate();
    }

    @Override
    public void onConnectionSuspended(final int cause)
    {
        // Do nothing
        Logger.debug(SDKLocationProvider.TAG, "onConnectionSuspended()");
    }

    // ----- NEW GMS API SECTION END ----- //

    // ----- LEGACY LOCATION API SECTION START ----- //

    @SuppressWarnings("nls")
    private void obtainLocationManagerLastLocation()
    {
        Logger.debug(SDKLocationProvider.TAG, "Obtaining LocationManager Last Location");

        final List<String> providers = this.mLocationManager.getAllProviders();

        if (providers == null || providers.isEmpty())
        {
            return;
        }

        List<Location> locations = new ArrayList<Location>();

        for (final String provider : providers)
        {
            final Location currProviderLocation = this.mLocationManager.getLastKnownLocation(provider);
            if (currProviderLocation == null)
            {
                continue;
            }

            locations.add(currProviderLocation);
        }

        if (locations.size() == 0)
        {
            return;
        }

        Collections.sort(locations, new Comparator<Location>() {
            @Override
            public int compare(Location lhs, Location rhs) {
                long l = lhs.getTime();
                long r = rhs.getTime();
                return l < r ? -1 : (l == r ? 0 : 1);
            }
        });

        this.updateLastKnownLocation(locations.get(locations.size() - 1));
    }

    private void startLocationManagerUpdate()
    {
        Logger.debug(SDKLocationProvider.TAG, "Subscribing to LocationManager Location Update");

        this.mLocationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,
                        SDKLocationProvider.LOCATION_UPDATE_FASTEST_INTERVAL,
                        SDKLocationProvider.LOCATION_UPDATE_SMALLEST_DISPLACEMENT, this);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        Logger.debug(SDKLocationProvider.TAG, "onLocationChanged");

        if (location == null)
        {
            return;
        }

        this.updateLastKnownLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
        // Don't care
    }

    @Override
    public void onProviderEnabled(String provider)
    {
        // Don't care
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        // Don't care
    }

    // ----- LEGACY LOCATION API SECTION END ----- //
}
