import { NativeModules } from 'react-native';

const WiFiP2PManager = NativeModules.WiFiP2PManagerModule;

const getAvailablePeers = () => new Promise((resolve, reject) => {
  WiFiP2PManager.getAvailablePeersList(peersList => {
    const peers = JSON.parse(peersList);
    resolve(peers);
  })
})

const connectTo = (deviceAddress) => new Promise((resolve, reject) => {
  WiFiP2PManager.connect(deviceAddress, data => {
    resolve(data);
  })
})

export {
  getAvailablePeers,
  connectTo
}
