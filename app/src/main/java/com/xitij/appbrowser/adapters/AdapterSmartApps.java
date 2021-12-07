package com.xitij.appbrowser.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.xitij.appbrowser.R;
import com.xitij.appbrowser.apps.SmartAppaActivity;
import com.xitij.appbrowser.databinding.ItemSmartappsBinding;
import com.xitij.appbrowser.models.SmartAppsRoot;

import java.util.List;

public class AdapterSmartApps extends RecyclerView.Adapter<AdapterSmartApps.TopAppsviewHolder> {


    private List<SmartAppsRoot> apps;
    private Context context;

    public AdapterSmartApps(List<SmartAppsRoot> apps) {


        this.apps = apps;
    }

    @NonNull
    @Override
    public TopAppsviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_smartapps, parent, false);
        return new TopAppsviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopAppsviewHolder holder, int position) {
        SmartAppsRoot smartAppsRoot = apps.get(position);
        Log.d("TAG", "onBindViewHolder: " + position + " ==" + smartAppsRoot.getName());
        holder.binding.tvName.setText(smartAppsRoot.getName());
        Glide.with(context)
                .load(smartAppsRoot.getIcon())
                .into(holder.binding.imageview);

        holder.binding.imageview.setOnClickListener(v -> {
            Intent intent = new Intent(context, SmartAppaActivity.class);
            intent.putExtra("smartapp", new Gson().toJson(smartAppsRoot));
            context.startActivity(intent);
        });
        holder.binding.imgAdd.setOnClickListener(v -> {
            View content = holder.binding.imageview;

            content.setDrawingCacheEnabled(true);
            Bitmap bitmap = content.getDrawingCache();

            Intent intent = new Intent(context, SmartAppaActivity.class).setAction(Intent.ACTION_MAIN);
            intent.putExtra("object", new Gson().toJson(smartAppsRoot));

            if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
                ShortcutInfoCompat shortcutInfo = new ShortcutInfoCompat.Builder(context, smartAppsRoot.getName())
                        .setIntent(intent) // !!! intent's action must be set on oreo
                        .setShortLabel(smartAppsRoot.getName())
                        .setIcon(IconCompat.createWithBitmap(bitmap))
                        .build();
                ShortcutManagerCompat.requestPinShortcut(context, shortcutInfo, null);
                Toast.makeText(context, "Added To Home Screen", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "not suppotted", Toast.LENGTH_SHORT).show();
                // Shortcut is not supported by your launcher
            }
        });
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public class TopAppsviewHolder extends RecyclerView.ViewHolder {
        ItemSmartappsBinding binding;
        public TopAppsviewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSmartappsBinding.bind(itemView);
        }
    }
}
