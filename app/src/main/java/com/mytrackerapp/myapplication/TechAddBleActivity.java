package com.mytrackerapp.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class TechAddBleActivity extends AppCompatActivity {
    private int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private Button button_scanBT;
    private Button button_getBLS;
    private ListView bleListView ;
    private ArrayAdapter<newBLEModel> listAdapter;
    private ArrayList<newBLEModel> modelItems;
    private String[] cords;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tech_add_ble);

        Intent intent = getIntent();
        Bundle extBundle = intent.getExtras();

        if(!extBundle.isEmpty()) {
            boolean hasGpsCords = extBundle.containsKey("cords");
            if (hasGpsCords) {
                cords = extBundle.getStringArray("cords");
            }
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase = mDatabase.getRoot().child("BLE Tags");
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        button_scanBT = (Button) findViewById(R.id.button_scanBTTech);
        button_getBLS = (Button) findViewById(R.id.getBLEBtnTech);
        modelItems = new ArrayList<newBLEModel>();
        // Find the ListView resource.
        bleListView = (ListView) findViewById( R.id.bleListView );
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
                bleListView.setAdapter(null);
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
                    if(modelItems.size()==0)
                        modelItems.add(new newBLEModel(bluetoothObject.getBluetooth_name(),bluetoothObject.getBluetooth_address(),false));
                    else if(modelItems.size()!=0){
                        boolean addToArray = true;
                        for (int i = 0 ; i < modelItems.size() ; i++) {
                            if (modelItems.get(i).getName().equals(bluetoothObject.getBluetooth_name())) {
                                addToArray = false;
                                break;
                            }
                        }
                        if(addToArray){
                            modelItems.add(new newBLEModel(bluetoothObject.getBluetooth_name(),bluetoothObject.getBluetooth_address(),false));
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
            listAdapter = new BleArrayAdapter(this, modelItems);
            bleListView.setAdapter(listAdapter);
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
    private static class BLEViewHolder {
        private CheckBox checkBox ;
        private TextView textView ;
        public BLEViewHolder() {}
        public BLEViewHolder(TextView textView, CheckBox checkBox ) {
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

    /** Custom adapter for displaying an array of Friends objects. */
    private static class BleArrayAdapter extends ArrayAdapter<newBLEModel> {

        private LayoutInflater inflater;

        public BleArrayAdapter( Context context, List<newBLEModel> bleList ) {
            super( context, R.layout.simplerow, R.id.rowTextView, bleList );
            // Cache the LayoutInflate to avoid asking for a new one each time.
            inflater = LayoutInflater.from(context) ;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Friend to display
            newBLEModel ble = (newBLEModel) this.getItem( position );

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
                convertView.setTag( new BLEViewHolder(textView,checkBox) );

                // If CheckBox is toggled, update the Friend it is tagged with.
                checkBox.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        newBLEModel ble2 = (newBLEModel) cb.getTag();
                        ble2.setChecked( cb.isChecked() );
                    }
                });
            }
            // Reuse existing row view
            else {
                // Because we use a ViewHolder, we avoid having to call findViewById().
                BLEViewHolder viewHolder = (BLEViewHolder) convertView.getTag();
                checkBox = viewHolder.getCheckBox() ;
                textView = viewHolder.getTextView() ;
            }

            // Tag the CheckBox with the frined it is displaying, so that we can
            // access the frined in onClick() when the CheckBox is toggled.
            checkBox.setTag( ble );

            // Display frined data
            checkBox.setChecked( ble.isChecked() );
            textView.setText( ble.getName() );

            return convertView;
        }

    }

    public void onAddBleClicked(View v) {
        if (modelItems.size() != 0) {
            int counterCheck = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                if (listAdapter.getItem(i).isChecked()) {
                    counterCheck++;
                }
            }
            if (counterCheck == 0) {
                return;
            } else if (counterCheck > 1) {
                Toast.makeText(this, "Choose only one BLE device!", Toast.LENGTH_LONG).show();
            } else {
                for (int i = 0 ; i < listAdapter.getCount(); i++) {
                    if (listAdapter.getItem(i).isChecked()) {
                        BLE bleToDB = new BLE(modelItems.get(i).getCordinates(), cords[0], cords[1]);
                        mDatabase.push().setValue(bleToDB);
                        Toast.makeText(this, "BLE tag has been added.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
}

