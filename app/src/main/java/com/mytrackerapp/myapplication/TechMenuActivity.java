package com.mytrackerapp.myapplication;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class TechMenuActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private LocationManager locationManager;
    private LocationListener listener;
    private static Map<Integer, String> hash;

    Button addQR;

    private String hashLocation;
    private double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_menu);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        hash = initHash();
        addQR = (Button) findViewById(R.id.addQRbtn);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                System.out.println("Location changed");
                Intent intentBundle = new Intent(TechMenuActivity.this, QRGeneratorActivity.class);
                Bundle bundle = new Bundle();
                lat = location.getLatitude();
                lon = location.getLongitude();
                System.out.println("Lat: " + lat);
                System.out.println("Lon: " + lon);
                String hashLat = hashCord(lat);
                String hashLon = hashCord(lon);
                hashLocation = hashLat + hashLon;
                String[] cords = {hashLat, hashLon};
                System.out.println("Hahs Lat :" + hashLat);
                System.out.println("Hahs Lon :" + hashLon);
                mDatabase = mDatabase.getRoot().child("QR Tags");
                String ID = hashLocation;
                QR qrToDB = new QR(ID,Double.toString(lat),Double.toString(lon));
                mDatabase.push().setValue(qrToDB);
                bundle.putStringArray("cords", cords);
                intentBundle.putExtras(bundle);
                startActivity(intentBundle);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
    }

    public void onClickAddQR(View v) {

        addQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                if (ActivityCompat.checkSelfPermission(TechMenuActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(TechMenuActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION},1);
                    }
                }
                locationManager.requestSingleUpdate("gps", listener, null);
            }
        });
        /*
        mDatabase = mDatabase.getRoot().child("QR Tags");
        String ID = hashLocation;
        String location = lat + "," + lon;
        QR qrToDB = new QR(ID,location.substring(0,location.indexOf(',')),location.substring(location.indexOf(',')+1));
        mDatabase.push().setValue(qrToDB);
        */
    }

    public void onClickAddBLE(View v){
        mDatabase = mDatabase.getRoot().child("BLE Tags");
        //get location from GPS
        String ID = "MAC";
        String location = "123,456";
        //send Location to HASH
        //ID = getHashToLocation();
        BLE qrToDB = new BLE(ID,location.substring(0,location.indexOf(',')),location.substring(location.indexOf(',')+1));
        mDatabase.push().setValue(qrToDB);
    }

    private static Map<Integer, String> initHash(){
        Map<Integer, String> hash = new HashMap<Integer, String>();

        // create hash for 94 signs
        for (int i = 33, j = 0; i < 127; i++, j++) {
            hash.put(j, (char)i+"");
        }

        hash.put(94, "ש");
        hash.put(95, "כ");
        hash.put(96, "ד");
        hash.put(97, "מ");
        hash.put(98, "ק");
        hash.put(99, "ג");

        //		for(Map.Entry<Integer, String> entry : hash.entrySet()) {
        //			int key = entry.getKey();
        //			String value = entry.getValue();
        //			System.out.println("Key: " + key +" Val: " + value);
        //		}

        return hash;
    }

    private static String hashCord(double cord){

        String s = Double.toString(cord);

        // check how much digit are before '.'
        int checkSum = s.indexOf('.');

        // check sign -> negative 1, else 0
        int checkSign = cord > 0 ? 0 : 1;

        // remove minus if exsist
        if(checkSign == 1){
            s = s.substring(1);
            checkSum--;
        }


        String first = s.substring(0, checkSum);
        String sec = s.substring(checkSum+1);

        // split each part of string to 2 chars string
        String[] pre = first.split("(?<=\\G..)");
        String[] post = new String[3];
        for (int i = 0; i < 3; i++){
            post[i] = sec.substring(i*2, i*2+2);
        }

        // hash the 2 chars to new char
        StringBuffer hashCord = new StringBuffer();
        for (String part : pre) {
            hashCord.append(hash.get(Integer.parseInt(part)));
        }

        for (String part : post) {
            hashCord.append(hash.get(Integer.parseInt(part)));
        }

        //add the checksum and checksign
        hashCord.append(checkSum);
        hashCord.append(checkSign);

        return hashCord.toString();
    }

    private static double reHashCord(String hashCord){
        StringBuffer cord = new StringBuffer();
        if(hashCord.endsWith("1"))
            cord.append("-");

        // get num of digit before '.'
        int preDigit = Integer.parseInt(hashCord.substring(hashCord.length()-2, hashCord.length()-1));

        String pre, post;

        if(preDigit < 3){
            pre = hashCord.substring(0, 1);
            post = hashCord.substring(1);
        }
        else{
            pre = hashCord.substring(0, 2);
            post = hashCord.substring(2);
        }

        // dec the part before '.'
        for (int i = 0; i < pre.length(); i++) {
            for (Map.Entry<Integer, String> entry : hash.entrySet()) {
                if (entry.getValue().equals(pre.charAt(i)+"")) {
                    cord.append(entry.getKey());
                }
            }
        }
        cord.append(".");

        // dec the part after '.'
        for (int i = 0; i < post.length()-2; i++) {
            for (Map.Entry<Integer, String> entry : hash.entrySet()) {
                if (entry.getValue().equals(post.charAt(i)+"")) {
                    cord.append(entry.getKey());
                }
            }
        }
        return Double.valueOf(cord.toString());
    }

}
