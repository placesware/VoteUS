package com.placesware.voteus.models;

import java.util.Dictionary;
import java.util.List;

/**
 * Created by kathrynkillebrew on 7/14/14.
 * https://developers.google.com/civic-information/docs/v1/representatives/representativeInfoByAddress
 */
public class RepresentativeInfoByAddressResponse {
    public String kind;
    public CivicApiAddress normalizedInput;
    public Dictionary<String, Division> divisions;
    public List<Office> offices;
    public List<Official> officials;
}
