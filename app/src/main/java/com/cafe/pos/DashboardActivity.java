package com.bizflow.pos;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardActivity extends AppCompatActivity {
    private TextView txtTodaySales, txtTotalOrders, txtBestSelling, txtTablesInUse;
    private Button btnDailyReport, btnMonthlyReport;
    private AppDatabase database;
    private ExecutorService executor;
    private Handler mainHandler;
    private static final int CREATE_DAILY_PDF = 101;
    private static final int CREATE_MONTHLY_PDF = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
        database = AppDatabase.getDatabase(this);

        initViews();
        loadDashboardData();
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void initViews() {
        txtTodaySales = findViewById(R.id.txtTodaySales);
        txtTotalOrders = findViewById(R.id.txtTotalOrders);
        txtBestSelling = findViewById(R.id.txtBestSelling);
        txtTablesInUse = findViewById(R.id.txtTablesInUse);
        btnDailyReport = findViewById(R.id.btnDailyReport);
        btnMonthlyReport = findViewById(R.id.btnMonthlyReport);
    }

    private void loadDashboardData() {
        executor.execute(() -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String today = sdf.format(new Date());

                List<Sale> todaySales = database.saleDao().getSalesByDate(today);
                double totalSales = 0;
                int totalOrders = todaySales.size();
                Map<String, Integer> itemCount = new HashMap<>();

                for (Sale sale : todaySales) {
                    totalSales += sale.finalAmount > 0 ? sale.finalAmount : sale.totalAmount;
                    if (sale.items != null && !sale.items.isEmpty()) {
                        String[] items = sale.items.split(";");
                        for (String item : items) {
                            String line = item.trim();
                            if (line.isEmpty()) continue;

                            String itemName = line;
                            int qty = 1;
                            int xIdx = line.indexOf(" x");
                            if (xIdx >= 0) {
                                itemName = line.substring(0, xIdx).trim();
                                int eqIdx = line.indexOf(" =", xIdx);
                                String qtyStr = line.substring(xIdx + 2, eqIdx > xIdx ? eqIdx : line.length()).trim();
                                try {
                                    qty = Integer.parseInt(qtyStr);
                                } catch (Exception ignored) {}
                            } else {
                                int eqIdx = line.indexOf("=");
                                if (eqIdx > 0) {
                                    itemName = line.substring(0, eqIdx).trim();
                                }
                            }

                            if (!itemName.isEmpty()) {
                                itemCount.put(itemName, itemCount.getOrDefault(itemName, 0) + Math.max(1, qty));
                            }
                        }
                    }
                }

                String bestSelling = "N/A";
                int maxCount = 0;
                for (Map.Entry<String, Integer> entry : itemCount.entrySet()) {
                    if (entry.getValue() > maxCount) {
                        maxCount = entry.getValue();
                        bestSelling = entry.getKey();
                    }
                }

                List<com.bizflow.pos.Table> tables = database.tableDao().getAllTables();
                int tablesInUse = 0;
                for (com.bizflow.pos.Table table : tables) {
                    if ("occupied".equals(table.status)) tablesInUse++;
                }

                double finalTotalSales = totalSales;
                int finalTotalOrders = totalOrders;
                String finalBestSelling = bestSelling;
                int finalTablesInUse = tablesInUse;

                mainHandler.post(() -> {
                    txtTodaySales.setText(String.format(Locale.getDefault(), "₹%.0f", finalTotalSales));
                    txtTotalOrders.setText(String.valueOf(finalTotalOrders));
                    txtBestSelling.setText(finalBestSelling);
                    txtTablesInUse.setText(String.valueOf(finalTablesInUse));
                });

            } catch (Exception e) {
                mainHandler.post(() -> 
                    Toast.makeText(this, "Error loading data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void setupClickListeners() {
        btnDailyReport.setOnClickListener(v -> {
            String fileName = "DailyReport_" + new SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(new Date()) + ".pdf";
            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(android.content.Intent.CATEGORY_OPENABLE);
            intent.setType("application/pdf");
            intent.putExtra(android.content.Intent.EXTRA_TITLE, fileName);
            startActivityForResult(intent, CREATE_DAILY_PDF);
        });
        
        btnMonthlyReport.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR);
            String fileName = "MonthlyReport_" + month + "_" + year + ".pdf";
            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(android.content.Intent.CATEGORY_OPENABLE);
            intent.setType("application/pdf");
            intent.putExtra(android.content.Intent.EXTRA_TITLE, fileName);
            startActivityForResult(intent, CREATE_MONTHLY_PDF);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == CREATE_DAILY_PDF) {
                generateDailyReport(data.getData());
            } else if (requestCode == CREATE_MONTHLY_PDF) {
                generateMonthlyReport(data.getData());
            }
        }
    }

    private void generateDailyReport(android.net.Uri uri) {
        executor.execute(() -> {
            try {
                String todayKey = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                String todayDisplay = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                List<Sale> sales = database.saleDao().getSalesByDate(todayKey);

                java.io.OutputStream outputStream = getContentResolver().openOutputStream(uri);
                PdfWriter writer = new PdfWriter(outputStream);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                document.add(new Paragraph("Daily Sales Report").setFontSize(20).setBold());
                document.add(new Paragraph("Date: " + todayDisplay).setFontSize(12));
                document.add(new Paragraph("\n"));

                float[] columnWidths = {150f, 100f, 100f};
                Table table = new Table(columnWidths);
                table.addHeaderCell("Invoice No");
                table.addHeaderCell("Amount");
                table.addHeaderCell("Payment");

                double total = 0;
                for (Sale sale : sales) {
                    table.addCell(sale.invoiceNumber != null ? sale.invoiceNumber : "N/A");
                    double amount = sale.finalAmount > 0 ? sale.finalAmount : sale.totalAmount;
                    table.addCell(String.format("₹%.0f", amount));
                    table.addCell(sale.paymentMethod != null ? sale.paymentMethod : "Cash");
                    total += amount;
                }

                document.add(table);
                document.add(new Paragraph("\nTotal Sales: ₹" + String.format("%.0f", total)).setBold());
                document.add(new Paragraph("Total Orders: " + sales.size()));
                document.close();

                mainHandler.post(() -> 
                    Toast.makeText(this, "Report saved successfully", Toast.LENGTH_LONG).show());

            } catch (Exception e) {
                mainHandler.post(() -> 
                    Toast.makeText(this, "Error generating report: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void generateMonthlyReport(android.net.Uri uri) {
        executor.execute(() -> {
            try {
                Calendar cal = Calendar.getInstance();
                int month = cal.get(Calendar.MONTH) + 1;
                int year = cal.get(Calendar.YEAR);

                List<Sale> allSales = database.saleDao().getAllSales();
                SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat sdfDateOnly = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                java.io.OutputStream outputStream = getContentResolver().openOutputStream(uri);
                PdfWriter writer = new PdfWriter(outputStream);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);

                document.add(new Paragraph("Monthly Sales Report").setFontSize(20).setBold());
                document.add(new Paragraph("Month: " + month + "/" + year).setFontSize(12));
                document.add(new Paragraph("\n"));

                float[] columnWidths = {100f, 150f, 100f};
                Table table = new Table(columnWidths);
                table.addHeaderCell("Date");
                table.addHeaderCell("Invoice No");
                table.addHeaderCell("Amount");

                double total = 0;
                int count = 0;
                for (Sale sale : allSales) {
                    try {
                        Date saleDate = null;
                        try {
                            saleDate = sdfDateTime.parse(sale.date);
                        } catch (Exception ignored) {}
                        if (saleDate == null) {
                            saleDate = sdfDateOnly.parse(sale.date);
                        }
                        if (saleDate != null) {
                            Calendar saleCal = Calendar.getInstance();
                            saleCal.setTime(saleDate);
                            if (saleCal.get(Calendar.MONTH) + 1 == month && saleCal.get(Calendar.YEAR) == year) {
                                table.addCell(sale.date);
                                table.addCell(sale.invoiceNumber != null ? sale.invoiceNumber : "N/A");
                                double amount = sale.finalAmount > 0 ? sale.finalAmount : sale.totalAmount;
                                table.addCell(String.format("₹%.0f", amount));
                                total += amount;
                                count++;
                            }
                        }
                    } catch (Exception ignored) {}
                }

                document.add(table);
                document.add(new Paragraph("\nTotal Sales: ₹" + String.format("%.0f", total)).setBold());
                document.add(new Paragraph("Total Orders: " + count));
                document.close();

                mainHandler.post(() -> 
                    Toast.makeText(this, "Report saved successfully", Toast.LENGTH_LONG).show());

            } catch (Exception e) {
                mainHandler.post(() -> 
                    Toast.makeText(this, "Error generating report: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
