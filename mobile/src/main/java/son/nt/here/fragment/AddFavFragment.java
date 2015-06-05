package son.nt.here.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import son.nt.here.R;
import son.nt.here.ResourceManager;
import son.nt.here.adapter.AddFavAdapter;
import son.nt.here.base.BaseFragment;
import son.nt.here.dto.MyPlaceDto;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddFavFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddFavFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFavFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private MyPlaceDto placeDto;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView txtAddress;
    private EditText txtTitle;
    private EditText txtNotes;
    private RecyclerView recyclerView;
    private AddFavAdapter adapter;
    private Button btnSubmit;
    private List<String> listImages;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFavFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFavFragment newInstance(MyPlaceDto param1, String param2) {
        AddFavFragment fragment = new AddFavFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AddFavFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            placeDto = (MyPlaceDto) getArguments().getSerializable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_fav, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateData();
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
        listImages = new ArrayList<>();
//        listImages.add("https://lh6.ggpht.com/5HmHU2cE12jLB1NSX9blKNVa_dj_ymh_FIzajC6joVd4jYBopGQFj5ZFHr9FboHFyQ=w300-rw");
//        listImages.add("http://media.ngoisao.vn/resize_580x1100/news/2015/05/29/hot-girl-malaysia-xinh-dep-khien-cu-dan-mang-dien-dao-1-ngoisao.vn.jpg");
//        listImages.add("http://media.ngoisao.vn/resize_580x1100/news/2015/05/29/hot-girl-malaysia-xinh-dep-khien-cu-dan-mang-dien-dao-3-ngoisao.vn.jpg");
//        listImages.add("http://stn.depplus.vn/NewsMedia/assets/image_20150509/MO11.jpeg");
//        listImages.add("http://media.ngoisao.vn/resize_580x1100/news/2015/05/29/hot-girl-malaysia-xinh-dep-khien-cu-dan-mang-dien-dao-7-ngoisao.vn.jpg");

        listImages.add("Add");


    }

    @Override
    public void initLayout(View view) {
        txtAddress = (TextView) view.findViewById(R.id.add_txt_address);
        recyclerView = (RecyclerView) view.findViewById(R.id.add_rv);
        recyclerView.hasFixedSize();
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(llm);
        adapter = new AddFavAdapter(getActivity(), listImages);
        recyclerView.setAdapter(adapter);

        txtTitle = (EditText) view.findViewById(R.id.add_txt_title);
        txtNotes = (EditText) view.findViewById(R.id.add_txt_notes);
        btnSubmit = (Button) view.findViewById(R.id.add_btn_submit);
        btnSubmit.setEnabled(false);

    }

    @Override
    public void initListener() {
        txtTitle.addTextChangedListener(textWatcher);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeDto.favTitle = txtTitle.getText().toString().trim();
                placeDto.favNotes = txtNotes.getText().toString().trim();
                placeDto.favUpdateTime = System.currentTimeMillis();
                if (ResourceManager.getInstance().getData().insertData(placeDto)) {
                        Toast.makeText(getActivity(), "Successful to add favourite:" + placeDto.formatted_address, Toast.LENGTH_SHORT).show();
                    }
            }
        });

    }

    private void updateData() {
        txtAddress.setText(placeDto.formatted_address);
    }

    final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(final CharSequence s,
                                      final int start, final int count,
                                      final int after) {

        }

        @Override
        public void onTextChanged(final CharSequence s,
                                  final int start, final int before,
                                  final int count) {

        }

        @Override
        public void afterTextChanged(final Editable s) {

            btnSubmit
                    .setEnabled(s != null
                            && s.toString()
                            .trim()
                            .length() > 0);
        }
    };
}
