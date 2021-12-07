package com.xitij.appbrowser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class SessionManager {
    private final Context context;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        this.context = context;
        this.pref = context.getSharedPreferences(Const.PREF_NAME, MODE_PRIVATE);
        this.editor = this.pref.edit();
    }

    public void saveStringValue(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String getStringValue(String key) {
        return pref.getString(key, "");
    }

    public void saveBooleanValue(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBooleanValue(String key) {
        return pref.getBoolean(key, false);
    }

    public int toggleBookmark(String id) {
        ArrayList<String> fav = getBookamrks();
        if (fav != null) {
            if (fav.contains(id)) {
                fav.remove(id);
                editor.putString(Const.BOOKMARK, new Gson().toJson(fav));
                editor.apply();
                return 0;
            } else {
                fav.add(id);
                editor.putString(Const.BOOKMARK, new Gson().toJson(fav));
                editor.apply();
                return 1;
            }
        } else {
            fav = new ArrayList<>();
            fav.add(id);
            editor.putString(Const.BOOKMARK, new Gson().toJson(fav));
            editor.apply();
            return 1;
        }

    }

    public ArrayList<String> getBookamrks() {
        String userString = pref.getString(Const.BOOKMARK, "");
        if (!userString.isEmpty()) {
            return new Gson().fromJson(userString, new TypeToken<ArrayList<String>>() {
            }.getType());
        }
        return new ArrayList<>();
    }

    public void addToHistory(String id) {
        ArrayList<String> fav = getHistory();
        if (fav != null) {

            fav.add(id);

        } else {
            fav = new ArrayList<>();
            fav.add(id);

        }
        editor.putString(Const.HISTORY, new Gson().toJson(fav));
        editor.apply();
    }

    public void removefromHistory(String id) {
        ArrayList<String> fav = getHistory();
        if (fav != null) {
            if (fav.contains(id)) {
                fav.remove(id);
                Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Bookmarked", Toast.LENGTH_SHORT).show();
            }
        } else {
            fav = new ArrayList<>();

        }
        editor.putString(Const.HISTORY, new Gson().toJson(fav));
        editor.apply();
    }

    public ArrayList<String> getHistory() {
        String userString = pref.getString(Const.HISTORY, "");
        if (!userString.isEmpty()) {
            return new Gson().fromJson(userString, new TypeToken<ArrayList<String>>() {
            }.getType());
        }
        return new ArrayList<>();
    }


    public void addToSearch(String id) {
        ArrayList<String> fav = getSearch();
        if (fav != null) {

            fav.add(id);

        } else {
            fav = new ArrayList<>();
            fav.add(id);

        }
        editor.putString(Const.SEARCH, new Gson().toJson(fav));
        editor.apply();
    }

    public void removefromSearch(String id) {
        ArrayList<String> fav = getSearch();
        if (fav != null) {
            if (fav.contains(id)) {
                fav.remove(id);
                Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
            }
        } else {
            fav = new ArrayList<>();

        }
        editor.putString(Const.SEARCH, new Gson().toJson(fav));
        editor.apply();
    }

    public ArrayList<String> getSearch() {
        String userString = pref.getString(Const.SEARCH, "");
        if (userString != null && !userString.isEmpty()) {
            return new Gson().fromJson(userString, new TypeToken<ArrayList<String>>() {
            }.getType());
        }
        return new ArrayList<>();
    }



}
