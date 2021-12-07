package com.xitij.appbrowser.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xitij.appbrowser.R;
import com.xitij.appbrowser.browser.WebActivity;
import com.xitij.appbrowser.databinding.ItemSearchhistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.SearchViewHolder> {
    private List<String> search = new ArrayList<>();
    private Context context;

    public SearchHistoryAdapter(List<String> search) {

        this.search = search;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searchhistory, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        String item = search.get(position);
        holder.binding.tvName.setText(item);
        holder.binding.imgopen.setOnClickListener(v -> {
            Intent intent = new Intent(context, WebActivity.class);
            intent.putExtra("words", item);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return search.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        ItemSearchhistoryBinding binding;
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSearchhistoryBinding.bind(itemView);
        }
    }
}
