package com.ksrsac.photolatlng.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CoordinateWithDistanceModel implements Comparable<CoordinateWithDistanceModel>, Parcelable {
    double lat, lng, dist;
    int pos;
    double startx, starty, endx, endy;

    public CoordinateWithDistanceModel() {
    }

    public CoordinateWithDistanceModel(double lat, double lng, double dist, int pos) {
        this.lat = lat;
        this.lng = lng;
        this.dist = dist;
        this.pos = pos;
    }

    protected CoordinateWithDistanceModel(Parcel in) {
        lat = in.readDouble();
        lng = in.readDouble();
        dist = in.readDouble();
        pos = in.readInt();
        startx = in.readDouble();
        starty = in.readDouble();
        endx = in.readDouble();
        endy = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeDouble(dist);
        dest.writeInt(pos);
        dest.writeDouble(startx);
        dest.writeDouble(starty);
        dest.writeDouble(endx);
        dest.writeDouble(endy);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CoordinateWithDistanceModel> CREATOR = new Creator<CoordinateWithDistanceModel>() {
        @Override
        public CoordinateWithDistanceModel createFromParcel(Parcel in) {
            return new CoordinateWithDistanceModel(in);
        }

        @Override
        public CoordinateWithDistanceModel[] newArray(int size) {
            return new CoordinateWithDistanceModel[size];
        }
    };

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

    public double getDist() {
        return dist;
    }

    public void setDist(double dist) {
        this.dist = dist;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public double getStartx() {
        return startx;
    }

    public void setStartx(double startx) {
        this.startx = startx;
    }

    public double getStarty() {
        return starty;
    }

    public void setStarty(double starty) {
        this.starty = starty;
    }

    public double getEndx() {
        return endx;
    }

    public void setEndx(double endx) {
        this.endx = endx;
    }

    public double getEndy() {
        return endy;
    }

    public void setEndy(double endy) {
        this.endy = endy;
    }

    @Override
    public int compareTo(CoordinateWithDistanceModel obj) {
        return Double.compare( this.dist, obj.dist);
    }
}
