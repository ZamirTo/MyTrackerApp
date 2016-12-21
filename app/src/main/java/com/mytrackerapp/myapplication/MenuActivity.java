package com.mytrackerapp.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.widget.Button;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class MenuActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private DatabaseReference mDatabase;
    private Button gpsBtn;
    private LocationManager locationManager;
    private LocationListener listener;
    private ZXingScannerView mScannerView;
    final private int REQUEST_PERMISSIONS = 1;
    private ArrayList<QR> modelItems;
    private String userKey;
    private String userName;
    private Button qrBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        qrBtn = (Button)findViewById(R.id.qrBtn);
        qrBtn.setEnabled(false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = mDatabase.getRoot().child("QR Tags");
        gpsBtn = (Button) findViewById(R.id.gpsBtn);
        modelItems = new ArrayList<QR>();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Intent intent = getIntent();
        Bundle extBundle = intent.getExtras();
        if(extBundle != null && !extBundle.isEmpty()) {
            String[] cords;
            boolean hasGpsCords = extBundle.containsKey("key");
            if (hasGpsCords) {
                cords = extBundle.getStringArray("key");
                userName = cords[0];
                userKey = cords[1];
            }
        }

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent intentBundle = new Intent(MenuActivity.this,MapsActivity.class);
                Bundle bundle = new Bundle();
                String[] cords = {userName,userKey,location.getLatitude()+"",location.getLongitude()+""};
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

        configure_button();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.e(this.getClass().getName(), "back button pressed");
        }
        startActivity(new Intent(this,MenuActivity.class));
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode ,String[] permissions, int[] grantResults) {
        switch(requestCode){
            case REQUEST_PERMISSIONS: {
                configure_button();
                break;
            }
            default: {
                break;
            }
        }
    }

    public void QrScanner(View view){
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    void configure_button(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
            //ActivityCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED &&
            //ActivityCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH_ADMIN)!=PackageManager.PERMISSION_GRANTED &&
            //ActivityCompat.checkSelfPermission(this,Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION}
                        //Manifest.permission.INTERNET,
                        //Manifest.permission.BLUETOOTH,
                        //Manifest.permission.BLUETOOTH_ADMIN}
                        ,REQUEST_PERMISSIONS);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                locationManager.requestSingleUpdate("gps", listener, null);
            }
        });
    }

    public void getFriends(View v){
        startActivity(new Intent(this,FriendsListActivity.class));
    }

    public void getBLE (View v) {
        Intent intentBundle = new Intent(MenuActivity.this,BLEActivity.class);
        Bundle bundle = new Bundle();
        String[] user = {userName,userKey};
        bundle.putStringArray("user", user);
        intentBundle.putExtras(bundle);
        startActivity(intentBundle);
    }

    public void logOutBtn(View v){
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mScannerView != null)
            mScannerView.stopCamera();
    }

    @Override
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
    }
}
