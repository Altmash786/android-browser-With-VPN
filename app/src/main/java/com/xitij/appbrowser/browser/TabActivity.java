package com.xitij.appbrowser.browser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.xitij.appbrowser.R;
import com.xitij.appbrowser.databinding.ActivityTabBinding;

import java.util.List;

public class TabActivity extends AppCompatActivity {

    public static final String TAB_OPEN = "OPEN";
    public static final String TAB_ADD = "ADD";
    public static final String CLEARALL = "CLEAR";
    private static final String TAG = "web:tablist";
    ActivityTabBinding binding;
    private List<BrowserFragment> websites;
    private TabAdapter tabAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tab);

        websites = ViewPager2Adapter.list;


        for (int i = 0; i < websites.size(); i++) {
            Log.d(TAG, "onCreate: " + websites.get(i));
        }

        tabAdapter = new TabAdapter(websites, (websiteModel, pos, work) -> {
            if (work.equals("CLOSE")) {
                websites.remove(pos);
                tabAdapter.notifyItemRemoved(pos);
            }
            if (work.equals("OPEN")) {
                setCallbackResult(TAB_OPEN, pos, false);
            }
        });
        binding.rvtabs.setAdapter(tabAdapter);

        initListnear();
    }

    private void setCallbackResult(String task, int pos, boolean isPrivate) {
        Intent intent = new Intent();
        intent.putExtra("task", task);
        intent.putExtra("pos", pos);
        intent.putExtra("isprivate", isPrivate);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initListnear() {
        binding.imgadd.setOnClickListener(v -> setCallbackResult(TAB_ADD, websites.size(), false));
        binding.tvClearAll.setOnClickListener(v -> setCallbackResult(CLEARALL, websites.size(), false));
    }

}