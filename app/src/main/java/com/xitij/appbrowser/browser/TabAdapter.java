package com.xitij.appbrowser.browser;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xitij.appbrowser.R;
import com.xitij.appbrowser.databinding.ItemTabsBinding;

import java.util.List;

public class TabAdapter extends RecyclerView.Adapter<TabAdapter.TabViewHolder> {
    private List<BrowserFragment> websites;
    private OnTabItemClickListnear onTabItemClickListnear;
    private Context context;

    public TabAdapter(List<BrowserFragment> websites, OnTabItemClickListnear onTabItemClickListnear) {

        this.websites = websites;
        this.onTabItemClickListnear = onTabItemClickListnear;
    }

    @NonNull
    @Override
    public TabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tabs, parent, false);
        return new TabViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TabViewHolder holder, int position) {

        BrowserFragment model = websites.get(position);
        Log.d("web tablistadapter", "onBindViewHolder: ");
        Log.d("web tablistadapter", "onBindViewHolder: " + model.getTitle());
        holder.binding.tvWebsite.setText(model.getTitle());
        try {
            Glide.with(context)
                    .asBitmap()
                    .load(model.getBitmap())
                    .centerCrop()
                    .into(holder.binding.image);
        } catch (Exception e) {
            Log.d("web ", "onBindViewHolder: cresh " + e.toString());
            e.printStackTrace();
        }
        holder.binding.image.setOnClickListener(v -> onTabItemClickListnear.onTabitemClick(model, position, "OPEN"));
        holder.binding.close.setOnClickListener(v -> onTabItemClickListnear.onTabitemClick(model, position, "CLOSE"));
    }

    @Override
    public int getItemCount() {
        return websites.size();
    }

    public interface OnTabItemClickListnear {
        void onTabitemClick(BrowserFragment websiteModel, int pos, String work);
    }

    public class TabViewHolder extends RecyclerView.ViewHolder {
        ItemTabsBinding binding;

        public TabViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemTabsBinding.bind(itemView);
        }
    }
}
