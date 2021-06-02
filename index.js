import { DeviceEventEmitter, NativeModules } from 'react-native';

const WiFiDirectModule = NativeModules.WiFiDirectModule;

const initialize = () => WiFiDirectModule.initialize()

const discoverPeers = () => WiFiDirectModule.discoverPeers()

const stopPeerDiscovery = () => WiFiDirectModule.stopPeerDiscovery()

const connect = (deviceAddress) => WiFiDirectModule.connect(deviceAddress)

const connectWithIntent = (deviceAddress, intent) => WiFiDirectModule.connect(deviceAddress, intent)

const disconnect = () => WiFiDirectModule.disconnect()  

const createGroup = () => WiFiDirectModule.createGroup()

const removeGroup = () => WiFiDirectModule.removeGroup()

const getGroupInfo = () => WiFiDirectModule.getGroupInfo()

const addLocalService = (record, name, type) => WiFiDirectModule.addLocalService(record, name, type)

const discoverServices = () => WiFiDirectModule.discoverServices()

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
  connectWithIntent,
  disconnect,
  createGroup,
  removeGroup,
  getGroupInfo,
  addLocalService,
  discoverServices,
  addListener,
  removeListener,
}
