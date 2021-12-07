package com.xitij.appbrowser.apps;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

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
import com.xitij.appbrowser.Const;
import com.xitij.appbrowser.R;
import com.xitij.appbrowser.SessionManager;
import com.xitij.appbrowser.adapters.AdapterTopApps;
import com.xitij.appbrowser.adapters.SearchHistoryAdapter;
import com.xitij.appbrowser.databinding.ActivitySearchBinding;
import com.xitij.appbrowser.models.AdsRoot;
import com.xitij.appbrowser.models.AppsRoot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "searchact";
    ActivitySearchBinding binding;
    private FirebaseFirestore db;
    private List<AppsRoot> topApps = new ArrayList<>();
    private SessionManager sessonManager;
    private InterstitialAd mInterstitialAd;
    private com.facebook.ads.InterstitialAd interstitialAdfb;
    private InterstitialAdListener interstitialAdListener;
    private boolean showAds = false;
    private String adWebsite;
    private boolean ownloded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        db = FirebaseFirestore.getInstance();
        sessonManager = new SessionManager(this);
        initListnear();
        getSearchHistory();

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

    private void getSearchHistory() {
        ArrayList<String> search;
        search = sessonManager.getSearch();
        SearchHistoryAdapter searchHistoryAdapter = new SearchHistoryAdapter(search);
        binding.rvSearchHistory.setAdapter(searchHistoryAdapter);


    }

    private void initListnear() {
        binding.tvSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//nn
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
//nn
            }
        });
        binding.tvSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String text = binding.tvSearch.getText().toString();
                sessonManager.addToSearch(text);

            }
            return true;
        });
    }

    private void getData(String toString) {

        db.collection("apps").orderBy("name").startAt("#" + toString).endAt(toString + "\uf8ff").get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        AppsRoot app = document.toObject(AppsRoot.class);
                        app.setId(document.getId());
                        Log.d("TAG", "onComplete:== " + app.getName());
                        topApps.add(app);
                    }

                    AdapterTopApps adapterTopApps = new AdapterTopApps(topApps, true);
                    binding.rvSearchApps.setAdapter(adapterTopApps);
                }).addOnFailureListener(e -> Log.d("TAG", "onFailure: " + e));
    }

    public void onclickBack(View view) {
        if (!showAds) {
            showAds = true;

        } else {
            return;
        }
        Log.d(TAG, "onclickBack: gms" + mInterstitialAd.isLoaded());
        Log.d(TAG, "onclickBack: fb" + interstitialAdfb.isAdLoaded());
        Log.d(TAG, "onclickBack: 1");
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

    public void onclickDelete(View view) {
        ArrayList<String> b = sessonManager.getSearch();
        Log.d(TAG, "clearBookmarks: " + b.size());
        for (int i = 0; i < b.size(); i++) {
            sessonManager.removefromSearch(b.get(i));
        }
        Log.d(TAG, "clearBookmarks:size2  " + sessonManager.getSearch().size());
        getSearchHistory();
    }
}