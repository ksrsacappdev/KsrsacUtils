package com.ksrsac.photolatlng.model;

import java.util.List;

public class MainDistanceDataModel {
    double lat, lng;
    List<CoordinateWithDistanceModel> distanceList;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public List<CoordinateWithDistanceModel> getDistanceList() {
        return distanceList;
    }

    public void setDistanceList(List<CoordinateWithDistanceModel> distanceList) {
        this.distanceList = distanceList;
    }
}
