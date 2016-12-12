package com.mytrackerapp.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TechMenuActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_menu);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void onClickAddQR(View v){
        mDatabase = mDatabase.getRoot().child("QR Tags");
        //get location from GPS
        String ID = "id";
        String location = "123,456";
        //send Location to HASH
        //ID = getHashToLocation();
        QR qrToDB = new QR(ID,location.substring(0,location.indexOf(',')),location.substring(location.indexOf(',')+1));
        mDatabase.push().setValue(qrToDB);
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


}
