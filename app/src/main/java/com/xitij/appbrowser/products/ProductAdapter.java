package com.xitij.appbrowser.products;

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
import com.xitij.appbrowser.databinding.ItemShoppingproductBinding;
import com.xitij.appbrowser.models.ProductRoot;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ShoppingViewHolder> {
    private List<ProductRoot> apps;
    private Context context;

    public ProductAdapter(List<ProductRoot> apps) {

        this.apps = apps;
    }

    @NonNull
    @Override
    public ShoppingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shoppingproduct, parent, false);
        return new ShoppingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingViewHolder holder, int position) {
        Log.d("TAG", "onBindViewHolder: " + apps.get(position).getImage());
        Glide.with(context)
                .load(apps.get(position).getImage())
                // .apply(new RequestOptions().transform(new RoundedCorners(20))).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)
                // .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                .transform(new RoundedCorners(20))
                .into(holder.binding.imageview);
        holder.binding.tvAppName.setText(apps.get(position).getName());
        holder.binding.tvBrand.setText(apps.get(position).getBrand());
        holder.binding.tvSaleprice.setText("Today's Price " + apps.get(position).getSalePrice() + "$");
        holder.binding.tvMainprice.setText(apps.get(position).getMainPrice() + "$");
        holder.binding.imageview.setOnClickListener(v -> {
            Intent intent = new Intent(context, AppsWebsiteActivity.class);
            intent.putExtra("URL", apps.get(position).getLink());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public class ShoppingViewHolder extends RecyclerView.ViewHolder {
        ItemShoppingproductBinding binding;

        public ShoppingViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemShoppingproductBinding.bind(itemView);
        }
    }
}
