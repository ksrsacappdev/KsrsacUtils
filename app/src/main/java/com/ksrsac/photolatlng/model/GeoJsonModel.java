package com.ksrsac.photolatlng.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class GeoJsonModel implements Parcelable {
    String type;
    List<GeoJsonFeatureModel> features;

    public GeoJsonModel() {
    }

    public GeoJsonModel(String type, List<GeoJsonFeatureModel> features) {
        this.type = type;
        this.features = features;
    }

    protected GeoJsonModel(Parcel in) {
        type = in.readString();
        features = in.createTypedArrayList(GeoJsonFeatureModel.CREATOR);
    }

    public static final Creator<GeoJsonModel> CREATOR = new Creator<GeoJsonModel>() {
        @Override
        public GeoJsonModel createFromParcel(Parcel in) {
            return new GeoJsonModel(in);
        }

        @Override
        public GeoJsonModel[] newArray(int size) {
            return new GeoJsonModel[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<GeoJsonFeatureModel> getFeatures() {
        return features;
    }

    public void setFeatures(List<GeoJsonFeatureModel> features) {
        this.features = features;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(type);
        parcel.writeTypedList(features);
    }
}
