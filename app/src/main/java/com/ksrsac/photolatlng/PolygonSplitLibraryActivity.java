package com.ksrsac.photolatlng;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.ksrsac.photolatlng.model.CoordinateWithDistanceModel;
import com.ksrsac.photolatlng.model.GeoJsonFeatureModel;
import com.ksrsac.photolatlng.model.GeoJsonGeometryModel;
import com.ksrsac.photolatlng.model.GeoJsonModel;
import com.ksrsac.photolatlng.model.MainDistanceDataModel;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PolygonSplitLibraryActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final String TAG = "MapActivity";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    ConstraintLayout mRootLayout;
    private GoogleMap mMap;
    private Button mButtonSend;
    PlaceAutocompleteFragment placeAutoComplete;
    private List<LatLng> mLatLngList = new ArrayList<>();
    private List<List<LatLng>> MasterLatLonglist = new ArrayList<>();

    private double LatitudeFromPrev, LongitudeFromPrev;
    private boolean isInsidePolygon = true;
    private boolean isGridEnabled;
    private Button mButtonSplit, mButtonSplit2, mButtonSelectPoints, mButtonSelectPoints2;
    private EditText editTextStartX, editTextStartY, editTextEndX, editTextEndY;
    private RadioGroup radioGroup;
    private int typeSelected = 1;
    LinearLayout linearLayoutVertex;
    private Button buttonAddPoint, buttonReset, currentLocationPoint;
    private LinearLayout linearLayoutCurrentLocation;
    private List<CoordinateWithDistanceModel> polygonCoordinates = new ArrayList<>();
    private List<CoordinateWithDistanceModel> vertexList = new ArrayList<>();
    private List<LatLng> latLngList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_polygon_split);
        LatitudeFromPrev = getIntent().getDoubleExtra("LAT", 13.058274);
        LongitudeFromPrev = getIntent().getDoubleExtra("LNG", 77.496196);

        mButtonSplit = findViewById(R.id.split);
        mButtonSplit2 = findViewById(R.id.split2);
        mButtonSelectPoints = findViewById(R.id.btn_selecting_split_pts);
        mButtonSelectPoints2 = findViewById(R.id.btn_selecting_split_pts2);

        mRootLayout = findViewById(R.id.constl);

        editTextStartX = findViewById(R.id.start_x);
        editTextStartY = findViewById(R.id.start_y);
        linearLayoutVertex = findViewById(R.id.vertec);
        buttonAddPoint = findViewById(R.id.add_point);
        buttonReset = findViewById(R.id.reset);
        linearLayoutCurrentLocation = findViewById(R.id.linearLayout_current_location);
        currentLocationPoint = findViewById(R.id.add_point_location);

        polygonCoordinates = getIntent().getParcelableArrayListExtra("Coordinates");
        vertexList = getIntent().getParcelableArrayListExtra("Vertices");
        polyGonCoordinateList = new ArrayList<>();

        for (int i = 0; i < polygonCoordinates.size(); i++) {
            Coordinate coordinate = new Coordinate(polygonCoordinates.get(i).getLat(), polygonCoordinates.get(i).getLng());
            polyGonCoordinateList.add(coordinate);
            Log.d(TAG, "onCreate: " + polyGonCoordinateList.get(i).x + " " + polyGonCoordinateList.get(i).y);
            latLngList.add(new LatLng(polygonCoordinates.get(i).getLat(), polygonCoordinates.get(i).getLng()));
        }

        mButtonSend = findViewById(R.id.selected);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        buttonAddPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextStartX.getText().toString().isEmpty() && !editTextStartY.getText().toString().isEmpty()) {
                    double startX = Double.parseDouble(editTextStartX.getText().toString());
                    double startY = Double.parseDouble(editTextStartY.getText().toString());
                    CoordinateWithDistanceModel coordinate = new CoordinateWithDistanceModel();
                    coordinate.setLat(startX);
                    coordinate.setLng(startY);
                    //vertexList.add(coordinate);
                    editTextStartX.setText("");
                    editTextStartY.setText("");
                    addMarker(new LatLng(startX, startY), 11);
                }
            }
        });
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetOnScreenMarkers();
            }
        });
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGridEnabled) {
                    if (isInsidePolygon) {
                        sendData();
                    } else {
                        Toast.makeText(PolygonSplitLibraryActivity.this, "Your outside the area which is assigned for you", Toast.LENGTH_LONG).show();
                    }
                } else {
                    sendData();
                }
            }
        });

        mButtonSelectPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow(1);
            }
        });

        mButtonSelectPoints2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow(2);
            }
        });
        mButtonSplit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                polygonNew(finalPoints, 1);
            }
        });

        mButtonSplit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //intersection2();
                if (vertexList.size() >= 2) {
                    checkLineCrossingPolygonOrNot();
                } else {
                    Toast.makeText(PolygonSplitLibraryActivity.this, "Minimum two coordinates", Toast.LENGTH_LONG).show();
                }
            }
        });
        radioGroup = findViewById(R.id.radio_gb_type);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_manual) {
                    typeSelected = 1;
                    linearLayoutVertex.setVisibility(View.VISIBLE);
                    linearLayoutCurrentLocation.setVisibility(View.GONE);
                    // resetOnScreenMarkers();
                } else if (checkedId == R.id.rb_gps) {
                    typeSelected = 2;
                    linearLayoutVertex.setVisibility(View.GONE);
                    linearLayoutCurrentLocation.setVisibility(View.VISIBLE);
                    // resetOnScreenMarkers();
                } else if (checkedId == R.id.rb_digitisation) {
                    typeSelected = 3;
                    linearLayoutVertex.setVisibility(View.GONE);
                    linearLayoutCurrentLocation.setVisibility(View.GONE);
                    //vertexList = new ArrayList<>();
                    // resetOnScreenMarkers();
                }
            }
        });
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        currentLocationPoint = findViewById(R.id.add_point_location);
        currentLocationPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(PolygonSplitLibraryActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PolygonSplitLibraryActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(PolygonSplitLibraryActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    CoordinateWithDistanceModel coordinate = new CoordinateWithDistanceModel();
                                    coordinate.setLat(location.getLatitude());
                                    coordinate.setLng(location.getLongitude());
                                    //vertexList.add(coordinate);
                                    editTextStartX.setText("");
                                    editTextStartY.setText("");
                                    addMarker(new LatLng(location.getLatitude(), location.getLongitude()), 11);
                                }
                            }
                        });
            }
        });

    }

    private void findOffset(List<LatLng> finalPoints, int d1, int d2) {

        double totalDistnacebtwnPoint = meters(finalPoints.get(0).latitude, finalPoints.get(0).longitude, finalPoints.get(1).latitude, finalPoints.get(1).longitude);
        double fraction = (double) d1/totalDistnacebtwnPoint;
        LatLng Intermediate = IntermediatePoint(finalPoints.get(0).latitude, finalPoints.get(0).longitude, finalPoints.get(1).latitude, finalPoints.get(1).longitude, fraction);


        LineSegment ls = new LineSegment(finalPoints.get(0).latitude, finalPoints.get(0).longitude, finalPoints.get(1).latitude, finalPoints.get(1).longitude);

        double brng2 = calculateDirection(finalPoints.get(0).latitude, finalPoints.get(0).longitude, finalPoints.get(1).latitude, finalPoints.get(1).longitude);


        //LatLng latLng = findNextPoint(brng2, d1 / 1000.0, finalPoints.get(0).latitude, finalPoints.get(0).longitude);

        double angleSum = (brng2 + 90);

        Log.d(TAG, "onCreate: angle:" + angleSum + " " + brng2);

        LatLng latLng2 = findNextPoint(angleSum, d2 / 1000.0, Intermediate.latitude, Intermediate.longitude);
        Log.d(TAG, "onCreate: " + latLng2.latitude + " " + latLng2.longitude);

        double offsetDistance = 1.0;
// calculate Point right to start point
        Coordinate startRight = ls.pointAlongOffset(0, offsetDistance);
        Log.d(TAG, "onCreate: startRight:" + startRight.x + "," + startRight.y);
// calculate Point left to start point
        Coordinate startLeft = ls.pointAlongOffset(0, -offsetDistance);
        Log.d(TAG, "onCreate: startLeft:" + startLeft.x + "," + startLeft.y);

// calculate Point right to end point
        Coordinate endRight = ls.pointAlongOffset(1, offsetDistance);
        Log.d(TAG, "onCreate: endRight:" + endRight.x + "," + endRight.y);

// calculate Point left to end point
        Coordinate endLeft = ls.pointAlongOffset(1, -offsetDistance);
        Log.d(TAG, "onCreate: endLeft:" + endLeft.x + "," + endLeft.y);
    }

    List<LatLng> finalPoints = new ArrayList<>();

    public void popupWindow(int type) {
        List<LatLng> selectedPointsforSplit = new ArrayList<>();

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_window, null);
        ViewGroup insertPoint = (ViewGroup) popupView.findViewById(R.id.llinsert);
        TextView textView = new TextView(this);
        textView.setText("Select points");
        insertPoint.addView(textView);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        try {
            for (int i = 0; i < latLngList.size() - 1; i++) {
                Button button = new Button(this);
                button.setText((i + 1) + "");
                button.setLayoutParams(params);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        button.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                        selectedPointsforSplit.add(latLngList.get(Integer.parseInt(button.getText().toString()) - 1));
                    }
                });
                insertPoint.addView(button);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        EditText editText = new EditText(this);
        editText.setHint("Enter distance in mtr");
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        insertPoint.addView(editText);


        EditText editText2 = new EditText(this);
        editText2.setHint("Enter offset distance in mtr");
        editText2.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (type == 2) {
            insertPoint.addView(editText2);
        }

        Button buttonClose = new Button(this);
        buttonClose.setText("Close");
        insertPoint.addView(buttonClose);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(mRootLayout, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });


        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double totalDistnacebtwnPoint = meters(selectedPointsforSplit.get(0).latitude, selectedPointsforSplit.get(0).longitude, selectedPointsforSplit.get(1).latitude, selectedPointsforSplit.get(1).longitude);
                double fraction = (double) Integer.parseInt(editText.getText().toString())/totalDistnacebtwnPoint;

                Log.d(TAG, "onClick: "+fraction+" and dist: "+totalDistnacebtwnPoint);
                LatLng latLng = IntermediatePoint(selectedPointsforSplit.get(0).latitude, selectedPointsforSplit.get(0).longitude, selectedPointsforSplit.get(1).latitude, selectedPointsforSplit.get(1).longitude, fraction);
                // double brng = calculateDirection(selectedPointsforSplit.get(0).latitude, selectedPointsforSplit.get(0).longitude, selectedPointsforSplit.get(1).latitude, selectedPointsforSplit.get(1).longitude);
                // LatLng latLng = findNextPoint(brng, (double) Integer.parseInt(editText.getText().toString()) / 1000.0, selectedPointsforSplit.get(0).latitude, selectedPointsforSplit.get(0).longitude);
                if (finalPoints.size() == 0) {
                    finalPoints.add(selectedPointsforSplit.get(0));
                    finalPoints.add(latLng);
                } else {
                    finalPoints.add(latLng);
                    finalPoints.add(selectedPointsforSplit.get(0));
                }
                addMarker(latLng, 12);
                if (type == 2) {
                    findOffset(selectedPointsforSplit, Integer.parseInt(editText.getText().toString()), Integer.parseInt(editText2.getText().toString()));
                }

                LineSegment ls = new LineSegment(13.096176, 77.570611, 13.084230, 77.570872);
                Log.d(TAG, "findOffset isVertical: " + ls.isVertical());
                Log.d(TAG, "findOffset isHorizontal : " + ls.isHorizontal());
                Coordinate startRight = ls.pointAlongOffset(0, 1);
                Log.d(TAG, "onCreate: startRight:" + startRight.x + "," + startRight.y);

                popupWindow.dismiss();
            }
        });
    }

    private static final double r2d = 180.0D / 3.141592653589793D;
    private static final double d2r = 3.141592653589793D / 180.0D;
    private static final double d2km = 111189.57696D * r2d;
    public  double meters(double lt1, double ln1, double lt2, double ln2) {
        double x = lt1 * d2r;
        double y = lt2 * d2r;
        return Math.acos( Math.sin(x) * Math.sin(y) + Math.cos(x) * Math.cos(y) * Math.cos(d2r * (ln1 - ln2))) * d2km;
    }
    LatLng IntermediatePoint(double lat1, double lon1, double lat2, double lon2, double fraction) {
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

    public void resetOnScreenMarkers() {
        mMap.clear();
        //vertexList.clear();
        //   retrieveFileFromResource();
    }

    List<String> polygondata;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(lat, lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(LatitudeFromPrev, LongitudeFromPrev), 20));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
        final CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(LatitudeFromPrev, LongitudeFromPrev))      // Sets the center of the map to Mountain View
                .zoom(20)                   // Sets the zoom
                .build();                   //
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                GeoJsonLayer layer3 = null;
                try {
                    // findOffset();
                    layer3 = new GeoJsonLayer(mMap, R.raw.geo, PolygonSplitLibraryActivity.this);
                    //  addGeoJsonLayerToMap(layer3);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (latLngList.size() > 0) {
                    polygon(latLngList);
                }
                //  retrieveFileFromResource();
                // new DownloadGeoJsonFile().execute("https://gist.githubusercontent.com/wavded/1200773/raw/e122cf709898c09758aecfef349964a8d73a83f3/sample.json");
            }
        });
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                lat = mMap.getCameraPosition().target.latitude;
                lon = mMap.getCameraPosition().target.longitude;
            }
        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (typeSelected == 3) {
                    CoordinateWithDistanceModel coordinateWithDistanceModel = new CoordinateWithDistanceModel();
                    coordinateWithDistanceModel.setLat(latLng.latitude);
                    coordinateWithDistanceModel.setLng(latLng.longitude);
                    //vertexList.add(coordinateWithDistanceModel);
                    addMarker(latLng, 11);
                    /*if(firstPoint==null) {
                        addMarker(latLng, 10);
                    }
                    else {
                        addMarker(latLng, 11);
                    }*/
                }
            }
        });

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void drawPolygon(List<LatLng> mLatLngList) {
        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .width(6.0f)
                .color(Color.RED)
                .addAll(mLatLngList));
        // Store a data object with the polyline, used here to indicate an arbitrary type.
        polyline1.setTag("A");
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
//Showing Current Location Marker on Map
        lat = mLastLocation.getLatitude();
        lon = mLastLocation.getLongitude();
        LatLng latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location locations = locationManager.getLastKnownLocation(provider);
        List<String> providerList = locationManager.getAllProviders();
        if (null != locations && null != providerList && providerList.size() > 0) {
            double longitude = locations.getLongitude();
            double latitude = locations.getLatitude();
            getAddress(longitude, latitude);
        }
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        mCurrLocationMarker.setDraggable(true);

        //  mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        //  mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                    this);
        }
    }

    Marker firstPoint, secondPoint;

    public void addMarker(LatLng latLng, int type) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        if (type == 1) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            mCurrLocationMarker = mMap.addMarker(markerOptions);
        } else if (type == 3) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            mCurrLocationMarker = mMap.addMarker(markerOptions);
        } else if (type == 4) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            mCurrLocationMarker = mMap.addMarker(markerOptions);
        }
        if (type == 10) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            firstPoint = mMap.addMarker(markerOptions);
        } else if (type == 11) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            secondPoint = mMap.addMarker(markerOptions);
        } else if (type == 12) {
            try {
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                Marker marker = mMap.addMarker(markerOptions);
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {

                        return false;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private String address = null;
    private double lat, lon;

    private void getAddress(double longitude, double latitude) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> listAddresses = geocoder.getFromLocation(latitude,
                    longitude, 1);
            if (null != listAddresses && listAddresses.size() > 0) {

                String state = listAddresses.get(0).getAdminArea();
                String country = listAddresses.get(0).getCountryName();
                String subLocality = listAddresses.get(0).getSubLocality();
                address = listAddresses.get(0).getAddressLine(0) + " " + listAddresses.get(0).getSubAdminArea();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void sendData() {
        Intent data = new Intent();
        data.putExtra("streetkey", "streetname");
        data.putExtra("LON", lon);
        data.putExtra("LAT", lat);
        data.putExtra("SplittedFirstPolygon", (Parcelable) FirstSplitList);
        data.putExtra("SplittedSecondPolygon", (Parcelable) SecondSplitList);
        data.putExtra("ADDS", address);
        setResult(RESULT_OK, data);
        finish();
    }

    private void addGeoJsonLayerToMap(GeoJsonLayer layer) {
        //addColorsToMarkers(layer);
        layer.addLayerToMap();
        // Demonstrate receiving features via GeoJsonLayer clicks.
        layer.setOnFeatureClickListener(new GeoJsonLayer.GeoJsonOnFeatureClickListener() {
            @Override
            public void onFeatureClick(Feature feature) {
                Geometry geo = feature.getGeometry();
                if (geo.getGeometryType().equalsIgnoreCase("Point")) {
                    Toast.makeText(PolygonSplitLibraryActivity.this, "Feature clicked: " + feature.getProperty("title"), Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(PolygonSplitActivity.this, MainActivity.class));
                }
            }

        });
    }

    GeoJsonLayer geoJsonLayer;

    private class DownloadGeoJsonFile extends AsyncTask<String, Void, GeoJsonLayer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected GeoJsonLayer doInBackground(String... params) {
            try {
                Log.d(TAG, "doInBackground URL: " + params[0]);
                InputStream stream = new URL(params[0]).openStream();
                String line;
                StringBuilder result = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();
                stream.close();

                geoJsonLayer = new GeoJsonLayer(mMap, new JSONObject(result.toString()));
                return geoJsonLayer;
            } catch (IOException e) {
                Log.d(TAG, "doInBackground: " + e.getLocalizedMessage());
            } catch (JSONException e) {
                Log.d(TAG, "doInBackground: " + e.getLocalizedMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(GeoJsonLayer layer) {
            if (layer != null) {
                Log.d(TAG, "onPostExecute: " + layer.toString());
                addGeoJsonLayerToMap(geoJsonLayer);
            }
        }
    }

    private void retrieveFileFromResource() {
        try {
            // GeoJsonLayer layer = new GeoJsonLayer(mMap, R.raw.geojson, this);
            JSONObject jsonFileObject = createJsonFileObject(this.getResources().openRawResource(R.raw.geo));
            polyGonCoordinateList = new ArrayList<>();
            GeoJsonModel geoJsonModel = new Gson().fromJson(jsonFileObject.toString(), GeoJsonModel.class);
            for (int i = 0; i < geoJsonModel.getFeatures().size(); i++) {
                GeoJsonFeatureModel geoJsonFeatureModel = geoJsonModel.getFeatures().get(i);
                Map<String, String> map = (Map<String, String>) geoJsonFeatureModel.getProperties();
                geoJsonFeatureModel.setPropertiesHashmap(map);
                geoJsonModel.getFeatures().remove(i);
                geoJsonModel.getFeatures().add(i, geoJsonFeatureModel);

                if (geoJsonModel.getFeatures().get(i).getGeometry().getType().equalsIgnoreCase("Point")) {
                    GeoJsonGeometryModel geometryModel = geoJsonModel.getFeatures().get(i).getGeometry();
                    double[] coordinates = new double[geometryModel.getCoordinates().length];
                    Object[] values = geometryModel.getCoordinates();
                    // copy elements from object array to integer array
                    for (int j = 0; j < geometryModel.getCoordinates().length; j++)
                        coordinates[j] = (double) values[j];


                    LatLng latLng = new LatLng((double) values[1], (double) values[0]);

                    // addMarker(latLng, geoJsonFeatureModel.getPropertiesHashmap(), i);
                } else if (geoJsonModel.getFeatures().get(i).getGeometry().getType().equalsIgnoreCase("LineString")) {

                    GeoJsonGeometryModel geometryModel = geoJsonModel.getFeatures().get(i).getGeometry();
                    List<Object> d = Arrays.asList(geometryModel.getCoordinates());
                    List<LatLng> polygonLatLngList = new ArrayList<>();

                    for (int l = 0; l < d.size(); l++) {
                        Object bj = d.get(l);
                        List<Object> d3 = (List<Object>) (bj);
                        LatLng latLng = new LatLng((double) d3.get(1), (double) d3.get(0));
                        polygonLatLngList.add(latLng);
                    }
                    addPolyLine(polygonLatLngList);
                } else {
                    GeoJsonGeometryModel geometryModel = geoJsonModel.getFeatures().get(i).getGeometry();
                    List<Object> d = Arrays.asList(geometryModel.getCoordinates());
                    List<LatLng> polygonLatLngList = new ArrayList<>();
                    Object bj = d.get(0);
                    List<List<Object>> d3 = (List<List<Object>>) (bj);
                    for (int k = 0; k < d3.size(); k++) {
                        if (d3.get(k) != null) {
                            Object[] values = d3.get(k).toArray();
                            if (values != null && values[1] != null) {
                                LatLng latLng = new LatLng((double) values[1], (double) values[0]);
                                polygonLatLngList.add(latLng);
                                Log.d(TAG, "retrieveFileFromResource: " + values[1] + " " + values[0]);
                                polyGonCoordinateList.add(new Coordinate((double) values[1], (double) values[0]));
                            }
                        }
                    }
                    geometryModel.setPolygonLatLngList(polygonLatLngList);
                    polygon(polygonLatLngList);
                    //intersection(startX, startY, endX, endy);
                }
            }

/*
            GeoJsonLayer layer3 = new GeoJsonLayer(mMap, R.raw.access_points, this);
            GeoJsonLayer layer4= new GeoJsonLayer(mMap, R.raw.gates, this);
            GeoJsonLayer layer5 = new GeoJsonLayer(mMap, R.raw.internal_roads, this);
            //    GeoJsonLayer layer6 = new GeoJsonLayer(mMap, R.raw.units, this);

            addGeoJsonLayerToMap(layer3);
            addGeoJsonLayerToMap(layer4);
            addGeoJsonLayerToMap(layer5);*/
            //    addGeoJsonLayerToMap(layer6);

            //     addGeoJsonLayerToMap(layer);
        } catch (IOException e) {
            Log.d(TAG, "retrieveFileFromResource: ");
        } catch (JSONException e) {
            Log.d(TAG, "retrieveFileFromResource: ");
        }
    }

    private JSONObject createJsonFileObject(InputStream stream)
            throws IOException, JSONException {
        String line;
        StringBuilder result = new StringBuilder();
        // Reads from stream
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        // Read each line of the GeoJSON file into a string
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        reader.close();

        // Converts the result string into a JSONObject
        return new JSONObject(result.toString());
    }

    private void addPolyLine(List<LatLng> latLngList) {
        Polyline polyline1 = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .width(6.0f)
                .color(Color.RED)
                .addAll(latLngList));
        // Store a data object with the polyline, used here to indicate an arbitrary type.
        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {

             /*   String []str = polyline.getTag().toString().split("-");
                Intent intent = new Intent(PolygonSplitActivity.this, AttributeChangesActivity.class);
                intent.putExtra("TYPE", str[1]);
                int tag = Integer.parseInt(str[0]);
                String data = new Gson().toJson(MasterfeaturList.get(tag));
                intent.putExtra("DATA", data);
                startActivity(intent);*/
            }
        });
    }

    List<Coordinate> polyGonCoordinateList;
    LatLngBounds latLngBounds;

    public void polygon(List<LatLng> latLngList) {
        Polygon polygon1 = mMap.addPolygon(new PolygonOptions()
                .clickable(true)
                .addAll(latLngList));

        for (int i = 0; i < latLngList.size(); i++) {
            addMarker(latLngList.get(i), 12);
        }

// Store a data object with the polygon, used here to indicate an arbitrary type.
        latLngBounds = getPolygonLatLngBounds(latLngList);
        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {

               /* String []str = polygon.getTag().toString().split("-");
                Intent intent = new Intent(GeoJsonActivity.this, AttributeChangesActivity.class);
                intent.putExtra("TYPE", str[1]);
                int tag = Integer.parseInt(str[0]);
                String data = new Gson().toJson(MasterfeaturList.get(tag));
                intent.putExtra("DATA", data);
                storeCurrentLatLngZoo();
                startActivity(intent);*/
            }
        });
        //showText(latLngList);
    }

    public void polygonNew(List<LatLng> latLngList, int type) {
        PolygonOptions polygon2 = new PolygonOptions()
                .clickable(true)
                .strokeColor(Color.RED)
                .addAll(latLngList);
        PolygonOptions polygon3 = new PolygonOptions()
                .clickable(true)
                .strokeColor(Color.GREEN)
                .addAll(latLngList);

        if (type == 1) {
            mMap.addPolygon(polygon2);
        } else {
            mMap.addPolygon(polygon3);
        }
    }


    private LatLngBounds getPolygonLatLngBounds(final List<LatLng> polygon) {
        final LatLngBounds.Builder centerBuilder = LatLngBounds.builder();
        for (LatLng point : polygon) {
            centerBuilder.include(point);
        }
        return centerBuilder.build();
    }

    List<CoordinateWithDistanceModel> DistanceList;
    List<MainDistanceDataModel> MainDataList;
    List<CoordinateWithDistanceModel> FirstSplitList;
    List<CoordinateWithDistanceModel> SecondSplitList;


    public void intersection(double startX, double startY, double endX, double endy) {
        // create ring: P1(0,0) - P2(0,10) - P3(10,10) - P4(0,10)
        Coordinate[] polygonArray = new Coordinate[polyGonCoordinateList.size()];
        MainDataList = new ArrayList<>();
        for (int i = 0; i < polyGonCoordinateList.size(); i++) {
            polygonArray[i] = polyGonCoordinateList.get(i);
        }
        LinearRing lr = new GeometryFactory().createLinearRing(polygonArray);
        //  LinearRing lr = new GeometryFactory().createLinearRing(new Coordinate[]{new Coordinate(14.171409, 76.641155), new Coordinate(14.160149, 76.640301), new Coordinate(14.160481, 76.660025), new Coordinate(14.172982, 76.660025), new Coordinate(14.171409, 76.641155)});
        //  LineString ls = new GeometryFactory().createLineString(new Coordinate[]{new Coordinate(13.058320, 77.496023), new Coordinate(13.058171, 77.496626)}); // vertical
        //  LineString ls = new GeometryFactory().createLineString(new Coordinate[]{new Coordinate(13.058530, 77.496375), new Coordinate(13.058015, 77.496271)}); // horizontal
        LineString ls = new GeometryFactory().createLineString(new Coordinate[]{new Coordinate(startX, startY), new Coordinate(endX, endy)}); // diagnol

        // calculate intersection points
        com.vividsolutions.jts.geom.Geometry intersectionPoints = lr.intersection(ls);
        // simple output of points

        int intersectingIndex = -1;
        for (Coordinate c : intersectionPoints.getCoordinates()) {
            DistanceList = new ArrayList<>();
            int i = 0;
            int j = 0;
            int size = polyGonCoordinateList.size() - 1;
            for (i = 0; i < size; i++) {
                j = i + 1;
                if (j >= size)
                    j = 0;
                if (lies_on_segment(c.x, c.y, polyGonCoordinateList.get(i).x, polyGonCoordinateList.get(i).y, polyGonCoordinateList.get(j).x, polyGonCoordinateList.get(j).y)) {
                    intersectingIndex = i;
                    CoordinateWithDistanceModel coordinateWithDistanceModel = new CoordinateWithDistanceModel();
                    coordinateWithDistanceModel.setPos(i);
                    coordinateWithDistanceModel.setLat(c.x);
                    coordinateWithDistanceModel.setLng(c.y);

                    coordinateWithDistanceModel.setStartx(polyGonCoordinateList.get(i).x);
                    coordinateWithDistanceModel.setStarty(polyGonCoordinateList.get(i).y);

                    coordinateWithDistanceModel.setEndx(polyGonCoordinateList.get(j).x);
                    coordinateWithDistanceModel.setEndy(polyGonCoordinateList.get(j).y);

                    //coordinateWithDistanceModel.setDist(distance(c.x, c.y, polyGonCoordinateList.get(j).x, polyGonCoordinateList.get(j).y, 0, 0));
                    DistanceList.add(coordinateWithDistanceModel);
                    Log.d(TAG, "intersection: " + polyGonCoordinateList.get(i).x + " " + polyGonCoordinateList.get(i).y + ", " + polyGonCoordinateList.get(j).x + " " + polyGonCoordinateList.get(j).y);
                    break;
                }
            }
            MainDistanceDataModel dataModel = new MainDistanceDataModel();
            dataModel.setLat(c.x);
            dataModel.setLng(c.y);
            //  Collections.sort(DistanceList, CoordinateWithDistanceModel::compareTo);
            dataModel.setDistanceList(DistanceList);
            MainDataList.add(dataModel);
        }
        FirstSplitList = new ArrayList<>();
        SecondSplitList = new ArrayList<>();
        CoordinateWithDistanceModel p1 = new CoordinateWithDistanceModel();
        CoordinateWithDistanceModel p2 = new CoordinateWithDistanceModel();
        p1 = MainDataList.get(0).getDistanceList().get(0);
        p2 = MainDataList.get(1).getDistanceList().get(0);


        FirstSplitList.add(p1);
        FirstSplitList.add(p2);
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

        SecondSplitList.add(p1);
        SecondSplitList.add(p2);
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
        mMap.clear();
        addMarker(new LatLng(startX, startY), 1);
        addMarker(new LatLng(endX, endy), 1);
        addMarker(new LatLng(p1.getLat(), p1.getLng()), 2);
        addMarker(new LatLng(p2.getLat(), p2.getLng()), 2);
        polygonNew(FSplitLtLnlist, 1);
        polygonNew(SSplitLtLnlist, 2);
    }


    public LatLng findNextPoint(double bearing, double distance, double lat, double lon) {
        double R = 6378.1;  //Radius of the Earth
        double brng = Math.toRadians(bearing);  //Bearing is 90 degrees converted to radians.
        double d = distance;  //Distance in km

        double lat2 = 52.20444; // - the lat result I'm hoping for
        double lon2 = 0.36056; // - the long result I'm hoping for

        double lat1 = Math.toRadians(lat); //Current lat point converted to radians
        double lon1 = Math.toRadians(lon); //Current long point converted to radians

        lat2 = Math.asin(Math.sin(lat1) * Math.cos(d / R) +
                Math.cos(lat1) * Math.sin(d / R) * Math.cos(brng));

        lon2 = lon1 + Math.atan2(Math.sin(brng) * Math.sin(d / R) * Math.cos(lat1),
                Math.cos(d / R) - Math.sin(lat1) * Math.sin(lat2));

        lat2 = Math.toDegrees(lat2);
        lon2 = Math.toDegrees(lon2);

        System.out.println(lat2 + ", " + lon2);
        LatLng latLng = new LatLng(lat2, lon2);
        addMarker(latLng, 12);
        return latLng;
    }

    public double calculateDirection(double srcLat, double srcLon, double destLat, double destLon) {
        double λ1 = srcLon, λ2 = destLon;
        double φ1 = srcLat, φ2 = destLat;

        System.out.println("φ1:" + φ1 + " φ2:" + φ2);

        double dif = Math.abs(λ2 - λ1);
        System.out.println("Diff:" + dif);
        double y = Math.sin(dif * Math.PI / 180) * Math.cos(φ2 * Math.PI / 180);

        double rad = φ1 * Math.PI / 180;
        System.out.println("1=" + Math.cos(rad) + " 2=" + Math.sin(φ2));

        double x = Math.cos(φ1 * Math.PI / 180) * Math.sin(φ2 * Math.PI / 180) - Math.sin(φ1 * Math.PI / 180) * Math.cos(φ2 * Math.PI / 180) * Math.cos(dif * Math.PI / 180);

        System.out.println("Y=" + y);
        System.out.println("X=" + x);

        double θ = Math.atan2(y, x);
        System.out.println("theta = " + θ);

        double brng = (θ * 180 / Math.PI + 360) % 360; // in degrees
        System.out.println("Direction:" + brng);

        return brng;

    }

    public LinearRing makeLineRing() {
        Coordinate[] polygonArray = new Coordinate[polyGonCoordinateList.size()];
        for (int i = 0; i < polyGonCoordinateList.size(); i++) {
            polygonArray[i] = polyGonCoordinateList.get(i);
        }
        return new GeometryFactory().createLinearRing(polygonArray);
    }

    public com.vividsolutions.jts.geom.Geometry getInteresectionPoints(LinearRing lr, LineString ls) {
        return lr.intersection(ls);
    }

    public void checkLineCrossingPolygonOrNot() {
        LinearRing lr = makeLineRing();
        List<CoordinateWithDistanceModel> SplitPolygonCoordinateList = new ArrayList<>();
        int NoOfIntersectionPoints = 0;

        CoordinateWithDistanceModel vertex = new CoordinateWithDistanceModel();
        vertex.setLat(13.058400353854454);
        vertex.setLng(77.49632477760315);
        // vertexList.add(vertex);
        addMarker(new LatLng(vertex.getLat(), vertex.getLng()), 3);

        CoordinateWithDistanceModel vertex2 = new CoordinateWithDistanceModel();
        vertex2.setLat(13.05830008579915);
        vertex2.setLng(77.4963066726923);
        // vertexList.add(vertex2);
        addMarker(new LatLng(vertex2.getLat(), vertex2.getLng()), 3);


        CoordinateWithDistanceModel vertex5 = new CoordinateWithDistanceModel();
        vertex5.setLat(13.058253);
        vertex5.setLng(77.496376);
        // vertexList.add(vertex5);
        addMarker(new LatLng(vertex5.getLat(), vertex5.getLng()), 3);

        CoordinateWithDistanceModel vertex6 = new CoordinateWithDistanceModel();
        vertex6.setLat(13.058221);
        vertex6.setLng(77.496396);
        //  vertexList.add(vertex6);
        addMarker(new LatLng(vertex6.getLat(), vertex6.getLng()), 3);


        CoordinateWithDistanceModel vertex3 = new CoordinateWithDistanceModel();
        vertex3.setLat(13.058178);
        vertex3.setLng(77.496347);
        //  vertexList.add(vertex3);
        addMarker(new LatLng(vertex3.getLat(), vertex3.getLng()), 3);

        CoordinateWithDistanceModel vertex4 = new CoordinateWithDistanceModel();
        vertex4.setLat(13.05802965579237);
        vertex4.setLng(77.49625872820617);
        // vertexList.add(vertex4);
        addMarker(new LatLng(vertex4.getLat(), vertex4.getLng()), 3);

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
                addMarker(new LatLng(intersectionPoints.getCoordinates()[0].x, intersectionPoints.getCoordinates()[0].y), 4);
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
        addPolyLine(latLngList);

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
        mMap.clear();
        //  addMarker(new LatLng(startX, startY), 1);
        // addMarker(new LatLng(endX, endy), 1);
        // addMarker(new LatLng(p1.getLat(), p1.getLng()), 2);
        // addMarker(new LatLng(p2.getLat(), p2.getLng()), 2);
        polygonNew(FSplitLtLnlist, 1);
        polygonNew(SSplitLtLnlist, 2);

    }

    public boolean lies_on_segment(double px, double py, double startx, double starty, double endx, double endy) {
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

    public double distance(double lat1, double lat2, double lon1, double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }
}



  /*  public static Geometry polygonize(Geometry geometry) {
        List lines = LineStringExtracter.getLines(geometry);
        Polygonizer polygonizer = new Polygonizer();
        polygonizer.add(lines);
        Collection polys = polygonizer.getPolygons();
        Polygon[] polyArray = GeometryFactory.toPolygonArray(polys);
        return geometry.getFactory().createGeometryCollection(polyArray);
    }

    public static Geometry splitPolygon(Geometry poly, Geometry line) {
        Geometry nodedLinework = poly.getBoundary().union(line);
        Geometry polys = polygonize(nodedLinework);

        // Only keep polygons which are inside the input
        List output = new ArrayList();
        for (int i = 0; i < polys.getNumGeometries(); i++) {
            Polygon candpoly = (Polygon) polys.getGeometryN(i);
            if (poly.contains(candpoly.getInteriorPoint())) {
                output.add(candpoly);
            }
        }
        return poly.getFactory().createGeometryCollection(GeometryFactory.toGeometryArray(output));
    }*/
