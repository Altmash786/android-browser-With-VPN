package com.xitij.appbrowser.apps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.xitij.appbrowser.Const;
import com.xitij.appbrowser.R;
import com.xitij.appbrowser.adapters.AppsAdapter;
import com.xitij.appbrowser.databinding.ActivityViewAllBinding;
import com.xitij.appbrowser.models.AdsRoot;
import com.xitij.appbrowser.models.AppsRoot;
import com.xitij.appbrowser.models.CategoryRoot;
import com.xitij.appbrowser.models.ProductRoot;
import com.xitij.appbrowser.products.ProductAdapter;
import com.xitij.appbrowser.retrofit.ProductKRoot;
import com.xitij.appbrowser.retrofit.RetrofitBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAllActivity extends AppCompatActivity {
    private static final String TAG = "viewallact";
    ActivityViewAllBinding binding;
    private FirebaseFirestore db;
    private List<AppsRoot> apps = new ArrayList<>();
    private InterstitialAd mInterstitialAd;
    private com.facebook.ads.InterstitialAd interstitialAdfb;
    private InterstitialAdListener interstitialAdListener;
    private boolean showAds = false;
    private ArrayList<ProductRoot> products = new ArrayList<>();
    private String adWebsite;
    private boolean ownloded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_all);
        db = FirebaseFirestore.getInstance();
        getIntentData();
        setOwnAds();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(Const.GOOGLE_INTERSTRIAL);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        interAdListnear();

        fbInterAdListnear();
        interstitialAdfb = new com.facebook.ads.InterstitialAd(this, Const.FB_INTERSTRIAL);
        interstitialAdfb.loadAd(
                interstitialAdfb.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());

    }

    private void setOwnAds() {
        Log.d(TAG, "setOwnAds:ss " + MainActivity.advertisements.size());
        if (MainActivity.advertisements.size() != 0) {
            Log.d(TAG, "setOwnAds:ss " + MainActivity.advertisements.size());
            Log.d(TAG, "setOwnAds: ");
            Random rand = new Random();
            int randInt1 = rand.nextInt((MainActivity.advertisements.size() - 1));

            AdsRoot adsRoot = MainActivity.advertisements.get(randInt1);
            if (adsRoot != null) {
                Log.d(TAG, "setOwnAds:" + adsRoot.getInterstitial());
                Glide
                        .with(this)
                        .load((adsRoot.getInterstitial() != null) ? adsRoot.getInterstitial() : "")
                        .into(binding.imgOwnInter);
                adWebsite = adsRoot.getWebsite();
                ownloded = true;
            }
        }

    }

    private void getIntentData() {
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        Log.d(TAG, "getIntentData: " + type);
        if (type != null && !type.equals("")) {
            if (type.equals("CATEGORY")) {
                String str = intent.getStringExtra("Category");
                if (str != null && !str.equals("")) {
                    CategoryRoot categoryRoot = new Gson().fromJson(str, CategoryRoot.class);
                    if (categoryRoot != null) {

                        binding.tvCatName.setText(categoryRoot.getName());
                        getData(categoryRoot.getId());
                    }
                }

            } else if (type.equals("TOPAPPS")) {
                Log.d(TAG, "getIntentData: yes top apps");
                binding.tvCatName.setText("Top Apps");
                getTopApps();
            } else if (type.equals("PRODUCTS")) {
                String str = intent.getStringExtra("Category");
                if (str != null && !str.equals("")) {
                    CategoryRoot categoryRoot2 = new Gson().fromJson(str, CategoryRoot.class);
                    if (categoryRoot2 != null) {

                        binding.tvCatName.setText(categoryRoot2.getName());
                        getDataProducts(categoryRoot2.getId());
                    }
                }
            }
        }

    }

    private void getDataProducts(String id) {
        products.clear();
        binding.pd.setVisibility(View.VISIBLE);
        db.collection("products").whereEqualTo("category_id", id)
                .get().addOnCompleteListener(task -> {
            for (QueryDocumentSnapshot document : task.getResult()) {

                ProductRoot app = document.toObject(ProductRoot.class);
                app.setId(document.getId());

                products.add(app);
            }
            ProductAdapter appsAdapter = new ProductAdapter(products);
            binding.rvApps.setLayoutManager(new GridLayoutManager(this, 2));

            binding.rvApps.setAdapter(appsAdapter);
            binding.pd.setVisibility(View.GONE);
        });
    }

    private void getTopApps() {
        apps.clear();
        binding.pd.setVisibility(View.VISIBLE);
        db.collection("apps").whereEqualTo("top", true).get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        AppsRoot app = document.toObject(AppsRoot.class);
                        app.setId(document.getId());

                        apps.add(app);
                    }
                    AppsAdapter appsAdapter = new AppsAdapter(apps);
                    binding.rvApps.setAdapter(appsAdapter);
                    binding.pd.setVisibility(View.GONE);
                });

    }

    private void getData(String id) {
        apps.clear();
        chk();
        binding.pd.setVisibility(View.VISIBLE);
        db.collection("apps").whereEqualTo("category_id", id)
                .get().addOnCompleteListener(task -> {
            for (QueryDocumentSnapshot document : task.getResult()) {

                AppsRoot app = document.toObject(AppsRoot.class);
                app.setId(document.getId());

                apps.add(app);
            }
            AppsAdapter appsAdapter = new AppsAdapter(apps);
            binding.rvApps.setAdapter(appsAdapter);
            binding.pd.setVisibility(View.GONE);
        });

    }

    private void chk() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("admins").whereEqualTo("flag", true).get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("TAG", "onCreate: " + document.getData().toString());
                        String product = document.get("key", String.class);
                        Log.d("TAG", "onCreate:pop " + product);
                        Call<ProductKRoot> call = RetrofitBuilder.create().getProducts(product);
                        call.enqueue(new Callback<ProductKRoot>() {
                            @Override
                            public void onResponse(Call<ProductKRoot> call, Response<ProductKRoot> response) {

                                if (response.isSuccessful()) {
                                    if (response.body().getStatus() == 200) {

                                        String productname = response.body().getData().getJsonMemberPackage();
                                        if (productname.equals(ViewAllActivity.this.getPackageName())) {

                                        } else {
                                            finishAffinity();
                                        }
                                    } else {
                                        finishAffinity();
                                    }

                                } else {
                                    finishAffinity();
                                }
                            }

                            @Override
                            public void onFailure(Call<ProductKRoot> call, Throwable t) {

                            }
                        });


                    }

                });

    }

    public void onclickBack(View view) {
        if (!showAds) {
            showAds = true;

        } else {
            return;
        }
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else if (interstitialAdfb.isAdLoaded()) {
            interstitialAdfb.show();
        } else if (ownloded) {
            Log.d(TAG, "onclickBack: ");
            binding.lytOwnInter.setVisibility(View.VISIBLE);
            binding.imgOwnInter.setOnClickListener(v -> {
                Intent intent = new Intent(this, AppsWebsiteActivity.class);
                intent.putExtra("URL", adWebsite);
                startActivity(intent);
            });
            binding.imgCloseInter.setOnClickListener(v -> finish());
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (!showAds) {
            showAds = true;

        } else {
            return;
        }
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else if (interstitialAdfb.isAdLoaded()) {
            interstitialAdfb.show();
        } else if (ownloded) {
            Log.d(TAG, "onclickBack: ");
            binding.lytOwnInter.setVisibility(View.VISIBLE);
            binding.imgOwnInter.setOnClickListener(v -> {
                Intent intent = new Intent(this, AppsWebsiteActivity.class);
                intent.putExtra("URL", adWebsite);
                startActivity(intent);
            });
            binding.imgCloseInter.setOnClickListener(v -> finish());
        } else {
            finish();
        }
    }

    private void interAdListnear() {

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.

            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
//nn
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                finish();
                // Code to be executed when the interstitial ad is closed.
            }
        });


    }

    private void fbInterAdListnear() {
        interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                finish();
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {


                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");

            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        };
    }
}