package com.xitij.appbrowser.vpn;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.anchorfree.partner.api.ClientInfo;
import com.anchorfree.partner.api.response.AvailableCountries;
import com.anchorfree.sdk.NotificationConfig;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.pixplicity.easyprefs.library.Prefs;
import com.xitij.appbrowser.R;

public class MainApplication extends MultiDexApplication {

    private static final String CHANNEL_ID = "vpn";

    private static Context context;
    UnifiedSDK unifiedSDK;

    public static Context getApplication() {
        return context;
    }

    public static Context getStaticContext() {
        return getApplication();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;

        // Initialize the Prefs class
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        initHydraSdk();
    }

    public void initHydraSdk() {
        createNotificationChannel();
        final ClientInfo info = ClientInfo.newBuilder()
                .baseUrl("https://backend.northghost.com/") // set base url for api calls
                .carrierId(Config.carrierID) // set your carrier id
                .build();
        unifiedSDK = UnifiedSDK.getInstance(info);

        unifiedSDK.getBackend().countries(new Callback<AvailableCountries>() {
            @Override
            public void success(@NonNull AvailableCountries availableCountries) {
                Log.d("TAG", "success: " + availableCountries.toString());
            }

            @Override
            public void failure(@NonNull VpnException e) {
                Log.d("TAG", "failure: " + e);
            }
        });
        NotificationConfig notificationConfig = NotificationConfig.newBuilder()
                .title(getResources().getString(R.string.app_name))
                .channelId(CHANNEL_ID)
                .build();
        UnifiedSDK.update(notificationConfig);

        UnifiedSDK.setLoggingLevel(Log.DEBUG);
    }

    public void setNewHostAndCarrier(String hostUrl, String carrierId) {
        SharedPreferences prefs = getPrefs();
        if (TextUtils.isEmpty(hostUrl)) {
            prefs.edit().remove(VPNCONST.STORED_HOST_URL_KEY).apply();
        } else {
            prefs.edit().putString(VPNCONST.STORED_HOST_URL_KEY, hostUrl).apply();
        }

        if (TextUtils.isEmpty(carrierId)) {
            prefs.edit().remove(VPNCONST.STORED_CARRIER_ID_KEY).apply();
        } else {
            prefs.edit().putString(VPNCONST.STORED_CARRIER_ID_KEY, carrierId).apply();
        }
        initHydraSdk();
    }

    public SharedPreferences getPrefs() {
        return getSharedPreferences(VPNCONST.SHARED_PREFS, Context.MODE_PRIVATE);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Sample VPN";
            String description = "VPN notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
