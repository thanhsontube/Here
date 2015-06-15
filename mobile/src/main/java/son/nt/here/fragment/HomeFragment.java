package son.nt.here.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.squareup.otto.Subscribe;

import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import son.nt.here.R;
import son.nt.here.base.BaseFragment;
import son.nt.here.db.MyData;
import son.nt.here.dto.DistanceDto;
import son.nt.here.dto.MyPlaceDto;
import son.nt.here.server.ReverseLatLngApi;
import son.nt.here.task.MapTaskManager;
import son.nt.here.task.ReversePlaceId;
import son.nt.here.utils.EventBus;
import son.nt.here.utils.Logger;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "HomeFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private GoogleMap mMap;
    CameraPosition cameraPosition;

    private List<MyPlaceDto> listFavourites;
    private SmoothProgressBar smoothProgressBar;
    private Handler mHandler = new Handler();
    private TextView txtAddress;
    private TextView txtDesAddress;
    private TextView txtDistance;

    private MapTaskManager mapTaskManager;
    private LatLng origin;

    private MyPlaceDto originPlace;
    private MyPlaceDto desPlace = null;
    private CheckBox chbMy, chbDes;
    private Toolbar toolbar;
    private MyData db;
    private ReversePlaceId reversePlaceId;

    boolean isLocationUpdated = false;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMapFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        smoothProgressBar.progressiveStart();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        smoothProgressBar.progressiveStop();
        setUpMapIfNeeded();
        EventBus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.unregister(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);

        void goDetail(MyPlaceDto location);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initLayout(View view) {
        txtAddress = (TextView) view.findViewById(R.id.txt_my_address);
        txtDesAddress = (TextView) view.findViewById(R.id.txt_des_address);
        txtDistance = (TextView) view.findViewById(R.id.txt_distance);
        smoothProgressBar = (SmoothProgressBar) view.findViewById(R.id.smooth_progress_bar);
        smoothProgressBar.progressiveStop();

        chbMy = (CheckBox) view.findViewById(R.id.home_chb_my);
        chbDes = (CheckBox) view.findViewById(R.id.home_chb_des);
    }

    @Override
    public void initListener() {
        txtAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goDetail(originPlace);
            }
        });

        txtDesAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goDetail(desPlace);
            }
        });

    }

    private void initMapFragment() {
        Logger.debug(TAG, ">>>" + "initMapFragment");
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSafeFragmentManager().findFragmentById(R.id.home_maps);
        if (supportMapFragment == null) {
            supportMapFragment = SupportMapFragment.newInstance();
            FragmentTransaction ft = getSafeFragmentManager().beginTransaction();
            ft.add(R.id.home_maps, supportMapFragment);
            ft.commit();
        }

    }

    //TODO MAPS
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            SupportMapFragment supportMapFragment = (SupportMapFragment) getSafeFragmentManager().findFragmentById(R.id.home_maps);
            mMap = supportMapFragment.getMap();
        }
        setUpMap();
    }

    private GoogleMap setUpMap() {
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.setMyLocationEnabled(true);
                mMap.setIndoorEnabled(false);
                mMap.setTrafficEnabled(false);
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setCompassEnabled(false);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
                GoogleMapOptions googleMapOptions = new GoogleMapOptions();
                googleMapOptions.liteMode(true);
                updateMap(origin);
            }
        });


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
//                reversePlaceId.reverse(myPlaceDto.place_id);

            }

            @Override
            public void onFailure(Throwable error) {
                Logger.debug(TAG, ">>>" + "onFailure:" + error.toString());
                smoothProgressBar.progressiveStop();

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
                isLocationUpdated = true;
                return false;
            }
        });
        return mMap;
    }

    @Subscribe
    public void getUpdateLastKnowLocation(Location location) {
        isLocationUpdated = true;
        Logger.debug(TAG, ">>>getUpdateLastKnowLocation");
        origin = new LatLng(location.getLatitude(), location.getLongitude());
        updateMap(origin);

    }
    @Subscribe
    public void updateDistanceTime(DistanceDto distanceDto) {
        if (distanceDto == null) {
            txtDistance.setText("-- - --");
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(distanceDto.distance).append(" - ").append(distanceDto.duration);
        txtDistance.setText(stringBuilder.toString());

    }

    private void updateMap(LatLng latLng) {
        if (latLng == null) {
            return;
        }
        adjustMap(latLng);

        String sFormat = "%s,%s";
        String position = String.format(sFormat, String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));
        ReverseLatLngApi.getInstance().reverseLatLng(position);

    }

    private void adjustMap(LatLng latLng) {
         cameraPosition = new CameraPosition.Builder().target(latLng)
                .tilt(45)
                .zoom(15)
                .bearing(0f)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_home_2, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_0:
                if (!isSafe() || mMap == null ) {
                    return false;
                }
                cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(originPlace.lat, originPlace.lng))
                        .zoom(15)
                        .tilt(0)
                        .bearing(0f)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                break;
            case R.id.action_45:
                if (!isSafe() || mMap == null ) {
                    return false;
                }
                cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(originPlace.lat, originPlace.lng))
                        .zoom(15)
                        .tilt(45f)
                        .bearing(0f)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateDes (MyPlaceDto dto) {
        if (!isSafe()) {
            return;
        }
//        txtDesAddress.setText(dto.formatted_address);
        this.desPlace = dto;
        String sFormat = "%s,%s";
        String position = String.format(sFormat, String.valueOf(dto.lat), String.valueOf(dto.lng));
        ReverseLatLngApi.getInstance().reverseLatLng(position);
        ReverseLatLngApi.getInstance().distance(origin, new LatLng(dto.lat, dto.lng));
    }
}
