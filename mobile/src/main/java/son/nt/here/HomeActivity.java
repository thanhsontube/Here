package son.nt.here;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.otto.Subscribe;

import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import son.nt.here.base.BaseActivity;
import son.nt.here.dto.FavouriteDto;
import son.nt.here.utils.EventBus;
import son.nt.here.utils.Logger;


public class HomeActivity extends BaseActivity {

    private static final String TAG = "HomeActivity";
    private GoogleMap mMap;

    private List<FavouriteDto> listFavourites;
    private SmoothProgressBar smoothProgressBar;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initLayout();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setupMapIfNeeded();
        EventBus.register(this);
    }
    @Override
    protected void onDestroy() {
        mMap = null;
        EventBus.unregister(this);
        super.onDestroy();
    }



    @Subscribe
    public void getUpdateLastKnowLocation(Location location) {
        Logger.debug(TAG, ">>>getUpdateLastKnowLocation");
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        adjustMap(latLng);
    }


    private void initLayout() {
        smoothProgressBar = (SmoothProgressBar) findViewById(R.id.smooth_progress_bar);
        smoothProgressBar.progressiveStop();

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSafeFragmentManager().findFragmentById(R.id.frame_map);
        if (supportMapFragment == null) {
            FragmentTransaction ft = getSafeFragmentManager().beginTransaction();
            supportMapFragment = SupportMapFragment.newInstance();
            ft.add(R.id.frame_map, supportMapFragment);
            ft.commit();
        }
    }

    private void setupMapIfNeeded() {

        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            SupportMapFragment supportMapFragment = (SupportMapFragment) getSafeFragmentManager().findFragmentById(R.id.frame_map);
            mMap = supportMapFragment.getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }

    }

    private GoogleMap setUpMap() {

        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                smoothProgressBar.progressiveStart();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        smoothProgressBar.progressiveStop();
                    }
                }, 1000);
            }
        });

        return mMap;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void adjustMap(LatLng latLng) {
        setupMapIfNeeded();

        if (mMap != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng)
                    .tilt(45)
                    .zoom(15)
                    .bearing(0f)
                    .build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

    }
}
