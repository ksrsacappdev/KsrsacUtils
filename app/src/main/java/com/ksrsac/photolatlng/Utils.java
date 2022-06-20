package com.ksrsac.photolatlng;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.ksrsac.photolatlng.model.CoordinateWithDistanceModel;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    private static final String TAG = "Utils";
    public static Map<String, List<LatLng>> checkLineCrossingPolygonOrNot(List<CoordinateWithDistanceModel> polygonCoordinates, List<CoordinateWithDistanceModel> vertexList) {
        List<CoordinateWithDistanceModel> FirstSplitList;
        List<CoordinateWithDistanceModel> SecondSplitList;

        List<Coordinate> polyGonCoordinateList = new ArrayList<>();
        for (int i = 0; i < polygonCoordinates.size(); i++) {
            Coordinate coordinate = new Coordinate(polygonCoordinates.get(i).getLat(), polygonCoordinates.get(i).getLng());
            polyGonCoordinateList.add(coordinate);
            Log.d(TAG, "onCreate: " + polyGonCoordinateList.get(i).x + " " + polyGonCoordinateList.get(i).y);
         //   latLngList.add(new LatLng(polygonCoordinates.get(i).getLat(), polygonCoordinates.get(i).getLng()));
        }

        LinearRing lr = makeLineRing(polyGonCoordinateList);
        List<CoordinateWithDistanceModel> SplitPolygonCoordinateList = new ArrayList<>();
        int NoOfIntersectionPoints = 0;

        for (int i = 0; i < vertexList.size() - 1; i++) {
            double lat1 = vertexList.get(i).getLat();
            double lon1 = vertexList.get(i).getLng();

            double lat2 = vertexList.get(i + 1).getLat();
            double lon2 = vertexList.get(i + 1).getLng();

            LineString ls = new GeometryFactory().createLineString(new Coordinate[]{new Coordinate(lat1, lon1), new Coordinate(lat2, lon2)}); // horizontal
            Log.d(TAG, "checkLineCrossingPolygonOrNot: " + lat1 + " " + lon1 + ", " + lat2 + " " + lon2);
            com.vividsolutions.jts.geom.Geometry intersectionPoints = lr.intersection(ls);
            if (intersectionPoints.getCoordinates().length == 1) {
                int k = 0;
                int l = 0;
                int size = polyGonCoordinateList.size() - 1;
                Log.d(TAG, "polyGonCoordinateList: " + polyGonCoordinateList.get(0).x + " " + polyGonCoordinateList.get(0).y);
                for (k = 0; k < size; k++) {
                    l = k + 1;
                    if (l >= size)
                        l = 0;
                    if (lies_on_segment(intersectionPoints.getCoordinates()[0].x, intersectionPoints.getCoordinates()[0].y, polyGonCoordinateList.get(k).x, polyGonCoordinateList.get(k).y, polyGonCoordinateList.get(l).x, polyGonCoordinateList.get(l).y)) {
                        NoOfIntersectionPoints++;
                        if (NoOfIntersectionPoints == 1) {
                            CoordinateWithDistanceModel withDistanceModel = new CoordinateWithDistanceModel();
                            withDistanceModel.setLat(intersectionPoints.getCoordinates()[0].x);
                            withDistanceModel.setLng(intersectionPoints.getCoordinates()[0].y);
                            withDistanceModel.setPos(k);
                            SplitPolygonCoordinateList.add(0, withDistanceModel);
                        }
                        if (NoOfIntersectionPoints >= 2) {
                            CoordinateWithDistanceModel withDistanceModel = new CoordinateWithDistanceModel();
                            withDistanceModel.setLat(intersectionPoints.getCoordinates()[0].x);
                            withDistanceModel.setLng(intersectionPoints.getCoordinates()[0].y);
                            withDistanceModel.setPos(k);
                            SplitPolygonCoordinateList.add(SplitPolygonCoordinateList.size(), withDistanceModel);
                        }
                        break;
                    }
                }


                Log.d(TAG, "checkLineCrossingPolygonOrNot: " + intersectionPoints.getCoordinates()[0].x + "," + intersectionPoints.getCoordinates()[0].y);
                //addMarker(new LatLng(intersectionPoints.getCoordinates()[0].x, intersectionPoints.getCoordinates()[0].y), 4);
                Log.d(TAG, "checkLineCrossingPolygonOrNot position:" + i + ":size: " + intersectionPoints.getCoordinates().length);
            } else if (intersectionPoints.getCoordinates().length == 2) {
                CoordinateWithDistanceModel withDistanceModel = new CoordinateWithDistanceModel();
                withDistanceModel.setLat(intersectionPoints.getCoordinates()[0].x);
                withDistanceModel.setLng(intersectionPoints.getCoordinates()[0].y);
                withDistanceModel.setPos(i);
                SplitPolygonCoordinateList.add(0, withDistanceModel);

                CoordinateWithDistanceModel withDistanceModel2 = new CoordinateWithDistanceModel();
                withDistanceModel2.setLat(intersectionPoints.getCoordinates()[1].x);
                withDistanceModel2.setLng(intersectionPoints.getCoordinates()[1].y);
                withDistanceModel2.setPos(i + 1);
                SplitPolygonCoordinateList.add(SplitPolygonCoordinateList.size(), withDistanceModel2);
            } else {
                CoordinateWithDistanceModel withDistanceModel = new CoordinateWithDistanceModel();
                withDistanceModel.setLat(lat1);
                withDistanceModel.setLng(lon1);
                withDistanceModel.setPos(i);
                if (NoOfIntersectionPoints == 1) {
                    SplitPolygonCoordinateList.add(SplitPolygonCoordinateList.size(), withDistanceModel);
                    CoordinateWithDistanceModel withDistanceModel2 = new CoordinateWithDistanceModel();
                    withDistanceModel2.setLat(lat2);
                    withDistanceModel2.setLng(lon2);
                    withDistanceModel2.setPos(i + 1);
                    SplitPolygonCoordinateList.add(SplitPolygonCoordinateList.size(), withDistanceModel2);
                    Log.d(TAG, "checkLineCrossingPolygonOrNot: intersecting");
                } else if (NoOfIntersectionPoints >= 2) {
                    SplitPolygonCoordinateList.add(SplitPolygonCoordinateList.size() - 1, withDistanceModel);
                    CoordinateWithDistanceModel withDistanceModel2 = new CoordinateWithDistanceModel();
                    withDistanceModel2.setLat(lat2);
                    withDistanceModel2.setLng(lon2);
                    withDistanceModel2.setPos(i + 1);
                    SplitPolygonCoordinateList.add(SplitPolygonCoordinateList.size() - 1, withDistanceModel2);
                    Log.d(TAG, "checkLineCrossingPolygonOrNot: intersecting");
                } else {
                    Log.d(TAG, "checkLineCrossingPolygonOrNot: not intersecting " + i + " and " + (i + 1));
                }
            }
        }
        List<LatLng> latLngList = new ArrayList<>();
        for (int i = 0; i < SplitPolygonCoordinateList.size(); i++) {
            latLngList.add(new LatLng(SplitPolygonCoordinateList.get(i).getLat(), SplitPolygonCoordinateList.get(i).getLng()));
        }
        Log.d(TAG, "checkLineCrossingPolygonOrNot: size:" + SplitPolygonCoordinateList.size());
       // addPolyLine(latLngList);

        CoordinateWithDistanceModel p1 = SplitPolygonCoordinateList.get(0);
        CoordinateWithDistanceModel p2 = SplitPolygonCoordinateList.get(SplitPolygonCoordinateList.size() - 1);

        FirstSplitList = new ArrayList<>();
        FirstSplitList.addAll(SplitPolygonCoordinateList);
        int a = p2.getPos();
        int size = polyGonCoordinateList.size() - 1;
        if (a >= size)
            a = 0;
        int l = a + 1;
        if (l >= size)
            l = 0;
        for (int k = a; k <= (size + a); k++) {
            int n = l;
            if (n >= size)
                n = 0;
            int o = n + 1;
            if (o >= size)
                o = 0;
            boolean val = lies_on_segment(p1.getLat(), p1.getLng(), polyGonCoordinateList.get(n).x, polyGonCoordinateList.get(n).y, polyGonCoordinateList.get(o).x, polyGonCoordinateList.get(o).y);
            if (!val) {
                CoordinateWithDistanceModel coordinate = new CoordinateWithDistanceModel();
                coordinate.setLat(polyGonCoordinateList.get(n).x);
                coordinate.setLng(polyGonCoordinateList.get(n).y);
                FirstSplitList.add(coordinate);
            } else {
                CoordinateWithDistanceModel coordinate = new CoordinateWithDistanceModel();
                coordinate.setLat(polyGonCoordinateList.get(n).x);
                coordinate.setLng(polyGonCoordinateList.get(n).y);
                FirstSplitList.add(coordinate);
                FirstSplitList.add(p1);
                break;
            }
            l = o;
        }

        List<LatLng> FSplitLtLnlist = new ArrayList<>();
        List<LatLng> SSplitLtLnlist = new ArrayList<>();

        for (int m = 0; m < FirstSplitList.size(); m++) {
            FSplitLtLnlist.add(new LatLng(FirstSplitList.get(m).getLat(), FirstSplitList.get(m).getLng()));
            Log.d(TAG, "First Split: " + (m + 1) + "-" + FirstSplitList.get(m).getLat() + "," + FirstSplitList.get(m).getLng());
        }

        SecondSplitList = new ArrayList<>();
        SecondSplitList.addAll(SplitPolygonCoordinateList);
        int b = p2.getPos();
        int c = b - 1;
        if (b == 0)
            c = size - 1;
        if (c < 0)
            c = size - 1;

        for (int k = size; k >= 0; k--) {
            boolean val = lies_on_segment(p1.getLat(), p1.getLng(), polyGonCoordinateList.get(b).x, polyGonCoordinateList.get(b).y, polyGonCoordinateList.get(c).x, polyGonCoordinateList.get(c).y);
            if (!val) {
                CoordinateWithDistanceModel coordinate = new CoordinateWithDistanceModel();
                coordinate.setLat(polyGonCoordinateList.get(b).x);
                coordinate.setLng(polyGonCoordinateList.get(b).y);
                SecondSplitList.add(coordinate);
            } else {
                CoordinateWithDistanceModel coordinate = new CoordinateWithDistanceModel();
                coordinate.setLat(polyGonCoordinateList.get(b).x);
                coordinate.setLng(polyGonCoordinateList.get(b).y);
                SecondSplitList.add(coordinate);
                SecondSplitList.add(p1);
                break;
            }
            b = c;
            if (b == 0)
                c = size - 1;
            c = b - 1;
            if (c < 0)
                c = size - 1;
        }
        for (int m = 0; m < SecondSplitList.size(); m++) {
            SSplitLtLnlist.add(new LatLng(SecondSplitList.get(m).getLat(), SecondSplitList.get(m).getLng()));
            Log.d(TAG, "Second Split: " + (m + 1) + "-" + SecondSplitList.get(m).getLat() + "," + SecondSplitList.get(m).getLng());
        }
      //  polygonNew(, 1);
       // polygonNew(SSplitLtLnlist, 2);
        Gson gson = new Gson();
        Map<String, List<LatLng>> data = new HashMap<>();
        data.put("First", FSplitLtLnlist);
        data.put("Second", SSplitLtLnlist);
        return data;
    }

    public static LinearRing makeLineRing(List<Coordinate> polyGonCoordinateList) {
        Coordinate[] polygonArray = new Coordinate[polyGonCoordinateList.size()];
        for (int i = 0; i < polyGonCoordinateList.size(); i++) {
            polygonArray[i] = polyGonCoordinateList.get(i);
        }
        return new GeometryFactory().createLinearRing(polygonArray);
    }

    public static boolean lies_on_segment(double px, double py, double startx, double starty, double endx, double endy) {
        double deltax = endx - startx;
        double deltay;
        double t = 0.0;
        boolean liesInXDir;
        if (deltax == 0)
            liesInXDir = (px == startx);
        else
            t = (px - startx) / deltax;
        liesInXDir = (t >= 0 && t <= 1);

        if (liesInXDir) {
            deltay = endy - starty;
            if (deltay == 0)
                return (py == starty);
            else
                t = (py - starty) / deltay;
            return (t >= 0 && t <= 1);
        } else
            return false;
    }


    public static LatLng findPointByDistance(int dist, LatLng latLng1, LatLng latLng2){
        List<LatLng> finalPoints = new ArrayList<>();
        double totalDistnacebtwnPoint = meters(latLng1.latitude, latLng1.longitude, latLng2.latitude, latLng2.longitude);
        double fraction = (double) dist/totalDistnacebtwnPoint;
        LatLng latLng = IntermediatePoint(latLng1.latitude, latLng1.longitude, latLng2.latitude, latLng2.longitude, fraction);
         if (finalPoints.size() == 0) {
            finalPoints.add(latLng1);
            finalPoints.add(latLng);
        } else {
            finalPoints.add(latLng);
            finalPoints.add(latLng1);
        }
        return latLng;
    }

    private static final double r2d = 180.0D / 3.141592653589793D;
    private static final double d2r = 3.141592653589793D / 180.0D;
    private static final double d2km = 111189.57696D * r2d;
    public static double meters(double lt1, double ln1, double lt2, double ln2) {
        double x = lt1 * d2r;
        double y = lt2 * d2r;
        return Math.acos( Math.sin(x) * Math.sin(y) + Math.cos(x) * Math.cos(y) * Math.cos(d2r * (ln1 - ln2))) * d2km;
    }
    public static LatLng IntermediatePoint(double lat1, double lon1, double lat2, double lon2, double fraction) {
        double φ1 = Math.toRadians(lat1), λ1 = Math.toRadians(lon1);
        double φ2 = Math.toRadians(lat2), λ2 = Math.toRadians(lon2);
        // distance between points
        double Δφ = φ2 - φ1;
        double Δλ = λ2 - λ1;
        double a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2)
                + Math.cos(φ1) * Math.cos(φ2) * Math.sin(Δλ / 2) * Math.sin(Δλ / 2);
        double δ = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double A = Math.sin((1 - fraction) * δ) / Math.sin(δ);
        double B = Math.sin(fraction * δ) / Math.sin(δ);

        double x = A * Math.cos(φ1) * Math.cos(λ1) + B * Math.cos(φ2) * Math.cos(λ2);
        double y = A * Math.cos(φ1) * Math.sin(λ1) + B * Math.cos(φ2) * Math.sin(λ2);
        double z = A * Math.sin(φ1) + B * Math.sin(φ2);

        double φ3 = Math.atan2(z, Math.sqrt(x * x + y * y));
        double λ3 = Math.atan2(y, x);

        double lat = Math.toDegrees(φ3);
        double lon = Math.toDegrees(λ3);
        System.out.println("Intermediate Lat:" + lat + "," + lon);
        return new LatLng(lat, lon);

    }


}
