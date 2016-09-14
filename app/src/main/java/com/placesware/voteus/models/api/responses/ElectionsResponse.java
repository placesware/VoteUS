package com.placesware.voteus.models.api.responses;

import com.placesware.voteus.models.Election;

import java.util.ArrayList;

/**
 * Created by placesware-dev on 9/13/16.
 */
public class ElectionsResponse {

    String type;
    ArrayList<Election> elections;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<Election> getElections() {
        return elections;
    }

    public void setElections(ArrayList<Election> elections) {
        this.elections = elections;
    }
}
