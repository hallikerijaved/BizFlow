package com.bizflow.pos;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InvoiceActivity extends AppCompatActivity {
    private TextView txtInvoiceNumber, txtInvoiceDate, txtInvoiceSubtotal, txtInvoiceDiscount, txtInvoiceTax, txtInvoiceTotal, txtBusinessName, txtBusinessAddress;
    private LinearLayout layoutInvoiceItems, layoutInvoiceDiscount, layoutInvoiceTax;
    private Button btnPrintInvoice, btnCloseInvoice;
    private boolean isPreviewMode;
    private double totalAmount;
    private String invoiceNumber, date, itemsString, paymentMethod, tableName;
    private double discountPercent, discountAmount, taxPercent, taxAmount, finalAmount;
    private ArrayList<OrderItem> orderItems;
    private ExecutorService executor;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        initViews();
        getIntentData();
        setupInvoice();
        setupClickListeners();
    }

    private void initViews() {
        txtInvoiceNumber = findViewById(R.id.txtInvoiceNumber);
        txtInvoiceDate = findViewById(R.id.txtInvoiceDate);
        txtInvoiceSubtotal = findViewById(R.id.txtInvoiceSubtotal);
        txtInvoiceDiscount = findViewById(R.id.txtInvoiceDiscount);
        txtInvoiceTax = findViewById(R.id.txtInvoiceTax);
        txtInvoiceTotal = findViewById(R.id.txtInvoiceTotal);
        txtBusinessName = findViewById(R.id.txtBusinessName);
        txtBusinessAddress = findViewById(R.id.txtBusinessAddress);
        layoutInvoiceItems = findViewById(R.id.layoutInvoiceItems);
        layoutInvoiceDiscount = findViewById(R.id.layoutInvoiceDiscount);
        layoutInvoiceTax = findViewById(R.id.layoutInvoiceTax);
        btnPrintInvoice = findViewById(R.id.btnPrintInvoice);
        btnCloseInvoice = findViewById(R.id.btnCloseInvoice);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            isPreviewMode = intent.getBooleanExtra("preview_mode", true);
            totalAmount = intent.getDoubleExtra("total_amount", 0.0);
            invoiceNumber = intent.getStringExtra("invoice_number");
            date = intent.getStringExtra("date");
            itemsString = intent.getStringExtra("items_string");
            paymentMethod = intent.getStringExtra("payment_method");
            tableName = intent.getStringExtra("table_name");
            discountPercent = intent.getDoubleExtra("discount_percent", 0);
            discountAmount = intent.getDoubleExtra("discount_amount", 0);
            taxPercent = intent.getDoubleExtra("tax_percent", 0);
            taxAmount = intent.getDoubleExtra("tax_amount", 0);
            finalAmount = intent.getDoubleExtra("final_amount", totalAmount);
            @SuppressWarnings("unchecked")
            ArrayList<OrderItem> items = (ArrayList<OrderItem>) intent.getSerializableExtra("order_items");
            orderItems = items != null ? items : new ArrayList<>();
        }
    }

    private void setupInvoice() {
        android.content.SharedPreferences prefs = getSharedPreferences("business_settings", MODE_PRIVATE);
        String businessName = prefs.getString("business_name", "BIZFLOW");
        String businessAddress = prefs.getString("business_address", "");
        
        txtBusinessName.setText(businessName.toUpperCase());
        if (!businessAddress.isEmpty()) {
            txtBusinessAddress.setText(businessAddress);
            txtBusinessAddress.setVisibility(android.view.View.VISIBLE);
        }
        
        if (invoiceNumber != null) {
            txtInvoiceNumber.setText(invoiceNumber);
        }
        if (date != null) {
            txtInvoiceDate.setText(date);
        }
        
        txtInvoiceSubtotal.setText(String.format(Locale.getDefault(), "₹%.0f", totalAmount));
        txtInvoiceDiscount.setText(String.format(Locale.getDefault(), "₹%.0f", discountAmount));
        txtInvoiceTax.setText(String.format(Locale.getDefault(), "₹%.0f", taxAmount));
        txtInvoiceTotal.setText(String.format(Locale.getDefault(), "₹%.0f", finalAmount));

        layoutInvoiceDiscount.setVisibility(discountAmount > 0 ? android.view.View.VISIBLE : android.view.View.GONE);
        layoutInvoiceTax.setVisibility(taxAmount > 0 ? android.view.View.VISIBLE : android.view.View.GONE);

        if (isPreviewMode) {
            btnPrintInvoice.setText("Print Invoice");
        }

        for (OrderItem item : orderItems) {
            if (item != null) {
                addInvoiceItem(item);
            }
        }
    }

    private void addInvoiceItem(OrderItem item) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView txtItemName = new TextView(this);
        txtItemName.setText(item.foodItem.name);
        txtItemName.setLayoutParams(new LinearLayout.LayoutParams(0, 
                LinearLayout.LayoutParams.WRAP_CONTENT, 2));

        TextView txtQuantity = new TextView(this);
        txtQuantity.setText(String.valueOf(item.quantity));
        txtQuantity.setGravity(android.view.Gravity.CENTER);
        txtQuantity.setLayoutParams(new LinearLayout.LayoutParams(0, 
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView txtPrice = new TextView(this);
        txtPrice.setText(String.format(Locale.getDefault(), "₹%.0f", item.foodItem.price));
        txtPrice.setGravity(android.view.Gravity.END);
        txtPrice.setLayoutParams(new LinearLayout.LayoutParams(0, 
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView txtTotal = new TextView(this);
        txtTotal.setText(String.format(Locale.getDefault(), "₹%.0f", item.totalPrice));
        txtTotal.setGravity(android.view.Gravity.END);
        txtTotal.setLayoutParams(new LinearLayout.LayoutParams(0, 
                LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        itemLayout.addView(txtItemName);
        itemLayout.addView(txtQuantity);
        itemLayout.addView(txtPrice);
        itemLayout.addView(txtTotal);

        layoutInvoiceItems.addView(itemLayout);
    }

    private void setupClickListeners() {
        btnPrintInvoice.setOnClickListener(v -> {
            if (isPreviewMode) {
                executor.execute(() -> {
                    try {
                        AppDatabase database = AppDatabase.getDatabase(this);
                        Sale sale = new Sale(date, totalAmount, itemsString, invoiceNumber);
                        sale.paymentMethod = paymentMethod != null ? paymentMethod : "Cash";
                        sale.discountPercent = discountPercent;
                        sale.discountAmount = discountAmount;
                        sale.taxAmount = taxAmount;
                        sale.finalAmount = finalAmount;
                        sale.tableName = tableName;
                        database.saleDao().insertSale(sale);
                        
                        mainHandler.post(() -> {
                            printToBluetooth();
                            setResult(RESULT_OK);
                            finish();
                        });
                    } catch (Exception e) {
                        mainHandler.post(() -> 
                            Toast.makeText(this, "Error saving sale: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                });
            } else {
                printToBluetooth();
            }
        });

        btnCloseInvoice.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private void printToBluetooth() {
        if (!BluetoothPermissionHelper.hasBtConnect(this)) {
            BluetoothPermissionHelper.requestBtConnect(this);
            return;
        }

        try {
            PrinterManager printerManager = PrinterManager.getInstance(this);
            String receipt = generateReceiptText();
            
            if (printerManager.print(receipt)) {
                Toast.makeText(this, "Printed successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Print failed. Check printer connection.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Print error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String generateReceiptText() {
        android.content.SharedPreferences prefs = getSharedPreferences("business_settings", MODE_PRIVATE);
        String businessName = prefs.getString("business_name", "BIZFLOW");
        String businessAddress = prefs.getString("business_address", "");
        
        StringBuilder sb = new StringBuilder();
        
        int paperWidth = 32;
        String nameUpper = businessName.toUpperCase();
        int nameSpaces = Math.max(0, (paperWidth - nameUpper.length()) / 2);
        for (int i = 0; i < nameSpaces; i++) sb.append(" ");
        sb.append(nameUpper).append("\n");
        
        if (!businessAddress.isEmpty()) {
            int addrSpaces = Math.max(0, (paperWidth - businessAddress.length()) / 2);
            for (int i = 0; i < addrSpaces; i++) sb.append(" ");
            sb.append(businessAddress).append("\n");
        }
        sb.append("   ---------------------\n");
        sb.append("Invoice No: ").append(invoiceNumber != null ? invoiceNumber : "N/A").append("\n");
        sb.append("Date: ").append(date != null && date.length() > 16 ? date.substring(0, 16) : date).append("\n");
        if (tableName != null && !tableName.isEmpty()) {
            sb.append("Table: ").append(tableName).append("\n");
        }
        sb.append("--------------------------------\n");
        sb.append(String.format("%-16s %3s %7s\n", "Item", "Qty", "Total"));
        sb.append("--------------------------------\n");
        
        for (OrderItem item : orderItems) {
            if (item != null && item.foodItem != null && item.foodItem.name != null) {
                String name = item.foodItem.name;
                if (name.length() > 16) name = name.substring(0, 16);
                
                double total = item.foodItem.price * item.quantity;
                sb.append(String.format("%-16s %3d %7.0f\n", name, item.quantity, total));
            }
        }
        
        sb.append("--------------------------------\n");
        sb.append(String.format("%-20s %10.0f\n", "Subtotal:", totalAmount));
        
        if (discountAmount > 0) {
            sb.append(String.format("%-20s %10.0f\n", "Discount (" + String.format("%.0f", discountPercent) + "%):", -discountAmount));
        }
        
        if (taxAmount > 0) {
            sb.append(String.format("%-20s %10.0f\n", "Tax (" + String.format("%.0f", taxPercent) + "%):", taxAmount));
        }
        
        sb.append("--------------------------------\n");
        sb.append(String.format("%-20s %10.0f\n", "Grand Total:", finalAmount));
        sb.append("--------------------------------\n");
        
        if (paymentMethod != null) {
            sb.append("Payment: ").append(paymentMethod).append("\n");
        }
        
        sb.append("      Thank You Visit Again\n\n\n");
        
        return sb.toString();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == BluetoothPermissionHelper.REQ_BT_CONNECT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                printToBluetooth();
            } else {
                Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
