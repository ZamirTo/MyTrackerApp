package com.mytrackerapp.myapplication.tech;

import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mytrackerapp.myapplication.R;
import com.mytrackerapp.myapplication.json.QR;
import com.mytrackerapp.myapplication.user.GPSTracker;

import java.util.HashMap;
import java.util.Map;

public class TechMenuActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private LocationManager locationManager;
    private LocationListener listener;
    private static Map<Integer, String> hash;
    private Button addQR;
    private Button addBLE;
    private String hashLocation;
    private double lat, lon;
    private String button;
    private GPSTracker gps;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_menu);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        button = "";
        hash = initHash();
        addQR = (Button) findViewById(R.id.addQRbtn);
        addBLE = (Button)findViewById(R.id.addBlebtn);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        gps = new GPSTracker(TechMenuActivity.this);

        if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        addBLE.setEnabled(true);
        addQR.setEnabled(true);
    }

    /**
     * move to add QR activity
     * @param v
     */
    public void onClickAddQR(View v) {
        addBLE.setEnabled(false);
        Intent intentBundle = new Intent(TechMenuActivity.this, QRGeneratorActivity.class);
        Bundle bundle = new Bundle();
        String hashLat = hashCord(latitude);
        String hashLon = hashCord(longitude);
        hashLocation = hashLat + hashLon;
        String[] cords = {hashLat, hashLon};
        mDatabase = mDatabase.getRoot().child("QR Tags");
        String ID = hashLocation;
        QR qrToDB = new QR(ID, Double.toString(latitude), Double.toString(longitude));
        mDatabase.push().setValue(qrToDB);
        bundle.putStringArray("cords", cords);
        intentBundle.putExtras(bundle);
        startActivity(intentBundle);
    }

    /**
     * move to add BLE activity
     * @param v
     */
    public void onClickAddBLE(View v){
        addQR.setEnabled(false);
        Intent intentBundle = new Intent(TechMenuActivity.this, TechAddBleActivity.class);
        Bundle bundle = new Bundle();
        String[] cords = {Double.toString(latitude), Double.toString(longitude)};
        bundle.putStringArray("cords", cords);
        intentBundle.putExtras(bundle);
        startActivity(intentBundle);
    }

    /**
     * init the hash for location cordinates
     * @return
     */
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

    /**
     * hash give cord and return its hash
     * @param cord - cord to hash
     * @return
     */
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

    /**
     * decrypt give hash cord
     * @param hashCord - hash cord to be decrypted
     * @return
     */
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
