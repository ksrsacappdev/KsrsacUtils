package com.ksrsac.photolatlng;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.ksrsac.photolatlng.model.CoordinateWithDistanceModel;
import com.ksrsac.photolatlng.model.GeoJsonFeatureModel;
import com.ksrsac.photolatlng.model.GeoJsonGeometryModel;
import com.ksrsac.photolatlng.model.GeoJsonModel;
import com.vividsolutions.jts.geom.Coordinate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SampleTestActivity extends AppCompatActivity {
    private static final String TAG = "Sample:";
    double lat = 1721117, lon = 237134;
    String latString = "1721117", lonString = "237134";
    String alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String gridCode;
    TextView textView;
    int k = 0;
    String FiveK, oneK, hundred, twenty, four, one;
    String data;
    char FiveCrossFive[][] = new char[5][5];
    char fourCrossFour[][] = new char[4][4];
    String TenCrossTen[][] = new String[10][10];
    private EditText editTextLat, editTextLng;
    Button button, btnSplit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_test);
        editTextLat = findViewById(R.id.et_lat);
        editTextLng = findViewById(R.id.et_lng);
        button = findViewById(R.id.cal);
        btnSplit = findViewById(R.id.btn_split);
        List<CoordinateWithDistanceModel> polygonCoordinatesList = new ArrayList<>();
        List<CoordinateWithDistanceModel> verticesList = new ArrayList<>();
        CoordinateWithDistanceModel distanceModel1 = new CoordinateWithDistanceModel(13.058306, 77.496169,0,0 );
        CoordinateWithDistanceModel distanceModel2= new CoordinateWithDistanceModel(13.058201, 77.496499, 0,0);
        CoordinateWithDistanceModel distanceModel3 = new CoordinateWithDistanceModel(13.058098, 77.496449, 0,0);
        CoordinateWithDistanceModel distanceModel4 = new CoordinateWithDistanceModel(13.058163, 77.496158, 0,0);
        CoordinateWithDistanceModel distanceModel5 = new CoordinateWithDistanceModel(13.058219, 77.496488, 0,0);
        CoordinateWithDistanceModel distanceModel6 = new CoordinateWithDistanceModel(13.058155, 77.496465, 0,0);
        CoordinateWithDistanceModel distanceModel7 = new CoordinateWithDistanceModel(13.058072, 77.496393, 0,0);
        CoordinateWithDistanceModel distanceModel8 = new CoordinateWithDistanceModel(13.058058, 77.496264, 0,0);
        CoordinateWithDistanceModel distanceModel9 = new CoordinateWithDistanceModel(13.058137, 77.496122, 0,0);
        CoordinateWithDistanceModel distanceModel10 = new CoordinateWithDistanceModel(13.058325, 77.496154, 0,0);
        CoordinateWithDistanceModel distanceModel11 = new CoordinateWithDistanceModel();

        polygonCoordinatesList.add(distanceModel1);
        polygonCoordinatesList.add(distanceModel2);
        polygonCoordinatesList.add(distanceModel3);
        polygonCoordinatesList.add(distanceModel4);
        polygonCoordinatesList.add(distanceModel1);

        /*
        polygonCoordinatesList.add(distanceModel5);
        polygonCoordinatesList.add(distanceModel6);
        polygonCoordinatesList.add(distanceModel7);
        polygonCoordinatesList.add(distanceModel8);
        polygonCoordinatesList.add(distanceModel9);
        polygonCoordinatesList.add(distanceModel10);
        polygonCoordinatesList.add(distanceModel11);
        polygonCoordinatesList.add(distanceModel1);
*/

        CoordinateWithDistanceModel verticesModel1 = new CoordinateWithDistanceModel(13.058399, 77.496321, 0,0);
        CoordinateWithDistanceModel verticesModel2 = new CoordinateWithDistanceModel(13.058238, 77.496268, 0,0);
        CoordinateWithDistanceModel verticesModel3 = new CoordinateWithDistanceModel(13.058148, 77.496236, 0,0);
        CoordinateWithDistanceModel verticesModel4 = new CoordinateWithDistanceModel(13.057987, 77.496249, 0 ,0);
        verticesList.add(verticesModel1);
        verticesList.add(verticesModel2);
        verticesList.add(verticesModel3);
        verticesList.add(verticesModel4);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lat = Double.parseDouble(editTextLat.getText().toString());
                lon =  Double.parseDouble(editTextLng.getText().toString());
                Map<String, String> data = AppUtils.GetGridCode(lat, lon, 1);
                Log.d(TAG, "5K: "+data.get("5K"));
                Log.d(TAG, "1K: "+data.get("1K"));
                Log.d(TAG, "100M: "+data.get("100M"));
                Log.d(TAG, "20M: "+data.get("20M"));
                Log.d(TAG, "4M: "+data.get("4M"));
                Log.d(TAG, "1M: "+data.get("1M"));
              //  startCal();
            }
        });

        btnSplit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewPolygonSplitActivity.class);
                intent.putParcelableArrayListExtra("Coordinates", (ArrayList<? extends Parcelable>) polygonCoordinatesList);
                intent.putParcelableArrayListExtra("Vertices", (ArrayList<? extends Parcelable>) verticesList);
                startActivityForResult(intent, 2000);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2000 && resultCode==1)
        {
            Log.d(TAG, "onActivityResult: "+data.getParcelableArrayListExtra("FirstSplitList").size());
            Log.d(TAG, "onActivityResult: "+data.getParcelableArrayListExtra("SplittedSecondPolygon").size());
        }
    }
    List<Coordinate> polyGonCoordinateList;

    private void retrieveFileFromResource() {
        try {
            // GeoJsonLayer layer = new GeoJsonLayer(mMap, R.raw.geojson, this);
            JSONObject jsonFileObject = createJsonFileObject(this.getResources().openRawResource(R.raw.geo));
            polyGonCoordinateList = new ArrayList<>();
            GeoJsonModel geoJsonModel = new Gson().fromJson(jsonFileObject.toString(), GeoJsonModel.class);
            for(int i=0;i<geoJsonModel.getFeatures().size(); i++) {
                GeoJsonFeatureModel geoJsonFeatureModel = geoJsonModel.getFeatures().get(i);
                Map<String, String> map = (Map<String, String>) geoJsonFeatureModel.getProperties();
                geoJsonFeatureModel.setPropertiesHashmap(map);
                geoJsonModel.getFeatures().remove(i);
                geoJsonModel.getFeatures().add(i, geoJsonFeatureModel);

                if(geoJsonModel.getFeatures().get(i).getGeometry().getType().equalsIgnoreCase("Point"))
                {
                    GeoJsonGeometryModel geometryModel = geoJsonModel.getFeatures().get(i).getGeometry();
                    double[] coordinates = new double[geometryModel.getCoordinates().length];
                    Object[] values = geometryModel.getCoordinates();
                    // copy elements from object array to integer array
                    for (int j = 0; j < geometryModel.getCoordinates().length; j++)
                        coordinates[j] = (double) values[j];


                    LatLng latLng = new LatLng((double)values[1], (double)values[0]);

                    // addMarker(latLng, geoJsonFeatureModel.getPropertiesHashmap(), i);
                }
                else if(geoJsonModel.getFeatures().get(i).getGeometry().getType().equalsIgnoreCase("LineString")){

                    GeoJsonGeometryModel geometryModel = geoJsonModel.getFeatures().get(i).getGeometry();
                    List<Object> d= Arrays.asList(geometryModel.getCoordinates());
                    List<LatLng> polygonLatLngList = new ArrayList<>();

                    for (int l=0; l<d.size(); l++) {
                        Object bj = d.get(l);
                        List<Object> d3 = (List<Object>) (bj);
                        LatLng latLng = new LatLng((double)d3.get(1), (double)d3.get(0));
                        polygonLatLngList.add(latLng);
                    }
                   // addPolyLine(polygonLatLngList);
                }
                else{
                    GeoJsonGeometryModel geometryModel = geoJsonModel.getFeatures().get(i).getGeometry();
                    List<Object> d = Arrays.asList(geometryModel.getCoordinates());
                    List<LatLng> polygonLatLngList = new ArrayList<>();
                    Object bj = d.get(0);
                    List<List<Object>> d3 = (List<List<Object>>) (bj);
                    for(int k=0;k<d3.size();k++) {
                        if(d3.get(k)!=null) {
                            Object[] values = d3.get(k).toArray();
                            if (values != null && values[1] != null) {
                                LatLng latLng = new LatLng((double) values[1], (double) values[0]);
                                polygonLatLngList.add(latLng);
                                Log.d(TAG, "retrieveFileFromResource: "+values[1]+" "+values[0]);
                                polyGonCoordinateList.add(new Coordinate((double) values[1], (double) values[0]));
                            }
                        }
                    }
                    geometryModel.setPolygonLatLngList(polygonLatLngList);
                  //  polygon(polygonLatLngList);
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
    public void startCal(){
        lat = Double.parseDouble(editTextLat.getText().toString());
        lon = Double.parseDouble(editTextLng.getText().toString());
        latString = editTextLat.getText().toString();
        lonString = editTextLng.getText().toString();
        for(int i = 0; i<5; i++){
            for (int j=0; j<5; j++)
            {
                FiveCrossFive[i][j]=alphabets.charAt(k);
                k++;
                System.out.print(FiveCrossFive[i][j]+" ");
            }
            System.out.print("\n");
        }
        k = 0;
        for(int i = 0; i<4; i++){
            for (int j=0; j<4; j++)
            {
                fourCrossFour[i][j]=alphabets.charAt(k);
                k++;
                System.out.print(fourCrossFour[i][j]+" ");
            }
            System.out.print("\n");
        }
        k = 0;
        for(int i = 0; i<10; i++){
            for (int j=0; j<10; j++)
            {
                TenCrossTen[i][j]=alphabets.charAt(i)+""+j;
                System.out.print(TenCrossTen[i][j]+" ");
            }
            //k++;
            System.out.print("\n");
        }
        gridCode = alphabets.charAt(Integer.parseInt(lonString.charAt(0) + "") - 1) + "" + alphabets.charAt(Integer.parseInt(latString.substring(0, 2)) - 1);
        int longi = Integer.parseInt(latString.substring(2, 4));
        int lati = Integer.parseInt(lonString.substring(1, 3));
        int roundedLongi = (int) (5 * (Math.floor(Math.abs(longi / 5))));
        int roundedLati = (int) (5 * (Math.floor(Math.abs(lati / 5))));
        Log.d(TAG, "onCreate -  " + gridCode + roundedLati + "" + roundedLongi);
        if(roundedLati<10)
        {
            gridCode = gridCode + "0"+roundedLati + "" + roundedLongi;
        }
        else if(roundedLongi<10){
            gridCode = gridCode + roundedLati + "0" + roundedLongi;
        }
        else {
            gridCode = gridCode + roundedLati + "" + roundedLongi;
        }
        textView = findViewById(R.id.gridcode);
        data ="5K - "+gridCode;
        FiveK = gridCode;
        textView.setText(data);
        generate1kGrid(gridCode);
    }

    int row, col;
    private void generate1kGrid(String gridCode) {
        int lngv = (int) lon % 5000;
        int latv = (int) lat % 5000;
        Log.d(TAG, "generate1kGrid -  " + lngv + " " + latv);

        double latDiv = (double) latv / 1000;
        double lngDiv = (double) lngv / 1000;

        Log.d(TAG, "generate1kGrid -  " + latDiv + " " + lngDiv);
        int latCeil = (int) Math.floor(latDiv);
        int lngCeil = (int) Math.floor(lngDiv);

        row = latCeil;
        col = lngCeil;

        /*
        if(latCeil>0)
            row = latCeil-1;
        if(lngCeil>0)
            col = lngCeil-1;

         */
        Log.d(TAG, "generate1kGrid -  "+(gridCode+""+FiveCrossFive[row][col]));
        gridCode = gridCode+""+FiveCrossFive[row][col];
        data = data +"\n 1K - "+gridCode;
        textView.setText(data);
        oneK = gridCode;
        generate100MGrid(gridCode);
    }

    private void generate100MGrid(String gridCode) {
        int lngv = (int) lon % 1000;
        int latv = (int) lat % 1000;
        Log.d(TAG, "generate1kGrid -  " + lngv + " " + latv);

        double latDiv = (double) latv / 100;
        double lngDiv = (double) lngv / 100;

        Log.d(TAG, "generate1kGrid -  " + latDiv + " " + lngDiv);
        int latCeil = (int) Math.floor(latDiv);
        int lngCeil = (int) Math.floor(lngDiv);
        row = latCeil;
        col = lngCeil;
        /*
        if(latCeil>0)
            row = latCeil-1;
        if(lngCeil>0)
            col = lngCeil-1;

         */
        Log.d(TAG, "generate100MGrid -  " + latCeil + " " + lngCeil);

        Log.d(TAG, "generate100MGrid -  "+(gridCode+""+TenCrossTen[row][col]));
        gridCode = gridCode+""+TenCrossTen[row][col];
        data = data +"\n 100M - "+gridCode;
        textView.setText(data);
        hundred = gridCode;
        generate20MGrid(gridCode);
    }

    private void generate20MGrid(String gridCode) {
        int lngv = (int) lon % 100;
        int latv = (int) lat % 100;

        double latDiv = (double) latv / 20;
        double lngDiv = (double) lngv / 20;

        int latCeil = (int) Math.floor(latDiv);
        int lngCeil = (int) Math.floor(lngDiv);
        row = latCeil;
        col = lngCeil;
        /*
        if(latCeil>0)
            row = latCeil-1;
        if(lngCeil>0)
            col = lngCeil-1;

         */
        Log.d(TAG, "generate20MGrid -  " + latCeil + " " + lngCeil);

        Log.d(TAG, "generate20MGrid -  "+(gridCode+""+FiveCrossFive[row][col]));
        gridCode = gridCode+""+FiveCrossFive[row][col];
        data = data +"\n 20M - "+gridCode;
        textView.setText(data);
        twenty = gridCode;
        generate4MGrid(gridCode);

    }

    private void generate4MGrid(String gridCode) {
        int lngv = (int) lon % 20;
        int latv = (int) lat % 20;

        double latDiv = (double) latv / 4;
        double lngDiv = (double) lngv / 4;

        int latCeil = (int) Math.floor(latDiv);
        int lngCeil = (int) Math.floor(lngDiv);
        row = latCeil;
        col = lngCeil;
        /*

        if(latCeil>0)
            row = latCeil-1;
        if(lngCeil>0)
            col = lngCeil-1;



        if(latCeil>0)
        {

            if (lat % 1 != 0)
            {
                System.out.print ("Decimal");
            }
            else
            {
                System.out.print ("Integer");
            }
            if(lat % 1 != 0)
            {
                row = latCeil;
            }
            else
            {
                row = latCeil-1;
            }
        }
        if(lngCeil>0)
        {
            if(lon % 1 != 0){
                col = lngCeil;
            }
            else
            {
                col = lngCeil-1;
            }

        }

         */

        Log.d(TAG, "generate4MGrid  LAT CELIL-  " + latCeil + " " + lngCeil);

        Log.d(TAG, "generate4MGrid  CODE AND COLUMNS-  "+(gridCode+""+FiveCrossFive[row][col]));
        gridCode = gridCode+""+FiveCrossFive[row][col];
        data = data +"\n 4M - "+gridCode;
        textView.setText(data);
        four = gridCode;
        generate1MGrid(gridCode);
    }

    private void generate1MGrid(String gridCode) {
        int lngv = (int) lon % 4;
        int latv = (int) lat % 4;

        double latDiv = (double) latv / 1;
        double lngDiv = (double) lngv / 1;

        int latCeil = (int) Math.ceil(latDiv);
        int lngCeil = (int) Math.ceil(lngDiv);
        row = latCeil;
        col = lngCeil;
        if(latCeil>0)
        {

            if (lat % 1 != 0)
            {
                System.out.print ("Decimal");
            }
            else
            {
                System.out.print ("Integer");
            }
            if(lat % 1 != 0)
            {
                row = latCeil;
            }
            else
            {
                row = latCeil-1;
            }
        }
        if(lngCeil>0)
        {
            if(lon % 1 != 0){
                col = lngCeil;
            }
            else
            {
                col = lngCeil-1;
            }

        }
        Log.d(TAG, "generate1MGrid 1M  columns-  " + row + " " + col);

        Log.d(TAG, "generate1MGrid 1M-  "+(gridCode+""+fourCrossFour[row][col]));
        gridCode = gridCode+""+fourCrossFour[row][col];
        data = data +"\n 1M - "+gridCode;
        one = gridCode;
        textView.setText(data);
    }

    private void sendData()
    {
        Intent data = new Intent();
        data.putExtra("5K", FiveK);
        data.putExtra("1K", oneK);
        data.putExtra("100M", hundred);
        data.putExtra("20M", twenty);
        data.putExtra("4M", four);
        data.putExtra("1M", oneK);
        setResult(RESULT_OK, data);
        finish();
    }


}