package com.placesware.voteus.models.GoogleDirections;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kathrynkillebrew on 7/31/14.
 */
public class Bounds implements Parcelable {
    public static final Creator<Bounds> CREATOR = new Creator<Bounds>() {
        @Override
        public Bounds createFromParcel(Parcel source) {
            return new Bounds(source);
        }

        @Override
        public Bounds[] newArray(int size) {
            return new Bounds[size];
        }
    };

    public final Location northeast;
    public final Location southwest;

    private Bounds(Parcel parcel) {
        northeast = parcel.readParcelable(Location.class.getClassLoader());
        southwest = parcel.readParcelable(Location.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(northeast, flags);
        dest.writeParcelable(southwest, flags);
    }
}
