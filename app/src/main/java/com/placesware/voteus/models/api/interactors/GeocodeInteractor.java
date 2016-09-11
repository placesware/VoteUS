package com.placesware.voteus.models.api.interactors;

import android.util.Log;

import com.demo.ergobot.civicusdemo.models.CivicApiAddress;
import com.demo.ergobot.civicusdemo.models.ElectionAdministrationBody;
import com.demo.ergobot.civicusdemo.models.GoogleDirections.Location;
import com.demo.ergobot.civicusdemo.models.PollingLocation;
import com.demo.ergobot.civicusdemo.models.VoterInfoResponse;
import com.demo.ergobot.civicusdemo.models.api.requests.GeocodeRequest;
import com.demo.ergobot.civicusdemo.models.api.requests.GeocodeVoterInfoRequest;
import com.demo.ergobot.civicusdemo.models.api.requests.RequestType;
import com.demo.ergobot.civicusdemo.models.geocode.GeocodeLocationResult;
import com.demo.ergobot.civicusdemo.models.geocode.Result;
import com.demo.ergobot.civicusdemo.models.singletons.VoterInformation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//import com.demo.ergobot.civicusdemo.models.CivicApiAddress;
//import com.demo.ergobot.civicusdemo.models.ElectionAdministrationBody;
//import com.demo.ergobot.civicusdemo.models.GoogleDirections.Location;
//import com.demo.ergobot.civicusdemo.models.PollingLocation;
//import com.demo.ergobot.civicusdemo.models.VoterInfoResponse;
//import com.demo.ergobot.civicusdemo.models.api.requests.GeocodeRequest;
//import com.demo.ergobot.civicusdemo.models.api.requests.GeocodeVoterInfoRequest;
//import com.demo.ergobot.civicusdemo.models.api.requests.RequestType;
//import com.demo.ergobot.civicusdemo.models.geocode.GeocodeLocationResult;
//import com.demo.ergobot.civicusdemo.models.geocode.Result;
//import com.demo.ergobot.civicusdemo.models.singletons.VoterInformation;

/**
 * Created by marcvandehey on 4/15/16.
 */
public class GeocodeInteractor extends BaseInteractor<GeocodeVoterInfoRequest, GeocodeInteractor.GeocodeCallback> {
    private static final String TAG = GeocodeInteractor.class.getSimpleName();
    private final static float MILES_IN_METER = 0.000621371192f;
    private final static float KILOMETERS_IN_METER = 0.001f;

    @Override
    protected GeocodeVoterInfoRequest doInBackground(RequestType... params) {
        GeocodeVoterInfoRequest geocodeVoterInfoRequest = null;
        if (params.length > 0 && params[0] instanceof GeocodeVoterInfoRequest) {
            geocodeVoterInfoRequest = (GeocodeVoterInfoRequest) params[0];

            VoterInfoResponse voterInfoResponse = geocodeVoterInfoRequest.getVoterInfoResponse();

            if (voterInfoResponse.normalizedInput != null) {
//                OkHttpClient client = new OkHttpClient();
//                Gson gson = new GsonBuilder().create();
//
//                GeocodeRequest homeGeocodeRequest = new GeocodeRequest(geocodeVoterInfoRequest.getGeocodeKey(), voterInfoResponse.normalizedInput.toGeocodeString());

//                OkHttpClient.Builder clientBuilder = client.newBuilder().readTimeout(10, TimeUnit.SECONDS);
//
//
//                final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
//                    @Override
//                    public X509Certificate[] getAcceptedIssuers() {
//                        X509Certificate[] cArrr = new X509Certificate[0];
//                        return cArrr;
//                    }
//
//                    @Override
//                    public void checkServerTrusted(final X509Certificate[] chain,
//                                                   final String authType) throws CertificateException {
//                    }
//
//                    @Override
//                    public void checkClientTrusted(final X509Certificate[] chain,
//                                                   final String authType) throws CertificateException {
//                    }
//                }};
//
//                SSLContext sslContext = null;
//                try {
//                    sslContext = SSLContext.getInstance("SSL");
//                } catch (NoSuchAlgorithmException e) {
//                    e.printStackTrace();
//                }
//
//                try {
//                    sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
//                } catch (KeyManagementException e) {
//                    e.printStackTrace();
//                }
//                clientBuilder.sslSocketFactory(sslContext.getSocketFactory());
//
//                HostnameVerifier hostnameVerifier = new HostnameVerifier() {
//                    @Override
//                    public boolean verify(String hostname, SSLSession session) {
//                        Log.d(TAG, "Trust Host :" + hostname);
//                        return true;
//                    }
//                };
//                clientBuilder.hostnameVerifier(hostnameVerifier);
//
//                Request homeAddressRequest = new Request.Builder().url(homeGeocodeRequest.buildQueryString()).build();
//
//
//                GeocodeLocationResult homeAddressResponse;

                OkHttpClient client = new OkHttpClient();
                Gson gson = new GsonBuilder().create();

                GeocodeRequest homeGeocodeRequest = new GeocodeRequest(geocodeVoterInfoRequest.getGeocodeKey(), voterInfoResponse.normalizedInput.toGeocodeString());

                Request homeAddressRequest = new Request.Builder().url(homeGeocodeRequest.buildQueryString()).build();
                GeocodeLocationResult homeAddressResponse;


                try {

//                    Response okHttpResponse = (clientBuilder.build().newCall(homeAddressRequest)).execute();
//                    homeAddressResponse = gson.fromJson(okHttpResponse.body().string(), GeocodeLocationResult.class);

                    Response okHttpResponse = client.newCall(homeAddressRequest).execute();
                    homeAddressResponse = gson.fromJson(okHttpResponse.body().string(), GeocodeLocationResult.class);


                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Unexpected error in Geocoding Home Location");
                    homeAddressResponse = new GeocodeLocationResult();
                }

                Location homeLocation = null;

                if (!homeAddressResponse.getResults().isEmpty()) {
                    Result homeGeocode = homeAddressResponse.getResults().get(0);

                    homeLocation = homeGeocode.getGeometry().getLocation();
                }

                if (homeLocation == null) {
                    //Error occurred with getting home location, cannot proceed
                    return geocodeVoterInfoRequest;
                }

                //TODO pass use metric in correctly
                ArrayList<PollingLocation> geocodedPollingLocations = getGeocodedLocationForList(client, gson, geocodeVoterInfoRequest.getGeocodeKey(), voterInfoResponse.getPollingLocations(), homeLocation, VoterInformation.useMetric());
                ArrayList<PollingLocation> geocodedEarlyVotingLocations = getGeocodedLocationForList(client, gson, geocodeVoterInfoRequest.getGeocodeKey(), voterInfoResponse.getOpenEarlyVoteSites(), homeLocation, VoterInformation.useMetric());
                ArrayList<PollingLocation> geocodedDropBoxLocations = getGeocodedLocationForList(client, gson, geocodeVoterInfoRequest.getGeocodeKey(), voterInfoResponse.getOpenDropOffLocations(), homeLocation, VoterInformation.useMetric());

                CivicApiAddress stateAdminAddress = voterInfoResponse.getAdminAddress(ElectionAdministrationBody.AdminBody.STATE);

                if (stateAdminAddress != null) {
                    Location stateAdminLocation = getGeocodedLocation(client, gson, geocodeVoterInfoRequest.getGeocodeKey(), stateAdminAddress);
                    float stateAdminDistance = getDistance(homeLocation, stateAdminLocation, VoterInformation.useMetric());

                    stateAdminAddress.latitude = stateAdminLocation.lat;
                    stateAdminAddress.longitude = stateAdminLocation.lng;
                    stateAdminAddress.distance = stateAdminDistance;
                }

                //Set up Local Administration Body
                ElectionAdministrationBody localAdministrationBody = voterInfoResponse.getLocalAdmin();

                if (localAdministrationBody != null) {
                    CivicApiAddress localPhysicalAddress = getLocationForApiAddress(localAdministrationBody.getPhysicalAddress(), client, gson, geocodeVoterInfoRequest.getGeocodeKey(), homeLocation);
                    CivicApiAddress localCorrespondenceAddress = getLocationForApiAddress(localAdministrationBody.getCorrespondenceAddress(), client, gson, geocodeVoterInfoRequest.getGeocodeKey(), homeLocation);

                    localAdministrationBody.setPhysicalAddress(localPhysicalAddress);
                    localAdministrationBody.setCorrespondenceAddress(localCorrespondenceAddress);

                    VoterInformation.setLocalAdministrationBody(localAdministrationBody);
                }

                //Setup State Administration Body
                ElectionAdministrationBody stateAdministrationBody = voterInfoResponse.getStateAdmin();

                if (stateAdministrationBody != null) {
                    CivicApiAddress statePhysicalAddress = getLocationForApiAddress(stateAdministrationBody.getPhysicalAddress(), client, gson, geocodeVoterInfoRequest.getGeocodeKey(), homeLocation);
                    CivicApiAddress stateCorrespondenceAddress = getLocationForApiAddress(stateAdministrationBody.getCorrespondenceAddress(), client, gson, geocodeVoterInfoRequest.getGeocodeKey(), homeLocation);

                    stateAdministrationBody.setPhysicalAddress(statePhysicalAddress);
                    stateAdministrationBody.setCorrespondenceAddress(stateCorrespondenceAddress);

                    VoterInformation.setStateAdministrationBody(stateAdministrationBody);
                }

                //Setup Home address
                CivicApiAddress homeAddress = voterInfoResponse.normalizedInput;
                homeAddress.latitude = homeLocation.lat;
                homeAddress.longitude = homeLocation.lng;

                VoterInformation.setHomeAddress(homeAddress);

                //Polling Locations
                VoterInformation.setPollingLocations(geocodedPollingLocations, geocodedEarlyVotingLocations, geocodedDropBoxLocations);
            }
        }

        return geocodeVoterInfoRequest;
    }

    /**
     * Helper Function to Geocode a Civic Api Address and update its latitude, longitude and distance from Home
     *
     * @param civicApiAddress
     * @param client
     * @param gson
     * @param key
     * @param homeLocation
     * @return
     */
    private CivicApiAddress getLocationForApiAddress(CivicApiAddress civicApiAddress, OkHttpClient client, Gson gson, String key, Location homeLocation) {
        if (civicApiAddress != null) {
            Location location = getGeocodedLocation(client, gson, key, civicApiAddress);

            float localAdminDistance = getDistance(homeLocation, location, VoterInformation.useMetric());

            civicApiAddress.latitude = location.lat;
            civicApiAddress.longitude = location.lng;
            civicApiAddress.distance = localAdminDistance;
        }

        return civicApiAddress;
    }

    private ArrayList<PollingLocation> getGeocodedLocationForList(OkHttpClient client, Gson gson, String geocodeKey, ArrayList<PollingLocation> locations, Location homeLocation, boolean useMetric) {
        android.location.Location home = new android.location.Location("home");
        home.setLatitude(homeLocation.lat);
        home.setLongitude(homeLocation.lng);
        ArrayList<PollingLocation> geocodedPollingLocations = new ArrayList<>();

        //Loop through list and geocode address
        for (PollingLocation pollingLocation : locations) {
            Location foundLocation = getGeocodedLocation(client, gson, geocodeKey, pollingLocation.address);
            float distance = getDistance(homeLocation, foundLocation, useMetric);

            pollingLocation.location = foundLocation;
            pollingLocation.distance = distance;

            //Add locations even if there is a geocode failure
            geocodedPollingLocations.add(pollingLocation);
        }

        return geocodedPollingLocations;
    }

    private Location getGeocodedLocation(OkHttpClient client, Gson gson, String geocodeKey, CivicApiAddress address) {
        //Setup temp home location to calculate distance
        Location location = new Location();

        if (address != null) {
            GeocodeRequest pollingLocationGeocodeRequest = new GeocodeRequest(geocodeKey, address.toGeocodeString());

            Request pollingAddressRequest = new Request.Builder().url(pollingLocationGeocodeRequest.buildQueryString()).build();
            GeocodeLocationResult pollingAddressResult = null;
            /*
                OkHttpClient.Builder clientBuilder = client.newBuilder().readTimeout(10, TimeUnit.SECONDS);


                final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        X509Certificate[] cArrr = new X509Certificate[0];
                        return cArrr;
                    }

                    @Override
                    public void checkServerTrusted(final X509Certificate[] chain,
                                                   final String authType) throws CertificateException {
                    }

                    @Override
                    public void checkClientTrusted(final X509Certificate[] chain,
                                                   final String authType) throws CertificateException {
                    }
                }};

                SSLContext sslContext = null;
                try {
                    sslContext = SSLContext.getInstance("SSL");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                try {
                    sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                }
                clientBuilder.sslSocketFactory(sslContext.getSocketFactory());

                HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        Log.d(TAG, "Trust Host :" + hostname);
                        return true;
                    }
                };

                clientBuilder.hostnameVerifier(hostnameVerifier);
                */

//                GeocodeRequest pollingLocationGeocodeRequest = new GeocodeRequest(geocodeKey, address.toGeocodeString());
//
//                Request pollingAddressRequest = new Request.Builder().url(pollingLocationGeocodeRequest.buildQueryString()).build();
//                GeocodeLocationResult pollingAddressResult = null;


                try {
//                    Response okHttpResponse = (clientBuilder.build().newCall(pollingAddressRequest)).execute();//client.newCall(pollingAddressRequest).execute();
//                    pollingAddressResult = gson.fromJson(okHttpResponse.body().string(), GeocodeLocationResult.class);

                    Response okHttpResponse = client.newCall(pollingAddressRequest).execute();
                    pollingAddressResult = gson.fromJson(okHttpResponse.body().string(), GeocodeLocationResult.class);


                    if (pollingAddressResult.getResults() != null && !pollingAddressResult.getResults().isEmpty()) {
                        location = pollingAddressResult.getResults().get(0).getGeometry().getLocation();

                        Log.v(TAG, "Success! - " + address.locationName);
                    } else {
                        Log.e(TAG, "No result Found in polling location: " + address.locationName);
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    Log.e(TAG, "Unexpected error in geocoding polling location: " + address.locationName);
                }
            }

            return location;
        }

    private float getDistance(Location homeLocation, Location otherLocation, boolean useMetric) {
        //Create a temp Android Location to calculate distance
        android.location.Location tempLocation = new android.location.Location("polling");
        tempLocation.setLatitude(otherLocation.lat);
        tempLocation.setLongitude(otherLocation.lng);

        android.location.Location home = new android.location.Location("home");
        home.setLatitude(homeLocation.lat);
        home.setLongitude(homeLocation.lng);

        if (homeLocation.lat == 0 && otherLocation.lat == 0) {
            //Error occurred with location. Set as -1
            return -1;
        } else if (useMetric) {
            // convert meters to kilometers
            return home.distanceTo(tempLocation) * KILOMETERS_IN_METER;
        } else {
            // convert result from meters to miles
            return home.distanceTo(tempLocation) * MILES_IN_METER;
        }
    }

    @Override
    protected void onPostExecute(GeocodeVoterInfoRequest geocodeVoterInfoRequest) {
        super.onPostExecute(geocodeVoterInfoRequest);

        if (getCallback() != null) {
            getCallback().onGeocodeResults(geocodeVoterInfoRequest.getVoterInfoResponse());
        }
    }

    public interface GeocodeCallback {
        void onGeocodeResults(VoterInfoResponse voterInfoResponse);
    }
}
