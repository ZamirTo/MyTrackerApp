package com.mytrackerapp.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class BLEActivity extends Activity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private int REQUEST_ENABLE_BT = 1; // Any positive integer should work.
    private BluetoothAdapter mBluetoothAdapter;
    private Button button_scanBT;
    private Button button_getBLS;
    private Button goToLocBtn;
    private ListView mainListView ;
    private ArrayAdapter<BluetoothObject> listAdapter;
    private ArrayList<BluetoothObject> scannedDev;
    private ArrayList<BLE> bleDevices;
    private String user;
    private String userKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        Intent intent = getIntent();
        Bundle extBundle = intent.getExtras();
        if(!extBundle.isEmpty()) {
            boolean hasUserDetail = extBundle.containsKey("user");
            if(hasUserDetail){
                String userDetail[] = extBundle.getStringArray("user");
                user = userDetail[0];
                userKey = userDetail[1];
            }
        }
        bleDevices = new ArrayList<BLE>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = mDatabase.getRoot().child("BLE Tags");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        button_scanBT = (Button) findViewById(R.id.button_scanBT);
        button_getBLS = (Button) findViewById(R.id.getBLEBtn);
        goToLocBtn = (Button) findViewById(R.id.goToLocBtn);
        scannedDev = new ArrayList<BluetoothObject>();
        // Find the ListView resource.
        mainListView = (ListView) findViewById( R.id.mainListView );
        enableBluetoothOnDevice();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("onAuthStateChanged", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("onAuthStateChanged", "onAuthStateChanged:signed_out");
                }
            }
        };
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    BLE post = postSnapshot.getValue(BLE.class);
                    bleDevices.add(new BLE(post.getMacAddress(), post.getCordinate1(), post.getCordinate2()));
                }
                System.out.println("Done loading BLE from DB");
                System.out.println("BLE Device size: " + bleDevices.size());
                button_scanBT.setEnabled(true);
                button_getBLS.setEnabled(true);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        button_getBLS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                getList();
            }
        });

        button_scanBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainListView.setAdapter(null);
                scannedDev.clear();
                startScanning();
            }
        });
    }//end onCreate


    /**
     * Start scan for BT device nearby
     */
    private void startScanning() {
        //disable goToLocBtn
        goToLocBtn.setEnabled(false);
        // start looking for bluetooth devices
        mBluetoothAdapter.startDiscovery();
        // Discover new devices
        // Create a BroadcastReceiver for ACTION_FOUND
        final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)){
                    System.out.println("FOUND ONE");
                    // Get the bluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Get the "RSSI" to get the signal strength as integer,
                    // but should be displayed in "dBm" units
                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                    // Create the device object and add it to the arrayList of devices
                    BluetoothObject bluetoothObject = new BluetoothObject();
                    bluetoothObject.setBluetooth_name(device.getName());
                    bluetoothObject.setBluetooth_address(device.getAddress());
                    bluetoothObject.setBluetooth_state(device.getBondState());
                    bluetoothObject.setBluetooth_type(device.getType());    // requires API 18 or higher
                    bluetoothObject.setBluetooth_uuids(device.getUuids());
                    bluetoothObject.setBluetooth_rssi(rssi);
                    System.out.println(scannedDev.size());
                    if(scannedDev.size()==0){
                        scannedDev.add(bluetoothObject);
                        goToLocBtn.setEnabled(true);
                    }
                    else if(scannedDev.size()!=0){
                        boolean addToArray = true;
                        for (int i = 0 ; i < scannedDev.size() ; i++) {
                            if (scannedDev.get(i).getBluetooth_address().equals(bluetoothObject.getBluetooth_address())) {
                                addToArray = false;
                                break;
                            }
                        }
                        if(addToArray){
                            scannedDev.add(bluetoothObject);
                        }
                    }
                }
            }
        };
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    /**
     * show list of all the scanned deviced
     */
    public void getList(){
        if(scannedDev.size()!=0) {
            listAdapter = new blesArrayAdapter(this, scannedDev);
            mainListView.setAdapter(listAdapter);
        }
    }

    /**
     * enable BT on user device
     */
    private void enableBluetoothOnDevice() {
        if (mBluetoothAdapter == null) {
            finish();
            // If the android device does not have bluetooth, just return and get out.
            // There's nothing the app can do in this case. Closing app.
        }
        // Check to see if bluetooth is enabled. Prompt to enable it
        if( !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == 0) {
                // If the resultCode is 0, the user selected "No" when prompt to
                // allow the app to enable bluetooth.
                // You may want to display a dialog explaining what would happen if
                // the user doesn't enable bluetooth.
                Toast.makeText(this, "The user decided to deny bluetooth access", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "The user decided to allow bluetooth access", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * show user location found by BLE scanned device
     */
    public void onGoToLocBtnClick(View v){
        Toast.makeText(this, "Load your location...", Toast.LENGTH_LONG).show();
        goToLocBtn.setEnabled(false);
        // search the closest device from all detected
        BluetoothObject closestDev = scannedDev.get(0);
        String[] cords = new String[4];
        System.out.println("Start searching for dev");
        System.out.println("ScannedDev size = " + scannedDev.size());
        if(scannedDev.size() > 1){
            System.out.println("More than one device select the closest");
            // select the strongest RSSI
            double closest = -101d;
            for (BluetoothObject dev: scannedDev
                    ) {
                System.out.println("CurrentClosest RSSI: " + closest);
                System.out.println("DEV RSSI: " + dev.getBluetooth_rssi());
                if(dev.getBluetooth_rssi() > closest){
                    closest = dev.getBluetooth_rssi();
                    closestDev = dev;
                }
            }
        }
        System.out.println("Finish selecting closest device");
        String mac = closestDev.getBluetooth_address();

        System.out.println("Start search in DB for it MAC");
        System.out.println("MAC: " + mac);
        //search in DB if MAC exsist
        for (BLE bleTag: bleDevices
                ) {
            System.out.println("BLE MAC: " + bleTag.getMacAddress());
            if(mac.equals(bleTag.getMacAddress())){
                System.out.println("Found MAC -> going to BLE location");
                cords[0] = user;
                cords[1] = userKey;
                cords[2] = bleTag.getCordinate1();
                cords[3] = bleTag.getCordinate2();
                Intent intentBundle = new Intent(BLEActivity.this,MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArray("cords", cords);
                intentBundle.putExtras(bundle);
                startActivity(intentBundle);
                return;
            }
            else{
                goToLocBtn.setEnabled(true);
                Toast.makeText(this, "BLE isn't register at DB.", Toast.LENGTH_SHORT).show();
            }
        }


    }

    /** Holds child views for one row. */
    private static class bleViewHolder {
        private TextView name;
        private TextView address;
        private TextView state;
        private TextView type;
        private TextView UUID;
        private TextView RSSI;

        public bleViewHolder() {}
        public bleViewHolder(TextView name, TextView address, TextView state, TextView type, TextView UUID, TextView RSSI) {
            this.name = name;
            this.address = address;
            this.state = state;
            this.type = type;
            this.UUID = UUID;
            this.RSSI = RSSI;
        }
        public TextView getName() {
            return this.name;
        }
        public TextView getAddress(){
            return this.address;
        }
        public TextView getState(){
            return this.state;
        }
        public TextView getType(){
            return this.type;
        }
        public TextView getUUID(){
            return this.UUID;
        }
        public TextView getRSSI(){
            return this.RSSI;
        }
        public void setName(TextView textView){
            this.name = textView;
        }
        public void setAddress(TextView textView){
            this.address = textView;
        }
        public void setState(TextView textView){
            this.state = textView;
        }
        public void setType(TextView textView){
            this.type = textView;
        }
        public void setUUID(TextView textView){
            this.UUID = textView;
        }
        public void setRSSI(TextView textView){
            this.RSSI = textView;
        }
    }

    /** Custom adapter for displaying an array of Friends objects. */
    private static class blesArrayAdapter extends ArrayAdapter<BluetoothObject> {

        private LayoutInflater inflater;

        public blesArrayAdapter(Context context, List<BluetoothObject> friendList ) {
            super(context, R.layout.row_bt_scan_new_device, friendList);
            // Cache the LayoutInflate to avoid asking for a new one each time.
            inflater = LayoutInflater.from(context) ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Friend to display
            BluetoothObject bleDevice = this.getItem(position);

            // The child views in each row.
            TextView name;
            TextView address;
            TextView state;
            TextView type;
            TextView UUID;
            TextView RSSI;

            // Create a new row view
            if ( convertView == null ) {
                convertView = inflater.inflate(R.layout.row_bt_scan_new_device, parent, false);

                // Find the child views.
                name = (TextView) convertView.findViewById(R.id.textview_bt_scan_name);
                address = (TextView) convertView.findViewById(R.id.textview_bt_scan_address);
                state = (TextView) convertView.findViewById(R.id.textview_bt_scan_state);
                type = (TextView) convertView.findViewById(R.id.textview_bt_scan_type);
                UUID = (TextView) convertView.findViewById(R.id.textview_bt_scan_uuid);
                RSSI = (TextView) convertView.findViewById(R.id.textview_bt_scan_signal_strength);

                // Optimization: Tag the row with it's child views, so we don't have to
                // call findViewById() later when we reuse the row.
                convertView.setTag( new BLEActivity.bleViewHolder(name,address,state,type,UUID,RSSI) );
            }
            // Reuse existing row view
            else {
                // Because we use a ViewHolder, we avoid having to call findViewById().
                BLEActivity.bleViewHolder viewHolder = (BLEActivity.bleViewHolder) convertView.getTag();
                name = viewHolder.getName() ;
                address = viewHolder.getAddress() ;
                state = viewHolder.getState() ;
                type = viewHolder.getType() ;
                UUID = viewHolder.getUUID() ;
                RSSI = viewHolder.getRSSI() ;
            }
            // Display BLE data
            name.setText(bleDevice.getBluetooth_name());
            address.setText("MAC Address: " + bleDevice.getBluetooth_address());
            state.setText("BT State: " + bleDevice.getBluetooth_state()+"");
            type.setText("BT Type: " + bleDevice.getBluetooth_type()+"");
            RSSI.setText("RSSI: " + bleDevice.getBluetooth_rssi()+"");
            return convertView;
        }
    }

}//end MainActivity