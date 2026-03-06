package com.bizflow.pos;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

public class SecurityHelper {
    private static final String PREFS_NAME = "admin_settings";
    private static final String PIN_KEY = "admin_pin";
    private static final String DEFAULT_PIN = "1234";

    public interface AuthCallback {
        void onAuthenticated();
        void onAuthFailed();
    }

    public static void authenticateAdmin(Context context, AuthCallback callback) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedPin = prefs.getString(PIN_KEY, DEFAULT_PIN);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Admin Authentication");
        builder.setMessage("Enter PIN to access food management:");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setHint("Enter PIN");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String enteredPin = input.getText().toString().trim();
            if (savedPin.equals(enteredPin)) {
                callback.onAuthenticated();
            } else {
                Toast.makeText(context, "Invalid PIN", Toast.LENGTH_SHORT).show();
                callback.onAuthFailed();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            callback.onAuthFailed();
        });

        builder.show();
    }

    public static void changePin(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Change PIN");
        builder.setMessage("Enter new PIN (4 digits):");

        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        input.setHint("New PIN");
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newPin = input.getText().toString().trim();
            if (newPin.length() >= 4) {
                SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                prefs.edit().putString(PIN_KEY, newPin).apply();
                Toast.makeText(context, "PIN changed successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "PIN must be at least 4 digits", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}