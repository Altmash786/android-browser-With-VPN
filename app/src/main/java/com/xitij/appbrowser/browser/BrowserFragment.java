package com.xitij.appbrowser.browser;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.xitij.appbrowser.R;
import com.xitij.appbrowser.SessionManager;
import com.xitij.appbrowser.databinding.BottomsheetMenuBinding;
import com.xitij.appbrowser.databinding.FragmentBrowserBinding;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class BrowserFragment extends Fragment {
    private static final String GOOGLE_URL = "https://www.google.com";
    private static final String TAG = "web:browserfrag";
    FragmentBrowserBinding binding;
    WebViewClient webViewClient;
    boolean loadingFinished = true;
    boolean redirect = false;
    int position = 0;
    BrowserFragmentListner browserFragmentListner;
    Bitmap bitmap;
    String title;
    private WebView webView;
    private MaterialProgressBar progressBar;
    private BottomSheetDialog bottomSheetDialog;
    private SessionManager sessonManager;
    private boolean isPrivate = false;
    private String url;


    public BrowserFragmentListner getBrowserFragmentListner() {
        return browserFragmentListner;
    }

    public void setBrowserFragmentListner(BrowserFragmentListner browserFragmentListner) {
        this.browserFragmentListner = browserFragmentListner;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_browser, container, false);
        sessonManager = new SessionManager(getActivity());
        return binding.getRoot();
    }

    public void back() {
        if (webView.canGoBack()) {
            binding.webview.goBack();
        }
    }

    public void load(String s) {
        Log.d(TAG, "load: ");
        loadUrl(s);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getBitmap() {

        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        webView = binding.webview;
        progressBar = binding.progressbar;
        webView = binding.webview;
        progressBar = binding.progressbar;
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.colorPrimary)));
        }


        if (isPrivate) {
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
            loadUrl(GOOGLE_URL);
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                // Turning on the light mode
                WebSettingsCompat.setForceDark(binding.webview.getSettings(), WebSettingsCompat.FORCE_DARK_ON);

            }
        } else {
            webViewClient = new WebViewClient();
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setSafeBrowsingEnabled(binding.webview.getSettings(), false);
            }

            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView.setWebViewClient(webViewClient);
            Log.d(TAG, "onActivityCreated: ");


            if (url != null && !url.equals("")) {
                loadUrl(url);
            } else {
                loadUrl(GOOGLE_URL);
            }


            browserFragmentListner.setWebObj(webView);


        }

        binding.swipe.setOnRefreshListener(() -> loadUrl(binding.webview.getUrl()));
        initListner();
    }

    private void initListner() {
        binding.imgback.setOnClickListener(v -> {

            if (webView.canGoBack()) {
                webView.goBack();
            }
        });
        binding.imgforward.setOnClickListener(v -> {

            if (webView.canGoForward()) {
                webView.goForward();
            }

        });
        binding.imghome.setOnClickListener(v -> {
            webView.loadUrl(GOOGLE_URL);
        });
        binding.imgmenu.setOnClickListener(v -> openBottomSheet());

        binding.imgtabs.setOnClickListener(v -> {

            binding.swipe.setDrawingCacheEnabled(true);
            bitmap = Bitmap.createBitmap(binding.swipe.getDrawingCache());
            binding.swipe.setDrawingCacheEnabled(false);
            setBitmap(bitmap);
            setTitle(webView.getUrl());
            Log.d(TAG, getTitle());
            browserFragmentListner.openTabList(getBitmap(), getTitle());


        });
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

                    setTextOnSearchView(urlNewString);
                    binding.progressbar.setVisibility(View.VISIBLE);
                    return true;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                    loadingFinished = false;
                    //SHOW LOADING IF IT ISNT ALREADY VISIBLE
                    if (!isPrivate) {
                        addToHistry(url);
                    }
                    if (!binding.swipe.isRefreshing()) {
                        progressBar.setVisibility(View.VISIBLE);
                    }


                    setTextOnSearchView(url);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    setTextOnSearchView(url);
                    if (!redirect) {
                        loadingFinished = true;
                        binding.swipe.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                    }

                    if (loadingFinished && !redirect) {
                        //HIDE LOADING IT HAS FINISHED
                        progressBar.setVisibility(View.GONE);
                        binding.swipe.setRefreshing(false);
                    } else {
                        redirect = false;
                        progressBar.setVisibility(View.GONE);
                        binding.swipe.setRefreshing(false);
                    }

                }
            });

        }
    }

    private void openBottomSheet() {
        bottomSheetDialog = new BottomSheetDialog(getActivity());
        BottomsheetMenuBinding menuBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.bottomsheet_menu, null, false);

        bottomSheetDialog.setContentView(menuBinding.getRoot());
        bottomSheetDialog.show();


        bottomsheetListnear(menuBinding);
    }

    private void bottomsheetListnear(BottomsheetMenuBinding menuBinding) {
        menuBinding.imgaddtab.setOnClickListener(v -> {
            browserFragmentListner.addTab();
            bottomSheetDialog.dismiss();
        });
        menuBinding.imgcopy.setOnClickListener(v -> {

            ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("text", binding.webview.getUrl());
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(getActivity(), "Copied", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });
        menuBinding.imgrefresh.setOnClickListener(v -> {
            loadUrliNFragment(binding.webview.getUrl());
            bottomSheetDialog.dismiss();
        });
        menuBinding.imgprivate.setOnClickListener(v -> {
            browserFragmentListner.openIngontigo();
            bottomSheetDialog.dismiss();
        });
        menuBinding.imgbookmark.setOnClickListener(v -> {
            toggleBookmark();
            bottomSheetDialog.dismiss();
        });
        menuBinding.imgbookmarks.setOnClickListener(v -> {

            browserFragmentListner.openBookmarks();
            bottomSheetDialog.dismiss();
        });
        menuBinding.imghistry.setOnClickListener(v -> {

            browserFragmentListner.openHistory();
            bottomSheetDialog.dismiss();
        });
        menuBinding.imgshare.setOnClickListener(v -> {
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            share.putExtra(Intent.EXTRA_SUBJECT, binding.webview.getTitle());
            share.putExtra(Intent.EXTRA_TEXT, binding.webview.getUrl());
            startActivity(Intent.createChooser(share, "Share Using.."));
            bottomSheetDialog.dismiss();
        });
    }

    private void loadUrliNFragment(String url) {
        loadUrl(url);
    }

    private void addToHistry(String url) {

        sessonManager.addToHistory(url);


    }

    private void toggleBookmark() {
        String url = binding.webview.getUrl();
        int i = sessonManager.toggleBookmark(url);
        if (i == 0) {
            Toast.makeText(getActivity(), "Bookmark Removed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Bookmarked", Toast.LENGTH_SHORT).show();
        }

    }

    private void setTextOnSearchView(String urlNewString) {
        browserFragmentListner.setUrl(urlNewString);
    }

    public BrowserFragment isPrivate() {
        return null;
    }

    public BrowserFragment setArgs(boolean b, String s) {
        Log.d(TAG, "setArgs: " + b + s);
        this.isPrivate = b;
        this.url = s;
        return null;
    }

    public interface BrowserFragmentListner {
        void setUrl(String url);

        void setWebObj(WebView webObj);

        void addTab();

        void openTabList(Bitmap bitmap, String title);

        void openIngontigo();

        void openBookmarks();

        void openHistory();
    }

}