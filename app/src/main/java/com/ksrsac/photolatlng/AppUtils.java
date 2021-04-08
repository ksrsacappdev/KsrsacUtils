package com.ksrsac.photolatlng;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public  class AppUtils {
    private static final String TAG = "AppUtils";
    static double lat = 1721117, lon = 237134;
    static String latString = "1721117", lonString = "237134";
    static String alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static String gridCode;
    static int k = 0;
    static String FiveK, oneK, hundred, twenty, four, one;
    static String data;
    static char FiveCrossFive[][] = new char[5][5];
    static char fourCrossFour[][] = new char[4][4];
    static String TenCrossTen[][] = new String[10][10];
    Button button;
    public static Map<String, String> GetGridCode(double lat, double lon){
        return startCal(lat, lon);
    }
    public static Map<String, String> startCal(double lat, double lon){
        latString = lat+"";
        lonString = lon+"";
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
        data ="5K - "+gridCode;
        FiveK = gridCode;
        return generate1kGrid(gridCode);
    }

    static  int row, col;
    private static Map<String, String> generate1kGrid(String gridCode) {
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
        oneK = gridCode;
        return generate100MGrid(gridCode);
    }

    private static Map<String, String> generate100MGrid(String gridCode) {
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
        
        hundred = gridCode;
       return generate20MGrid(gridCode);
    }

    private static Map<String, String> generate20MGrid(String gridCode) {
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
        
        twenty = gridCode;
       return generate4MGrid(gridCode);

    }

    private static Map<String, String> generate4MGrid(String gridCode) {
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
        
        four = gridCode;
        return generate1MGrid(gridCode);
    }

    private static Map<String, String> generate1MGrid(String gridCode) {
        int lngv = (int) lon % 4;
        int latv = (int) lat % 4;

        double latDiv = (double) latv / 1;
        double lngDiv = (double) lngv / 1;

        int latCeil = (int) Math.ceil(latDiv);
        int lngCeil = (int) Math.ceil(lngDiv);
        row = latCeil;
        col = lngCeil;
        if(latCeil>0)
            row = latCeil-1;
        if(lngCeil>0)
            col = lngCeil-1;
        Log.d(TAG, "generate20MGrid -  " + latCeil + " " + lngCeil);

        Log.d(TAG, "generate1MGrid -  "+(gridCode+""+fourCrossFour[row][col]));
        gridCode = gridCode+""+fourCrossFour[row][col];
        data = data +"\n 1M - "+gridCode;
        one = gridCode;
        Map<String, String> data = new HashMap<>();
        data.put("5K", FiveK);
        data.put("1K", oneK);
        data.put("100M", hundred);
        data.put("20M", twenty);
        data.put("4M", four);
        data.put("1M", one);
        return data;
    }
}
