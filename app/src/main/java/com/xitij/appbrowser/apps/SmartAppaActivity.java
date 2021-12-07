package com.xitij.appbrowser.apps;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.xitij.appbrowser.Const;
import com.xitij.appbrowser.R;
import com.xitij.appbrowser.adapters.SmartWebsitesAdapter;
import com.xitij.appbrowser.databinding.ActivitySmartAppaBinding;
import com.xitij.appbrowser.models.AdsRoot;
import com.xitij.appbrowser.models.SmartAppsRoot;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class SmartAppaActivity extends AppCompatActivity {

    private static final String TAG = "smartappact";
    ActivitySmartAppaBinding binding;
    private List<Map<String, Object>> websites;
    private InterstitialAd mInterstitialAd;
    private com.facebook.ads.InterstitialAd interstitialAdfb;
    private InterstitialAdListener interstitialAdListener;
    private boolean showAds = false;
    private boolean isShopping = false;
    private String word = "";
    private String adWebsite;
    private boolean ownloded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.coloroffwhite));
        binding = DataBindingUtil.setContentView(this, R.layout.activity_smart_appa);

        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);

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


        in.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
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
        String str = intent.getStringExtra("smartapp");
        String shortcut = intent.getStringExtra("object");
        if (str != null && !str.equals("")) {
            Log.d("TAG", "getIntentData: str-----------------------");
            SmartAppsRoot smartAppsRoot = new Gson().fromJson(str, SmartAppsRoot.class);
            if (smartAppsRoot != null) {

                websites = smartAppsRoot.getApps();
                if (!websites.isEmpty()) {
                    loadWebsites();
                }
                if (smartAppsRoot.getName().equalsIgnoreCase("shopping")) {
                    binding.lytSearch.setVisibility(View.VISIBLE);
                    Log.d("smartt", "getIntentData: is shopping yes");
                    isShopping = true;
                    binding.etSearch.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//ll
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            word = s.toString();
                            if (!websites.isEmpty()) {
                                loadWebsites();
                            }
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
//ll
                        }
                    });
                    binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                            if (!websites.isEmpty()) {
                                loadWebsites();
                            }
                            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            in.hideSoftInputFromWindow(binding.etSearch.getWindowToken(), 0);
                            binding.etSearch.clearFocus();
                            return true;
                        }
                        return false;
                    });
                }


                if (!websites.isEmpty()) {
                    loadWebsites();
                }

            }
        }
        if (shortcut != null && !shortcut.equals("")) {
            Log.d("TAG", "getIntentData: shortcut----------------------------");
            SmartAppsRoot smartAppsRoot = new Gson().fromJson(shortcut, SmartAppsRoot.class);
            if (smartAppsRoot != null) {
                if (smartAppsRoot.getName().equalsIgnoreCase("shopping")) {
                    binding.lytSearch.setVisibility(View.VISIBLE);
                    isShopping = true;
                }
                websites = smartAppsRoot.getApps();
                if (!websites.isEmpty()) {
                    loadWebsites();
                }

            }
        }
    }

    private void loadWebsites() {
        TabLayout tabLayout = binding.tabLayout;
        SmartWebsitesAdapter smartWebsitesAdapter = new SmartWebsitesAdapter(this, websites, isShopping, word, (b, p) -> {
            Log.d("lodddd", "onTabLoded: " + p);
            View v = tabLayout.getTabAt(p).getCustomView();
            v.findViewById(R.id.pdweb).setVisibility(View.GONE);
        });
        binding.pager.setAdapter(smartWebsitesAdapter);


        new TabLayoutMediator(tabLayout, binding.pager, (tab, position) -> tab.setCustomView(createTabItemView(websites.get(position)))).attach();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.pager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
//ll
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//ll
            }
        });

        Log.d("TAG", "loadWebsites: " + websites);
    }

    private View createTabItemView(Map<String, Object> obj) {
        View v = LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        TextView tv = (TextView) v.findViewById(R.id.tvTab);
        tv.setText(obj.get("name").toString());
        ImageView img = (ImageView) v.findViewById(R.id.imagetab);

        Glide.with(this)
                .load(obj.get("icon"))
                .into(img);
        return v;

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