package son.nt.here.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionMenu;

import son.nt.here.R;
import son.nt.here.base.BaseFragment;
import son.nt.here.dto.MyPlaceDto;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailAddressFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailAddressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailAddressFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private MyPlaceDto mParam1;
    private String mParam2;

    private TextView txtFullAddress, txtStreet, txtLocality ,txtDistrict, txtCity, txtCountry, txtPostCode, txtLocation;
    private TextView tvSMS;

    private FloatingActionMenu fabMenu;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailAddressFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailAddressFragment newInstance(MyPlaceDto param1, String param2) {
        DetailAddressFragment fragment = new DetailAddressFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public DetailAddressFragment() {
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
        return inflater.inflate(R.layout.fragment_detail_address, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateData();
    }

    @Override
    public void initData() {

    }

    @Override
    public void initLayout(View view) {
        txtFullAddress = (TextView) view.findViewById(R.id.detail_txt_full_address);
        txtStreet = (TextView) view.findViewById(R.id.detail_txt_street);
        txtLocality = (TextView) view.findViewById(R.id.detail_txt_locality);
        txtDistrict = (TextView) view.findViewById(R.id.detail_txt_district);
        txtCity = (TextView) view.findViewById(R.id.detail_txt_city);
        txtCountry = (TextView) view.findViewById(R.id.detail_txt_country);
        txtPostCode = (TextView) view.findViewById(R.id.detail_txt_post_code);
        txtLocation = (TextView) view.findViewById(R.id.detail_txt_location);

        tvSMS = (TextView) view.findViewById(R.id.tvSMS);
        fabMenu = (FloatingActionMenu) view.findViewById(R.id.detail_fab_menu);
    }

    @Override
    public void initListener() {
        tvSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mParam1 == null) {
                    return;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(mParam1.formatted_address);
                stringBuilder.append("\n");
                String format = "http://maps.google.com/maps?q=%s,%s";
                String maps = String.format(format, String.valueOf(mParam1.lat), String.valueOf(mParam1.lng));
                stringBuilder.append(maps);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("sms:"));
                intent.putExtra("sms_body", stringBuilder.toString());
                startActivity(intent);
            }
        });

    }

    private void updateData () {
        if (mParam1 == null) {
            return;
        }
        txtFullAddress.setText(mParam1.formatted_address);
        txtStreet.setText(mParam1.street_number + " " + mParam1.streetName);
        txtLocality.setText(mParam1.subLv1);
        txtDistrict.setText(mParam1.district);
        txtCity.setText(mParam1.city);
        txtCountry.setText(mParam1.country);
        txtPostCode.setText(mParam1.postal_code);
        txtLocation.setText("" + mParam1.lat + "," + mParam1.lng);
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

}
