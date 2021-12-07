package com.xitij.appbrowser.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.xitij.appbrowser.apps.SmartWebsitesFragment;

import java.util.List;
import java.util.Map;

public class SmartWebsitesAdapter extends FragmentStateAdapter {

    private List<Map<String, Object>> websites;
    private boolean isShopping;
    private String word;
    private OnTabLodedListner2 onTabLodedListner2;

    public SmartWebsitesAdapter(@NonNull FragmentActivity fragmentActivity, List<Map<String, Object>> websites, boolean isShopping, String word, OnTabLodedListner2 onTabLodedListner2) {
        super(fragmentActivity);
        this.websites = websites;
        this.isShopping = isShopping;
        this.word = word;
        this.onTabLodedListner2 = onTabLodedListner2;


        runfragments();
    }

    private void runfragments() {
        for (int i = 0; i < websites.size(); i++) {
            int finalI = i;
            new SmartWebsitesFragment(websites.get(i).get("url").toString(), isShopping, word, websites.get(i).get("name").toString()
                    , b -> {
                onTabLodedListner2.onTabLoded(b, finalI);
                Log.d("lodddd", "onTabLoded: " + finalI);
            });
        }

    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.d("smartt", "createFragment: " + word);

        return new SmartWebsitesFragment(websites.get(position).get("url").toString(), isShopping, word, websites.get(position).get("name").toString()
                , b -> {
            onTabLodedListner2.onTabLoded(b, position);
            Log.d("lodddd", "onTabLoded: " + position);
        });
    }


    public interface OnTabLodedListner2 {
        void onTabLoded(boolean b, int p);
    }

    @Override
    public int getItemCount() {
        return websites.size();
    }

}
