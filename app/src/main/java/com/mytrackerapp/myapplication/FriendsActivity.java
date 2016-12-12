package com.mytrackerapp.myapplication;

import android.os.Bundle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import java.util.LinkedList;



public class FriendsActivity extends Activity {
    ListView lv;
    CustomAdapter adapter;
    LinkedList<newUserModel> modelItems;
    private DatabaseReference mDatabase;
    int index = 0;
    Button friendsListbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_activity);
        friendsListbtn = (Button)findViewById(R.id.btn_run);
        friendsListbtn.setEnabled(false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = mDatabase.getRoot().child("Users");
        lv = (ListView) findViewById(R.id.listView1);
        modelItems = new LinkedList<newUserModel>();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    newUser post = postSnapshot.getValue(newUser.class);
                    modelItems.add(new newUserModel(post.getName(),true));
                    System.out.println("DONE");
                }
                friendsListbtn.setEnabled(true);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onClick(View V){
        if(modelItems.size()!=0) {
            adapter = new CustomAdapter(this, modelItems);
            lv.setAdapter(adapter);
        }
    }

    public void onClick2(View V){
        if(modelItems.size()!=0) {
            for (int x = 0 ; x < modelItems.size() ; x++){
                //System.out.println();
                /*if (modelItems.get(x).getValue()==1){
                    System.out.println("CHEKECED:" + modelItems.get(x).getName());
                }*/
            }
        }
    }
}