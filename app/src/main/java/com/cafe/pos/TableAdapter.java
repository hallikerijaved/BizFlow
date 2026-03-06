package com.bizflow.pos;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.TableViewHolder> {
    private List<Table> tables;
    private final OnTableClickListener listener;

    public interface OnTableClickListener {
        void onTableClick(Table table);
    }

    public TableAdapter(List<Table> tables, OnTableClickListener listener) {
        this.tables = tables;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_table, parent, false);
        return new TableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TableViewHolder holder, int position) {
        if (tables != null && position < tables.size()) {
            Table table = tables.get(position);
            if (table != null) {
                holder.bind(table);
            }
        }
    }

    @Override
    public int getItemCount() {
        return tables != null ? tables.size() : 0;
    }

    public void updateTables(List<Table> newTables) {
        this.tables = newTables != null ? newTables : new java.util.ArrayList<>();
        notifyDataSetChanged();
    }

    class TableViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txtTableName, txtTableCapacity, txtTableStatus;

        public TableViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            txtTableName = itemView.findViewById(R.id.txtTableName);
            txtTableCapacity = itemView.findViewById(R.id.txtTableCapacity);
            txtTableStatus = itemView.findViewById(R.id.txtTableStatus);
        }

        public void bind(Table table) {
            if (table == null) return;
            
            txtTableName.setText(table.name);
            txtTableCapacity.setText("Capacity: " + table.capacity);
            txtTableStatus.setText(table.status.toUpperCase());
            
            // Set card color based on status
            int backgroundColor;
            switch (table.status) {
                case "available":
                    backgroundColor = Color.parseColor("#E8F5E8"); // Light green
                    break;
                case "occupied":
                    backgroundColor = Color.parseColor("#FFE8E8"); // Light red
                    break;
                case "reserved":
                    backgroundColor = Color.parseColor("#FFF8E1"); // Light yellow
                    break;
                default:
                    backgroundColor = Color.parseColor("#F5F5F5"); // Light gray
                    break;
            }
            cardView.setCardBackgroundColor(backgroundColor);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTableClick(table);
                }
            });
        }
    }
}