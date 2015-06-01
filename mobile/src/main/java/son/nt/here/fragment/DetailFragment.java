package son.nt.here.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.List;

import son.nt.here.R;
import son.nt.here.adapter.DetailAdapter;
import son.nt.here.base.BaseFragment;
import son.nt.here.db.MyData;
import son.nt.here.dto.DisplayDto;
import son.nt.here.dto.FavDto;
import son.nt.here.dto.MyPlaceDto;
import son.nt.here.utils.Logger;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends BaseFragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "DetailFragment";

    private MyPlaceDto mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailFragment.
     */

    private RecyclerView recyclerView;
    private DetailAdapter adapter;
    private List<DisplayDto> mList;
    private FloatingActionMenu fab;
    private FloatingActionButton fabMaps;
    private FloatingActionButton fabSms;
    private FloatingActionButton fabFav;
    private FloatingActionButton fabMail;

    private MyData db;

    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(MyPlaceDto param1, String param2) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (MyPlaceDto) getArguments().getSerializable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_location, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    }

    @Override
    public void initData() {

        db = new MyData(getActivity());
        mList = new ArrayList<>();
        if (mParam1 == null) {
            return;
        }
        mList.add(new DisplayDto("Address", mParam1.formatted_address));
        mList.add(new DisplayDto("Street", mParam1.street_number + " " + mParam1.streetName));
        mList.add(new DisplayDto("Locality", mParam1.subLv1));
        mList.add(new DisplayDto("District", mParam1.district));
        mList.add(new DisplayDto("City", mParam1.city));
        mList.add(new DisplayDto("Country", mParam1.country));
        mList.add(new DisplayDto("Postcode", mParam1.postal_code));
        mList.add(new DisplayDto("Location", "" + mParam1.lat + "," + mParam1.lng));
    }

    @Override
    public void initLayout(View view) {

        fab = (FloatingActionMenu) view.findViewById(R.id.detail_fab_menu);
        fabMaps = (FloatingActionButton) view.findViewById(R.id.menu_google_maps);
        fabFav = (FloatingActionButton) view.findViewById(R.id.menu_fav);
        fabMail = (FloatingActionButton) view.findViewById(R.id.menu_email);
        fabSms = (FloatingActionButton) view.findViewById(R.id.menu_sms);
        recyclerView = (RecyclerView) view.findViewById(R.id.detail_rv);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);
        adapter = new DetailAdapter(getActivity(), mList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void initListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Logger.debug(TAG, ">>>" + "dy:" + dy);
                if (dy > 0) {
                    fab.hideMenuButton(true);
                } else {
                    fab.showMenuButton(true);
                }
            }
        });

        fabMaps.setOnClickListener(this);
        fabFav.setOnClickListener(this);
        fabMail.setOnClickListener(this);
        fabSms.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_google_maps:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("geo:").append(mParam1.lat).append(",").append(mParam1.lng);
                stringBuilder.append("?q=").append(mParam1.lat).append(",").append(mParam1.lng)
                        .append("(").append(mParam1.formatted_address).append(")")
                ;
                Uri gmmIntentUri = Uri.parse(stringBuilder.toString());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                startActivity(mapIntent);
                break;

            case R.id.menu_sms:
                sendSms();
                break;
            case R.id.menu_email:
                sendEmail();
                break;
            case R.id.menu_fav:
                if (mParam1 != null) {
                    FavDto favDto = new FavDto();
                    favDto.favTitle = "this is title";
                    favDto.favNotes = "this is notes";
                    favDto.formatted_address = mParam1.formatted_address;
                    if (db.insertData(favDto)) {
                        Toast.makeText(getActivity(), "Successful to add favourite:" + mParam1.formatted_address, Toast.LENGTH_SHORT).show();

                    }
                }
                break;

        }
    }

    private void sendSms() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("sms:"));
        intent.putExtra("sms_body", getAddressAndMaps());
        startActivity(intent);
    }

    private void sendEmail() {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_SUBJECT, "I am Here");
        email.putExtra(Intent.EXTRA_TEXT, getAddressAndMaps());
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "Choose an Email client :"));
    }

    private String getAddressAndMaps() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mParam1.formatted_address);
        stringBuilder.append("\n");
        String format = "http://maps.google.com/maps?q=%s,%s";
        String maps = String.format(format, String.valueOf(mParam1.lat), String.valueOf(mParam1.lng));
        stringBuilder.append(maps);
        return stringBuilder.toString();
    }
}
