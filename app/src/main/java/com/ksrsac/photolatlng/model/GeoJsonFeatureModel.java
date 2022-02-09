package com.ksrsac.photolatlng.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

public class GeoJsonFeatureModel implements Parcelable {
    String type;
    GeoJsonGeometryModel geometry;
    Object properties;
    String ID;
    String action;
    Map<String, String> propertiesHashmap;

    public GeoJsonFeatureModel() {
    }

    public GeoJsonFeatureModel(String type, GeoJsonGeometryModel geometry, Object properties, Map<String, String> propertiesHashmap) {
        this.type = type;
        this.geometry = geometry;
        this.properties = properties;
        this.propertiesHashmap = propertiesHashmap;
    }

    protected GeoJsonFeatureModel(Parcel in) {
        type = in.readString();
        geometry = in.readParcelable(GeoJsonGeometryModel.class.getClassLoader());
        ID = in.readString();
        action = in.readString();
    }

    public static final Creator<GeoJsonFeatureModel> CREATOR = new Creator<GeoJsonFeatureModel>() {
        @Override
        public GeoJsonFeatureModel createFromParcel(Parcel in) {
            return new GeoJsonFeatureModel(in);
        }

        @Override
        public GeoJsonFeatureModel[] newArray(int size) {
            return new GeoJsonFeatureModel[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GeoJsonGeometryModel getGeometry() {
        return geometry;
    }

    public void setGeometry(GeoJsonGeometryModel geometry) {
        this.geometry = geometry;
    }

    public Object getProperties() {
        return properties;
    }

    public void setProperties(Object properties) {
        this.properties = properties;
       // HashMap<String, String> yourHashMap = new Gson().fromJson(this.properties.toString(), HashMap.class);
     //   setPropertiesHashmap(yourHashMap);
    }

    public Map<String, String> getPropertiesHashmap() {
        return propertiesHashmap;
    }

    public void setPropertiesHashmap(Map<String, String> propertiesHashmap) {
        this.propertiesHashmap = propertiesHashmap;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(type);
        parcel.writeParcelable(geometry, i);
        parcel.writeString(ID);
        parcel.writeString(action);
    }
}
