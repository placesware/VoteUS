package com.placesware.voteus;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.placesware.voteus.models.PollingLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

public class PollingLocationMapActivity extends FragmentActivity implements OnMapReadyCallback, SelectedModelFragment.OnFragmentInteractionListener {

    public static String TAG = "PollingLocationMap";

    private GoogleMap mMap;

    ArrayList<PollingLocation> pollingLocations;
    SlidingUpPanelLayout slidingUpPanelLayout;
    SlidingUpPanelLayout.PanelState panelState;
//    public FloatingActionButton directionsMiddleFab;
    public FloatingActionButton directionsLowerFab;

    FrameLayout rootParent;

//    int percentMiddleHeightDp;
    int percentLowerHeightDp;
    int percentWidthDp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polling_location_map_second);

        rootParent = (FrameLayout) findViewById(R.id.parent);
//        directionsMiddleFab = (FloatingActionButton) findViewById(R.id.directionsmiddlefab);
        directionsLowerFab = (FloatingActionButton) findViewById(R.id.directionsfab);

        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
//        percentMiddleHeightDp = new Double(dpHeight * .4f).intValue();
        percentLowerHeightDp = new Double(dpHeight * .1f).intValue();
        percentWidthDp = new Double(dpWidth * .05f).intValue();
//        setMargins(directionsMiddleFab, 0, 0, percentWidthDp, new Double(dpHeight).intValue()+new Double(dpHeight*.05).intValue());
//        setMargins(directionsLowerFab, 0, 0, percentWidthDp, percentLowerHeightDp - new Double(percentLowerHeightDp*.05).intValue());

        directionsLowerFab.hide();
//        directionsMiddleFab.hide();


        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setAnchorPoint(.4f);
//        slidingUpPanelLayout.setParallaxOffset(800);
        slidingUpPanelLayout.setPanelHeight(percentLowerHeightDp);

        retrieveValuesFromBundle(savedInstanceState);
        if (getIntent().getExtras().containsKey("pollingPlace")) {
            pollingLocations = getIntent().getExtras().getParcelableArrayList("pollingPlace");
        }

        // start fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (pollingLocations.size() == 1) {
            fragmentManager.beginTransaction().replace(R.id.slidecontainer, SelectedModelFragment.newInstance("test", pollingLocations.get(0)), SelectedModelFragment.TAG).commitNow();
        } else {
            fragmentManager.beginTransaction().replace(R.id.slidecontainer, SelectedModelFragment.newInstance("test", pollingLocations.get(0)), SelectedModelFragment.TAG).commitNow();
        }

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
                directionsLowerFab.hide();
//                directionsMiddleFab.hide();

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);

//                if(mMap != null){
//                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pollingLocations.get(0).location.lat, pollingLocations.get(0).location.lng), 12));
//
//                }
                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    directionsLowerFab.show();
                }
                if (newState == SlidingUpPanelLayout.PanelState.ANCHORED) {
//                    directionsMiddleFab.show();
                }


            }
        });
        slidingUpPanelLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });


        if (panelState == null) {
            panelState = SlidingUpPanelLayout.PanelState.COLLAPSED;
        }

        slidingUpPanelLayout.setPanelState(panelState);

        if (panelState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
            directionsLowerFab.show();
        } else {
//            directionsMiddleFab.show();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

//    private int computePanelTopPosition(float slideOffset) {
//        int mSlideRange = slidingUpPanelLayout.getMeasuredHeight() - slidingUpPanelLayout.getPanelHeight();
//        int slidingViewHeight = slidingUpPanelLayout != null ? slidingUpPanelLayout.getMeasuredHeight() : 0;
//        int slidePixelOffset = (int) (slideOffset * mSlideRange);
//        // Compute the top of the panel if its collapsed
//        return mIsSlidingUp
//                ? getMeasuredHeight() - getPaddingBottom() - mPanelHeight - slidePixelOffset
//                : getPaddingTop() - slidingViewHeight + mPanelHeight + slidePixelOffset;
//    }


    public void setPanelPeakHeight(int height) {
        slidingUpPanelLayout.setPanelHeight(height);
        setMargins(directionsLowerFab, 0, 0, percentWidthDp, height- new Double(height*.25).intValue());
        float middleY = slidingUpPanelLayout.getBottom() + slidingUpPanelLayout.getPanelHeight()/getApplicationContext().getResources().getDisplayMetrics().density;
//        setMargins(directionsMiddleFab, 0, 0, percentWidthDp, new Double(middleY).intValue()+new Double(middleY).intValue());

        slidingUpPanelLayout.setParallaxOffset(height);


    }

    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    public void getMapsLocationIntent(String lat, String lng){

        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?f=d&daddr="+lat +"," + lng));
        intent.setComponent(new ComponentName("com.google.android.apps.maps",
                "com.google.android.maps.MapsActivity"));
        startActivity(intent);
    }


    public void retrieveValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {

            if (savedInstanceState.containsKey("pollingLocation")) {
                pollingLocations = savedInstanceState.getParcelableArrayList("pollingPlace");
            }
            if (savedInstanceState.containsKey("panelState")) {
                String state = savedInstanceState.getString("panelState");
                if (SlidingUpPanelLayout.PanelState.HIDDEN.toString().equals(state)) {
                    panelState = SlidingUpPanelLayout.PanelState.HIDDEN;
                } else if (SlidingUpPanelLayout.PanelState.ANCHORED.toString().equals(state)) {
                    panelState = SlidingUpPanelLayout.PanelState.ANCHORED;
                } else if (SlidingUpPanelLayout.PanelState.COLLAPSED.toString().equals(state)) {
                    panelState = SlidingUpPanelLayout.PanelState.COLLAPSED;
                } else if (SlidingUpPanelLayout.PanelState.DRAGGING.toString().equals(state)) {
                    panelState = SlidingUpPanelLayout.PanelState.DRAGGING;
                } else if (SlidingUpPanelLayout.PanelState.EXPANDED.toString().equals(state)) {
                    panelState = SlidingUpPanelLayout.PanelState.EXPANDED;
                } else {
                    panelState = SlidingUpPanelLayout.PanelState.COLLAPSED;
                }


            }


        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


        for (PollingLocation p : pollingLocations) {
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(p.getDrawableDot());
            LatLng latLng = new LatLng(p.location.lat, p.location.lng);
            MarkerOptions options = new MarkerOptions().position(latLng);//.icon(icon);
            mMap.addMarker(options);
//            mMap.addMarker(new MarkerOptions()
//                    .position(new LatLng(p.location.lat, p.location.lng))
//                    .icon(icon)
//            );
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pollingLocations.get(0).location.lat, pollingLocations.get(0).location.lng), 12));


    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state

        if (slidingUpPanelLayout != null) {
            savedInstanceState.putString("panelState", slidingUpPanelLayout.getPanelState().toString());
        }
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onBackPressed() {
        if (slidingUpPanelLayout != null &&
                (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

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

}
