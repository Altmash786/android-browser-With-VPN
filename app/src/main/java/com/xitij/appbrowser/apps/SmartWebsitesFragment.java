package com.xitij.appbrowser.apps;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.xitij.appbrowser.R;
import com.xitij.appbrowser.databinding.FragmentSmartWebsitesBinding;

import java.util.ArrayList;


public class SmartWebsitesFragment extends Fragment {

    private static final String TAG = "smartt";
    FragmentSmartWebsitesBinding binding;
    String searchUrl;
    private String s;
    private String url;
    private boolean isShopping;
    private String appname;
    boolean loadingFinished = true;
    private ArrayList<Object> urls;
    boolean redirect = false;
    private OnTabLodedListnear onTabLodedListnear;
    Activity smartappaactivity;

    public SmartWebsitesFragment(String url, boolean isShopping, String s, String appname, OnTabLodedListnear onTabLodedListnear) {
        this.s = s;
        this.isShopping = isShopping;


        this.url = url;
        this.appname = appname.toLowerCase();
        this.onTabLodedListnear = onTabLodedListnear;
    }

    @SuppressLint("JavascriptInterface")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_smart_websites, container, false);

        InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);

        preapareSearchUrls();
        Log.d("TAG", "onCreateView: " + url);
        binding.webview.setWebViewClient(new WebViewClient());
        binding.webview.setWebChromeClient(new WebChromeClient());
        binding.webview.getSettings().setLoadsImagesAutomatically(true);
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        binding.webview.getSettings().setLoadWithOverviewMode(true);
        binding.webview.getSettings().setUseWideViewPort(true);
        binding.webview.getSettings().setPluginState(WebSettings.PluginState.ON);

        WebSettings settings = binding.webview.getSettings();
        settings.setJavaScriptEnabled(true);
        binding.webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        binding.webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        binding.webview.getSettings().setAppCacheEnabled(false);
        binding.webview.getSettings().setAllowFileAccess(true);
        binding.webview.getSettings().setSupportMultipleWindows(true);
        binding.webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        binding.webview.getSettings().setLoadWithOverviewMode(true);
        binding.webview.getSettings().setUseWideViewPort(true);
        binding.webview.getSettings().setBuiltInZoomControls(true);
        binding.webview.getSettings().setDisplayZoomControls(false);
        binding.webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        settings.setDomStorageEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setUseWideViewPort(true);
        settings.setSavePassword(true);
        settings.setSaveFormData(true);
        binding.webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);


        Log.d(TAG, "onCreateView: " + s);
        if (!url.equals("")) {
            if (isShopping) {
                searchUrl = getSearchUrl(url);
                if (searchUrl != null && !searchUrl.equals("")) {
                    Log.d(TAG, "search url1 : " + searchUrl + s);

                    if (appname.equals("snapdeal")) {
                        loadUrl(searchUrl + s + "&sort=rlvncy");
                    } else {
                        loadUrl(searchUrl + s);
                    }

                } else {
                    loadUrl(url);
                }
            } else {
                loadUrl(url);
            }


            Log.d(TAG, "onCreateView: urlll  " + binding.webview.getUrl());
        }


        return binding.getRoot();
    }

    private void loadUrl(String url) {
        if (url != null) {
            binding.webview.loadUrl(url);
            binding.webview.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
                    if (!loadingFinished) {
                        redirect = true;
                    }

                    loadingFinished = false;
                    view.loadUrl(urlNewString);
                    onTabLodedListnear.onTabLoded(true);
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                    loadingFinished = false;
                    //SHOW LOADING IF IT ISNT ALREADY VISIBLE


                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    onTabLodedListnear.onTabLoded(true);
                    if (!redirect) {
                        loadingFinished = true;

                    }

                    if (loadingFinished && !redirect) {
                        //HIDE LOADING IT HAS FINISHED

                    } else {
                        redirect = false;

                    }

                }
            });

        }
    }

    private void preapareSearchUrls() {
        urls = new ArrayList<>();
        urls.add("https://www.amazon.in/s?k=");
        urls.add("https://www.snapdeal.com/search?keyword=");
        urls.add("https://www.flipkart.com/search?q=");
        urls.add("https://www.shopclues.com/search?q=");
        urls.add("https://paytmmall.com/shop/search?q=");
        urls.add("https://www.aliexpress.com/wholesale?catId=0&initiative_id=SB_20201113224504&SearchText=");

    }

    public interface OnTabLodedListnear {
        void onTabLoded(boolean b);
    }

    private String getSearchUrl(String url) {
        for (int i = 0; i < urls.size(); i++) {
            Log.d(TAG, appname + " getSearchUrl: " + urls.get(i) + "      " + url);
            if (urls.get(i).toString().toLowerCase().contains(appname)) {
                return urls.get(i).toString();
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume() {
        super.onResume();
    }


}