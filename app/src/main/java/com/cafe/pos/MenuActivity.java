package com.bizflow.pos;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MenuActivity extends AppCompatActivity {
    private static final int INVOICE_REQUEST_CODE = 1001;
    private static final int VOICE_SEARCH_REQUEST_CODE = 1002;
    private RecyclerView recyclerViewFood, recyclerViewCategories;
    private FoodAdapter foodAdapter;
    private CategoryAdapter categoryAdapter;
    private TextView txtTotal, txtTableInfo;
    private Button btnPrint, btnClearOrder;
    private EditText etSearchFood;
    private android.widget.ImageButton btnVoiceSearch;
    private AppDatabase database;
    private List<OrderItem> orderItems = new ArrayList<>();
    private List<FoodItem> allFoodItems = new ArrayList<>();
    private double totalAmount = 0.0;
    private ExecutorService executor;
    private Handler mainHandler;
    private String selectedCategory = "All";
    private Table currentTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        
        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        getTableInfo();
        initViews();
        setupDatabase();
        setupRecyclerView();
        setupClickListeners();
        loadFoodItems();
        loadRunningOrders();
    }

    private void saveOrderToTable(FoodItem foodItem, int quantity) {
        if (currentTable == null) return;
        
        executor.execute(() -> {
            try {
                // Clear existing order for this food item
                List<TableOrder> existingOrders = database.tableOrderDao().getRunningOrdersForTable(currentTable.id);
                for (TableOrder order : existingOrders) {
                    if (order.foodId == foodItem.id) {
                        database.tableOrderDao().clearRunningOrders(currentTable.id);
                        break;
                    }
                }
                
                // Add new order if quantity > 0
                if (quantity > 0) {
                    TableOrder order = new TableOrder(currentTable.id, foodItem.id, foodItem.name, foodItem.price, quantity);
                    database.tableOrderDao().insertOrder(order);
                    
                    // Update table status to occupied
                    mainHandler.post(() -> {
                        executor.execute(() -> {
                            database.tableDao().updateTableStatus(currentTable.id, "occupied", "", System.currentTimeMillis());
                        });
                    });
                }
            } catch (Exception e) {
                mainHandler.post(() -> 
                    Toast.makeText(this, "Error saving order: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void loadRunningOrders() {
        if (currentTable == null) return;
        
        executor.execute(() -> {
            try {
                List<TableOrder> runningOrders = database.tableOrderDao().getRunningOrdersForTable(currentTable.id);
                mainHandler.post(() -> {
                    // Restore order items from running orders
                    orderItems.clear();
                    for (TableOrder order : runningOrders) {
                        FoodItem foodItem = new FoodItem();
                        foodItem.id = order.foodId;
                        foodItem.name = order.foodName;
                        foodItem.price = order.foodPrice;
                        orderItems.add(new OrderItem(foodItem, order.quantity));
                    }
                    updateTotal();
                    
                    // Update food adapter quantities
                    if (foodAdapter != null) {
                        for (TableOrder order : runningOrders) {
                            foodAdapter.setQuantity(order.foodId, order.quantity);
                        }
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> 
                    Toast.makeText(this, "Error loading running orders: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void getTableInfo() {
        Intent intent = getIntent();
        if (intent != null) {
            int tableId = intent.getIntExtra("table_id", -1);
            String tableName = intent.getStringExtra("table_name");
            int tableCapacity = intent.getIntExtra("table_capacity", 0);
            
            currentTable = new Table();
            currentTable.id = tableId;
            currentTable.name = tableName;
            currentTable.capacity = tableCapacity;
        }
    }

    private void initViews() {
        recyclerViewFood = findViewById(R.id.recyclerViewFood);
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        txtTotal = findViewById(R.id.txtTotal);
        txtTableInfo = findViewById(R.id.txtTableInfo);
        btnPrint = findViewById(R.id.btnPrint);
        btnClearOrder = findViewById(R.id.btnClearOrder);
        etSearchFood = findViewById(R.id.etSearchFood);
        btnVoiceSearch = findViewById(R.id.btnVoiceSearch);
        
        if (currentTable != null) {
            txtTableInfo.setText(currentTable.name + " (Capacity: " + currentTable.capacity + ")");
        }
        
        setupSearchListener();
        setupVoiceSearch();
    }

    private void setupDatabase() {
        database = AppDatabase.getDatabase(this);
    }

    private void setupRecyclerView() {
        foodAdapter = new FoodAdapter(new ArrayList<>(), this::onQuantityChanged);
        
        boolean isTablet = getResources().getConfiguration().smallestScreenWidthDp >= 600;
        int orientation = getResources().getConfiguration().orientation;
        
        if (isTablet) {
            int spanCount = orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE ? 3 : 2;
            androidx.recyclerview.widget.GridLayoutManager gridLayoutManager = 
                new androidx.recyclerview.widget.GridLayoutManager(this, spanCount);
            recyclerViewFood.setLayoutManager(gridLayoutManager);
        } else {
            recyclerViewFood.setLayoutManager(new LinearLayoutManager(this));
        }
        
        recyclerViewFood.setAdapter(foodAdapter);
        
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), this::filterByCategory);
        androidx.recyclerview.widget.LinearLayoutManager categoryLayoutManager = 
            new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCategories.setLayoutManager(categoryLayoutManager);
        recyclerViewCategories.setAdapter(categoryAdapter);
    }

    private void setupClickListeners() {
        btnPrint.setOnClickListener(v -> printInvoice());
        btnClearOrder.setOnClickListener(v -> {
            clearOrder();
            // Clear table orders and make table available
            if (currentTable != null) {
                executor.execute(() -> {
                    database.tableOrderDao().clearRunningOrders(currentTable.id);
                    database.tableDao().updateTableStatus(currentTable.id, "available", "", System.currentTimeMillis());
                });
            }
            Toast.makeText(this, "Order Cleared", Toast.LENGTH_SHORT).show();
        });
    }

    private void onQuantityChanged(FoodItem foodItem, int quantity) {
        // Animate the item
        androidx.recyclerview.widget.RecyclerView.ViewHolder holder = recyclerViewFood.findViewHolderForAdapterPosition(
            foodAdapter.getItemPosition(foodItem.id)
        );
        if (holder != null && holder.itemView != null) {
            android.view.animation.Animation animation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.item_add_animation);
            holder.itemView.startAnimation(animation);
        }
        
        // Update local order
        for (int i = 0; i < orderItems.size(); i++) {
            if (orderItems.get(i).foodItem.id == foodItem.id) {
                orderItems.remove(i);
                break;
            }
        }
        
        if (quantity > 0) {
            orderItems.add(new OrderItem(foodItem, quantity));
        }
        
        // Save to table order
        saveOrderToTable(foodItem, quantity);
        updateTotal();
    }

    private void updateTotal() {
        totalAmount = 0;
        for (OrderItem item : orderItems) {
            totalAmount += item.totalPrice;
        }
        txtTotal.setText(String.format(Locale.getDefault(), "₹%.0f", totalAmount));
    }

    private void printInvoice() {
        if (orderItems.isEmpty()) {
            Toast.makeText(this, "No items in order", Toast.LENGTH_SHORT).show();
            return;
        }
        
        showPaymentDialog();
    }
    
    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == INVOICE_REQUEST_CODE && resultCode == RESULT_OK) {
            // Complete table orders and mark table as available
            if (currentTable != null) {
                executor.execute(() -> {
                    database.tableOrderDao().completeTableOrders(currentTable.id);
                    database.tableDao().updateTableStatus(currentTable.id, "available", "", System.currentTimeMillis());
                });
            }
            clearOrder();
            finish(); // Return to table view
        } else if (requestCode == VOICE_SEARCH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                String spokenText = results.get(0);
                etSearchFood.setText(spokenText);
                searchFoodItems(spokenText);
            }
        }
    }
    
    public void clearOrder() {
        orderItems.clear();
        totalAmount = 0.0;
        txtTotal.setText("₹0");
        if (foodAdapter != null) {
            foodAdapter.clearQuantities();
        }
    }

    private void loadFoodItems() {
        executor.execute(() -> {
            try {
                List<FoodItem> foodItems = database.foodDao().getAvailableFoodItems();
                mainHandler.post(() -> {
                    allFoodItems = foodItems;
                    updateCategoryButtons();
                    filterByCategory(selectedCategory);
                });
            } catch (Exception e) {
                mainHandler.post(() -> 
                    Toast.makeText(this, "Error loading food items: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void filterByCategory(String category) {
        if (category == null) {
            category = "All";
        }
        
        selectedCategory = category;
        searchFoodItems(etSearchFood != null ? etSearchFood.getText().toString() : "");
    }
    
    private void updateCategoryButtons() {
        if (allFoodItems == null) {
            return;
        }
        
        java.util.Set<String> uniqueCategories = new java.util.HashSet<>();
        for (FoodItem item : allFoodItems) {
            if (item != null && item.category != null && !item.category.isEmpty()) {
                uniqueCategories.add(item.category);
            }
        }
        
        java.util.List<String> categoryList = new java.util.ArrayList<>();
        categoryList.add("All");
        categoryList.addAll(uniqueCategories);
        java.util.Collections.sort(categoryList.subList(1, categoryList.size()));
        
        if (categoryAdapter != null) {
            categoryAdapter.updateCategories(categoryList, selectedCategory);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
    
    private void setupSearchListener() {
        etSearchFood.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchFoodItems(s.toString());
            }
            
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }
    
    private void searchFoodItems(String query) {
        if (allFoodItems == null || foodAdapter == null) return;
        
        List<FoodItem> filteredItems = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();
        
        for (FoodItem item : allFoodItems) {
            if (item != null) {
                boolean matchesSearch = lowerQuery.isEmpty() || 
                    (item.name != null && item.name.toLowerCase().contains(lowerQuery)) ||
                    (item.category != null && item.category.toLowerCase().contains(lowerQuery));
                
                boolean matchesCategory = "All".equals(selectedCategory) || 
                    (item.category != null && selectedCategory.equals(item.category));
                
                if (matchesSearch && matchesCategory) {
                    filteredItems.add(item);
                }
            }
        }
        
        foodAdapter.updateFoodItems(filteredItems);
    }
    
    private void showPaymentDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_payment, null);
        builder.setView(dialogView);
        
        android.app.AlertDialog dialog = builder.create();
        
        android.widget.TextView txtSubtotal = dialogView.findViewById(R.id.txtSubtotal);
        android.widget.EditText etDiscount = dialogView.findViewById(R.id.etDiscount);
        android.widget.TextView txtDiscountAmount = dialogView.findViewById(R.id.txtDiscountAmount);
        android.widget.EditText etTax = dialogView.findViewById(R.id.etTax);
        android.widget.TextView txtTaxAmount = dialogView.findViewById(R.id.txtTaxAmount);
        android.widget.TextView txtFinalAmount = dialogView.findViewById(R.id.txtFinalAmount);
        android.widget.RadioGroup rgPaymentMethod = dialogView.findViewById(R.id.rgPaymentMethod);
        android.widget.Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        android.widget.Button btnProceed = dialogView.findViewById(R.id.btnProceed);
        
        txtSubtotal.setText(String.format(Locale.getDefault(), "₹%.0f", totalAmount));
        
        android.text.TextWatcher calculationWatcher = new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateFinalAmount(etDiscount, txtDiscountAmount, etTax, txtTaxAmount, txtFinalAmount);
            }
            
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        };
        
        etDiscount.addTextChangedListener(calculationWatcher);
        etTax.addTextChangedListener(calculationWatcher);
        
        calculateFinalAmount(etDiscount, txtDiscountAmount, etTax, txtTaxAmount, txtFinalAmount);
        
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        btnProceed.setOnClickListener(v -> {
            String paymentMethod = "Cash";
            int selectedId = rgPaymentMethod.getCheckedRadioButtonId();
            if (selectedId == R.id.rbCard) paymentMethod = "Card";
            else if (selectedId == R.id.rbUPI) paymentMethod = "UPI";
            
            double discountPercent = 0;
            try {
                discountPercent = Double.parseDouble(etDiscount.getText().toString());
            } catch (Exception e) {}
            
            double taxPercent = 0;
            try {
                taxPercent = Double.parseDouble(etTax.getText().toString());
            } catch (Exception e) {}
            
            double discountAmount = totalAmount * discountPercent / 100;
            double afterDiscount = totalAmount - discountAmount;
            double taxAmount = afterDiscount * taxPercent / 100;
            double finalAmount = afterDiscount + taxAmount;
            
            dialog.dismiss();
            proceedToInvoice(paymentMethod, discountPercent, discountAmount, taxPercent, taxAmount, finalAmount);
        });
        
        dialog.show();
    }
    
    private void calculateFinalAmount(android.widget.EditText etDiscount, android.widget.TextView txtDiscountAmount,
                                     android.widget.EditText etTax, android.widget.TextView txtTaxAmount,
                                     android.widget.TextView txtFinalAmount) {
        double discountPercent = 0;
        try {
            discountPercent = Double.parseDouble(etDiscount.getText().toString());
        } catch (Exception e) {}
        
        double taxPercent = 0;
        try {
            taxPercent = Double.parseDouble(etTax.getText().toString());
        } catch (Exception e) {}
        
        double discountAmount = totalAmount * discountPercent / 100;
        double afterDiscount = totalAmount - discountAmount;
        double taxAmount = afterDiscount * taxPercent / 100;
        double finalAmount = afterDiscount + taxAmount;
        
        txtDiscountAmount.setText(String.format(Locale.getDefault(), "Discount: ₹%.0f", discountAmount));
        txtTaxAmount.setText(String.format(Locale.getDefault(), "Tax: ₹%.0f", taxAmount));
        txtFinalAmount.setText(String.format(Locale.getDefault(), "Final Amount: ₹%.0f", finalAmount));
    }
    
    private void setupVoiceSearch() {
        btnVoiceSearch.setOnClickListener(v -> startVoiceSearch());
    }
    
    private void startVoiceSearch() {
        Intent intent = new Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL, 
                       android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra(android.speech.RecognizerIntent.EXTRA_PROMPT, "Say food item name...");
        
        try {
            startActivityForResult(intent, VOICE_SEARCH_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "Voice search not supported on this device", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void proceedToInvoice(String paymentMethod, double discountPercent, double discountAmount,
                                  double taxPercent, double taxAmount, double finalAmount) {
        try {
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            String invoiceNumber = "INV" + System.currentTimeMillis();
            StringBuilder itemsStr = new StringBuilder();
            
            for (OrderItem item : orderItems) {
                if (item != null && item.foodItem != null) {
                    itemsStr.append(item.foodItem.name)
                           .append(" x")
                           .append(item.quantity)
                           .append(" = ₹")
                           .append(String.format(Locale.getDefault(), "%.0f", item.totalPrice))
                           .append("; ");
                }
            }
            
            Intent intent = new Intent(this, InvoiceActivity.class);
            intent.putExtra("preview_mode", true);
            intent.putExtra("total_amount", totalAmount);
            intent.putExtra("invoice_number", invoiceNumber);
            intent.putExtra("date", date);
            intent.putExtra("items_string", itemsStr.toString());
            intent.putExtra("table_name", currentTable != null ? currentTable.name : "");
            intent.putExtra("order_items", new ArrayList<OrderItem>(orderItems));
            intent.putExtra("payment_method", paymentMethod);
            intent.putExtra("discount_percent", discountPercent);
            intent.putExtra("discount_amount", discountAmount);
            intent.putExtra("tax_percent", taxPercent);
            intent.putExtra("tax_amount", taxAmount);
            intent.putExtra("final_amount", finalAmount);
            startActivityForResult(intent, INVOICE_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(this, "Error creating invoice: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}