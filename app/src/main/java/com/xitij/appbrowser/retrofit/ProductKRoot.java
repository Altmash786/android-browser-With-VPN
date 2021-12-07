package com.xitij.appbrowser.retrofit;

import com.google.gson.annotations.SerializedName;

public class ProductKRoot {

    @SerializedName("data")
    private Data data;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    public Data getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public static class Data {

        @SerializedName("createdAt")
        private String createdAt;

        @SerializedName("package")
        private String jsonMemberPackage;

        @SerializedName("admin_id")
        private String adminId;

        @SerializedName("__v")
        private int V;

        @SerializedName("_id")
        private String id;

        @SerializedName("key")
        private String key;

        @SerializedName("updatedAt")
        private String updatedAt;

        public String getCreatedAt() {
            return createdAt;
        }

        public String getJsonMemberPackage() {
            return jsonMemberPackage;
        }

        public String getAdminId() {
            return adminId;
        }

        public int getV() {
            return V;
        }

        public String getId() {
            return id;
        }

        public String getKey() {
            return key;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }
}