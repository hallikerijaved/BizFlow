package com.bizflow.pos;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PrinterManager {

    private static final UUID PRINTER_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static PrinterManager instance;
    private final Context context;
    private BluetoothSocket socket;
    private BluetoothDevice printerDevice;
    private final ExecutorService executor;
    private final Handler mainHandler;
    private boolean isConnected = false;
    private int reconnectAttempts = 0;
    private static final int MAX_RECONNECT_ATTEMPTS = 5;

    public interface PrinterStatusListener {
        void onStatusChanged(boolean connected);
        void onError(String error);
    }

    private PrinterStatusListener statusListener;

    private PrinterManager(Context context) {
        this.context = context.getApplicationContext();
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
        loadSavedPrinter();
        startAutoReconnect();
    }

    public static synchronized PrinterManager getInstance(Context context) {
        if (instance == null) {
            instance = new PrinterManager(context);
        }
        return instance;
    }

    public void setStatusListener(PrinterStatusListener listener) {
        this.statusListener = listener;
    }

    public boolean isConnected() {
        return isConnected && socket != null && socket.isConnected();
    }

    private void loadSavedPrinter() {
        SharedPreferences prefs =
                context.getSharedPreferences("cafe_prefs", Context.MODE_PRIVATE);

        String address = prefs.getString("printer_address", null);

        if (address != null) {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

            if (adapter == null) {
                notifyError("Bluetooth not supported on this device");
                return;
            }

            try {
                printerDevice = adapter.getRemoteDevice(address);
                connectToPrinter();
            } catch (Exception e) {
                notifyError("Invalid printer address");
            }
        }
    }

    public void connectToPrinter() {
        if (printerDevice == null) {
            notifyError("No printer selected");
            return;
        }

        executor.execute(() -> {
            try {
                if (socket != null) {
                    socket.close();
                }

                socket = createBluetoothSocket(printerDevice);
                socket.connect();

                isConnected = true;
                reconnectAttempts = 0;

                mainHandler.post(() -> {
                    if (statusListener != null) {
                        statusListener.onStatusChanged(true);
                    }
                });

            } catch (Exception e) {
                isConnected = false;
                notifyError("Connection failed: " + e.getMessage());
            }
        });
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            return device.createRfcommSocketToServiceRecord(PRINTER_UUID);
        } catch (Exception e) {
            try {
                Method method =
                        device.getClass().getMethod("createRfcommSocket", int.class);
                return (BluetoothSocket) method.invoke(device, 1);
            } catch (Exception ex) {
                throw new IOException("Could not create Bluetooth socket");
            }
        }
    }

    public boolean testPrint() {
        String test =
                formatReceipt(
                        "BIZFLOW POS",
                        "Test Item 1  100\nTest Item 2  200\n",
                        300
                );

        return print(test);
    }

    public boolean print(String text) {
        if (!isConnected() || text == null) {
            return false;
        }

        try {
            OutputStream output = socket.getOutputStream();

            output.write(text.getBytes("GBK")); // More compatible than UTF-8
            output.flush();
            return true;

        } catch (Exception e) {
            isConnected = false;
            notifyError("Print failed");
            return false;
        }
    }

    // ======== ✅ FIXED & PERFECTLY CENTERED RECEIPT ========
    public String formatReceipt(String header, String items, double total) {

        StringBuilder sb = new StringBuilder();

        // RESET PRINTER
        sb.append("\u001B\u0040"); // ESC @ initialize

        // --------- BUSINESS NAME (MANUALLY CENTERED) ----------
        int paperWidth = 32; // 58mm paper = ~32 chars
        int nameLen = header.length();
        int spaces = Math.max(0, (paperWidth - nameLen) / 2);
        
        sb.append("\u001B\u0045\u0001"); // BOLD ON
        for (int i = 0; i < spaces; i++) sb.append(" ");
        sb.append(header).append("\n");
        sb.append("\u001B\u0045\u0000"); // BOLD OFF
        sb.append("\n");

        // --------- ITEMS (LEFT ALIGN) ----------
        sb.append("------------------------------\n");
        sb.append(items);
        sb.append("------------------------------\n");

        // --------- GRAND TOTAL (BOLD) ----------
        sb.append("\u001B\u0045\u0001"); // BOLD ON
        sb.append(String.format("%-18s %8.0f\n", "Grand Total:", total));
        sb.append("\u001B\u0045\u0000"); // BOLD OFF
        sb.append("------------------------------\n");

        // --------- THANK YOU (MANUALLY CENTERED) ----------
        String thanks = "Thank You! Visit Again";
        int thanksLen = thanks.length();
        int thanksSpaces = Math.max(0, (paperWidth - thanksLen) / 2);
        
        for (int i = 0; i < thanksSpaces; i++) sb.append(" ");
        sb.append(thanks).append("\n");
        sb.append("\n\n\n");

        // CUT PAPER
        sb.append("\u001D\u0056\u0042\u0000");

        return sb.toString();
    }

    private void startAutoReconnect() {
        SharedPreferences prefs =
                context.getSharedPreferences("printer_settings", Context.MODE_PRIVATE);

        int interval = prefs.getInt("reconnect_interval", 20) * 1000;

        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (!isConnected()
                        && printerDevice != null
                        && reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {

                    reconnectAttempts++;
                    connectToPrinter();
                }

                mainHandler.postDelayed(this, interval);
            }
        }, interval);
    }

    public void disconnect() {
        executor.execute(() -> {
            try {
                if (socket != null) {
                    socket.close();
                }
                isConnected = false;

                mainHandler.post(() -> {
                    if (statusListener != null) {
                        statusListener.onStatusChanged(false);
                    }
                });

            } catch (Exception ignored) {
            }
        });
    }

    private void notifyError(String message) {
        Log.e("PrinterManager", message);

        mainHandler.post(() -> {
            if (statusListener != null) {
                statusListener.onError(message);
            }
        });
    }
}
