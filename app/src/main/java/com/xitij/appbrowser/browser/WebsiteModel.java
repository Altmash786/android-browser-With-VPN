package com.xitij.appbrowser.browser;

import android.graphics.Bitmap;
import android.webkit.WebView;

public class WebsiteModel {
    Bitmap bitmap;
    String website;
    String title;
    WebView webView;

    public WebsiteModel(String url, Bitmap bitmap, String title, WebView webView) {
        this.website = url;
        this.bitmap = bitmap;
        this.title = title;
        this.webView = webView;
    }

    public WebView getWebView() {
        return webView;
    }

    public void setWebView(WebView webView) {
        this.webView = webView;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
