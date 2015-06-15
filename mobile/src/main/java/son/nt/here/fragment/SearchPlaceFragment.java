package son.nt.here.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import son.nt.here.R;
import son.nt.here.base.BaseFragment;
import son.nt.here.dto.MyPlaceDto;
import son.nt.here.utils.Logger;
import son.nt.here.utils.PlaceSearchManager;
import son.nt.here.utils.TsPlace;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchPlaceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchPlaceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchPlaceFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "SearchPlaceFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView listView;
    private ArrayAdapter<MyPlaceDto> adapter;
    private List<MyPlaceDto> list = new ArrayList<>();
//    private List<String> list = new ArrayList<>();


    PlaceSearchManager placeSearchManager;

    private OnFragmentInteractionListener mListener;
    private SmoothProgressBar smoothProgressBar;

    private SearchView itemSearch;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchPlaceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchPlaceFragment newInstance(String param1, String param2) {
        SearchPlaceFragment fragment = new SearchPlaceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchPlaceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
        rebuildGoogleApiClient();
        mGoogleApiClient.connect();
        placeSearchManager = new PlaceSearchManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_place, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);

        void onSelected(MyPlaceDto myPlaceDto);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initLayout(View view) {
        listView = (ListView) view.findViewById(R.id.search_list_view);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onItemClickListener);
        smoothProgressBar = (SmoothProgressBar) view.findViewById(R.id.search_smooth_progress_bar);
        smoothProgressBar.progressiveStop();
    }

    AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            final String placeId = list.get(position).place_id;
            Logger.debug(TAG, ">>>" + "placeId:" + placeId);
            try {

                PendingResult<PlaceBuffer> results = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
                results.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess()) {

                            Place place = places.get(0);
                            MyPlaceDto myPlaceDto = list.get(position);
                            myPlaceDto.formatted_address = place.getAddress().toString();
                            myPlaceDto.phoneNumber = place.getPhoneNumber().toString();
                            myPlaceDto.lat = place.getLatLng().latitude;
                            myPlaceDto.lng = place.getLatLng().longitude;
                            myPlaceDto.country = place.getLocale().getCountry();
                            places.release();
                            mListener.onSelected(myPlaceDto);

                        }

                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    public void initListener() {
    }

    GoogleApiClient mGoogleApiClient;

    private void rebuildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(onConnectionFailedListener)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }

    private GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            Logger.debug(TAG, ">>>" + "GoogleApiClient onConnected");
            placeSearchManager.mGoogleApiClient = mGoogleApiClient;


        }

        @Override
        public void onConnectionSuspended(int i) {
            Logger.debug(TAG, ">>>" + "GoogleApiClient onConnectionSuspended");


        }
    };

    GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Logger.error(TAG, ">>>" + "GoogleApiClient onConnectionFailed");

        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        itemSearch = (SearchView) menu.findItem(R.id.action_search).getActionView();
        itemSearch.setQueryHint(getString(R.string.search_hint));
        itemSearch.onActionViewExpanded();
        itemSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                placeSearchManager.load(new TsPlace(getActivity(), newText) {
                    @Override
                    public void onStart() {
                        smoothProgressBar.progressiveStart();
                        ;
                    }

                    @Override
                    public void onSucceed(List<MyPlaceDto> listPlaceSearch) {
                        Logger.debug(TAG, ">>>" + "onSucceed:" + listPlaceSearch.size());

                        if (listPlaceSearch.size() > 0) {
                            list.clear();
                            list.addAll(listPlaceSearch);
                            adapter.notifyDataSetChanged();
                        }
                        smoothProgressBar.progressiveStop();
                    }

                    @Override
                    public void onFailed(Throwable error) {
                        smoothProgressBar.progressiveStop();
                    }

                    @Override
                    public void onReservePlaceIdOK(Place place) {

                    }
                });
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Logger.debug(TAG, ">>>" + "Search");
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
