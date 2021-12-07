package com.xitij.appbrowser.apps;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.xitij.appbrowser.BuildConfig;
import com.xitij.appbrowser.R;
import com.xitij.appbrowser.adapters.AdapterSmartApps;
import com.xitij.appbrowser.adapters.AdapterTopApps;
import com.xitij.appbrowser.adapters.CategoryAdapter;
import com.xitij.appbrowser.browser.WebActivity;
import com.xitij.appbrowser.databinding.ActivityMainBinding;
import com.xitij.appbrowser.models.AdsRoot;
import com.xitij.appbrowser.models.AppsRoot;
import com.xitij.appbrowser.models.CategoryRoot;
import com.xitij.appbrowser.models.SmartAppsRoot;
import com.xitij.appbrowser.products.ProductsFragment;
import com.xitij.appbrowser.vpn.ConnectVpnActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int SPEECH_REQUEST_CODE = 1;
    private static final String TAG = "mainact";
    ActivityMainBinding binding;
    FirebaseFirestore db;
    List<CategoryRoot> topCategoris = new ArrayList<>();
    private List<CategoryRoot> categories = new ArrayList<>();
    private List<AppsRoot> topApps = new ArrayList<>();
    private List<SmartAppsRoot> smartApps = new ArrayList<>();
    private boolean lodedTopCategories = false;
    private boolean lodedSmartApps = false;
    private boolean lodedApps = false;
    private boolean lodedCategories = false;
    private SmartAppsRoot tempSmartApp;
    private CategoryAdapter categoryAdapter;
    public static List<AdsRoot> advertisements = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        MobileAds.initialize(this, initializationStatus -> {

        });
        AudienceNetworkAds.initialize(this);

        db = FirebaseFirestore.getInstance();


        binding.pd.setVisibility(View.VISIBLE);
        initListnear();
        getAdsData();
        getData();


        setUpDrawer();

    }

    private void initListnear() {
        categoryAdapter = new CategoryAdapter(new CategoryAdapter.OnCategoryListnear() {
            @Override
            public void oncatOperation(CategoryRoot categoryRoot) {
                categories.remove(categoryRoot);
                categoryAdapter.notifyDataSetChanged();
            }
        });

    }

    private void getAdsData() {
        db.collection("advertisements").whereEqualTo("show", true).get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, "getAdsData: " + document.getData().toString());
                        AdsRoot app = document.toObject(AdsRoot.class);
                        app.setId(document.getId());
                        app.setMnative(Objects.requireNonNull(document.get("native")).toString());
                        advertisements.add(app);
                        Log.d(TAG, "getAdsData: " + app.getMnative());
                        Log.d(TAG, "getAdsData: " + app.getId());
                    }

                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void setUpDrawer() {
        binding.navToolbar.navHome.setOnClickListener(v -> binding.drawerLayout.closeDrawer(GravityCompat.START));
        binding.navToolbar.navBrowser.setOnClickListener(v -> startActivity(new Intent(this, WebActivity.class)));
        binding.navToolbar.navVpn.setOnClickListener(v -> startActivity(new Intent(this, ConnectVpnActivity.class)));
       
    }

    private void openAboutPage() {
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("words", "terms");
        startActivity(intent);

    }


    private void getData() {

        lodedTopCategories = false;
        lodedSmartApps = false;
        lodedApps = false;
        lodedCategories = false;
        db.collection("categories").whereEqualTo("top", true).get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        CategoryRoot categoryRoot = document.toObject(CategoryRoot.class);
                        categoryRoot.setId(document.getId());
                        if (document.get("icon") != null) {
                            categoryRoot.setImage(document.get("icon").toString());
                        }


                        topCategoris.add(categoryRoot);

                        Log.d(TAG, "onComplete:cc cat " + categoryRoot.getName());
                        Log.d(TAG, "onComplete:cc cat " + categoryRoot.getImage());
                        Log.d(TAG, "onComplete:cc doc " + document.get("icon"));
                        Log.d(TAG, document.getId() + " => " + document.getData().toString());


                    }
                    if (topCategoris != null && !topCategoris.isEmpty()) {
                        setTopCategories();
                        lodedTopCategories = true;
                        chkIsLoded();
                    }

                });

        db.collection("smartapps").get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        SmartAppsRoot smartAppsRoot = document.toObject(SmartAppsRoot.class);

                        Log.d(TAG, "onComplete:11 " + smartAppsRoot.getName());

                        List<Map<String, Object>> apps = (List<Map<String, Object>>) document.get("apps");
                        Log.d(TAG, "onComplete:112 " + apps.toString());

                        smartAppsRoot.setApps(apps);
                        smartApps.add(smartAppsRoot);


                    }
                    for (int i = 0; i < smartApps.size(); i++) {
                        if (smartApps.get(i).getName().equalsIgnoreCase("shopping")) {
                            tempSmartApp = smartApps.get(i);
                            Log.d(TAG, "getData: yes removed shopping" + i);
                            smartApps.remove(i);
                        }
                    }
                    if (tempSmartApp != null) {
                        smartApps.add(0, tempSmartApp);
                    }

                    AdapterSmartApps adapterSmartApps = new AdapterSmartApps(smartApps);
                    binding.rvSmartApps.setAdapter(adapterSmartApps);
                    lodedSmartApps = true;
                    chkIsLoded();
                });
        db.collection("apps").whereEqualTo("top", true).limit(8).get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        AppsRoot app = document.toObject(AppsRoot.class);
                        app.setId(document.getId());

                        topApps.add(app);
                    }
                    AdapterTopApps adapterTopApps = new AdapterTopApps(topApps, false);
                    binding.rvTopApps.setAdapter(adapterTopApps);
                    binding.tvSeeAlltop.setOnClickListener(this);
                    lodedApps = true;
                    chkIsLoded();
                });
        db.collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            CategoryRoot categoryRoot = document.toObject(CategoryRoot.class);
                            categoryRoot.setId(document.getId());
                            categories.add(categoryRoot);

                            Log.d(TAG, "onComplete: cat " + categoryRoot.getName());
                            Log.d(TAG, "onComplete: doc " + document.getId());
                            Log.d(TAG, document.getId() + " => " + document.getData().toString());

                        }

                        for (int i = 0; i < categories.size(); i++) {
                            if (i % 4 == 0) {
                                categories.add(i, null);
                            }

                        }

                        categoryAdapter.addAdvertisement(advertisements);
                        categoryAdapter.addData(categories);
                        binding.rvCategories.setAdapter(categoryAdapter);
                    } else {
                        Log.d(TAG, "Error getting documents.", task.getException());
                    }
                    lodedCategories = true;
                    chkIsLoded();
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });


    }


    private void chkIsLoded() {
        if (lodedTopCategories && lodedApps && lodedCategories && lodedSmartApps) {
            binding.pd.setVisibility(View.GONE);

        }
    }

    private void setTopCategories() {

        binding.lyttopap.tv1.setText(topCategoris.get(0).getName());
        binding.lyttopap.tv2.setText(topCategoris.get(1).getName());
        binding.lyttopap.tv3.setText(topCategoris.get(2).getName());
        binding.lyttopap.tv4.setText(topCategoris.get(3).getName());
        binding.lyttopap.tv5.setText(topCategoris.get(4).getName());


        Glide.with(this).load(topCategoris.get(0).getImage()).into(binding.lyttopap.imageview1);
        Glide.with(this).load(topCategoris.get(1).getImage()).into(binding.lyttopap.imageview2);
        Glide.with(this).load(topCategoris.get(2).getImage()).into(binding.lyttopap.imageview3);
        Glide.with(this).load(topCategoris.get(3).getImage()).into(binding.lyttopap.imageview4);
        Glide.with(this).load(topCategoris.get(4).getImage()).into(binding.lyttopap.imageview5);

        binding.lyttopap.imageview1.setOnClickListener(v -> openCategory(topCategoris.get(0)));
        binding.lyttopap.imageview2.setOnClickListener(v -> openCategory(topCategoris.get(1)));
        binding.lyttopap.imageview3.setOnClickListener(v -> openCategory(topCategoris.get(2)));
        binding.lyttopap.imageview4.setOnClickListener(v -> openCategory(topCategoris.get(3)));
        binding.lyttopap.imageview5.setOnClickListener(v -> openCategory(topCategoris.get(4)));


    }

    private void openCategory(CategoryRoot id) {
        Intent intent = new Intent(this, ViewAllActivity.class);
        intent.putExtra("type", "CATEGORY");
        intent.putExtra("Category", new Gson().toJson(id));
        startActivity(intent);
    }

    public void onclickBrowser(View view) {
        startActivity(new Intent(this, WebActivity.class));
    }

    public void onclickMic(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = null;
            if (data != null) {
                results = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
            }
            if (results != null && !results.isEmpty()) {
                String spokenText = results.get(0);
                // Do something with spokenText
                binding.tvSearch.setText(spokenText);
                Intent intent = new Intent(MainActivity.this, WebActivity.class);
                intent.putExtra("words", spokenText);
                startActivity(intent);
                binding.tvSearch.setText("Search Apps &amp; Web");
            }

        }
    }

    public void onclickSearch(View view) {
        startActivity(new Intent(this, SearchActivity.class));
    }

    public void onClickMenu(View view) {
        binding.drawerLayout.openDrawer(GravityCompat.START);
    }


    @Override
    public void onClick(View v) {
        if (v == binding.tvSeeAlltop) {
            Intent intent = new Intent(this, ViewAllActivity.class);
            intent.putExtra("type", "TOPAPPS");
            startActivity(intent);
        }
    }
}