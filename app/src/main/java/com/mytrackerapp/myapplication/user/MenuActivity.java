package com.mytrackerapp.myapplication.user;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.mytrackerapp.myapplication.R;
import com.mytrackerapp.myapplication.json.QR;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MenuActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private Button gpsBtn;
    private LocationManager locationManager;
    //private ZXingScannerView mScannerView;
    final private int REQUEST_PERMISSIONS = 1;
    private ArrayList<QR> modelItems;
    private String userKey;
    private String userName;
    private Button qrBtn;
    private GPSTracker gps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        qrBtn = (Button) findViewById(R.id.qrBtn);
        qrBtn.setEnabled(false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = mDatabase.getRoot().child("QR Tags");
        gpsBtn = (Button) findViewById(R.id.gpsBtn);
        modelItems = new ArrayList<QR>();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Intent intent = getIntent();
        Bundle extBundle = intent.getExtras();
        if (extBundle != null && !extBundle.isEmpty()) {
            String[] cords;
            boolean hasGpsCords = extBundle.containsKey("key");
            if (hasGpsCords) {
                cords = extBundle.getStringArray("key");
                userName = cords[0];
                userKey = cords[1];
            }
        }

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    QR post = postSnapshot.getValue(QR.class);
                    modelItems.add(new QR(post.getID(), post.getCordinate1(), post.getCordinate2()));
                    System.out.println("QR DONE");
                    qrBtn.setEnabled(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        gpsBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // create class object
                gps = new GPSTracker(MenuActivity.this);

                // check if GPS enabled
                if(gps.canGetLocation()){

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    System.out.println("LAT "+latitude+", Lon "+longitude);
                    Intent intentBundle = new Intent(MenuActivity.this, MapsActivity.class);
                    Bundle bundle = new Bundle();
                    System.out.println(userName);
                    System.out.println(userKey);
                    String[] cords = {userName, userKey, latitude + "", longitude + ""};
                    bundle.putStringArray("cords", cords);
                    intentBundle.putExtras(bundle);
                    startActivity(intentBundle);

                    // \n is for new line
                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

            }
        });

        configure_button();
    }

    /**
     * goes back to menu activity
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.e(this.getClass().getName(), "back button pressed");
        }
        startActivity(new Intent(this, MenuActivity.class));
        return super.onKeyDown(keyCode, event);
    }

    /**
     * request permission for app requiers
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                configure_button();
                break;
            }
            default: {
                break;
            }
        }
    }

    public void qrClikcedBtn(View v){
        Intent intentBundle2 = new Intent(MenuActivity.this,CameraActivity.class);
        Bundle bundle2 = new Bundle();
        String[] cords2 = new String[modelItems.size()*3+2];
        cords2[0] = userName;
        cords2[1] = userKey;
        for (int i = 2, j = 0; j < modelItems.size(); i+=3, j++){
            cords2[i] = modelItems.get(j).getID();
            cords2[i+1] = modelItems.get(j).getCordinate1()+"";
            cords2[i+2] = modelItems.get(j).getCordinate2()+"";
        }
        bundle2.putStringArray("cords", cords2);
        intentBundle2.putExtras(bundle2);
        startActivity(intentBundle2);
    }


    /**
     * start the QR scanner
     * @param view
     */
    /*public void QrScanner(View view) {
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }*/

    /**
     * check if user had gave all the required permission
     * ask for any missing permission
     */
    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION}
                        , REQUEST_PERMISSIONS);
            }
            return;
        }
    }

    /**
     * open get friends activity
     * @param v
     */
    public void getFriends(View v){
        startActivity(new Intent(this,FriendsListActivity.class));
    }

    /**
     * open get BLE activity
     * @param v
     */
    public void getBLE (View v) {
        Intent intentBundle = new Intent(MenuActivity.this,BLEActivity.class);
        Bundle bundle = new Bundle();
        String[] user = {userName,userKey};
        bundle.putStringArray("user", user);
        intentBundle.putExtras(bundle);
        startActivity(intentBundle);
    }

    /**
     * logout from accont
     * @param v
     */
    public void logOutBtn(View v){
        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * stop the camera from QR scan
     */
    @Override
    public void onPause(){
        super.onPause();
        /*if(mScannerView != null)
            mScannerView.stopCamera();*/
    }

    /**
     * handle the result of the QR scan
     * send it to next activity
     * @param result
     */
    /*@Override
    public void handleResult(final Result result) {
        for (int i = 0; i < modelItems.size() ; i++) {
            if(modelItems.get(i).getID().equals(result.getText())){
                Intent intentBundle = new Intent(MenuActivity.this,MapsActivity.class);
                Bundle bundle = new Bundle();
                String[] cords = {userName,userKey,modelItems.get(i).getCordinate1()+"",modelItems.get(i).getCordinate2()+""};
                bundle.putStringArray("cords", cords);
                intentBundle.putExtras(bundle);
                startActivity(intentBundle);
                return;
            }
        }
        startActivity(new Intent(this,MenuActivity.class));
    }*/
}
