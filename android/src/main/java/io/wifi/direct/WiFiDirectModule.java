package io.wifi.direct;

import java.util.*;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

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
    public void discoverPeers(final Promise promise) {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                promise.resolve(null);
            }

            @Override
            public void onFailure(int code) {
                promise.reject(String.valueOf(code));
            }
        });
    }

    @ReactMethod
    public void stopPeerDiscovery(final Promise promise) {
        manager.stopPeerDiscovery(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                promise.resolve(null);
            }

            @Override
            public void onFailure(int code) {
                promise.reject(String.valueOf(code));
            }
        });
    }

    @ReactMethod
    public void connect(String deviceAddress, final Promise promise) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceAddress;

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                promise.resolve(null);
            }

            @Override
            public void onFailure(int code) {
                promise.reject(String.valueOf(code));
            }
        });
    }

    @ReactMethod
    public void connectWithIntent(String deviceAddress, int intent, final Promise promise) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = deviceAddress;
        config.groupOwnerIntent = intent;

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                promise.resolve(null);
            }

            @Override
            public void onFailure(int code) {
                promise.reject(String.valueOf(code));
            }
        });
    }

    @ReactMethod
    public void disconnect(final Promise promise) {
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                promise.resolve(null);
            }

            @Override
            public void onFailure(int code) {
                promise.reject(String.valueOf(code));
            }
        });
    }

    @ReactMethod
    public void createGroup(final Promise promise) {
        manager.createGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                promise.resolve(null);
            }

            @Override
            public void onFailure(int code) {
                promise.reject(String.valueOf(code));
            }
        });
    }

    @ReactMethod
    public void removeGroup(final Promise promise) {
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                promise.resolve(null);
            }

            @Override
            public void onFailure(int code) {
                promise.reject(String.valueOf(code));
            }
        });
    }

    @ReactMethod
    public void getGroupInfo(final Promise promise) {
        manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup group) {
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

                promise.resolve(params);
            }
        });
    }

    @ReactMethod
    public void addLocalService(ReadableMap recordMap, String name, String type, final Promise promise) {
        Bundle bundle = Arguments.toBundle(recordMap);
        Map<String, String> record = new HashMap<String, String>();
        Iterator<String> iterator = bundle.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            record.put(key, bundle.getString(key));
        }

        WifiP2pDnsSdServiceInfo serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance(name, type, record);

        manager.addLocalService(channel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                promise.resolve(null);
            }

            @Override
            public void onFailure(int code) {
                promise.reject(String.valueOf(code));
            }
        });
    }

    @ReactMethod
    private void discoverServices(final Promise promise) {
        WifiP2pManager.DnsSdTxtRecordListener txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(String fullDomainName, Map record, WifiP2pDevice device) {
                WritableMap service = Arguments.createMap();
                service.putInt("status", device.status);
                service.putString("primaryType", device.primaryDeviceType);
                service.putString("secondaryType", device.secondaryDeviceType);
                service.putString("deviceName", device.name);
                service.putString("deviceAddress", device.deviceAddress);
                service.putBoolean("isGroupOwner", device.isGroupOwner());
                sercive.putString("fullDomainName", fullDomainName);

                WritableMap record2 = Arguments.createMap();
                Iterator<Map.Entry<String, String>> iterator = record.entrySet().iterator();
                while(iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    record2.putString(entry.getKey(), entry.getValue());
                }
                service.putMap("record", record2);

                reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("WIFI_DIRECT:DNS_SD_TEXT_RECORD", service);
            }
        };

        WifiP2pManager.DnsSdServiceResponseListener servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice device) {
                WritableMap service = Arguments.createMap();
                service.putInt("status", device.status);
                service.putString("primaryType", device.primaryDeviceType);
                service.putString("secondaryType", device.secondaryDeviceType);
                service.putString("deviceName", device.name);
                service.putString("deviceAddress", device.deviceAddress);
                service.putBoolean("isGroupOwner", device.isGroupOwner());
                service.putString("instanceName", instanceName);
                service.putString("registrationType", registrationType);

                reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("WIFI_DIRECT:DNS_SD_SERVICE", service);
            }
        };

        manager.setDnsSdResponseListeners(channel, servListener, txtListener);

        manager.addServiceRequest(channel, WifiP2pDnsSdServiceRequest.newInstance(), new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int code) {
            }
        });

        manager.discoverServices(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                promise.resolve(null);
            }

            @Override
            public void onFailure(int code) {
                promise.reject(String.valueOf(code));
            }
        });
    }
}
