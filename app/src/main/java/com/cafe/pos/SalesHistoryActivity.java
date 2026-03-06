package com.bizflow.pos;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SalesHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerViewSales;
    private TextView txtTodaySales;
    private SalesAdapter salesAdapter;
    private AppDatabase database;
    private ExecutorService executor;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_history);

        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        initViews();
        setupDatabase();
        setupRecyclerView();
        loadSalesData();
    }

    private void initViews() {
        recyclerViewSales = findViewById(R.id.recyclerViewSales);
        txtTodaySales = findViewById(R.id.txtTodaySales);
    }

    private void setupDatabase() {
        database = AppDatabase.getDatabase(this);
    }

    private void setupRecyclerView() {
        salesAdapter = new SalesAdapter();
        recyclerViewSales.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSales.setAdapter(salesAdapter);
    }

    private void loadSalesData() {
        executor.execute(() -> {
            try {
                List<Sale> sales = database.saleDao().getAllSales();
                String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                double todaySales = database.saleDao().getDailySales(today + "%");
                
                mainHandler.post(() -> {
                    salesAdapter.updateSales(sales);
                    txtTodaySales.setText(String.format(Locale.getDefault(), "₹%.0f", todaySales));
                });
            } catch (Exception e) {
                mainHandler.post(() -> 
                    txtTodaySales.setText("₹0"));
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