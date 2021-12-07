package com.xitij.appbrowser.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.xitij.appbrowser.R;
import com.xitij.appbrowser.apps.AppsWebsiteActivity;
import com.xitij.appbrowser.databinding.ItemSearchappsBinding;
import com.xitij.appbrowser.databinding.ItemShoppingBinding;
import com.xitij.appbrowser.models.AppsRoot;

import java.util.List;

public class AdapterTopApps extends RecyclerView.Adapter<AdapterTopApps.TopAppsViewHolder> {
    private List<AppsRoot> topApps;
    private boolean isSearch = false;
    private Context context;

    public AdapterTopApps(List<AppsRoot> topApps, boolean isSearch) {

        this.topApps = topApps;
        this.isSearch = isSearch;
    }

    @NonNull
    @Override
    public TopAppsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

      /*  if (isSearch) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searchapps, parent, false);
            return new TopAppsViewHolder(view);
        } else {*/
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping, parent, false);
        return new TopAppsViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull TopAppsViewHolder holder, int position) {

      /*  if (isSearch) {
            Glide.with(context)
                    .load(topApps.get(position).getIcon())
                    .centerCrop()
                    .into(holder.binding1.imageview);
            holder.binding1.tvName.setText(topApps.get(position).getName());

            holder.binding1.imageview.setOnClickListener(v -> {
                Intent intent = new Intent(context, AppsWebsiteActivity.class);
                intent.putExtra("URL", topApps.get(position).getUrl());
                context.startActivity(intent);
            });
        } else {*/
        Glide.with(context)
                .load(topApps.get(position).getIcon())
                .centerCrop()
                .apply(new RequestOptions().transform(new RoundedCorners(20))).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(holder.binding.imageview);
            holder.binding.tvAppName.setText(topApps.get(position).getName());

            holder.binding.imageview.setOnClickListener(v -> {
                Intent intent = new Intent(context, AppsWebsiteActivity.class);
                intent.putExtra("URL", topApps.get(position).getUrl());
                context.startActivity(intent);
            });


    }

    @Override
    public int getItemCount() {
        return topApps.size();
    }

    public class TopAppsViewHolder extends RecyclerView.ViewHolder {
        ItemShoppingBinding binding;
        ItemSearchappsBinding binding1;

        public TopAppsViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = ItemShoppingBinding.bind(itemView);


        }
    }
}
