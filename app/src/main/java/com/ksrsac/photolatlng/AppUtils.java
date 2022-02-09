package com.ksrsac.photolatlng;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public  class AppUtils {

    public class Constants{
        public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 95;
        public static final int MY_PERMISSIONS_REQUEST_ACCESS_CAMERA = 97;
        public static final int MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE = 95;
        public static final int MY_PERMISSIONS_REQUEST_LOCATION = 96;
        public static final int MY_PERMISSIONS_REQUEST_ACCESS_WIFI_STATE = 98;
        public static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 97;
    }
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
    public static Map<String, String> GetGridCode(double lat, double lon, int type){
        if(type==1)
        {
            return startCal(lat, lon);
        }
        else if(type==2){
            Deg2UTM deg2UTM = new Deg2UTM(lat, lon);
            return startCal(deg2UTM.Easting, deg2UTM.Northing);
        }
        return null;
    }
    public static  Map<String, String> startCal(double latv, double lonv){
        latString = latv+"";
        lonString = lonv+"";
        lat = latv;
        lon = lonv;
        for(int i = 0; i<5; i++){
            for (int j=0; j<5; j++)
            {
                FiveCrossFive[i][j]=alphabets.charAt(k);
                k++;
                // System.out.print(FiveCrossFive[i][j]+" ");
            }
            //  System.out.print("\n");
        }
        k = 0;
        for(int i = 0; i<4; i++){
            for (int j=0; j<4; j++)
            {
                fourCrossFour[i][j]=alphabets.charAt(k);
                k++;
                //  System.out.print(fourCrossFour[i][j]+" ");
            }
            //   System.out.print("\n");
        }
        k = 0;
        for(int i = 0; i<10; i++){
            for (int j=0; j<10; j++)
            {
                TenCrossTen[i][j]=alphabets.charAt(i)+""+j;
                // System.out.print(TenCrossTen[i][j]+" ");
            }
            //k++;
            //   System.out.print("\n");
        }
        gridCode = alphabets.charAt(Integer.parseInt(lonString.charAt(0) + "") - 1) + "" + alphabets.charAt(Integer.parseInt(latString.substring(0, 2)) - 1);
        int longi = Integer.parseInt(latString.substring(2, 4));
        int lati = Integer.parseInt(lonString.substring(1, 3));
        int roundedLongi = (int) (5 * (Math.floor(Math.abs(longi / 5))));
        int roundedLati = (int) (5 * (Math.floor(Math.abs(lati / 5))));
        if(roundedLati<10)
        {
            if(roundedLongi==0 && roundedLati==0) {
                gridCode = gridCode + "0"+roundedLati + "0" + roundedLongi;
            }


            else if(roundedLongi<10)
            {

                gridCode = gridCode + "0"+roundedLati + "0" + roundedLongi;
            }
            else
            {
                gridCode = gridCode + "0"+roundedLati + "" + roundedLongi;
            }

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

    static int row;
    static int col;
    private static  Map<String, String> generate1kGrid(String gridCode) {
        int lngv = (int) lon % 5000;
        int latv = (int) lat % 5000;

        double latDiv = (double) latv / 1000;
        double lngDiv = (double) lngv / 1000;

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
        gridCode = gridCode+""+FiveCrossFive[row][col];
        data = data +"\n 1K - "+gridCode;
        oneK = gridCode;
        return generate100MGrid(gridCode);
    }

    private static  Map<String, String> generate100MGrid(String gridCode) {
        int lngv = (int) lon % 1000;
        int latv = (int) lat % 1000;

        double latDiv = (double) latv / 100;
        double lngDiv = (double) lngv / 100;

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

        gridCode = gridCode+""+TenCrossTen[row][col];
        data = data +"\n 100M - "+gridCode;
        hundred = gridCode;
        return generate20MGrid(gridCode);
    }

    private static  Map<String, String> generate20MGrid(String gridCode) {
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

        gridCode = gridCode+""+FiveCrossFive[row][col];
        data = data +"\n 20M - "+gridCode;
        twenty = gridCode;
        return generate4MGrid(gridCode);

    }

    private static  Map<String, String> generate4MGrid(String gridCode) {
        int lngv = (int) lon % 20;
        int latv = (int) lat % 20;

        double latDiv = (double) latv / 4;
        double lngDiv = (double) lngv / 4;

        int latCeil = (int) Math.floor(latDiv);
        int lngCeil = (int) Math.floor(lngDiv);
        row = latCeil;
        col = lngCeil;
        double d = lon;
        if((d-(int)d)!=0)
        {
            //System.out.println("decimal value is there");

        }
        else
        {
            //System.out.println("decimal value is not there");
            if(lon%4==0)
            {
                if(col!=0)
                {
                    col-=1;
                }
            }
        }

        double d2 = lat;
        if((d2-(int)d2)!=0)
        {
            //System.out.println("decimal value is there");

        }
        else
        {
            //System.out.println("decimal value is not there");
            if(lat%4==0)
            {
                if(row!=0)
                    row-=1;
            }
        }

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


        gridCode = gridCode+""+FiveCrossFive[row][col];
        data = data +"\n 4M - "+gridCode;
        four = gridCode;
        return generate1MGrid(gridCode);
    }

    private static  Map<String, String> generate1MGrid(String gridCode) {
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



    public static class Deg2UTM{
        double Easting;
        double Northing;
        int Zone;
        char Letter;
        private Deg2UTM(double Lat,double Lon)
        {
            Zone= (int) Math.floor(Lon/6+31);
            if (Lat<-72)
                Letter='C';
            else if (Lat<-64)
                Letter='D';
            else if (Lat<-56)
                Letter='E';
            else if (Lat<-48)
                Letter='F';
            else if (Lat<-40)
                Letter='G';
            else if (Lat<-32)
                Letter='H';
            else if (Lat<-24)
                Letter='J';
            else if (Lat<-16)
                Letter='K';
            else if (Lat<-8)
                Letter='L';
            else if (Lat<0)
                Letter='M';
            else if (Lat<8)
                Letter='N';
            else if (Lat<16)
                Letter='P';
            else if (Lat<24)
                Letter='Q';
            else if (Lat<32)
                Letter='R';
            else if (Lat<40)
                Letter='S';
            else if (Lat<48)
                Letter='T';
            else if (Lat<56)
                Letter='U';
            else if (Lat<64)
                Letter='V';
            else if (Lat<72)
                Letter='W';
            else
                Letter='X';
            Easting=0.5*Math.log((1+Math.cos(Lat*Math.PI/180)*Math.sin(Lon*Math.PI/180-(6*Zone-183)*Math.PI/180))/(1-Math.cos(Lat*Math.PI/180)*Math.sin(Lon*Math.PI/180-(6*Zone-183)*Math.PI/180)))*0.9996*6399593.62/Math.pow((1+Math.pow(0.0820944379, 2)*Math.pow(Math.cos(Lat*Math.PI/180), 2)), 0.5)*(1+ Math.pow(0.0820944379,2)/2*Math.pow((0.5*Math.log((1+Math.cos(Lat*Math.PI/180)*Math.sin(Lon*Math.PI/180-(6*Zone-183)*Math.PI/180))/(1-Math.cos(Lat*Math.PI/180)*Math.sin(Lon*Math.PI/180-(6*Zone-183)*Math.PI/180)))),2)*Math.pow(Math.cos(Lat*Math.PI/180),2)/3)+500000;
            Easting=Math.round(Easting*100)*0.01;
            Northing = (Math.atan(Math.tan(Lat*Math.PI/180)/Math.cos((Lon*Math.PI/180-(6*Zone -183)*Math.PI/180)))-Lat*Math.PI/180)*0.9996*6399593.625/Math.sqrt(1+0.006739496742*Math.pow(Math.cos(Lat*Math.PI/180),2))*(1+0.006739496742/2*Math.pow(0.5*Math.log((1+Math.cos(Lat*Math.PI/180)*Math.sin((Lon*Math.PI/180-(6*Zone -183)*Math.PI/180)))/(1-Math.cos(Lat*Math.PI/180)*Math.sin((Lon*Math.PI/180-(6*Zone -183)*Math.PI/180)))),2)*Math.pow(Math.cos(Lat*Math.PI/180),2))+0.9996*6399593.625*(Lat*Math.PI/180-0.005054622556*(Lat*Math.PI/180+Math.sin(2*Lat*Math.PI/180)/2)+4.258201531e-05*(3*(Lat*Math.PI/180+Math.sin(2*Lat*Math.PI/180)/2)+Math.sin(2*Lat*Math.PI/180)*Math.pow(Math.cos(Lat*Math.PI/180),2))/4-1.674057895e-07*(5*(3*(Lat*Math.PI/180+Math.sin(2*Lat*Math.PI/180)/2)+Math.sin(2*Lat*Math.PI/180)*Math.pow(Math.cos(Lat*Math.PI/180),2))/4+Math.sin(2*Lat*Math.PI/180)*Math.pow(Math.cos(Lat*Math.PI/180),2)*Math.pow(Math.cos(Lat*Math.PI/180),2))/3);
            if (Letter<'M')
                Northing = Northing + 10000000;
            Northing=Math.round(Northing*100)*0.01;
        }
    }

    public class UTM2Deg
    {
        double latitude;
        double longitude;
        private  UTM2Deg(String UTM)
        {
            String[] parts=UTM.split(" ");
            int Zone=Integer.parseInt(parts[0]);
            char Letter=parts[1].toUpperCase(Locale.ENGLISH).charAt(0);
            double Easting=Double.parseDouble(parts[2]);
            double Northing=Double.parseDouble(parts[3]);
            double Hem;
            if (Letter>'M')
                Hem='N';
            else
                Hem='S';
            double north;
            if (Hem == 'S')
                north = Northing - 10000000;
            else
                north = Northing;
            latitude = (north/6366197.724/0.9996+(1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)-0.006739496742*Math.sin(north/6366197.724/0.9996)*Math.cos(north/6366197.724/0.9996)*(Math.atan(Math.cos(Math.atan(( Math.exp((Easting - 500000) / (0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((Easting - 500000) / (0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2)/3))-Math.exp(-(Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*( 1 -  0.006739496742*Math.pow((Easting - 500000) / (0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2)/3)))/2/Math.cos((north-0.9996*6399593.625*(north/6366197.724/0.9996-0.006739496742*3/4*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996 )/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/3))/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2))+north/6366197.724/0.9996)))*Math.tan((north-0.9996*6399593.625*(north/6366197.724/0.9996 - 0.006739496742*3/4*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996 )*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/3))/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2))+north/6366197.724/0.9996))-north/6366197.724/0.9996)*3/2)*(Math.atan(Math.cos(Math.atan((Math.exp((Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2)/3))-Math.exp(-(Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2)/3)))/2/Math.cos((north-0.9996*6399593.625*(north/6366197.724/0.9996-0.006739496742*3/4*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/3))/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2))+north/6366197.724/0.9996)))*Math.tan((north-0.9996*6399593.625*(north/6366197.724/0.9996-0.006739496742*3/4*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/3))/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2))+north/6366197.724/0.9996))-north/6366197.724/0.9996))*180/Math.PI;
            latitude=Math.round(latitude*10000000);
            latitude=latitude/10000000;
            longitude =Math.atan((Math.exp((Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2)/3))-Math.exp(-(Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2)/3)))/2/Math.cos((north-0.9996*6399593.625*( north/6366197.724/0.9996-0.006739496742*3/4*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.pow(0.006739496742*3/4,2)*5/3*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2* north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4-Math.pow(0.006739496742*3/4,3)*35/27*(5*(3*(north/6366197.724/0.9996+Math.sin(2*north/6366197.724/0.9996)/2)+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/4+Math.sin(2*north/6366197.724/0.9996)*Math.pow(Math.cos(north/6366197.724/0.9996),2)*Math.pow(Math.cos(north/6366197.724/0.9996),2))/3)) / (0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2))))*(1-0.006739496742*Math.pow((Easting-500000)/(0.9996*6399593.625/Math.sqrt((1+0.006739496742*Math.pow(Math.cos(north/6366197.724/0.9996),2)))),2)/2*Math.pow(Math.cos(north/6366197.724/0.9996),2))+north/6366197.724/0.9996))*180/Math.PI+Zone*6-183;
            longitude=Math.round(longitude*10000000);
            longitude=longitude/10000000;
        }
    }
}
