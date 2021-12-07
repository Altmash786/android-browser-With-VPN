package com.xitij.appbrowser.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.xitij.appbrowser.R;
import com.xitij.appbrowser.apps.AppsWebsiteActivity;
import com.xitij.appbrowser.databinding.ItemShoppingBinding;
import com.xitij.appbrowser.models.AppsRoot;

import java.util.List;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ShoppingViewHolder> {
    private List<AppsRoot> apps;
    private Context context;

    public AppsAdapter(List<AppsRoot> apps) {

        this.apps = apps;
    }

    @NonNull
    @Override
    public ShoppingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping, parent, false);
        return new ShoppingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingViewHolder holder, int position) {
        Log.d("TAG", "onBindViewHolder: " + apps.get(position).getIcon());
        Glide.with(context)
                .load(apps.get(position).getIcon())
                // .apply(new RequestOptions().transform(new RoundedCorners(20))).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                // .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                .transform(new RoundedCorners(20))
                .into(holder.binding.imageview);
        holder.binding.tvAppName.setText(apps.get(position).getName());

        holder.binding.imageview.setOnClickListener(v -> {
            Intent intent = new Intent(context, AppsWebsiteActivity.class);
            intent.putExtra("URL", apps.get(position).getUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public class ShoppingViewHolder extends RecyclerView.ViewHolder {
        ItemShoppingBinding binding;

        public ShoppingViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemShoppingBinding.bind(itemView);
        }
    }
}
