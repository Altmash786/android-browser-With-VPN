package com.xitij.appbrowser.products;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.xitij.appbrowser.R;
import com.xitij.appbrowser.apps.ViewAllActivity;
import com.xitij.appbrowser.databinding.ItemProducttopBinding;
import com.xitij.appbrowser.models.CategoryRoot;

import java.util.List;

public class ProductTopCategoryAdapter extends RecyclerView.Adapter<ProductTopCategoryAdapter.CategoryViewHolder> {
    private Context context;
    private List<CategoryRoot> categories;

    public ProductTopCategoryAdapter(List<CategoryRoot> categories) {

        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_producttop, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {


        if (categories.get(position).getImage() != null) {
            Glide.with(context)
                    .load(categories.get(position).getImage())
                    .circleCrop()
                    .into(holder.binding.imageview1);
        }
        holder.binding.tv1.setText(categories.get(position).getName());

        holder.binding.imageview1.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewAllActivity.class);
            intent.putExtra("type", "PRODUCTS");
            intent.putExtra("Category", new Gson().toJson(categories.get(position)));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        ItemProducttopBinding binding;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemProducttopBinding.bind(itemView);
        }
    }
}
