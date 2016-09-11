package com.placesware.voteus.models.api.requests;

/**
 * Generic POJO to fix unsafe varargs error in interactors
 * <p/>
 * Created by marcvandehey on 3/22/16.
 */
public interface RequestType {
    String buildQueryString();
}
