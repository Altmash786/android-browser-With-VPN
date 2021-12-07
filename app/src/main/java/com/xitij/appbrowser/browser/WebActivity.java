package com.xitij.appbrowser.browser;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleRegistry;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.xitij.appbrowser.Const;
import com.xitij.appbrowser.R;
import com.xitij.appbrowser.SessionManager;
import com.xitij.appbrowser.apps.MainActivity;
import com.xitij.appbrowser.databinding.ActivityWebBinding;
import com.xitij.appbrowser.models.AdsRoot;
import com.xitij.appbrowser.retrofit.ProductKRoot;
import com.xitij.appbrowser.retrofit.RetrofitBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.xitij.appbrowser.browser.BookamrksActivity.BOOKMAR_ACTIVITY_REQUEST_CODE;
import static com.xitij.appbrowser.browser.HistoryActivity.HISTORY_ACTIVITY_CODE;

public class WebActivity extends AppCompatActivity {
    private static final String TAG = "web:activity";

    private static final int SPEECH_REQUEST_CODE = 1;
    private static final String GOOGLE_URL = "https://www.google.com";
    private static final boolean IS_PRIVATE = false;
    static List<WebsiteModel> websites = new ArrayList<>();
    ActivityWebBinding binding;
    String baseGoogleSearch = "https://www.google.com/search?q=";


    private BottomSheetDialog bottomSheetDialog;
    private final boolean isIncogntigo = false;
    private SessionManager sessonManager;


    private InterstitialAd mInterstitialAd;
    private com.facebook.ads.InterstitialAd interstitialAdfb;
    private InterstitialAdListener interstitialAdListener;
    private boolean showAds = false;
    private String adWebsite;
    private boolean ownloded = false;
    private ViewPager2Adapter viewPager2Adapter;
    private String fragmentUrl;
    private WebView fragmentWebview;
    private BrowserFragment fragment;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web);
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(binding.etSearch.getWindowToken(), 0);

        sessonManager = new SessionManager(this);


        viewPager2Adapter = new ViewPager2Adapter(getSupportFragmentManager(), new LifecycleRegistry(this));

        viewPager2Adapter.setViewPagerListnear(new ViewPager2Adapter.ViewPagerListnear() {
            @Override
            public void setUrl(String url) {
                fragmentUrl = url;
                binding.etSearch.setText(url);
            }

            @Override
            public void openHistory() {
                Intent intent = new Intent(WebActivity.this, HistoryActivity.class);
                startActivityForResult(intent, HISTORY_ACTIVITY_CODE);
            }

            @Override
            public void setWebObj(WebView webObj) {
                fragmentWebview = webObj;
            }

            @Override
            public void addTab() {
                viewPager2Adapter.addItem(new BrowserFragment(), false, "");
                binding.viewpager2.setCurrentItem(viewPager2Adapter.getItemCount() - 1);

            }

            @Override
            public void openBookmarks() {
                Intent intent = new Intent(WebActivity.this, BookamrksActivity.class);
                startActivityForResult(intent, BOOKMAR_ACTIVITY_REQUEST_CODE);
            }

            @Override
            public void openIntigo() {
                viewPager2Adapter.addItem(new BrowserFragment(), true, "");
                binding.viewpager2.setCurrentItem(viewPager2Adapter.getItemCount() - 1);
                //viewPager2Adapter.getItemAt(binding.viewpager2.getCurrentItem()),true,"");
            }

            @Override
            public void openTabList(Bitmap bitmap, String title) {
                Log.d(TAG, "openTabList: " + title);
                viewPager2Adapter.getList().get(binding.viewpager2.getCurrentItem()).setTitle(title);
                viewPager2Adapter.getList().get(binding.viewpager2.getCurrentItem()).setBitmap(bitmap);
                TabListFragment listFragment = new TabListFragment(viewPager2Adapter.getList(), new TabListFragment.ListFragmentListner() {
                    @Override
                    public void onListClick(BrowserFragment browserFragment, int pos) {
                        binding.viewpager2.setCurrentItem(pos);
                    }

                    @Override
                    public void onClickRemove(BrowserFragment browserFragment, int pos) {
                        viewPager2Adapter.removePage(browserFragment, pos);
                        if (viewPager2Adapter.getList().size() == 0) {
                            viewPager2Adapter.addItem(new BrowserFragment(), false, "");
                            binding.viewpager2.setCurrentItem(viewPager2Adapter.getItemCount() - 1);
                            return;
                        }
                    }

                    @Override
                    public void onClickBookmark() {
                        onBackPressed();
                        Intent intent = new Intent(WebActivity.this, BookamrksActivity.class);
                        startActivityForResult(intent, BOOKMAR_ACTIVITY_REQUEST_CODE);
                    }

                    @Override
                    public void onClickHistory() {
                        onBackPressed();
                        Intent intent = new Intent(WebActivity.this, HistoryActivity.class);
                        startActivityForResult(intent, HISTORY_ACTIVITY_CODE);
                    }


                    @Override
                    public void onClickAdd() {
                        viewPager2Adapter.addItem(new BrowserFragment(), false, "");
                        binding.viewpager2.setCurrentItem(viewPager2Adapter.getItemCount() - 1);
                    }

                    @Override
                    public void closeAll() {
                        for (int i = 0; i < viewPager2Adapter.getList().size(); i++) {
                            if (viewPager2Adapter.getItemAt(i) != null) {
                                viewPager2Adapter.removePage(viewPager2Adapter.getItemAt(i), i);
                            }
                        }
                        viewPager2Adapter.addItem(new BrowserFragment(), false, "");
                        binding.viewpager2.setCurrentItem(viewPager2Adapter.getItemCount() - 1);
                    }
                });
                getSupportFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .add(R.id.framelyt, listFragment).commit();


            }
        });
        binding.viewpager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.d(TAG, "onPageSelected: " + position);
                if (viewPager2Adapter.getList().size() == 0) {
                    viewPager2Adapter.addItem(new BrowserFragment(), false, "");
                    binding.viewpager2.setCurrentItem(viewPager2Adapter.getItemCount() - 1);
                    return;
                }
                if (viewPager2Adapter.getItemAt(position) != null) {
                    fragment = viewPager2Adapter.getItemAt(position);


                    viewPager2Adapter.getItemAt(position).setBrowserFragmentListner(new BrowserFragment.BrowserFragmentListner() {
                        @Override
                        public void setUrl(String url) {
                            setUrlintoTextView(url);
                        }

                        @Override
                        public void setWebObj(WebView webObj) {
                            fragmentWebview = webObj;
                        }

                        @Override
                        public void addTab() {
                            viewPager2Adapter.addItem(new BrowserFragment(), false, "");
                            binding.viewpager2.setCurrentItem(viewPager2Adapter.getItemCount() - 1);
                        }

                        @Override
                        public void openIngontigo() {
                            viewPager2Adapter.addItem(new BrowserFragment(), true, "");
                            binding.viewpager2.setCurrentItem(viewPager2Adapter.getItemCount() - 1);
                        }

                        @Override
                        public void openHistory() {
                            Intent intent = new Intent(WebActivity.this, HistoryActivity.class);
                            startActivityForResult(intent, HISTORY_ACTIVITY_CODE);
                        }

                        @Override
                        public void openBookmarks() {
                            Intent intent = new Intent(WebActivity.this, BookamrksActivity.class);
                            startActivityForResult(intent, BOOKMAR_ACTIVITY_REQUEST_CODE);
                        }

                        @Override
                        public void openTabList(Bitmap bitmap, String title) {
                            viewPager2Adapter.getList().get(position).setTitle(title);
                            viewPager2Adapter.getList().get(position).setBitmap(bitmap);


                            TabListFragment listFragment = new TabListFragment(viewPager2Adapter.getList(), new TabListFragment.ListFragmentListner() {
                                @Override
                                public void onListClick(BrowserFragment browserFragment, int pos) {
                                    binding.viewpager2.setCurrentItem(pos);
                                }

                                @Override
                                public void onClickBookmark() {
                                    Intent intent = new Intent(WebActivity.this, BookamrksActivity.class);
                                    startActivityForResult(intent, BOOKMAR_ACTIVITY_REQUEST_CODE);
                                }

                                @Override
                                public void onClickHistory() {
                                    Intent intent = new Intent(WebActivity.this, HistoryActivity.class);
                                    startActivityForResult(intent, HISTORY_ACTIVITY_CODE);
                                }

                                @Override
                                public void onClickRemove(BrowserFragment browserFragment, int pos) {
                                    viewPager2Adapter.removePage(browserFragment, pos);
                                    if (viewPager2Adapter.getList().size() == 0) {
                                        viewPager2Adapter.addItem(new BrowserFragment(), false, "");
                                        binding.viewpager2.setCurrentItem(viewPager2Adapter.getItemCount() - 1);
                                        return;
                                    }
                                }

                                @Override
                                public void onClickAdd() {
                                    viewPager2Adapter.addItem(new BrowserFragment(), false, "");
                                    binding.viewpager2.setCurrentItem(viewPager2Adapter.getItemCount() - 1);
                                }

                                @Override
                                public void closeAll() {
                                    for (int i = 0; i < viewPager2Adapter.getList().size(); i++) {
                                        viewPager2Adapter.removePage(viewPager2Adapter.getItemAt(i), i);
                                    }
                                    viewPager2Adapter.addItem(new BrowserFragment(), false, "");
                                    binding.viewpager2.setCurrentItem(viewPager2Adapter.getItemCount() - 1);
                                }
                            });
                            getSupportFragmentManager().beginTransaction()
                                    .addToBackStack("null")
                                    .add(R.id.framelyt, listFragment).commit();


                        }
                    });
                }
            }

        });
        binding.viewpager2.setAdapter(viewPager2Adapter);
        binding.viewpager2.setUserInputEnabled(false);
        viewPager2Adapter.addItem(new BrowserFragment(), false, "");
        binding.viewpager2.setCurrentItem(viewPager2Adapter.getItemCount() - 1);


        getIntentData();

        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String text = binding.etSearch.getText().toString();
                if (text.contains("https://") || text.contains("http://") || text.contains(".com")) {
                    loadUrliNFragment(binding.etSearch.getText().toString());
                } else {
                    loadUrliNFragment(baseGoogleSearch + text);

                }

                in.hideSoftInputFromWindow(binding.etSearch.getWindowToken(), 0);
                binding.etSearch.clearFocus();
                return true;
            }
            return false;
        });


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
        chk();
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
                                        if (productname.equals(WebActivity.this.getPackageName())) {

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

    private void setUrlintoTextView(String url) {
        binding.etSearch.setText(url);
    }

    private void loadUrliNFragment(String toString) {
        viewPager2Adapter.addItem(new BrowserFragment(), IS_PRIVATE, toString);
        binding.viewpager2.setCurrentItem(viewPager2Adapter.getItemCount() - 1);
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
        if (intent != null) {
            String str = intent.getStringExtra("words");
            if (str != null && !str.equals("")) {
                if (str.equals("terms")) {
                    loadUrliNFragment("https://www.termsandconditionsgenerator.com/live.php?token=KtQKy1KH7rmbxZx7kk8UBFoyYIAtgld0");
                } else {
                    binding.etSearch.setText(baseGoogleSearch + str);
                    loadUrliNFragment(baseGoogleSearch + str);

                }

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
/* @Override
    public void onBackPressed() {

            if (!showAds) {
                showAds = true;

            } else {
                return;
            }
            if (mInterstitialAd.isLoaded()) {
                Log.d(TAG, "onBackPressed: google");
                mInterstitialAd.show();
            } else if (interstitialAdfb.isAdLoaded()) {
                interstitialAdfb.show();
                Log.d(TAG, "onBackPressed: fb");
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

    }*/

    private void initListnear() {

        binding.googleMic.setOnClickListener(v -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// Start the activity, the intent will be populated with the speech text
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        });
    }


    private void toggleIncogntigo() {
      /*  webView = new WebView(this);
        webView = binding.webview;
        if (isIncogntigo) {

            updateUI(false);
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setSafeBrowsingEnabled(binding.webview.getSettings(), false);
            }
            CookieManager.getInstance().setAcceptCookie(true);

//Make sure no caching is done
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
            webView.getSettings().setAppCacheEnabled(true);


//Make sure no autofill for Forms/ user-name password happens for the app
            webView.getSettings().setSavePassword(true);
            webView.getSettings().setSaveFormData(true);
            isIncogntigo = false;

        } else {
            updateUI(true);
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setSafeBrowsingEnabled(binding.webview.getSettings(), true);
            }
            CookieManager.getInstance().setAcceptCookie(false);

//Make sure no caching is done
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.getSettings().setAppCacheEnabled(false);
            webView.clearHistory();
            webView.clearCache(true);
            webView.clearSslPreferences();

            webView.clearMatches();

//Make sure no autofill for Forms/ user-name password happens for the app
            webView.clearFormData();
            webView.getSettings().setSavePassword(false);
            webView.getSettings().setSaveFormData(false);
            isIncogntigo = true;


        }

        loadUrl(GOOGLE_URL);*/
    }

/*
    private void updateUI(boolean isIncogntigo) {


        if (isIncogntigo) {
            binding.etSearch.setTextColor(ContextCompat.getColor(this, R.color.white));
            binding.etSearch.setHintTextColor(ContextCompat.getColor(this, R.color.white));
            binding.lytSearch.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_searchmainblack));
            binding.lytMain.setBackgroundColor(ContextCompat.getColor(this, R.color.blackdark));
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                // Turning on the light mode
                WebSettingsCompat.setForceDark(binding.webview.getSettings(), WebSettingsCompat.FORCE_DARK_ON);

            }
        } else {
            binding.etSearch.setTextColor(ContextCompat.getColor(this, R.color.black));
            binding.etSearch.setHintTextColor(ContextCompat.getColor(this, R.color.black));
            binding.lytSearch.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_searchmain));
            binding.lytMain.setBackgroundColor(ContextCompat.getColor(this, R.color.coloroffwhite));

            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                // Turning on the light mode
                WebSettingsCompat.setForceDark(binding.webview.getSettings(), WebSettingsCompat.FORCE_DARK_OFF);

            }
        }
    }
*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: code" + requestCode);
        Log.d(TAG, "onActivityResult: code" + resultCode);
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = null;
            if (data != null) {
                results = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
            }
            String spokenText = null;
            if (results != null) {
                spokenText = results.get(0);
            }
            // Do something with spokenText
            binding.etSearch.setText(baseGoogleSearch + spokenText);
            loadUrliNFragment(baseGoogleSearch + spokenText);
        }

        if (requestCode == BOOKMAR_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: requestcode " + requestCode);
                Log.d(TAG, "onActivityResult: result  " + resultCode);
                if (data != null) {
                    Log.d(TAG, "onActivityResult: datawork " + data.getStringExtra("url"));
                    loadUrliNFragment(data.getStringExtra("url"));
                }

            }


        }

        if (requestCode == HISTORY_ACTIVITY_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: requestcode " + requestCode);
                Log.d(TAG, "onActivityResult: result  " + resultCode);
                if (data != null) {
                    Log.d(TAG, "onActivityResult: datawork " + data.getStringExtra("url"));
                    loadUrliNFragment(data.getStringExtra("url"));
                }

            }


        }
    }



    private void interAdListnear() {

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.d(TAG, "onAdFailedToLoad: loded");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
//nn
                Log.d(TAG, "onAdFailedToLoad: google" + adError);
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

    public void onclickApp(View view) {
        finish();
    }
}