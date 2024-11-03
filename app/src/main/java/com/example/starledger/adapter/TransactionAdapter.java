package com.example.starledger.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.starledger.R;
import com.example.starledger.bean.Transaction;

import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private List<Transaction> transactions;
    private ViewHolder.OnItemClickListener onItemClickListener;

    public TransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void setOnItemClickListener(ViewHolder.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        String type = transaction.getAmount() >= 0 ? "Income: " : "Expense: ";
        String category = transaction.getCategory();
        String amount = String.format(Locale.getDefault(), "%.2f", transaction.getAmount());
        holder.textMsg.setText(type + category + " is $" + amount);
        holder.textDescription.setText(transaction.getDescription());

        // 设置图标
        if (transaction.getAmount() >= 0) {
            holder.imageView.setImageResource(R.drawable.income); // 假设你有一个收入的图标
        } else {
            holder.imageView.setImageResource(R.drawable.expense); // 假设你有一个支出的图标
        }
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textMsg, textDescription;
        public ImageView imageView;
        private OnItemClickListener onItemClickListener;

        public ViewHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            textMsg = itemView.findViewById(R.id.text_msg);
            textDescription = itemView.findViewById(R.id.text_description);
            imageView = itemView.findViewById(R.id.imageView);
            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(view, getAdapterPosition());
            }
        }

        public interface OnItemClickListener {
            void onItemClick(View view, int position);
        }
    }
}
