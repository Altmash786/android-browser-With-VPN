package com.xitij.appbrowser.vpn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.anchorfree.partner.api.ClientInfo;
import com.anchorfree.partner.api.auth.AuthMethod;
import com.anchorfree.partner.api.data.Country;
import com.anchorfree.partner.api.response.AvailableCountries;
import com.anchorfree.partner.api.response.User;
import com.anchorfree.reporting.TrackingConstants;
import com.anchorfree.sdk.SessionConfig;
import com.anchorfree.sdk.SessionInfo;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.sdk.exceptions.PartnerApiException;
import com.anchorfree.sdk.rules.TrafficRule;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.callbacks.CompletableCallback;
import com.anchorfree.vpnsdk.callbacks.TrafficListener;
import com.anchorfree.vpnsdk.callbacks.VpnStateListener;
import com.anchorfree.vpnsdk.exceptions.NetworkRelatedException;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.exceptions.VpnPermissionDeniedException;
import com.anchorfree.vpnsdk.exceptions.VpnPermissionRevokedException;
import com.anchorfree.vpnsdk.transporthydra.HydraTransport;
import com.anchorfree.vpnsdk.transporthydra.HydraVpnTransportException;
import com.anchorfree.vpnsdk.vpnservice.VPNState;
import com.northghost.caketube.CaketubeTransport;
import com.xitij.appbrowser.R;
import com.xitij.appbrowser.TimerService.TimerService;
import com.xitij.appbrowser.databinding.ActivityConnectVpnBinding;
import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter;
import com.yarolegovich.discretescrollview.transform.Pivot;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ConnectVpnActivity extends AppCompatActivity implements VpnStateListener, TrafficListener {
    public static final String TAG = "connectvpnact";
    private static final int NOTCONNECTED = 1;
    private static final int CONNECTED = 2;
    VpnAdapter vpnAdapter = new VpnAdapter();
    ActivityConnectVpnBinding binding;
    private boolean isOn = false;
    InfiniteScrollAdapter wrapper;
    private Country selected;
    private int pStart = 0;

    int i = 0;
    Intent intent;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int time = intent.getIntExtra("time", 0);

            Log.d("Hello", "Time " + time);

            int mins = time / 60;
            int secs = time % 60;
            int hour = time / 3600;
            binding.tvClock.setText("" + String.format("%02d", hour) + ":" + "" + String.format("%02d", mins) + ":"
                    + String.format("%02d", secs));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_connect_vpn);


        binding.rvVpn.setVisibility(View.GONE);
        binding.tvName.setVisibility(View.GONE);
        binding.pd.show();
        binding.pd.setVisibility(View.VISIBLE);
        intent = new Intent(ConnectVpnActivity.this, TimerService.class);
        initView();
        initViewListners();
        loginToVPN();
        chkServer();


    }

    private void loginToVPN() {

        binding.pd.setVisibility(View.VISIBLE);

        final ClientInfo info = ClientInfo.newBuilder()
                .baseUrl("https://backend.northghost.com/") // set base url for api calls
                .carrierId(Config.carrierID) // set your carrier id
                .build();
        UnifiedSDK.getInstance(info);
        UnifiedSDK unifiedSDK = UnifiedSDK.getInstance(info);
        AuthMethod authMethod = AuthMethod.anonymous();
        UnifiedSDK.getInstance().getBackend().login(authMethod, new Callback<User>() {
            @Override
            public void success(User user) {
                Log.d(TAG, "success: " + user.toString());
                Log.d(TAG, "success: " + user.getAccessToken());
                binding.pd.hide();
                getCountries(unifiedSDK);
            }

            @Override
            public void failure(VpnException e) {
                binding.pd.setVisibility(View.VISIBLE);
                Log.d(TAG, "failure:104 " + e.toString());
                handleError(e);
            }
        });
    }

    private void showMessage(String check_internet_connection) {
        Toast.makeText(this, check_internet_connection, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "showMessage: " + check_internet_connection);
    }

    private void chkServer() {

        binding.pd.setVisibility(View.VISIBLE);
        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState state) {
                Log.d(TAG, "success: state " + state.toString());
                if (state == VPNState.CONNECTED) {
                    UnifiedSDK.getStatus(new Callback<SessionInfo>() {
                        @Override
                        public void success(@NonNull SessionInfo sessionInfo) {
                            // callback.success(CredentialsCompat.getServerCountry(sessionInfo.getCredentials()));
                            Log.d(TAG, "success: connected " + sessionInfo.toString());
                            setUI(CONNECTED);


                            Locale locale = new Locale("", sessionInfo.getSessionConfig().getVirtualLocation());

                            binding.tvName2.setText(locale.getDisplayCountry());
                            binding.tvip.setText(Objects.requireNonNull(sessionInfo.getCredentials()).getClientIp());


                            String str = sessionInfo.getSessionConfig().getVirtualLocation();
                            binding.imgflag2.setImageResource(getResources().getIdentifier("raw/" + str, null, getPackageName()));

                            binding.pd.hide();
                        }

                        @Override
                        public void failure(@NonNull VpnException e) {
                            Log.d(TAG, "failure: 109 " + e.toString());
                            binding.pd.setVisibility(View.VISIBLE);
                            // callback.success(selectedCountry);
                        }
                    });
                } else if (state == VPNState.IDLE) {
                    setUI(NOTCONNECTED);
                    binding.pd.hide();
                    binding.pd.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(ConnectVpnActivity.this, "not connected", Toast.LENGTH_SHORT).show();
                    // callback.success(selectedCountry);
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
                Log.d(TAG, "failure: 121 " + e.toString());
                binding.pd.setVisibility(View.VISIBLE);
            }
        });
    }

    private void getCountries(UnifiedSDK unifiedSDK) {

        binding.pd.setVisibility(View.VISIBLE);
        unifiedSDK.getBackend().countries(new Callback<AvailableCountries>() {
            @Override
            public void success(@NonNull final AvailableCountries countries) {

                Log.d(TAG, "success: " + countries.toString());
                // hideProress();
                //regionAdapter.setRegions(countries.getCountries());
                vpnAdapter.addCountries(countries.getCountries());
                binding.pd.hide();
                binding.rvVpn.setVisibility(View.VISIBLE);
                binding.tvName.setVisibility(View.VISIBLE);
                binding.rvVpn.addOnItemChangedListener(new DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>() {
                    @Override
                    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
                        Log.d("TAG", "onCurrentItemChanged: " + adapterPosition);
                        Log.d("TAG", "onCurrentItemChanged:pos " + wrapper.getRealPosition(adapterPosition));
                        Log.d("TAG", "onCurrentItemChanged:poss " + wrapper.getRealCurrentPosition());
                        selected = countries.getCountries().get(wrapper.getRealCurrentPosition());
                        Locale locale = new Locale("", selected.getCountry());
                        binding.tvName.setText(locale.getDisplayCountry());
                        binding.tvName2.setText(locale.getDisplayCountry());

                        String str = countries.getCountries().get(wrapper.getRealCurrentPosition()).getCountry();
                        binding.imgflag2.setImageResource(getResources().getIdentifier("raw/" + str, null, getPackageName()));


                    }
                });
            }

            @Override
            public void failure(VpnException e) {
                Log.d(TAG, "failure: " + e);
                binding.pd.setVisibility(View.VISIBLE);
                // hideProress();
                // dismiss();
            }
        });
    }

    public void handleError(Throwable e) {
        binding.pd.setVisibility(View.VISIBLE);
        Log.w(TAG, e.toString());
        if (e instanceof NetworkRelatedException) {

            showMessage("Check internet connection");
        } else if (e instanceof VpnException) {
            if (e instanceof VpnPermissionRevokedException) {
                showMessage("User revoked vpn permissions");
            } else if (e instanceof VpnPermissionDeniedException) {
                showMessage("User canceled to grant vpn permissions");
            } else if (e instanceof HydraVpnTransportException) {
                HydraVpnTransportException hydraVpnTransportException = (HydraVpnTransportException) e;
                if (hydraVpnTransportException.getCode() == HydraVpnTransportException.HYDRA_ERROR_BROKEN) {
                    showMessage("Connection with vpn server was lost");
                } else if (hydraVpnTransportException.getCode() == HydraVpnTransportException.HYDRA_DCN_BLOCKED_BW) {
                    showMessage("Client traffic exceeded");
                } else {
                    showMessage("Error in VPN transport");
                }
            } else {
                showMessage("Error in VPN Service");
            }
        } else if (e instanceof PartnerApiException) {
            switch (((PartnerApiException) e).getContent()) {
                case PartnerApiException.CODE_NOT_AUTHORIZED:
                    showMessage("User unauthorized");
                    break;
                case PartnerApiException.CODE_TRAFFIC_EXCEED:
                    showMessage("Server unavailable");
                    break;
                default:
                    showMessage("Other error. Check PartnerApiException constants");
                    break;
            }
        }
    }

    private void initView() {
        setUI(NOTCONNECTED);
        binding.lytbtnmainon.setVisibility(View.GONE);
        binding.lytbtnmainoff.setVisibility(View.VISIBLE);

        // binding.rvVpn.setAdapter(vpnAdapter);

        binding.rvVpn.setOffscreenItems(3); //Reserve extra space equal to (childSize * count) on each side of the view
        //binding.rvVpn.setOverScrollEnabled(true);
        binding.rvVpn.setItemTransformer(new ScaleTransformer.Builder()
                .setMaxScale(1.5f)
                .setMinScale(.9f)
                .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                .setPivotY(Pivot.Y.CENTER) // CENTER is a default one
                .build());

        binding.rvVpn.setSlideOnFling(true);

        wrapper = InfiniteScrollAdapter.wrap(vpnAdapter);
        binding.rvVpn.setAdapter(wrapper);

    }

    private void setUI(int state) {

        switch (state) {
            case 1:
                binding.lytNotConnected.setVisibility(View.VISIBLE);
                binding.lytConnected.setVisibility(View.GONE);
                binding.lytbtnmainon.setVisibility(View.GONE);
                binding.lytbtnmainoff.setVisibility(View.VISIBLE);
                break;
            case 2:
                binding.lytbtnmainon.setVisibility(View.VISIBLE);
                binding.lytbtnmainoff.setVisibility(View.GONE);
                binding.lytNotConnected.setVisibility(View.GONE);
                binding.lytConnected.setVisibility(View.VISIBLE);

                break;
        }
    }

    private void initViewListners() {
        binding.lytbtnmainon.setOnClickListener(v -> {

            disConnectVPN();
            isOn = true;
            binding.lytbtnmainon.setVisibility(View.GONE);
            binding.lytbtnmainoff.setVisibility(View.VISIBLE);
            //  setUI(CONNECTED);
        });
        binding.lytbtnmainoff.setOnClickListener(v -> {
            if (selected != null) {
                binding.rvVpn.setVisibility(View.GONE);
                binding.tvName.setVisibility(View.GONE);
                binding.pd.setVisibility(View.VISIBLE);
                connectToVpn();
                isOn = false;
                binding.lytbtnmainon.setVisibility(View.VISIBLE);
                binding.lytbtnmainoff.setVisibility(View.GONE);
            }


            //setUI(NOTCONNECTED);
        });

    }

    @Override
    public void vpnError(@NonNull VpnException e) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        UnifiedSDK.addTrafficListener(this);
        UnifiedSDK.addVpnStateListener(this);
        updateUI();
    }

    @Override
    protected void onStop() {
        super.onStop();
        UnifiedSDK.removeVpnStateListener(this);
        UnifiedSDK.removeTrafficListener(this);
        updateUI();
    }

    private void disConnectVPN() {
        UnifiedSDK.getInstance().getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
            @Override
            public void complete() {
                //  Toast.makeText(ConnectVpnActivity.this, "VPN DisConnected", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "complete: disconnected");
                setUI(NOTCONNECTED);
                binding.pd.hide();
                binding.rvVpn.setVisibility(View.VISIBLE);
                binding.tvName.setVisibility(View.VISIBLE);
                stopService(intent);
                unregisterReceiver(broadcastReceiver);

                binding.imgonoff.setImageResource(getResources().getIdentifier("raw/back2", null, getPackageName()));

            }

            @Override
            public void error(VpnException e) {
                Log.d(TAG, "error: 292" + e.toString());
                handleError(e);
            }
        });
    }

    private void connectToVpn() {
        UnifiedSDK.getInstance().getBackend().isLoggedIn(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (Boolean.TRUE.equals(aBoolean)) {
                    List<String> fallbackOrder = new ArrayList<>();
                    fallbackOrder.add(HydraTransport.TRANSPORT_ID);
                    fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_TCP);
                    fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_UDP);
                    // showConnectProgress();
                    List<String> bypassDomains = new LinkedList<>();
                    bypassDomains.add("*facebook.com");
                    bypassDomains.add("*wtfismyip.com");
                    UnifiedSDK.getInstance().getVPN().start(new SessionConfig.Builder()
                            .withReason(TrackingConstants.GprReasons.M_UI)
                            .withTransportFallback(fallbackOrder)
                            .withTransport(HydraTransport.TRANSPORT_ID)
                            .withVirtualLocation(selected.getCountry())
                            .addDnsRule(TrafficRule.Builder.bypass().fromDomains(bypassDomains))
                            .build(), new CompletableCallback() {
                        @Override
                        public void complete() {
                            Toast.makeText(ConnectVpnActivity.this, "VPN Connected completed", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "complete: yesss====================");
                            //setUI(CONNECTED);
                            // updateUI();
                            startTimer();
                            binding.imgonoff.setImageResource(getResources().getIdentifier("raw/startvpn", null, getPackageName()));
                        }

                        @Override
                        public void error(@NonNull VpnException e) {
                            // Toast.makeText(ConnectVpnActivity.this, "Error Connecting", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "error: 329" + e.toString());
                            handleError(e);
                            // updateUI();
                        }
                    });
                } else {
                    showMessage("Login please");
                    loginToVPN();
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
                Log.d(TAG, "failure: " + e.toString());
            }
        });

    }

    public void onclickBack(View view) {
        finish();
    }

    @Override
    public void vpnStateChanged(@NonNull VPNState vpnState) {
        Log.d(TAG, "vpnStateChanged: " + vpnState.toString());
        updateUI();
    }

    protected void updateUI() {
        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {
                Log.d(TAG, "vpn :state " + vpnState.toString());
                switch (vpnState) {
                    case IDLE: {
                        binding.pd.setVisibility(View.VISIBLE);
                        binding.tvupload.setText("0 B");
                        binding.tvdownload.setText("0 B");
                        binding.tvnotconnected.setText("Not Connected");
                        binding.pd.hide();
                        setUI(NOTCONNECTED);
                        binding.rvVpn.setVisibility(View.VISIBLE);
                        binding.lytNotConnected.setVisibility(View.VISIBLE);
                        binding.imgonoff.setImageResource(getResources().getIdentifier("raw/back2", null, getPackageName()));

                        break;
                    }
                    case CONNECTED: {
                        setUI(CONNECTED);
                        chkServer();
                        startTimer();
                        binding.pd.hide();
                        binding.imgonoff.setImageResource(getResources().getIdentifier("raw/startvpn", null, getPackageName()));

                        break;
                    }
                    case CONNECTING_VPN:
                        setUI(NOTCONNECTED);
                        binding.pd.setVisibility(View.VISIBLE);
                        binding.tvnotconnected.setText(R.string.connecting);
                        break;
                    case CONNECTING_CREDENTIALS:
                        binding.pd.setVisibility(View.VISIBLE);
                        binding.tvnotconnected.setText(R.string.connecting);
                        break;
                    case CONNECTING_PERMISSIONS: {
                        binding.pd.setVisibility(View.VISIBLE);
                        binding.tvnotconnected.setText(R.string.connecting);
                        break;
                    }
                    case DISCONNECTING:

                        break;
                    case PAUSED: {
                        binding.tvnotconnected.setText(R.string.paused);
                        binding.tvconnected.setText(R.string.paused);
                        binding.pd.hide();
                        break;


                    }
                    default:

                }

            }

            @Override
            public void failure(@NonNull VpnException e) {
//ll
                Log.d(TAG, "failure:468  " + e.toString());
                chkServer();

            }
        });


    }

    @Override
    public void onTrafficUpdate(long l, long l1) {
        //   Log.d(TAG, "onTrafficUpdate: " + l);
        String outString = Converter.humanReadableByteCountOld(l, false);
        String inString = Converter.humanReadableByteCountOld(l1, false);
        //  Log.d(TAG, "onTrafficUpdate: " + outString);
        binding.tvdownload.setText(inString);
        binding.tvupload.setText(outString);
    }

    public void onclickrefresh(View view) {
        binding.rvVpn.setVisibility(View.GONE);
        binding.tvName.setVisibility(View.GONE);
        binding.pd.setVisibility(View.VISIBLE);
        initView();
        initViewListners();
        loginToVPN();
        chkServer();

    }

    public void onclickLocation(View view) {

    }

    private void startTimer() {

        startService(intent);
        registerReceiver(broadcastReceiver, new IntentFilter(TimerService.BROADCAST_ACTION));
    }
}