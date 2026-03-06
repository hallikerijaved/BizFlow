package com.bizflow.pos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ManageFoodAdapter extends RecyclerView.Adapter<ManageFoodAdapter.ManageFoodViewHolder> {
    private List<FoodItem> foodItems = new ArrayList<>();
    private OnEditFoodListener editListener;
    private OnDeleteFoodListener deleteListener;

    public interface OnEditFoodListener {
        void onEditFood(FoodItem foodItem);
    }

    public interface OnDeleteFoodListener {
        void onDeleteFood(FoodItem foodItem);
    }

    public ManageFoodAdapter(OnEditFoodListener editListener, OnDeleteFoodListener deleteListener) {
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ManageFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_food, parent, false);
        return new ManageFoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageFoodViewHolder holder, int position) {
        FoodItem foodItem = foodItems.get(position);
        holder.bind(foodItem);
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public void updateFoodItems(List<FoodItem> newFoodItems) {
        this.foodItems = newFoodItems;
        notifyDataSetChanged();
    }

    class ManageFoodViewHolder extends RecyclerView.ViewHolder {
        TextView txtManageFoodName, txtManageFoodPrice, txtManageFoodCategory;
        Button btnEditFood, btnDeleteFood;

        public ManageFoodViewHolder(@NonNull View itemView) {
            super(itemView);
            txtManageFoodName = itemView.findViewById(R.id.txtManageFoodName);
            txtManageFoodPrice = itemView.findViewById(R.id.txtManageFoodPrice);
            txtManageFoodCategory = itemView.findViewById(R.id.txtManageFoodCategory);
            btnEditFood = itemView.findViewById(R.id.btnEditFood);
            btnDeleteFood = itemView.findViewById(R.id.btnDeleteFood);
        }

        public void bind(FoodItem foodItem) {
            txtManageFoodName.setText(foodItem.name);
            txtManageFoodPrice.setText(String.format(Locale.getDefault(), "₹%.0f", foodItem.price));
            txtManageFoodCategory.setText(foodItem.category);

            btnEditFood.setOnClickListener(v -> editListener.onEditFood(foodItem));
            btnDeleteFood.setOnClickListener(v -> deleteListener.onDeleteFood(foodItem));
        }
    }
}