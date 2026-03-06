package com.bizflow.pos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.SalesViewHolder> {
    private List<Sale> sales = new ArrayList<>();

    @NonNull
    @Override
    public SalesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sale, parent, false);
        return new SalesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalesViewHolder holder, int position) {
        Sale sale = sales.get(position);
        holder.bind(sale);
    }

    @Override
    public int getItemCount() {
        return sales.size();
    }

    public void updateSales(List<Sale> newSales) {
        this.sales = newSales;
        notifyDataSetChanged();
    }

    static class SalesViewHolder extends RecyclerView.ViewHolder {
        TextView txtSaleInvoiceNumber, txtSaleAmount, txtSaleDate, txtSaleItems;

        public SalesViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSaleInvoiceNumber = itemView.findViewById(R.id.txtSaleInvoiceNumber);
            txtSaleAmount = itemView.findViewById(R.id.txtSaleAmount);
            txtSaleDate = itemView.findViewById(R.id.txtSaleDate);
            txtSaleItems = itemView.findViewById(R.id.txtSaleItems);
        }

        public void bind(Sale sale) {
            txtSaleInvoiceNumber.setText(sale.invoiceNumber);
            txtSaleAmount.setText(String.format(Locale.getDefault(), "₹%.0f", sale.totalAmount));
            txtSaleDate.setText(sale.date);
            txtSaleItems.setText("Items: " + sale.items);
        }
    }
}