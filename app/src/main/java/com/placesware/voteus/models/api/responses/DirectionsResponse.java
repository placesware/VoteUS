package com.placesware.voteus.models.api.responses;

import com.demo.ergobot.civicusdemo.models.GoogleDirections.Route;

import java.util.List;

/**
 * Created by kathrynkillebrew on 7/31/14.
 */
public class DirectionsResponse {
    public static final String STATUS_ZERO_RESULTS = "ZERO_RESULTS";

    public String status;
    public List<Route> routes;
    public String mode;

    public boolean hasErrors() {
        return status != null && !status.equals("OK");
    }
}
