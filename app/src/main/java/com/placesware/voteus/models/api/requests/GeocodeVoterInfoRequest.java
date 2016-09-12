package com.placesware.voteus.models.api.requests;

import android.support.annotation.NonNull;

import com.placesware.voteus.models.VoterInfoResponse;

/**
 * Created by marcvandehey on 4/18/16.
 */
public class GeocodeVoterInfoRequest implements RequestType {
    private static final String TAG = GeocodeRequest.class.getSimpleName();

    private final VoterInfoResponse voterInfoResponse;
    private final String geocodeKey;

    public GeocodeVoterInfoRequest(@NonNull String geocodeKey, @NonNull VoterInfoResponse voterInfoResponse) {
        this.geocodeKey = geocodeKey;
        this.voterInfoResponse = voterInfoResponse;
    }

    public VoterInfoResponse getVoterInfoResponse() {
        return voterInfoResponse;
    }

    public String getGeocodeKey() {
        return geocodeKey;
    }

    @Override
    public String buildQueryString() {
        return "";
    }
}
