package com.placesware.voteus.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.google.android.gms.maps.model.LatLng;
import com.demo.ergobot.civicusdemo.R;
import com.demo.ergobot.civicusdemo.models.GoogleDirections.Location;

/**
 * Created by kathrynkillebrew on 7/14/14.
 * Polling location or early voting site
 * https://developers.google.com/civic-information/docs/v1/voterinfo
 */
public class PollingLocation implements Parcelable {
    /**
     * Static field used to regenerate object, individually or as arrays
     */
    public static final Creator<PollingLocation> CREATOR = new Creator<PollingLocation>() {
        public PollingLocation createFromParcel(Parcel pc) {
            return new PollingLocation(pc);
        }

        public PollingLocation[] newArray(int size) {
            return new PollingLocation[size];
        }
    };

    public final static int POLLING_TYPE_LOCATION = 0x0;
    public final static int POLLING_TYPE_EARLY_VOTE = 0x1;
    public final static int POLLING_TYPE_DROP_BOX = 0x2;

    public final CivicApiAddress address;
    public final String id;
    public final String name;
    public final String startDate;
    public final String endDate;
    public final String pollingHours;
    public final String voterServices; // This field is not populated for polling locations.

    //These aren't set in the response, but when the app is geocoded.
    public Location location;
    public float distance;

    public int pollingLocationType = POLLING_TYPE_LOCATION;

    /**
     * Creator from Parcel, reads back fields IN THE ORDER they were written
     */
    public PollingLocation(Parcel parcel) {
        address = parcel.readParcelable(CivicApiAddress.class.getClassLoader());
        id = parcel.readString();
        name = parcel.readString();
        startDate = parcel.readString();
        endDate = parcel.readString();
        pollingHours = parcel.readString();
        voterServices = parcel.readString();
        pollingLocationType = parcel.readInt();
        location = parcel.readParcelable(Location.class.getClassLoader());
        distance = parcel.readFloat();
    }


//    public PollingLocation()

    public LatLng getLatLongLocation() {
        return new LatLng(location.lat, location.lng);
    }

    /**
     * Set the polling location type based on where the information was grabbed from
     * Will default to polling location if a random value is entered
     *
     * @param pollingLocationType
     */
    public void setPollingLocationType(int pollingLocationType) {
        this.pollingLocationType = pollingLocationType;
    }

    public
    @DrawableRes
    int getDrawableDot() {
        switch (pollingLocationType) {
            case POLLING_TYPE_DROP_BOX:
                return R.drawable.ic_dot_drop_box;
            case POLLING_TYPE_EARLY_VOTE:
                return R.drawable.ic_dot_early_vote;
            default:
                return R.drawable.ic_dot_polling_location;
        }
    }

    public
    @DrawableRes
    int getDrawableMarker(boolean selected) {
        switch (pollingLocationType) {
            case POLLING_TYPE_DROP_BOX:
                return selected ? R.drawable.ic_marker_drop_box_selected : R.drawable.ic_marker_drop_box;
            case POLLING_TYPE_EARLY_VOTE:
                return selected ? R.drawable.ic_marker_early_voting_selected : R.drawable.ic_marker_early_voting;
            case POLLING_TYPE_LOCATION:
                return selected ? R.drawable.ic_marker_poll_selected : R.drawable.ic_marker_poll;
        }

        return 0;
    }

    public
    @StringRes
    int getPollingTypeString() {
        switch (pollingLocationType) {
            case POLLING_TYPE_DROP_BOX:
                return R.string.locations_list_label_drop_off;
            case POLLING_TYPE_EARLY_VOTE:
                return R.string.locations_list_label_early_voting;
            default:
                return R.string.locations_list_label_polling_sites;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(address, 0);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeString(pollingHours);
        dest.writeString(voterServices);
        dest.writeInt(pollingLocationType);
        dest.writeParcelable(location, 0);
        dest.writeFloat(distance);
    }
}
