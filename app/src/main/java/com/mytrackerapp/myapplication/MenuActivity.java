package com.mytrackerapp.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static android.content.Context.LOCATION_SERVICE;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MenuActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private Button gpsBtn;
    private LocationManager locationManager;
    private LocationListener listener;
    private ZXingScannerView mScannerView;
    final private int REQUEST_GPS_PERMISSIONS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        gpsBtn = (Button) findViewById(R.id.gpsBtn);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent intentBundle = new Intent(MenuActivity.this,MapActivity.class);
                Bundle bundle = new Bundle();
                String[] cords = {location.getLongitude()+"",location.getLatitude()+""};
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
            case REQUEST_GPS_PERMISSIONS: {
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,REQUEST_GPS_PERMISSIONS);
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

    @Override
    public void onPause(){
        super.onPause();
        if(mScannerView != null)
            mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        Log.e("handler", result.getText());
        Log.e("handle", result.getBarcodeFormat().toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setMessage(result.getText());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.e("ResultClick", "" + i);
                dialogInterface.dismiss();

            }
        });

        AlertDialog alert1 = builder.create();
        alert1.show();

        startActivity(new Intent(this,MenuActivity.class));

    }
}
