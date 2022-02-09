package com.ksrsac.photolatlng.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class GeoJsonGeometryModel implements Parcelable {
    private static final String TAG = "GeoJsonGeometryModel";
    String type;
     Object[]coordinates;
     List<List<Double>> polyGonCoordinates;
     List<LatLng> polygonLatLngList;
    public GeoJsonGeometryModel() {
    }

    public GeoJsonGeometryModel(String type, Object[] coordinates, List<List<Double>> polyGonCoordinates) {
        this.type = type;
        this.coordinates = coordinates;
        this.polyGonCoordinates = polyGonCoordinates;
    }

    protected GeoJsonGeometryModel(Parcel in) {
        type = in.readString();
        polygonLatLngList = in.createTypedArrayList(LatLng.CREATOR);
    }

    public static final Creator<GeoJsonGeometryModel> CREATOR = new Creator<GeoJsonGeometryModel>() {
        @Override
        public GeoJsonGeometryModel createFromParcel(Parcel in) {
            return new GeoJsonGeometryModel(in);
        }

        @Override
        public GeoJsonGeometryModel[] newArray(int size) {
            return new GeoJsonGeometryModel[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Object[] coordinates) {
        this.coordinates = coordinates;
        Log.d(TAG, "setCoordinates: "+this.coordinates);
      //  setPolyGonCoordinates( (List<List<Double>>)(Object)this.coordinates);
    }

    public List<List<Double>> getPolyGonCoordinates() {
        return polyGonCoordinates;
    }

    public void setPolyGonCoordinates(List<List<Double>> polyGonCoordinates) {
        this.polyGonCoordinates = polyGonCoordinates;
    }

    public List<LatLng> getPolygonLatLngList() {
        return polygonLatLngList;
    }

    public void setPolygonLatLngList(List<LatLng> polygonLatLngList) {
        this.polygonLatLngList = polygonLatLngList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(type);
        parcel.writeTypedList(polygonLatLngList);
    }
}
