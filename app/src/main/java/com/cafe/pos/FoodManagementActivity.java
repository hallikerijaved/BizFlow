package com.bizflow.pos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FoodManagementActivity extends AppCompatActivity {
    private EditText etFoodName, etFoodPrice;
    private Spinner spinnerFoodCategory;
    private Button btnAddFood, btnUpdateFood, btnAddCategory, btnSelectImage;
    private ImageView imgFoodPreview;
    private RecyclerView recyclerViewManageFood;
    private ManageFoodAdapter manageFoodAdapter;
    private AppDatabase database;
    private FoodItem editingFood = null;
    private ExecutorService executor;
    private Handler mainHandler;
    private ArrayAdapter<String> categoryAdapter;
    private List<String> categories;
    private String selectedImagePath = null;
    private static final int PICK_IMAGE_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_management);

        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        initViews();
        setupDatabase();
        setupRecyclerView();
        setupClickListeners();
        loadFoodItems();
    }

    private void addNewCategory() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Add New Category");
        builder.setMessage("Enter category name:");

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Category name");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String newCategory = input.getText().toString().trim();
            if (!newCategory.isEmpty() && !categories.contains(newCategory)) {
                categories.add(newCategory);
                categoryAdapter.notifyDataSetChanged();
                spinnerFoodCategory.setSelection(categories.size() - 1);
                android.widget.Toast.makeText(this, "Category added", android.widget.Toast.LENGTH_SHORT).show();
            } else if (categories.contains(newCategory)) {
                android.widget.Toast.makeText(this, "Category already exists", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void initViews() {
        etFoodName = findViewById(R.id.etFoodName);
        etFoodPrice = findViewById(R.id.etFoodPrice);
        spinnerFoodCategory = findViewById(R.id.spinnerFoodCategory);
        btnAddFood = findViewById(R.id.btnAddFood);
        btnUpdateFood = findViewById(R.id.btnUpdateFood);
        btnAddCategory = findViewById(R.id.btnAddCategory);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        imgFoodPreview = findViewById(R.id.imgFoodPreview);
        recyclerViewManageFood = findViewById(R.id.recyclerViewManageFood);
        
        setupCategorySpinner();
        loadExistingCategories();
    }
    
    private void loadExistingCategories() {
        executor.execute(() -> {
            try {
                List<FoodItem> foodItems = database.foodDao().getAllFoodItems();
                java.util.Set<String> uniqueCategories = new java.util.HashSet<>();
                for (FoodItem item : foodItems) {
                    if (item != null && item.category != null && !item.category.isEmpty()) {
                        uniqueCategories.add(item.category);
                    }
                }
                
                mainHandler.post(() -> {
                    categories.clear();
                    categories.addAll(uniqueCategories);
                    java.util.Collections.sort(categories);
                    categoryAdapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                // Ignore errors, categories list will remain empty
            }
        });
    }

    private void setupCategorySpinner() {
        categories = new java.util.ArrayList<>();
        
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFoodCategory.setAdapter(categoryAdapter);
    }

    private void setupDatabase() {
        database = AppDatabase.getDatabase(this);
    }

    private void setupRecyclerView() {
        manageFoodAdapter = new ManageFoodAdapter(this::onEditFood, this::onDeleteFood);
        recyclerViewManageFood.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewManageFood.setAdapter(manageFoodAdapter);
    }

    private void setupClickListeners() {
        btnAddFood.setOnClickListener(v -> addFood());
        btnUpdateFood.setOnClickListener(v -> updateFood());
        btnAddCategory.setOnClickListener(v -> addNewCategory());
        btnSelectImage.setOnClickListener(v -> selectImage());
    }

    private void addFood() {
        String name = etFoodName.getText().toString().trim();
        String priceStr = etFoodPrice.getText().toString().trim();
        String category = spinnerFoodCategory.getSelectedItem().toString();

        if (name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            executor.execute(() -> {
                try {
                    FoodItem foodItem = new FoodItem(name, price, category);
                    foodItem.imagePath = selectedImagePath;
                    database.foodDao().insertFoodItem(foodItem);
                    
                    mainHandler.post(() -> {
                        clearFields();
                        loadFoodItems();
                        Toast.makeText(this, "Food item added successfully", Toast.LENGTH_SHORT).show();
                    });
                } catch (Exception e) {
                    mainHandler.post(() -> 
                        Toast.makeText(this, "Error adding food item", Toast.LENGTH_SHORT).show());
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFood() {
        if (editingFood == null) return;

        String name = etFoodName.getText().toString().trim();
        String priceStr = etFoodPrice.getText().toString().trim();
        String category = spinnerFoodCategory.getSelectedItem().toString();

        if (name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            editingFood.name = name;
            editingFood.price = price;
            editingFood.category = category;
            editingFood.imagePath = selectedImagePath;
            
            executor.execute(() -> {
                try {
                    database.foodDao().updateFoodItem(editingFood);
                    
                    mainHandler.post(() -> {
                        clearFields();
                        cancelEdit();
                        loadFoodItems();
                        Toast.makeText(this, "Food item updated successfully", Toast.LENGTH_SHORT).show();
                    });
                } catch (Exception e) {
                    mainHandler.post(() -> 
                        Toast.makeText(this, "Error updating food item", Toast.LENGTH_SHORT).show());
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
        }
    }

    private void onEditFood(FoodItem foodItem) {
        editingFood = foodItem;
        etFoodName.setText(foodItem.name);
        etFoodPrice.setText(String.valueOf(foodItem.price));
        
        // Load existing image with better format handling
        selectedImagePath = foodItem.imagePath;
        if (foodItem.imagePath != null && !foodItem.imagePath.isEmpty()) {
            try {
                Uri imageUri = Uri.parse(foodItem.imagePath);
                android.graphics.Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(
                    getContentResolver(), imageUri);
                imgFoodPreview.setImageBitmap(bitmap);
            } catch (Exception e) {
                try {
                    imgFoodPreview.setImageURI(Uri.parse(foodItem.imagePath));
                } catch (Exception e2) {
                    imgFoodPreview.setImageResource(android.R.drawable.ic_menu_camera);
                }
            }
        } else {
            imgFoodPreview.setImageResource(android.R.drawable.ic_menu_camera);
        }
        
        int position = categories.indexOf(foodItem.category);
        if (position >= 0) {
            spinnerFoodCategory.setSelection(position);
        }
        
        btnAddFood.setVisibility(Button.GONE);
        btnUpdateFood.setVisibility(Button.VISIBLE);
    }

    private void onDeleteFood(FoodItem foodItem) {
        executor.execute(() -> {
            try {
                database.foodDao().deleteFoodItem(foodItem);
                
                mainHandler.post(() -> {
                    loadFoodItems();
                    Toast.makeText(this, "Food item deleted", Toast.LENGTH_SHORT).show();
                });
            } catch (Exception e) {
                mainHandler.post(() -> 
                    Toast.makeText(this, "Error deleting food item", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void cancelEdit() {
        editingFood = null;
        btnAddFood.setVisibility(Button.VISIBLE);
        btnUpdateFood.setVisibility(Button.GONE);
    }

    private void clearFields() {
        etFoodName.setText("");
        etFoodPrice.setText("");
        spinnerFoodCategory.setSelection(0);
        selectedImagePath = null;
        imgFoodPreview.setImageResource(android.R.drawable.ic_menu_camera);
    }
    
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                // Take persistent permission and load with bitmap for better compatibility
                try {
                    getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (Exception e) {
                    // Ignore if permission can't be taken
                }
                selectedImagePath = imageUri.toString();
                
                // Load image using bitmap for better format support
                try {
                    android.graphics.Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);
                    imgFoodPreview.setImageBitmap(bitmap);
                } catch (Exception e) {
                    imgFoodPreview.setImageURI(imageUri);
                }
            }
        }
    }

    private void loadFoodItems() {
        executor.execute(() -> {
            try {
                List<FoodItem> foodItems = database.foodDao().getAllFoodItems();
                mainHandler.post(() -> {
                    if (manageFoodAdapter != null) {
                        manageFoodAdapter.updateFoodItems(foodItems);
                    }
                });
            } catch (Exception e) {
                mainHandler.post(() -> 
                    Toast.makeText(this, "Error loading food items", Toast.LENGTH_SHORT).show());
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