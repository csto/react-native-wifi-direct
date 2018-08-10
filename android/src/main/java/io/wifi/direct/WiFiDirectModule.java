package io.wifi.direct;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import static android.os.Looper.getMainLooper;

public class WiFiDirectModule extends ReactContextBaseJavaModule {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private final IntentFilter intentFilter = new IntentFilter();
    private ReactApplicationContext reactContext;

    public WiFiDirectModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "WiFiDirectModule";
    }


    @ReactMethod
    public void initialize() {
        // Only initialize once
        if (manager != null) {
            return;
        }

       // Indicates a change in the Wi-Fi P2P status.
       intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

       // Indicates a change in the list of available peers.
       intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

       // Indicates the state of Wi-Fi P2P connectivity has changed.
       intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

       // Indicates this device's details have changed.
       intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        Activity activity = getCurrentActivity();

        manager = (WifiP2pManager) activity.getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(activity, getMainLooper(), null);

        WiFiDirectBroadcastReceiver receiver = new WiFiDirectBroadcastReceiver(manager, channel, reactContext);
        activity.registerReceiver(receiver, intentFilter);
    }

    @ReactMethod
    public void discoverPeers(final Callback listener) {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                listener.invoke(true);
            }

            @Override
            public void onFailure(int reasonCode) {
                listener.invoke(false);
            }
        });
    }

    @ReactMethod
    public void stopPeerDiscovery(final Callback listener) {
        manager.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                listener.invoke(true);
            }

            @Override
            public void onFailure(int reasonCode) {
                listener.invoke(false);
            }
        });
    }

    @ReactMethod
    public void connect(String deviceAddress, final Callback listener) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceAddress;

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                listener.invoke(true);
            }

            @Override
            public void onFailure(int reason) {
                listener.invoke(false);
            }
        });
    }

    @ReactMethod
    public void disconnect(final Callback listener) {
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                listener.invoke(true);
            }

            @Override
            public void onFailure(int reason) {
                listener.invoke(false);
            }
        });
    }
}
