package son.nt.here;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.squareup.otto.Subscribe;

import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import son.nt.here.activity.DetailAddressActivity;
import son.nt.here.activity.SearchActivity;
import son.nt.here.base.BaseActivity;
import son.nt.here.dto.DistanceDto;
import son.nt.here.dto.FavouriteDto;
import son.nt.here.dto.MyPlaceDto;
import son.nt.here.server.ReverseLatLngApi;
import son.nt.here.task.MapTaskManager;
import son.nt.here.utils.EventBus;
import son.nt.here.utils.Logger;


public class HomeActivity extends BaseActivity {

    private static final String TAG = "HomeActivity";
    private GoogleMap mMap;

    private List<FavouriteDto> listFavourites;
    private SmoothProgressBar smoothProgressBar;
    private Handler mHandler = new Handler();
    private TextView txtAddress;
    private TextView txtDesAddress;
    private TextView txtDistance;

    private MapTaskManager mapTaskManager;
    private LatLng origin;

    private MyPlaceDto originPlace;
    private MyPlaceDto desPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mapTaskManager = new MapTaskManager();
        initLayout();
        initListener();
    }

    private void initLayout() {
        txtAddress = (TextView) findViewById(R.id.txt_my_address);
        txtDesAddress = (TextView) findViewById(R.id.txt_des_address);
        txtDistance = (TextView) findViewById(R.id.txt_distance);
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

    private void initListener () {
        txtAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDetail(originPlace);
            }
        });

        txtDesAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDetail(desPlace);
            }
        });
    }

    private void startDetail (MyPlaceDto myPlaceDto) {
        Intent intent = new Intent(this, DetailAddressActivity.class);
        intent.putExtra("data", myPlaceDto);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        setupMapIfNeeded();
        EventBus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.unregister(this);
    }

    @Override
    protected void onDestroy() {
        mMap = null;
        super.onDestroy();
    }


    boolean isLocationUpdated = false;

    @Subscribe
    public void getUpdateLastKnowLocation(Location location) {
        isLocationUpdated = true;
        Logger.debug(TAG, ">>>getUpdateLastKnowLocation");
        origin = new LatLng(location.getLatitude(), location.getLongitude());
        adjustMap(origin);

        String sFormat = "%s,%s";
        String position = String.format(sFormat, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
        ReverseLatLngApi.getInstance().reverseLatLng(position);
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
        mMap.setIndoorEnabled(false);
        mMap.setTrafficEnabled(false);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);


        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                LatLng latLng = cameraPosition.target;
                String sFormat = "%s,%s";
                String position = String.format(sFormat, String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));
                Logger.debug(TAG, ">>>" + "position:" + position);
                ReverseLatLngApi.getInstance().reverseLatLng(position);
                ReverseLatLngApi.getInstance().distance(origin, cameraPosition.target);
            }
        });

        ReverseLatLngApi.getInstance().setCallbackListener(new ReverseLatLngApi.IApiListener() {
            @Override
            public void onStart() {
                smoothProgressBar.progressiveStart();
                Logger.debug(TAG, ">>>" + "onStart");

            }

            @Override
            public void onSuccess(MyPlaceDto myPlaceDto) {
                Logger.debug(TAG, ">>>" + "onSuccess:" + myPlaceDto.status + ";formatted_address:" + myPlaceDto.formatted_address);
                if (isLocationUpdated) {
                    originPlace = myPlaceDto;

                    txtAddress.setText(myPlaceDto.formatted_address);
                    isLocationUpdated = false;
                }
                txtDesAddress.setText(myPlaceDto.formatted_address);
                desPlace = myPlaceDto;
                smoothProgressBar.progressiveStop();

            }

            @Override
            public void onFailure(Throwable error) {
                Logger.debug(TAG, ">>>" + "onFailure:" + error.toString());
                smoothProgressBar.progressiveStop();

            }
        });

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
//                String sFormat = "%s,%s";
//                String position = String.format(sFormat, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
//                ReverseLatLngApi.getInstance().reverseLatLng(position);

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                return false;
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

        if (id == R.id.action_search) {

            startActivity(new Intent(this, SearchActivity.class));

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

    @Subscribe
    public void getDistance (DistanceDto distanceDto) {
        Logger.debug(TAG, ">>>" + "getDistance:" + distanceDto.distance);
        if (distanceDto.status.equalsIgnoreCase("OK")) {
            txtDistance.setText(distanceDto.distance + " - " + distanceDto.duration);
        }
    }
}
