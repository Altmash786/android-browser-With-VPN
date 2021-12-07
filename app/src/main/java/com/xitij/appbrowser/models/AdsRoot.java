package com.xitij.appbrowser.models;

public class AdsRoot {
    boolean hasInterstitial, hasNative, show;
    String interstitial, mnative, website, title, description;
    String id;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isHasInterstitial() {
        return hasInterstitial;
    }

    public void setHasInterstitial(boolean hasInterstitial) {
        this.hasInterstitial = hasInterstitial;
    }

    public boolean isHasNative() {
        return hasNative;
    }

    public void setHasNative(boolean hasNative) {
        this.hasNative = hasNative;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public String getInterstitial() {
        return interstitial;
    }

    public void setInterstitial(String interstitial) {
        this.interstitial = interstitial;
    }

    public String getMnative() {
        return mnative;
    }

    public void setMnative(String mnative) {
        this.mnative = mnative;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
