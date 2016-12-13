package com.mytrackerapp.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MapActivity extends AppCompatActivity {

    TextView mapCords = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapCords = (TextView)findViewById(R.id.gpsCords);
        Intent intent = getIntent();
        Bundle extBundle = intent.getExtras();

        if(!extBundle.isEmpty()){
            boolean hasGpsCords = extBundle.containsKey("cords");
            if(hasGpsCords){
                String[] cords = extBundle.getStringArray("cords");
                mapCords.setText(cords[0]+" , "+cords[1]);
            }
        }
    }
}
