package com.bizflow.pos;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Set;

public class PrinterSelectionActivity extends AppCompatActivity {

    public static final String PREFS = "cafe_prefs";
    public static final String KEY_PRINTER_ADDRESS = "printer_address";

    private BluetoothAdapter bluetoothAdapter;
    private ListView listView;
    private ArrayList<BluetoothDevice> deviceList = new ArrayList<>();
    private ArrayList<String> deviceNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer_selection);

        listView = findViewById(R.id.listViewPrinters);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!BluetoothPermissionHelper.hasBtConnect(this)) {
            BluetoothPermissionHelper.requestBtConnect(this);
            return;
        }

        loadPairedDevices();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, deviceNames);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (position < deviceList.size()) {
                BluetoothDevice selected = deviceList.get(position);
                SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
                sp.edit().putString(KEY_PRINTER_ADDRESS, selected.getAddress()).apply();
                Toast.makeText(this, "Printer selected: " + selected.getName(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadPairedDevices() {
        try {
            Set<BluetoothDevice> paired = bluetoothAdapter.getBondedDevices();
            deviceList.clear();
            deviceNames.clear();

            if (paired == null || paired.size() == 0) {
                deviceNames.add("No paired bluetooth devices found");
                return;
            }

            for (BluetoothDevice d : paired) {
                deviceList.add(d);
                deviceNames.add(d.getName() + "\n" + d.getAddress());
            }
        } catch (SecurityException e) {
            Toast.makeText(this, "Bluetooth permission required", Toast.LENGTH_SHORT).show();
        }
    }
}