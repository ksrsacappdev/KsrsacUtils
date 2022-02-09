package com.ksrsac.photolatlng;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Map;

public class GridCodeGenerationActivity extends AppCompatActivity {
    private static final String TAG = "GridCodeGenerationActiv";
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
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_code_generation);
        editTextLat = findViewById(R.id.et_lat);
        editTextLng = findViewById(R.id.et_lng);
        button = findViewById(R.id.cal);
        button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               lat = Double.parseDouble(editTextLat.getText().toString());
               lon =  Double.parseDouble(editTextLng.getText().toString());
               Map<String, String> data = AppUtils.GetGridCode(lat, lon, 1);
               Log.d(TAG, "onClick: "+data.get("5K"));
           }
       });

    }

    public void startCal(){
        lat = Double.parseDouble(editTextLat.getText().toString());
        lon =  Double.parseDouble(editTextLng.getText().toString());
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
        int latCeil = (int) Math.ceil(latDiv);
        int lngCeil = (int) Math.ceil(lngDiv);

        row = latCeil;
        col = lngCeil;
        if(latCeil>0)
            row = latCeil-1;
        if(lngCeil>0)
            col = lngCeil-1;

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
        int latCeil = (int) Math.ceil(latDiv);
        int lngCeil = (int) Math.ceil(lngDiv);
        row = latCeil;
        col = lngCeil;
        if(latCeil>0)
            row = latCeil-1;
        if(lngCeil>0)
            col = lngCeil-1;
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

        int latCeil = (int) Math.ceil(latDiv);
        int lngCeil = (int) Math.ceil(lngDiv);
        row = latCeil;
        col = lngCeil;
        if(latCeil>0)
            row = latCeil-1;
        if(lngCeil>0)
            col = lngCeil-1;
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

        int latCeil = (int) Math.ceil(latDiv);
        int lngCeil = (int) Math.ceil(lngDiv);
        row = latCeil;
        col = lngCeil;
        if(latCeil>0)
            row = latCeil-1;
        if(lngCeil>0)
            col = lngCeil-1;
        Log.d(TAG, "generate20MGrid -  " + latCeil + " " + lngCeil);

        Log.d(TAG, "generate4MGrid -  "+(gridCode+""+FiveCrossFive[row][col]));
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