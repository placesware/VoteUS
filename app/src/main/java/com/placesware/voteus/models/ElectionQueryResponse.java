package com.placesware.voteus.models;

import java.util.List;

/**
 * Created by kathrynkillebrew on 7/14/14.
 * https://developers.google.com/civic-information/docs/v1/elections/electionQuery
 */
public class ElectionQueryResponse {
    public String kind;
    public List<Election> elections;
    private CivicApiError error;

    public CivicApiError getError() {
        return error;
    }

    public boolean isSuccessful() {
        return error == null;
    }
}
