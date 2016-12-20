package com.mytrackerapp.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private ListView mainListView ;
    private ArrayAdapter<newUserModel> listAdapter ;
    private ArrayAdapter<newQRModel> listAdapter2 ;
    private ArrayAdapter<newBLEModel> listAdapter3 ;
    private DatabaseReference mDatabase;
    private ArrayList<newUserModel> modelItems;
    private ArrayList<newUserModel> modelItemsAfterDelete;
    private ArrayList<newQRModel> qrItems;
    private ArrayList<newBLEModel> bleItems;
    private int whatAdapterIsSet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        mainListView = (ListView) findViewById( R.id.mainListViewAdmin );
        whatAdapterIsSet = 0;

        modelItemsAfterDelete = new ArrayList<newUserModel>();
        qrItems = new ArrayList<newQRModel>();
        bleItems = new ArrayList<newBLEModel>();
        modelItems = new ArrayList<newUserModel>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = mDatabase.getRoot().child("Users");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    newUser post = postSnapshot.getValue(newUser.class);
                    modelItems.add(new newUserModel(post.getName(),postSnapshot.getKey(),false));
                    System.out.println("Done Users");
                }
                mDatabase.removeEventListener(this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase = mDatabase.getRoot().child("QR Tags");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    QR post = postSnapshot.getValue(QR.class);
                    qrItems.add(new newQRModel(post.getID(),postSnapshot.getKey(),false));
                    System.out.println("Done QRs");
                }
                mDatabase.removeEventListener(this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase = mDatabase.getRoot().child("BLE Tags");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    BLE post = postSnapshot.getValue(BLE.class);
                    bleItems.add(new newBLEModel(post.getMacAddress(),postSnapshot.getKey(),false));
                    System.out.println("Done BELs");
                }
                mDatabase.removeEventListener(this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onClickGetUsers(View v){
        if(modelItems.size()!=0) {
            // When item is tapped, toggle checked properties of CheckBox and UsersList.
            mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick( AdapterView<?> parent, View item,
                                         int position, long id) {
                    newUserModel friend = listAdapter.getItem(position);
                    friend.toggleChecked();
                    AdminActivity.userViewHolder viewHolder = (AdminActivity.userViewHolder) item.getTag();
                    viewHolder.getCheckBox().setChecked(friend.isChecked());
                }
            });
            // Set our custom array adapter as the ListView's adapter.
            listAdapter = new AdminActivity.UsersArrayAdapter(this, modelItems);
            mainListView.setAdapter(listAdapter);
            whatAdapterIsSet = 1;
        }
    }

    public void onClickGetQRs(View v){
        if(qrItems.size()!=0) {
            // When item is tapped, toggle checked properties of CheckBox and UsersList.
            mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick( AdapterView<?> parent, View item,
                                         int position, long id) {
                    newQRModel qr = listAdapter2.getItem(position);
                    qr.toggleChecked();
                    AdminActivity.qrViewHolder viewHolder = (AdminActivity.qrViewHolder) item.getTag();
                    viewHolder.getCheckBox().setChecked(qr.isChecked());
                }
            });
            // Set our custom array adapter as the ListView's adapter.
            listAdapter2 = new AdminActivity.QRsArrayAdapter(this, qrItems);
            mainListView.setAdapter(listAdapter2);
            whatAdapterIsSet = 2;
        }
    }

    public void onClickGetBLEs(View v){
        if(bleItems.size()!=0) {
            // When item is tapped, toggle checked properties of CheckBox and UsersList.
            mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick( AdapterView<?> parent, View item,
                                         int position, long id) {
                    newBLEModel ble = listAdapter3.getItem(position);
                    ble.toggleChecked();
                    AdminActivity.bleViewHolder viewHolder = (AdminActivity.bleViewHolder) item.getTag();
                    viewHolder.getCheckBox().setChecked(ble.isChecked());
                }
            });
            // Set our custom array adapter as the ListView's adapter.
            listAdapter3 = new AdminActivity.BLEsArrayAdapter(this, bleItems);
            mainListView.setAdapter(listAdapter3);
            whatAdapterIsSet = 3;
        }
    }

    /** Holds child views for one row. */
    private static class userViewHolder {
        private CheckBox checkBox ;
        private TextView textView ;
        public userViewHolder() {}
        public userViewHolder(TextView textView, CheckBox checkBox ) {
            this.checkBox = checkBox ;
            this.textView = textView ;
        }
        public CheckBox getCheckBox() {
            return checkBox;
        }
        public void setCheckBox(CheckBox checkBox) {
            this.checkBox = checkBox;
        }
        public TextView getTextView() {
            return textView;
        }
        public void setTextView(TextView textView) {
            this.textView = textView;
        }
    }

    /** Holds child views for one row. */
    private static class qrViewHolder {
        private CheckBox checkBox ;
        private TextView textView ;
        public qrViewHolder() {}
        public qrViewHolder(TextView textView, CheckBox checkBox ) {
            this.checkBox = checkBox ;
            this.textView = textView ;
        }
        public CheckBox getCheckBox() {
            return checkBox;
        }
        public void setCheckBox(CheckBox checkBox) {
            this.checkBox = checkBox;
        }
        public TextView getTextView() {
            return textView;
        }
        public void setTextView(TextView textView) {
            this.textView = textView;
        }
    }

    private static class bleViewHolder {
        private CheckBox checkBox ;
        private TextView textView ;
        public bleViewHolder() {}
        public bleViewHolder(TextView textView, CheckBox checkBox ) {
            this.checkBox = checkBox ;
            this.textView = textView ;
        }
        public CheckBox getCheckBox() {
            return checkBox;
        }
        public void setCheckBox(CheckBox checkBox) {
            this.checkBox = checkBox;
        }
        public TextView getTextView() {
            return textView;
        }
        public void setTextView(TextView textView) {
            this.textView = textView;
        }
    }

    public void onClickRemove(View v){
        if(whatAdapterIsSet == 1) {
            mainListView.setOnItemClickListener(null);
            int size = listAdapter.getCount();
            for (int i = 0; i < size; i++) {
                if (listAdapter.getItem(i).isChecked()) {
                    for (int j = 0 ; j < modelItems.size() ; j++){
                        if(i!=j) {
                            modelItemsAfterDelete.add(modelItems.get(i));
                        }
                    }
                    mDatabase.getRoot().child("Users").child(modelItems.get(i).getCordinates()).removeValue();
                    //modelItems.remove(i);
                    //size = modelItems.size();
                    //i = -1;
                }
            }
            mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick( AdapterView<?> parent, View item,
                                         int position, long id) {
                    newUserModel friend = listAdapter.getItem(position);
                    friend.toggleChecked();
                    AdminActivity.userViewHolder viewHolder = (AdminActivity.userViewHolder) item.getTag();
                    viewHolder.getCheckBox().setChecked(friend.isChecked());
                }
            });
            // Set our custom array adapter as the ListView's adapter.
            listAdapter = new AdminActivity.UsersArrayAdapter(this, modelItemsAfterDelete);
            mainListView.setAdapter(listAdapter);
        }
    }

    /** Custom adapter for displaying an array of Friends objects. */
    private static class UsersArrayAdapter extends ArrayAdapter<newUserModel> {

        private LayoutInflater inflater;

        public UsersArrayAdapter(Context context, List<newUserModel> friendList ) {
            super( context, R.layout.simplerow, R.id.rowTextView, friendList );
            // Cache the LayoutInflate to avoid asking for a new one each time.
            inflater = LayoutInflater.from(context) ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Friend to display
            newUserModel friend = (newUserModel) this.getItem( position );

            // The child views in each row.
            CheckBox checkBox ;
            TextView textView ;

            // Create a new row view
            if ( convertView == null ) {
                convertView = inflater.inflate(R.layout.simplerow, null);

                // Find the child views.
                textView = (TextView) convertView.findViewById( R.id.rowTextView );
                checkBox = (CheckBox) convertView.findViewById( R.id.CheckBox01 );

                // Optimization: Tag the row with it's child views, so we don't have to
                // call findViewById() later when we reuse the row.
                convertView.setTag( new AdminActivity.userViewHolder(textView,checkBox) );

                // If CheckBox is toggled, update the Friend it is tagged with.
                checkBox.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        newUserModel friend = (newUserModel) cb.getTag();
                        friend.setChecked( cb.isChecked() );
                    }
                });
            }
            // Reuse existing row view
            else {
                // Because we use a ViewHolder, we avoid having to call findViewById().
                AdminActivity.userViewHolder viewHolder = (AdminActivity.userViewHolder) convertView.getTag();
                checkBox = viewHolder.getCheckBox() ;
                textView = viewHolder.getTextView() ;
            }

            // Tag the CheckBox with the frined it is displaying, so that we can
            // access the frined in onClick() when the CheckBox is toggled.
            checkBox.setTag( friend );

            // Display frined data
            checkBox.setChecked( friend.isChecked() );
            textView.setText( friend.getName() );

            return convertView;
        }

    }

    /** Custom adapter for displaying an array of Friends objects. */
    private static class QRsArrayAdapter extends ArrayAdapter<newQRModel> {

        private LayoutInflater inflater;

        public QRsArrayAdapter(Context context, List<newQRModel> friendList ) {
            super( context, R.layout.simplerow, R.id.rowTextView, friendList );
            // Cache the LayoutInflate to avoid asking for a new one each time.
            inflater = LayoutInflater.from(context) ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Friend to display
            newQRModel friend = (newQRModel) this.getItem( position );

            // The child views in each row.
            CheckBox checkBox ;
            TextView textView ;

            // Create a new row view
            if ( convertView == null ) {
                convertView = inflater.inflate(R.layout.simplerow, null);

                // Find the child views.
                textView = (TextView) convertView.findViewById( R.id.rowTextView );
                checkBox = (CheckBox) convertView.findViewById( R.id.CheckBox01 );

                // Optimization: Tag the row with it's child views, so we don't have to
                // call findViewById() later when we reuse the row.
                convertView.setTag( new AdminActivity.qrViewHolder(textView,checkBox) );

                // If CheckBox is toggled, update the Friend it is tagged with.
                checkBox.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        newQRModel friend = (newQRModel) cb.getTag();
                        friend.setChecked( cb.isChecked() );
                    }
                });
            }
            // Reuse existing row view
            else {
                // Because we use a ViewHolder, we avoid having to call findViewById().
                AdminActivity.qrViewHolder viewHolder = (AdminActivity.qrViewHolder) convertView.getTag();
                checkBox = viewHolder.getCheckBox() ;
                textView = viewHolder.getTextView() ;
            }

            // Tag the CheckBox with the frined it is displaying, so that we can
            // access the frined in onClick() when the CheckBox is toggled.
            checkBox.setTag( friend );

            // Display frined data
            checkBox.setChecked( friend.isChecked() );
            textView.setText( friend.getName() );

            return convertView;
        }

    }

    /** Custom adapter for displaying an array of Friends objects. */
    private static class BLEsArrayAdapter extends ArrayAdapter<newBLEModel> {

        private LayoutInflater inflater;

        public BLEsArrayAdapter(Context context, List<newBLEModel> friendList ) {
            super( context, R.layout.simplerow, R.id.rowTextView, friendList );
            // Cache the LayoutInflate to avoid asking for a new one each time.
            inflater = LayoutInflater.from(context) ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Friend to display
            newBLEModel friend = (newBLEModel) this.getItem( position );

            // The child views in each row.
            CheckBox checkBox ;
            TextView textView ;

            // Create a new row view
            if ( convertView == null ) {
                convertView = inflater.inflate(R.layout.simplerow, null);

                // Find the child views.
                textView = (TextView) convertView.findViewById( R.id.rowTextView );
                checkBox = (CheckBox) convertView.findViewById( R.id.CheckBox01 );

                // Optimization: Tag the row with it's child views, so we don't have to
                // call findViewById() later when we reuse the row.
                convertView.setTag( new AdminActivity.bleViewHolder(textView,checkBox) );

                // If CheckBox is toggled, update the Friend it is tagged with.
                checkBox.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        newBLEModel friend = (newBLEModel) cb.getTag();
                        friend.setChecked( cb.isChecked() );
                    }
                });
            }
            // Reuse existing row view
            else {
                // Because we use a ViewHolder, we avoid having to call findViewById().
                AdminActivity.bleViewHolder viewHolder = (AdminActivity.bleViewHolder) convertView.getTag();
                checkBox = viewHolder.getCheckBox() ;
                textView = viewHolder.getTextView() ;
            }

            // Tag the CheckBox with the frined it is displaying, so that we can
            // access the frined in onClick() when the CheckBox is toggled.
            checkBox.setTag( friend );

            // Display frined data
            checkBox.setChecked( friend.isChecked() );
            textView.setText( friend.getName() );

            return convertView;
        }

    }
}
