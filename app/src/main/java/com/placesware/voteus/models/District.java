package com.placesware.voteus.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kathrynkillebrew on 7/15/14.
 */
public class District implements Parcelable {
    public static final Creator<District> CREATOR = new Creator<District>() {
        public District createFromParcel(Parcel pc) {
            return new District(pc);
        }

        public District[] newArray(int size) {
            return new District[size];
        }
    };

    public final String id;
    public final String name;
    public final String scope;

    public District(Parcel parcel) {
        id = parcel.readString();
        name = parcel.readString();
        scope = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(scope);
    }
}
