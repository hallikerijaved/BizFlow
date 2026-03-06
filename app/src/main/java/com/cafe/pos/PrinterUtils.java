package com.bizflow.pos;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

public class PrinterUtils {
    private static final UUID PRINTER_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    
    public static BluetoothDevice findPrinter(Context context) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null || !adapter.isEnabled()) {
            return null;
        }
        
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        
        try {
            Set<BluetoothDevice> devices = adapter.getBondedDevices();
            for (BluetoothDevice device : devices) {
                String name = device.getName();
                if (name != null && (name.toLowerCase().contains("printer") || 
                    name.toLowerCase().contains("pos") ||
                    name.toLowerCase().contains("thermal") ||
                    name.toLowerCase().contains("58mm") ||
                    name.toLowerCase().contains("rp"))) {
                    return device;
                }
            }
        } catch (SecurityException e) {
            return null;
        }
        return null;
    }
    
    public static boolean printReceipt(Context context, String receiptText) {
        BluetoothDevice printer = findPrinter(context);
        if (printer == null || receiptText == null || receiptText.trim().isEmpty()) {
            return false;
        }
        
        BluetoothSocket socket = null;
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            
            // Try multiple connection methods for better compatibility
            socket = createBluetoothSocket(printer);
            socket.connect();
            
            OutputStream output = socket.getOutputStream();
            
            // ESC/POS commands for 58mm thermal printer
            output.write(new byte[]{0x1B, 0x40}); // Initialize
            output.write(new byte[]{0x1B, 0x61, 0x01}); // Center align
            output.write(receiptText.getBytes("UTF-8"));
            output.write(new byte[]{0x1D, 0x56, 0x42, 0x00}); // Full cut
            output.flush();
            
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ignored) {}
            }
        }
    }
    
    private static BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            // Try standard method first
            return device.createRfcommSocketToServiceRecord(PRINTER_UUID);
        } catch (Exception e) {
            try {
                // Fallback method using reflection for problematic devices
                Method method = device.getClass().getMethod("createRfcommSocket", int.class);
                return (BluetoothSocket) method.invoke(device, 1);
            } catch (Exception ex) {
                throw new IOException("Could not create Bluetooth socket");
            }
        }
    }
}