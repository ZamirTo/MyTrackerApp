package com.mytrackerapp.myapplication;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MenuActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView mScannerView;
    final private int REQUEST_CAMERA_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ActivityCompat.requestPermissions(MenuActivity.this,
                new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSIONS);

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
            case REQUEST_CAMERA_PERMISSIONS :{
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Permission", "Camera permission granted");
                } else {
                    Log.e("Permission", "Camera permission denayied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    public void QrScanner(View view){
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
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
