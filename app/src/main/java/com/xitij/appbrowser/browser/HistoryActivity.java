package com.xitij.appbrowser.browser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import com.xitij.appbrowser.Const;
import com.xitij.appbrowser.R;
import com.xitij.appbrowser.SessionManager;
import com.xitij.appbrowser.apps.AppsWebsiteActivity;
import com.xitij.appbrowser.apps.MainActivity;
import com.xitij.appbrowser.databinding.ActivityHistoryBinding;
import com.xitij.appbrowser.models.AdsRoot;

import java.util.ArrayList;
import java.util.Random;

public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.OnHistoryClickListnear {
    private static final String TAG = "historyact";
    public static final int HISTORY_ACTIVITY_CODE = 1001;
    ActivityHistoryBinding binding;
    private SessionManager sessonManager;
    private ArrayList<String> list = new ArrayList<>();
    private InterstitialAd mInterstitialAd;
    private com.facebook.ads.InterstitialAd interstitialAdfb;
    private InterstitialAdListener interstitialAdListener;
    private boolean showAds = false;
    private String adWebsite;
    private boolean ownloded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history);

        sessonManager = new SessionManager(this);
        initView();
        initListnear();

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

    private void initListnear() {
        binding.tvClear.setOnClickListener(v -> clearBookmarks());
    }

    private void clearBookmarks() {
        ArrayList<String> b = sessonManager.getHistory();
        Log.d(TAG, "clearBookmarks: " + b.size());
        for (int i = 0; i < b.size(); i++) {
            sessonManager.removefromHistory(b.get(i));
        }
        Log.d(TAG, "clearBookmarks:size2  " + sessonManager.getHistory().size());
        initView();
    }

    private void initView() {
        list.clear();
        list = sessonManager.getHistory();
        Log.d(TAG, "clearBookmarks:size3  " + sessonManager.getHistory().size());
        Log.d(TAG, "clearBookmarks:size34  " + list.size());


        HistoryAdapter historyAdapter = new HistoryAdapter(list, this);
        binding.rvHistory.setAdapter(historyAdapter);


    }

    @Override
    public void onHistoryClick(String url, String work) {
        if (work.equals("DELETE")) {
            sessonManager.removefromHistory(url);
            initView();
        }
        if (work.equals("OPEN")) {
            Intent intent = new Intent();
            intent.putExtra("url", url);

            setResult(RESULT_OK, intent);
            finish();
        }
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