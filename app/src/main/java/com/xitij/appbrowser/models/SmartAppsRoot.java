package com.xitij.appbrowser.models;

import java.util.List;
import java.util.Map;

public class SmartAppsRoot {
    String icon;
    String name;
    List<Map<String, Object>> apps;

    public List<Map<String, Object>> getApps() {
        return apps;
    }

    public void setApps(List<Map<String, Object>> apps) {
        this.apps = apps;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
