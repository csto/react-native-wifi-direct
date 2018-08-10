import { DeviceEventEmitter, NativeModules } from 'react-native';

const WiFiDirectModule = NativeModules.WiFiDirectModule;

const initialize = () => {
  WiFiDirectModule.initialize()
}

const discoverPeers = () => {
  return new Promise((resolve, reject) => {
    WiFiDirectModule.discoverPeers((success) => {
      resolve(success);
    })
  })
}

const stopPeerDiscovery = () => {
  return new Promise((resolve, reject) => {
    WiFiDirectModule.stopPeerDiscovery((success) => {
      resolve(success);
    })
  })
}

const connect = (deviceAddress) => {
  return new Promise((resolve, reject) => {
    WiFiDirectModule.connect(deviceAddress, (data) => {
      resolve(data);
    })
  })
}

const disconnect = () => {
  return new Promise((resolve, reject) => {
    WiFiDirectModule.disconnect((data) => {
      resolve(data)
    })
  })
}

const addListener = (eventName, callback) => {
  DeviceEventEmitter.addListener(`WIFI_DIRECT:${eventName}`, callback)
}

const removeListener = (eventName, callback) => {
  DeviceEventEmitter.removeListener(`WIFI_DIRECT:${eventName}`, callback)
}

export default {
  initialize,
  discoverPeers,
  stopPeerDiscovery,
  connect,
  disconnect,
  addListener,
  removeListener
}
