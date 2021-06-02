package io.wifi.direct;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pGroup;
import androidx.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private ReactApplicationContext reactContext;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, ReactApplicationContext reactContext) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.reactContext = reactContext;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // TODO
            } else {
                // TODO
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            manager.requestPeers(channel, peerListListener);

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                manager.requestConnectionInfo(channel, connectionListener);
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            manager.requestGroupInfo(channel, groupInfoListener);
        }
    }

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            WritableArray array = Arguments.createArray();

            for (WifiP2pDevice device : peerList.getDeviceList()) {
                WritableMap params = Arguments.createMap();
                params.putInt("status", device.status);
                params.putString("primaryType", device.primaryDeviceType);
                params.putString("secondaryType", device.secondaryDeviceType);
                params.putString("deviceName", device.deviceName);
                params.putString("deviceAddress", device.deviceAddress);
                params.putBoolean("isGroupOwner", device.isGroupOwner());
                array.pushMap(params);
            }

            WritableMap params = Arguments.createMap();
            params.putArray("devices", array);
            sendEvent(reactContext, "WIFI_DIRECT:PEERS_UPDATED", params);
        }
    };

    private WifiP2pManager.ConnectionInfoListener connectionListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(final WifiP2pInfo info) {
            String groupOwnerAddress = info.groupOwnerAddress.getHostAddress();

            WritableMap params = Arguments.createMap();
            params.putString("address", groupOwnerAddress);
            params.putBoolean("groupFormed", info.groupFormed);
            params.putBoolean("isGroupOwner", info.isGroupOwner);
            sendEvent(reactContext, "WIFI_DIRECT:CONNECTION_INFO_UPDATED", params);
        }
    };

    private WifiP2pManager.GroupInfoListener groupInfoListener = new WifiP2pManager.GroupInfoListener() {
        @Override
        public void onGroupInfoAvailable(WifiP2pGroup group) {
            if (group != null) {
                WritableMap params = Arguments.createMap();

                if (group != null) {
                    params.putString("interface", group.getInterface());
                    params.putString("networkName", group.getNetworkName());
                    params.putString("passphrase", group.getPassphrase());

                    WifiP2pDevice groupOwner = group.getOwner();

                    if (group.getOwner() != null) {
                        WritableMap owner = Arguments.createMap();

                        owner.putString("deviceAddress", groupOwner.deviceAddress);
                        owner.putString("deviceName", groupOwner.deviceName);
                        owner.putInt("status", groupOwner.status);
                        owner.putString("primaryDeviceType", groupOwner.primaryDeviceType);
                        owner.putString("secondaryDeviceType", groupOwner.secondaryDeviceType);

                        params.putMap("owner", owner);
                    } else {
                        params.putNull("owner");
                    }
                }

                sendEvent(reactContext, "WIFI_DIRECT:THIS_DEVICE_CHANGED", params);
            }
        }
    };

    private void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }
}
