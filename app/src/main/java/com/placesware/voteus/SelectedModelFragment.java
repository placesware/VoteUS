package com.placesware.voteus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.demo.ergobot.civicusdemo.models.PollingLocation;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SelectedModelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectedModelFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static String PARENT_TAG = "PollingLocationMapActivity";

    public static final String TAG = "SelectedModelFragment";
    private static PollingLocation selectedModel;
    private String voterAddress;


    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventLocatorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelectedModelFragment newInstance(String param1, String param2) {
        SelectedModelFragment fragment = new SelectedModelFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static SelectedModelFragment newInstance(String param1, String param2, PollingLocation selectedModel) {
        SelectedModelFragment fragment = new SelectedModelFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SelectedModelFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            selectedModel = getArguments().getParcelable("pollingLocation");
            voterAddress = getArguments().getString("voterAddress");
        }
    }

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "1";

    // set the parent of the
    public static SelectedModelFragment newInstance(String param1) {
        SelectedModelFragment fragment = new SelectedModelFragment();
        Bundle args = new Bundle();
        PARENT_TAG = param1;
        args.putString(PARENT_TAG, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public static SelectedModelFragment newInstance(String voterAddress, PollingLocation pollingLocation) {
        if (pollingLocation == null) {
            throw new NullPointerException("SelectedModelFragment can't have null event");
        }
        SelectedModelFragment fragment = new SelectedModelFragment();
        Bundle args = new Bundle();
        args.putString("voterAddress", voterAddress);
        args.putParcelable("pollingLocation", pollingLocation);
        fragment.setArguments(args);
        return fragment;
    }

    public static SelectedModelFragment newInstance() {
        SelectedModelFragment fragment = new SelectedModelFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, -1);
        fragment.setArguments(args);
        return fragment;
    }

    // header
    TextView modelPollingName;
    TextView modelPollingTypeHeader;

    // middle buttons

    // Model information in rows
    TextView modelPollingAddressRow;
    public RelativeLayout header;
    Button shareButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_selected_model_expanded, container, false);
        // header
        modelPollingName = (TextView) rootView.findViewById(R.id.pollingname);
        modelPollingTypeHeader = (TextView) rootView.findViewById(R.id.pollingtype);

        header = (RelativeLayout)rootView.findViewById(R.id.headerlayout);

        header.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                header.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                header.getHeight(); //height is ready
                ((PollingLocationMapActivity)getActivity()).setPanelPeakHeight(header.getHeight());


            }
        });

        modelPollingAddressRow = (TextView) rootView.findViewById(R.id.pollingaddress);

        if (TextUtils.isEmpty(selectedModel.name)){
           modelPollingName.setText(selectedModel.address.locationName);
        } else {
            modelPollingName.setText(selectedModel.name);
        }

        modelPollingTypeHeader.setText(getString(selectedModel.getPollingTypeString()));


        // event information in rows
        if (selectedModel.location != null) {
            modelPollingAddressRow.setText(selectedModel.address.toString());
        } else {
            modelPollingAddressRow.setVisibility(View.GONE);
        }

        View.OnClickListener fabClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedModel.location != null){
                    String lat = String.valueOf(selectedModel.location.lat);
                    String lng = String.valueOf(selectedModel.location.lng);
                    ((PollingLocationMapActivity)getActivity()).getMapsLocationIntent(lat,lng);
                }
            }
        };

        ((PollingLocationMapActivity)getActivity()).directionsMiddleFab.setOnClickListener(null);
        ((PollingLocationMapActivity)getActivity()).directionsMiddleFab.setOnClickListener(fabClickListener);
        ((PollingLocationMapActivity)getActivity()).directionsLowerFab.setOnClickListener(null);
        ((PollingLocationMapActivity)getActivity()).directionsLowerFab.setOnClickListener(fabClickListener);


        shareButton = (Button)rootView.findViewById(R.id.sharebutton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                StringBuilder sb = new StringBuilder();
                sb.append("US General Election 2016\n");
                sb.append("Tuesday, November 8\n\n");
                sb.append("Voter Address:\n");
                sb.append(voterAddress);
                sb.append("\n\n");
                sb.append("Polling Place:\n");
                sb.append(selectedModel.address.toString());
                sb.append("\n");
                sb.append("http://maps.google.com/?q=" + selectedModel.location.lat + "," + selectedModel.location.lng);
                sb.append("by Placesware");

                Intent shareIntent = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(sb.toString())
                        .getIntent();
                if (shareIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(shareIntent);
                }
            }
        });

        return rootView;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;

        ((PollingLocationMapActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}