package com.bizflow.pos;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ChangePinActivity extends AppCompatActivity {
    private EditText etCurrentPin, etNewPin, etConfirmPin;
    private Button btnChangePin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pin);
        
        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etCurrentPin = findViewById(R.id.etCurrentPin);
        etNewPin = findViewById(R.id.etNewPin);
        etConfirmPin = findViewById(R.id.etConfirmPin);
        btnChangePin = findViewById(R.id.btnChangePin);
    }

    private void setupClickListeners() {
        btnChangePin.setOnClickListener(v -> changePin());
    }

    private void changePin() {
        String currentPin = etCurrentPin.getText().toString().trim();
        String newPin = etNewPin.getText().toString().trim();
        String confirmPin = etConfirmPin.getText().toString().trim();

        if (currentPin.isEmpty() || newPin.isEmpty() || confirmPin.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        android.content.SharedPreferences prefs = getSharedPreferences("admin_settings", MODE_PRIVATE);
        String savedPin = prefs.getString("admin_pin", "1234");

        if (!currentPin.equals(savedPin)) {
            Toast.makeText(this, "Current PIN is incorrect", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPin.equals(confirmPin)) {
            Toast.makeText(this, "New PIN and Confirm PIN do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPin.length() < 4) {
            Toast.makeText(this, "PIN must be at least 4 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        prefs.edit().putString("admin_pin", newPin).apply();
        Toast.makeText(this, "PIN changed successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}