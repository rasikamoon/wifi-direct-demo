package com.example.samplewifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {

    WifiP2pManager wifiP2pManager;
    WifiP2pManager.Channel channel;
    WifiActivity wifiActivity;


    public WifiDirectBroadcastReceiver(WifiP2pManager wifiP2pManager,
                                       WifiP2pManager.Channel channel, WifiActivity wifiActivity) {
        this.wifiP2pManager = wifiP2pManager;
        this.channel = channel;
        this.wifiActivity = wifiActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                wifiActivity.setIsWifiP2pEnabled(true);
                Toast.makeText(wifiActivity, "Wifi on", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(wifiActivity, "Wifi off", Toast.LENGTH_SHORT).show();
                wifiActivity.setIsWifiP2pEnabled(false);
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (wifiP2pManager != null) {
                wifiP2pManager.requestPeers(channel, wifiActivity.peerListListener);
            }


        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

        }

    }
}
