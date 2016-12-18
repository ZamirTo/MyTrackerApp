package com.mytrackerapp.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;


public class BLEActivity extends Activity {
    private int REQUEST_ENABLE_BT = 1; // Any positive integer should work.
    private BluetoothAdapter mBluetoothAdapter;
    private Button button_scanBT;
    private Button button_getBLS;
    private ListView mainListView ;
    private ArrayAdapter<BluetoothObject> listAdapter;
    private ArrayList<BluetoothObject> modelItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        button_scanBT = (Button) findViewById(R.id.button_scanBT);
        button_getBLS = (Button) findViewById(R.id.getBLEBtn);
        modelItems = new ArrayList<BluetoothObject>();
        // Find the ListView resource.
        mainListView = (ListView) findViewById( R.id.mainListView );
        enableBluetoothOnDevice();

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
                modelItems.clear();
                startScanning();
            }
        });
    }//end onCreate

    private void startScanning() {
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
                    System.out.println(modelItems.size());
                    if(modelItems.size()==0)
                        modelItems.add(bluetoothObject);
                    else if(modelItems.size()!=0){
                        boolean addToArray = true;
                        for (int i = 0 ; i < modelItems.size() ; i++) {
                            if (modelItems.get(i).getBluetooth_address().equals(bluetoothObject.getBluetooth_address())) {
                                addToArray = false;
                                break;
                            }
                        }
                        if(addToArray){
                            modelItems.add(bluetoothObject);
                        }
                    }
                }
            }
        };
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    public void getList(){
        if(modelItems.size()!=0) {
            listAdapter = new blesArrayAdapter(this, modelItems);
            mainListView.setAdapter(listAdapter);
        }
    }


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
            address.setText(bleDevice.getBluetooth_address());
            state.setText(bleDevice.getBluetooth_state()+"");
            type.setText(bleDevice.getBluetooth_type()+"");
            RSSI.setText(bleDevice.getBluetooth_rssi()+"");
            return convertView;
        }
    }

}//end MainActivity