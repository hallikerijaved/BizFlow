package com.bizflow.pos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<String> categories;
    private String selectedCategory;
    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }

    public CategoryAdapter(List<String> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
        this.selectedCategory = "All";
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Button button = new Button(parent.getContext());
        button.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        
        // Get padding from resources
        int padding = parent.getContext().getResources().getDimensionPixelSize(
            com.bizflow.pos.R.dimen.category_button_padding);
        button.setPadding(padding, padding/2, padding, padding/2);
        button.setTextSize(12);
        
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(button.getLayoutParams());
        int margin = parent.getContext().getResources().getDimensionPixelSize(
            com.bizflow.pos.R.dimen.margin_small);
        params.setMargins(margin, 0, margin, 0);
        button.setLayoutParams(params);
        
        return new CategoryViewHolder(button);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.button.setText(category);
        holder.button.setSelected(category.equals(selectedCategory));
        holder.button.setOnClickListener(v -> {
            selectedCategory = category;
            notifyDataSetChanged();
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    public void updateCategories(List<String> newCategories, String selected) {
        this.categories = newCategories;
        this.selectedCategory = selected;
        notifyDataSetChanged();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        Button button;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            button = (Button) itemView;
        }
    }
}