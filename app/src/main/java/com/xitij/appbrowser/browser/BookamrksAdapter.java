package com.xitij.appbrowser.browser;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xitij.appbrowser.R;
import com.xitij.appbrowser.databinding.ItemBookmarkBinding;

import java.util.List;

public class BookamrksAdapter extends RecyclerView.Adapter<BookamrksAdapter.BookmarkViewHolder> {
    private List<String> list;
    private OnBookmarkClickListner onBookmarkClickListner;

    public BookamrksAdapter(List<String> list, OnBookmarkClickListner onBookmarkClickListner) {

        this.list = list;
        this.onBookmarkClickListner = onBookmarkClickListner;
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookmark, parent, false);
        return new BookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position) {
        String url = list.get(position);
        holder.binding.tvBookmark.setText(url);
        Log.d("TAG", url);
        String[] trim1 = url.split("//");
        Log.d("TAG", trim1[0]);
        Log.d("TAG", trim1[1]);
        String str = trim1[1].split("/")[0];
        Log.d("TAG", str);
        String[] c = str.split("\\.");
        Log.d("TAG", c[1]);
        holder.binding.tvName.setText(str);
        holder.binding.tvLetter.setText(String.valueOf(c[1].charAt(0)).toUpperCase());


        holder.binding.btnremove.setOnClickListener(v -> onBookmarkClickListner.onBookmarkClick(url, "DELETE"));
        holder.itemView.setOnClickListener(v -> onBookmarkClickListner.onBookmarkClick(url, "OPEN"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnBookmarkClickListner {
        void onBookmarkClick(String url, String work);
    }

    public class BookmarkViewHolder extends RecyclerView.ViewHolder {
        ItemBookmarkBinding binding;

        public BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemBookmarkBinding.bind(itemView);
        }
    }
}
