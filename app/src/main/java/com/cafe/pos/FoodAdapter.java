package com.bizflow.pos;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private List<FoodItem> foodItems;
    private final OnQuantityChangeListener listener;
    private final Map<Integer, Integer> quantities = new HashMap<>();

    public interface OnQuantityChangeListener {
        void onQuantityChanged(FoodItem foodItem, int quantity);
    }

    public FoodAdapter(List<FoodItem> foodItems, OnQuantityChangeListener listener) {
        this.foodItems = foodItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        if (foodItems != null && position < foodItems.size()) {
            FoodItem foodItem = foodItems.get(position);
            if (foodItem != null) {
                holder.bind(foodItem);
            }
        }
    }

    @Override
    public int getItemCount() {
        return foodItems != null ? foodItems.size() : 0;
    }

    public void updateFoodItems(List<FoodItem> newFoodItems) {
        this.foodItems = newFoodItems != null ? newFoodItems : new java.util.ArrayList<>();
        notifyDataSetChanged();
    }

    public void clearQuantities() {
        quantities.clear();
        notifyDataSetChanged();
    }

    public void setQuantity(int foodId, int quantity) {
        quantities.put(foodId, quantity);
        notifyDataSetChanged();
    }

    public int getItemPosition(int foodId) {
        for (int i = 0; i < foodItems.size(); i++) {
            if (foodItems.get(i).id == foodId) {
                return i;
            }
        }
        return -1;
    }

    class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView txtFoodName, txtFoodPrice, txtFoodCategory, txtQuantity;
        Button btnMinus, btnPlus;
        ImageView imgFood;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFoodName = itemView.findViewById(R.id.txtFoodName);
            txtFoodPrice = itemView.findViewById(R.id.txtFoodPrice);
            txtFoodCategory = itemView.findViewById(R.id.txtFoodCategory);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            imgFood = itemView.findViewById(R.id.imgFood);
        }

        public void bind(FoodItem foodItem) {
            if (foodItem == null) return;
            
            txtFoodName.setText(foodItem.name != null ? foodItem.name : "Unknown");
            txtFoodPrice.setText(String.format(Locale.getDefault(), "₹%.0f", foodItem.price));
            txtFoodCategory.setText(foodItem.category != null ? foodItem.category : "Other");
            
            // Load food image with better format handling
            if (foodItem.imagePath != null && !foodItem.imagePath.isEmpty()) {
                try {
                    Uri imageUri = Uri.parse(foodItem.imagePath);
                    // Use content resolver to load bitmap for better compatibility
                    android.graphics.Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(
                        imgFood.getContext().getContentResolver(), imageUri);
                    imgFood.setImageBitmap(bitmap);
                } catch (Exception e) {
                    // Fallback to direct URI loading
                    try {
                        imgFood.setImageURI(Uri.parse(foodItem.imagePath));
                    } catch (Exception e2) {
                        imgFood.setImageResource(android.R.drawable.ic_menu_gallery);
                    }
                }
            } else {
                imgFood.setImageResource(android.R.drawable.ic_menu_gallery);
            }
            
            int quantity = quantities.getOrDefault(foodItem.id, 0);
            txtQuantity.setText(String.valueOf(quantity));

            btnPlus.setOnClickListener(v -> {
                int currentQuantity = quantities.getOrDefault(foodItem.id, 0);
                int newQuantity = currentQuantity + 1;
                quantities.put(foodItem.id, newQuantity);
                txtQuantity.setText(String.valueOf(newQuantity));
                if (listener != null) {
                    listener.onQuantityChanged(foodItem, newQuantity);
                }
            });

            btnMinus.setOnClickListener(v -> {
                int currentQuantity = quantities.getOrDefault(foodItem.id, 0);
                if (currentQuantity > 0) {
                    int newQuantity = currentQuantity - 1;
                    quantities.put(foodItem.id, newQuantity);
                    txtQuantity.setText(String.valueOf(newQuantity));
                    if (listener != null) {
                        listener.onQuantityChanged(foodItem, newQuantity);
                    }
                }
            });
        }
    }
}