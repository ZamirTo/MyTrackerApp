package com.mytrackerapp.myapplication.user;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.Result;
import com.mytrackerapp.myapplication.R;
import com.mytrackerapp.myapplication.json.QR;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class CameraActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private ZXingScannerView mScannerView;
    private ArrayList<QR> modelItems;
    String userName;
    String userKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        modelItems = new ArrayList<QR>();

        Intent intent = getIntent();
        Bundle extBundle = intent.getExtras();
        if (extBundle != null && !extBundle.isEmpty()) {
            String[] cords;
            boolean hasGpsCords = extBundle.containsKey("cords");
            if (hasGpsCords) {
                cords = extBundle.getStringArray("cords");
                userName = cords[0];
                userKey = cords[1];
                System.out.println(userKey+","+userKey);
                for (int i = 2; i<cords.length;i+=3) {
                    modelItems.add(new QR(cords[i],cords[i+1],cords[i+2]));
                    System.out.println(cords[i]);
                    System.out.println(cords[i+1]);
                    System.out.println(cords[i+2]);
                }
                System.out.println("done");
            }
        }
    }

    /**
     * stop the camera from QR scan
     */
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
                Intent intentBundle = new Intent(CameraActivity.this,MapsActivity.class);
                Bundle bundle = new Bundle();
                String[] cords = {userName,userKey,modelItems.get(i).getCordinate1()+"",modelItems.get(i).getCordinate2()+""};
                bundle.putStringArray("cords", cords);
                intentBundle.putExtras(bundle);
                startActivity(intentBundle);
                return;
            }
        }
        Toast.makeText(this,"Nothing found",Toast.LENGTH_SHORT).show();
    }
}
