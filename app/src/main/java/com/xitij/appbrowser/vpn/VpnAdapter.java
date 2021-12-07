package com.xitij.appbrowser.vpn;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anchorfree.partner.api.data.Country;
import com.xitij.appbrowser.R;
import com.xitij.appbrowser.databinding.ItemCountriesBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VpnAdapter extends RecyclerView.Adapter<VpnAdapter.VpnViewHolder> {

    private List<Country> countries = new ArrayList<>();
    private Context context;

    @NonNull
    @Override
    public VpnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_countries, parent, false);
        return new VpnViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VpnViewHolder holder, int position) {
        Country country = countries.get(position);
        Locale locale = new Locale("", countries.get(position).getCountry());
        country.getCountry();
        Log.d(ConnectVpnActivity.TAG, "onBindViewHolder:vpn " + position + " " + country.getCountry());
        if (!country.getCountry().equals("")) {
            // holder.regionTitle.setText(locale.getDisplayCountry());
            String str = countries.get(position).getCountry();
            Log.d(ConnectVpnActivity.TAG, "onBindViewHolder: yes get " + country.getCountry());
            holder.binding.imageview.setImageResource(context.getResources().getIdentifier("raw/" + str, null, context.getPackageName()));
        /*    holder.itemView.setOnClickListener(view -> {
                listAdapterInterface.onCountrySelected(context.get(holder.getAdapterPosition()));
                Prefs.putString("sname", regions.get(position).getCountry());
                Prefs.putString("simage", regions.get(position).getCountry());
            });*/
        } else {
            Log.d(ConnectVpnActivity.TAG, "onBindViewHolder: not get");
        }
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }

    public void addCountries(List<Country> countries) {

        this.countries = countries;
        notifyDataSetChanged();
    }

    public class VpnViewHolder extends RecyclerView.ViewHolder {
        ItemCountriesBinding binding;

        public VpnViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCountriesBinding.bind(itemView);
        }
    }
}
