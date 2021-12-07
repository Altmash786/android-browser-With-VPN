package com.xitij.appbrowser.browser;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xitij.appbrowser.R;
import com.xitij.appbrowser.databinding.ItemHistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistryViewHolder> {


    private List<String> list = new ArrayList<>();
    private OnHistoryClickListnear onHistoryClickListnear;

    public HistoryAdapter(List<String> list, OnHistoryClickListnear onHistoryClickListnear) {
        this.list = list;
        this.onHistoryClickListnear = onHistoryClickListnear;
    }

    @NonNull
    @Override
    public HistryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistryViewHolder holder, int position) {
        String url = list.get(position);
        holder.binding.tvHistory.setText(url);
        String[] trim1 = url.split("\\.");
        Log.d("TAG", url);

        if (trim1.length >= 2) {
            Log.d("TAG", trim1[0]);
            Log.d("TAG", "onBindViewHolder: " + trim1[1] + "=============================\n");
            String str = trim1[1];
            holder.binding.tvName.setText(str);
        } else {
            holder.binding.tvName.setText("Blank");
        }


        holder.binding.btnremove.setOnClickListener(v -> onHistoryClickListnear.onHistoryClick(url, "DELETE"));
        holder.itemView.setOnClickListener(v -> onHistoryClickListnear.onHistoryClick(url, "OPEN"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnHistoryClickListnear {
        void onHistoryClick(String url, String work);
    }

    public class HistryViewHolder extends RecyclerView.ViewHolder {
        ItemHistoryBinding binding;

        public HistryViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemHistoryBinding.bind(itemView);
        }
    }
}
