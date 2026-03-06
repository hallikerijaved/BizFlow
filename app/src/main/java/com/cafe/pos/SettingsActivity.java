package com.bizflow.pos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity implements PrinterManager.PrinterStatusListener {
    
    private Button btnSelectPrinter, btnTestPrinter, btnFoodManagement, btnChangePin, btnEditBusinessName, btnEditBusinessAddress, btnTableManagement;
    private TextView txtPrinterStatus;
    private Spinner spinnerReconnectInterval;
    private PrinterManager printerManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        initViews();
        setupPrinterManager();
        setupClickListeners();
        setupReconnectSpinner();
    }
    
    private void initViews() {
        btnSelectPrinter = findViewById(R.id.btnSelectPrinter);
        btnTestPrinter = findViewById(R.id.btnTestPrinter);
        btnFoodManagement = findViewById(R.id.btnFoodManagement);
        btnChangePin = findViewById(R.id.btnChangePin);
        btnEditBusinessName = findViewById(R.id.btnEditBusinessName);
        btnEditBusinessAddress = findViewById(R.id.btnEditBusinessAddress);
        btnTableManagement = findViewById(R.id.btnTableManagement);
        txtPrinterStatus = findViewById(R.id.txtPrinterStatus);
        spinnerReconnectInterval = findViewById(R.id.spinnerReconnectInterval);
    }
    
    private void setupPrinterManager() {
        printerManager = PrinterManager.getInstance(this);
        printerManager.setStatusListener(this);
        updatePrinterStatus(printerManager.isConnected());
    }
    
    private void setupClickListeners() {
        btnSelectPrinter.setOnClickListener(v -> {
            startActivity(new Intent(this, PrinterSelectionActivity.class));
        });
        
        btnTestPrinter.setOnClickListener(v -> {
            if (printerManager.testPrint()) {
                Toast.makeText(this, "Test print successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Test print failed. Check printer connection.", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnFoodManagement.setOnClickListener(v -> {
            SecurityHelper.authenticateAdmin(this, new SecurityHelper.AuthCallback() {
                @Override
                public void onAuthenticated() {
                    startActivity(new Intent(SettingsActivity.this, FoodManagementActivity.class));
                }

                @Override
                public void onAuthFailed() {
                    // Authentication failed, do nothing
                }
            });
        });
        
        btnChangePin.setOnClickListener(v -> {
            SecurityHelper.authenticateAdmin(this, new SecurityHelper.AuthCallback() {
                @Override
                public void onAuthenticated() {
                    startActivity(new Intent(SettingsActivity.this, ChangePinActivity.class));
                }

                @Override
                public void onAuthFailed() {
                    // Authentication failed, do nothing
                }
            });
        });
        
        btnEditBusinessName.setOnClickListener(v -> {
            editBusinessName();
        });
        
        btnEditBusinessAddress.setOnClickListener(v -> {
            editBusinessAddress();
        });
        
        btnTableManagement.setOnClickListener(v -> {
            startActivity(new Intent(this, TableManagementActivity.class));
        });
    }
    
    private void setupReconnectSpinner() {
        String[] intervals = {"10 seconds", "20 seconds", "30 seconds"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, intervals);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerReconnectInterval.setAdapter(adapter);
        
        SharedPreferences prefs = getSharedPreferences("printer_settings", MODE_PRIVATE);
        int savedInterval = prefs.getInt("reconnect_interval", 20);
        int position = savedInterval == 10 ? 0 : savedInterval == 30 ? 2 : 1;
        spinnerReconnectInterval.setSelection(position);
        
        spinnerReconnectInterval.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                int interval = position == 0 ? 10 : position == 2 ? 30 : 20;
                prefs.edit().putInt("reconnect_interval", interval).apply();
            }
            
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }
    
    @Override
    public void onStatusChanged(boolean connected) {
        runOnUiThread(() -> updatePrinterStatus(connected));
    }
    
    @Override
    public void onError(String error) {
        runOnUiThread(() -> Toast.makeText(this, "Printer Error: " + error, Toast.LENGTH_SHORT).show());
    }
    
    private void updatePrinterStatus(boolean connected) {
        if (connected) {
            txtPrinterStatus.setText("✅ Printer Connected");
            txtPrinterStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark, getTheme()));
        } else {
            txtPrinterStatus.setText("❌ Printer Disconnected");
            txtPrinterStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark, getTheme()));
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updatePrinterStatus(printerManager.isConnected());
    }
    
    private void editBusinessName() {
        SharedPreferences prefs = getSharedPreferences("business_settings", MODE_PRIVATE);
        String currentName = prefs.getString("business_name", "BizFlow");
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Edit Business Name");
        builder.setMessage("Enter your business name:");

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setText(currentName);
        input.setHint("Business Name");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                prefs.edit().putString("business_name", newName).apply();
                Toast.makeText(this, "Business name updated", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    
    private void editBusinessAddress() {
        SharedPreferences prefs = getSharedPreferences("business_settings", MODE_PRIVATE);
        String currentAddress = prefs.getString("business_address", "");
        
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Edit Business Address");
        builder.setMessage("Enter your business address:");

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setText(currentAddress);
        input.setHint("Business Address");
        input.setLines(3);
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newAddress = input.getText().toString().trim();
            prefs.edit().putString("business_address", newAddress).apply();
            Toast.makeText(this, "Business address updated", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}