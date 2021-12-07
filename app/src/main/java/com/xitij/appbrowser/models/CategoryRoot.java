package com.xitij.appbrowser.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CategoryRoot implements Serializable {
    String id;
    String createdAt;
    String name;

    @SerializedName("icon")
    String icon;

    public String getImage() {
        return icon;
    }

    public void setImage(String image) {
        this.icon = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
