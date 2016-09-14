package com.placesware.voteus;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.placesware.voteus.models.CivicApiError;
import com.placesware.voteus.models.Election;
import com.placesware.voteus.models.PollingLocation;
import com.placesware.voteus.models.VoterInfoResponse;
import com.placesware.voteus.models.api.interactors.CivicInfoInteractor;
import com.placesware.voteus.models.api.interactors.ElectionsInteractor;
import com.placesware.voteus.models.api.interactors.GeocodeInteractor;
import com.placesware.voteus.models.api.requests.CivicInfoRequest;
import com.placesware.voteus.models.api.requests.ElectionsRequest;
import com.placesware.voteus.models.api.requests.GeocodeVoterInfoRequest;
import com.placesware.voteus.models.api.responses.ElectionsResponse;
import com.placesware.voteus.models.singletons.VoterInformation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.vision.text.Text;

import java.util.ArrayList;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

public class MainActivity extends AppCompatActivity implements CivicInfoInteractor.CivicInfoCallback, GeocodeInteractor.GeocodeCallback, ElectionsInteractor.ElectionsCallback, SelectedModelFragment.OnFragmentInteractionListener {

    public static String TAG = "MainActivity";
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;

    private VoterInfoResponse mVoterInfoResponse;
    String selectedAddress;
    String nameAddress;

    RelativeLayout rootLayout;

    //    PlaceAutocompleteFragment autocompleteFragment;
    private TextView mPlaceDetailsText;
    private TextView mPlaceHeaderText;
    private TextView mElectionsDateText;
//    private TextView mPlaceAttribution;

    Button openAutocompleteButton;
    Button searchButton;
    private AdView mAdView;
    MaterialDialog dialog;

    String[] SPINNERLIST = {"Available Election"};

    MaterialBetterSpinner materialDesignSpinner;
    int selectedIndex = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mElectionsDateText = (TextView) findViewById(R.id.electiondate);

        materialDesignSpinner = (MaterialBetterSpinner) findViewById(R.id.android_material_design_spinner);
        materialDesignSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = adapterView.getItemAtPosition(i).toString();
                selectedIndex = i;
                for (Election election : elections) {
                    if (election.getName().equals(selected) && !selected.equals(SPINNERLIST[0])) {
                        mElectionsDateText.setVisibility(View.VISIBLE);
                        mElectionsDateText.setText(election.getFormattedDate());
                        openAutocompleteButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        rootLayout = (RelativeLayout) findViewById(R.id.rootlayout);

        restoreFromBundle(savedInstanceState);


//        input = (EditText)findViewById(R.id.autocomplete_fragment);

//        autocompleteFragment = (PlaceAutocompleteFragment)
//                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

/*
* The following code example shows setting an AutocompleteFilter on a PlaceAutocompleteFragment to
* set a filter returning only results with a precise address.
*/
//        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
//                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
//                .build();
//        autocompleteFragment.setFilter(typeFilter);

//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                 TODO: Get info about the selected place.
//                selectedAddress = place.getAddress().toString();
//                Log.i(TAG, "Place: " + place.getName());//get place details here
//            }
//
//            @Override
//            public void onError(Status status) {
//                 TODO: Handle the error.
//                Log.i(TAG, "An error occurred: " + status);
//            }
//        });


        // Open the autocomplete activity when the button is clicked.
        openAutocompleteButton = (Button) findViewById(R.id.open_button);
        openAutocompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAutocompleteActivity();
            }
        });

        // Retrieve the TextViews that will display details about the selected place.
        mPlaceHeaderText = (TextView) findViewById(R.id.place_header);
        mPlaceDetailsText = (TextView) findViewById(R.id.place_details);
//        mPlaceAttribution = (TextView) findViewById(R.id.place_attribution);

        searchButton = (Button) findViewById(R.id.getinfobutton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (TextUtils.isEmpty(selectedAddress)) {
                    Toast.makeText(MainActivity.this, "Please enter an address.", Toast.LENGTH_SHORT).show();
                } else {
                    dialog = new MaterialDialog.Builder(MainActivity.this)
                            .title(R.string.progress_dialog)
                            .content(R.string.please_wait)
                            .progress(true, 0)
                            .show();

                    materialDesignSpinner.getText().toString();


                    String electionId = "";

                    for (Election election : elections) {
                        if (materialDesignSpinner.getText().toString().equals(election.getName())) {
                            electionId = election.getId();
                        }
                    }

                    CivicInfoInteractor mCivicInteractor = new CivicInfoInteractor();
                    CivicInfoRequest request = new CivicInfoRequest(getApplicationContext(), electionId, selectedAddress);
                    mCivicInteractor.enqueueRequest(request, MainActivity.this);
                }

            }
        });


        if (elections == null) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, SPINNERLIST);
            materialDesignSpinner.setAdapter(arrayAdapter);

            mElectionsDateText.setVisibility(View.GONE);
            mPlaceDetailsText.setVisibility(View.GONE);
            searchButton.setVisibility(View.GONE);
            openAutocompleteButton.setVisibility(View.GONE);
            requestElections();
        } else if (elections.size() == 0) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, SPINNERLIST);
            materialDesignSpinner.setAdapter(arrayAdapter);
            mElectionsDateText.setVisibility(View.GONE);
            mPlaceDetailsText.setVisibility(View.GONE);
            searchButton.setVisibility(View.GONE);
            openAutocompleteButton.setVisibility(View.GONE);
            requestElections();
        } else {

            ArrayList<String> electionNames = new ArrayList<String>();
            for (Election election : elections) {
                electionNames.add(election.getName());
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, electionNames);
            materialDesignSpinner.setAdapter(arrayAdapter);

            if (selectedIndex != -1) {
                materialDesignSpinner.setSelection(selectedIndex);
            }


        }

        // there is no place set when inflating the ui
        if (TextUtils.isEmpty(nameAddress)) {
            mPlaceHeaderText.setVisibility(View.GONE);
            mPlaceDetailsText.setVisibility(View.GONE);
            searchButton.setVisibility(View.GONE);
        } else {
            mPlaceHeaderText.setVisibility(View.VISIBLE);
            mPlaceDetailsText.setVisibility(View.VISIBLE);
            mPlaceDetailsText.setText(nameAddress);
            searchButton.setVisibility(View.VISIBLE);
        }


    }

    public void requestElections() {
        ElectionsInteractor electionsInteractor = new ElectionsInteractor();
        ElectionsRequest request = new ElectionsRequest(getApplicationContext());
        electionsInteractor.enqueueRequest(request, MainActivity.this);
    }

    public void restoreFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            selectedAddress = savedInstanceState.getString("selectedAddress");

            nameAddress = savedInstanceState.getString("nameAddress");

            if (savedInstanceState.containsKey("elections")) {
                elections = savedInstanceState.getParcelableArrayList("elections");
                selectedIndex = savedInstanceState.getInt("selectedIndex", -1);

            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state

        if (selectedAddress != null) {
            savedInstanceState.putString("selectedAddress", selectedAddress);
            if (mPlaceDetailsText.getVisibility() == View.VISIBLE && !TextUtils.isEmpty(mPlaceDetailsText.getText())) {
                savedInstanceState.putString("nameAddress", mPlaceDetailsText.getText().toString());
            }
        }

        if (mVoterInfoResponse != null) {
            savedInstanceState.putParcelableArrayList("pollingLocations", mVoterInfoResponse.getPollingLocations());
        }

        if (elections != null) {
            savedInstanceState.putParcelableArrayList("elections", elections);
            savedInstanceState.putString("selectedElection", materialDesignSpinner.getText().toString());
        }

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void showEmptyGeneralElectionDialog() {
        dialog = new MaterialDialog.Builder(MainActivity.this)
                .title("General Elections 2016")
                .content("Data for the General Election 2016 isn't available until 3-4 weeks before the election.  Please try again later.")
                .positiveText("Okay")
                .show();
    }


    @Override
    public void civicInfoResponse(VoterInfoResponse response) {

        String electionId = "";
        for (Election election : elections) {
            if (materialDesignSpinner.getText().toString().equals(election.getName())) {
                electionId = election.getId();
            }
        }
        if (response != null) {


            if (response.isSuccessful()) {
                mVoterInfoResponse = response;

                VoterInformation.updateWithVoterInfoResponse(mVoterInfoResponse);

                //If this succeeds, it is assumed Play services is available for the rest of the app
                GoogleApiAvailability api = GoogleApiAvailability.getInstance();
                int code = api.isGooglePlayServicesAvailable(getApplicationContext());
                if (code == ConnectionResult.SUCCESS) {
                    //Start loading up location overhead data

                    try {
                        mVoterInfoResponse.setUpLocations();
                    } catch (Exception ex) {
                        Log.e("ERR", ex.toString());
                    }
                    GeocodeInteractor interactor = new GeocodeInteractor();
                    //TODO use key here when it is hooked up correctly
                    GeocodeVoterInfoRequest request = new GeocodeVoterInfoRequest(getString(R.string.google_api_browser_key), mVoterInfoResponse);

                    interactor.enqueueRequest(request, this);
                } else {
                    Log.e(TAG, "Play Services Unavailable");

                    if ("5000".equals(electionId)) {
                        showEmptyGeneralElectionDialog();
                    } else {
                        Snackbar.make(rootLayout, "No voting sites found", Snackbar.LENGTH_SHORT).show();
                    }
//                    updateViewWithVoterInfo();

//                    getView().showMessage(R.string.locations_map_error_play_services_unavailable);
                }
            } else {
//                getView().hideGoButton();
                CivicApiError error = response.getError();

                CivicApiError.Error error1 = error.errors.get(0);

                Log.e(TAG, "Civic API returned error: " + error.code + ": " +
                        error.message + " : " + error1.domain + " : " + error1.reason);

                if (CivicApiError.errorMessages.get(error1.reason) != null) {
                    int reason = CivicApiError.errorMessages.get(error1.reason);
//                    getView().showMessage(reason);
                } else {
                    Log.e(TAG, "Unknown API error reason: " + error1.reason);
//                    getView().showMessage(R.string.fragment_home_error_unknown);
                }
                dialog.dismiss();
                if ("5000".equals(electionId)) {
                    showEmptyGeneralElectionDialog();
                } else {
                    Snackbar.make(rootLayout, "No voting sites found", Snackbar.LENGTH_SHORT).show();
                }

            }
        } else {
            Log.d(TAG, "API returned null response");

            dialog.dismiss();
            if ("5000".equals(electionId)) {
                showEmptyGeneralElectionDialog();
            } else {
                Snackbar.make(rootLayout, "No voting sites found", Snackbar.LENGTH_SHORT).show();
            }
//            getView().showMessage(R.string.fragment_home_error_unknown);
        }

//        cancelSearch();
    }


//    public void getInfo(View view){
//
//        CivicInfoInteractor mCivicInteractor = new CivicInfoInteractor();
//        CivicInfoRequest request = new CivicInfoRequest(getApplicationContext(), "2000", "8608 Holly Ln Urbandale, IA 50322");
//        mCivicInteractor.enqueueRequest(request, MainActivity.this);
//    }

    @Override
    public void onGeocodeResults(VoterInfoResponse voterInfoResponse) {
        mVoterInfoResponse = voterInfoResponse;

        Bundle bundle = new Bundle();
        if (mVoterInfoResponse != null) {

            if (mVoterInfoResponse.getPollingLocations().size() > 0) {
                dialog.dismiss();
                bundle.putParcelableArrayList("pollingPlace", mVoterInfoResponse.getAllLocations());

                Intent intent = new Intent(MainActivity.this, PollingLocationMapActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                dialog.dismiss();

                String electionId = "";
                for (Election election : elections) {
                    if (materialDesignSpinner.getText().toString().equals(election.getName())) {
                        electionId = election.getId();
                    }
                }

                //No voting sites found
                if ("5000".equals(electionId)) {
                    showEmptyGeneralElectionDialog();
                } else {
                    Snackbar.make(rootLayout, "No voting sites found", Snackbar.LENGTH_SHORT).show();
                }
            }

        } else {

            ArrayList<PollingLocation> pollingLocations = new ArrayList<PollingLocation>();
        }
    }


    /*
    PlacePicker logic
     */
    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place Selected: " + place.getName());

//                // Format the place's details and display them in the TextView.
//                mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
//                        place.getId(), place.getAddress(), place.getPhoneNumber(),
//                        place.getWebsiteUri()));
//
//
//
//                // Display attributions if required.
//                CharSequence attributions = place.getAttributions();
//                if (!TextUtils.isEmpty(attributions)) {
//                    mPlaceAttribution.setText(Html.fromHtml(attributions.toString()));
//                } else {
//                    mPlaceAttribution.setText("");
//                }

                selectedAddress = place.getAddress().toString();
                mPlaceHeaderText.setVisibility(View.VISIBLE);
                mPlaceDetailsText.setVisibility(View.VISIBLE);
                mPlaceDetailsText.setText(place.getName());
                searchButton.setVisibility(View.VISIBLE);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                mPlaceDetailsText.setText("There was an error, please try again");

                Log.e(TAG, "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if
                // the user pressed the back button.
            }
        }
    }

    /**
     * Helper method to format information about a place nicely.
     */
//    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
//                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
//        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
//                websiteUri));
//        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
//                websiteUri));
//
//    }



    /*
    Required for fragments
     */
    public void onSectionAttached(int number) {

        switch (number) {
            case 1:
                // something
                // mTitle = getString(R.string.title_accounts);
                break;
            case 2:
                // something
                // mTitle = getString(R.string.title_sendmonty);
                break;
            case 3:
                // dark side
                // mTitle = getString(R.string.title_managepayments);
                break;
            case 4:
                // stormtropper..
                // mTitle = getString(R.string.title_stormtropper);
                break;
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // this.onSectionAttached(position);
    }


    ArrayList<Election> elections = null;

    @Override
    public void electionsResponse(ElectionsResponse response) {
        // here
        if (response != null) {
            ArrayList<String> electionNames = new ArrayList<String>();
            elections = response.getElections();
            for (Election election : elections) {
                if(!"2000".equals(election.getId())){
                    electionNames.add(election.getName());
                }
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, electionNames);
            materialDesignSpinner.setAdapter(arrayAdapter);
        }

    }
}
