# react-native-wifi-direct
**_This currently only supports Android_**

A react native module for connecting and disconnecting from Android Wi-Fi Direct networks (p2p).  This was built to be used with IOT devices that broadcast a Wi-Fi Direct network.

### Installation

Install library from `npm`
```bash
npm install react-native-wifi-direct
```

Then link the library:
```bash
react-native link react-native-wifi-direct
```

### Example Usage

```javascript
import WifiDirect from 'react-native-wifi-direct'
```

Permissions: Starting with Android API 25, apps must be granted the ACCESS_COARSE_LOCATION (or ACCESS_FINE_LOCATION) permission in order to obtain results.
```javascript
import { PermissionsAndroid } from 'react-native'

...

const permission = PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION
const options = {
  'title': 'Wifi networks',
  'message': 'We need your permission in order to find wifi networks'
}

PermissionsAndroid.request(permission, options).then((granted) => {
  if (granted === PermissionsAndroid.RESULTS.GRANTED) {
    console.log("Permission granted!");
  } else {
    console.log("You will not able to retrieve wifi available networks list")
  }
}).catch((error) => {
  console.warn(error)
})
```

Initialize `react-native-wifi-direct`.  This should be done in your `index.js` (`index.android.js`) or root level `app.js` file.
```javascript
WifiDirect.initialize()
```

Start scanning for Wi-Fi Direct networks in the area.  The scan will continue until a connection is initiated.  The scan does not return any results.  It only returns whether it successfully started.
```javascript
WifiDirect.discoverPeers().then((success) => {
  if (success) {
    console.log("Peer discovery has initiated successfully.")
  } else {
    console.log("Peer discover failed to initiate.  Is your Wi-Fi on?")
  }
})
```

If you need to stop peer discovery to do non Wi-Fi Direct scan, you can call `stopPeerDiscovery`.  Note that `discoverPeers` will stop on it's own if you connect to a Wi-Fi Direct network.
```javascript
WifiDirect.stopPeerDiscovery().then((success) => {
  if (success) {
    console.log("Peer discovery will stop.")
  } else {
    console.log("Peer discovery can not be stopped.")
  }
})
```

Register an event to listen for when devices are found.  This event will be called every time your device updates its network list.
```javascript
componentWillMount () {
  WifiDirect.addListener('PEERS_UPDATED', this.peersUpdated)
}

// Don't forget to remove the listener to prevent a memory leak
componentWillUnmount () {
  WifiDirect.removeListener('PEERS_UPDATED', this.peersUpdated)
}

peersUpdated = (event) => {
  console.log("Devices found!", event.devices)
}
```

Connect to a Wi-Fi Direct network.  The devices found from `event.devices` in the `PEERS_UPDATED` listener, will have a MAC address that is used to connect.
```javascript
WifiDirect.connect(device.address).then((success) => {
  if (success) {
    console.log("Connection has initiated.")
  } else {
    console.log("Connection failed to initiated.  Check your Wi-Fi.")
  }
})
```

The `CONNECTION_INFO_UPDATED` event is triggered when a connection is successfully established.
```javascript
componentWillMount () {
  WifiDirect.addListener('CONNECTION_INFO_UPDATED', this.connectionInfoUpdated)
}

// Don't forget to remove the listener to prevent a memory leak
componentWillUnmount () {
  WifiDirect.removeListener('CONNECTION_INFO_UPDATED', this.connectionInfoUpdated)
}

connectionInfoUpdated = (event) => {
  console.log("Connection established!", event.connectionInfo)
}
```

```javascript
WifiDirect.disconnect().then((success) => {
  if (success) {
    console.log("Disconnecting initiated.")
  } else {
    console.log("Disconnect initiation failed.  Are you already disconnected?")
  }
})
```

