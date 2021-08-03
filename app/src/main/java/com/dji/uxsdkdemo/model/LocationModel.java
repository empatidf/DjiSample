package com.dji.uxsdkdemo.model;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationModel implements Parcelable {
    public double latitude;
    public double longitude;
    public double altitude;

    public LocationModel(double latitude, double longitude, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    protected LocationModel(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        altitude = in.readFloat();
    }

    public static final Creator<LocationModel> CREATOR = new Creator<LocationModel>() {
        @Override
        public LocationModel createFromParcel(Parcel in) {
            return new LocationModel(in);
        }

        @Override
        public LocationModel[] newArray(int size) {
            return new LocationModel[size];
        }
    };

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeDouble(altitude);
    }
}
