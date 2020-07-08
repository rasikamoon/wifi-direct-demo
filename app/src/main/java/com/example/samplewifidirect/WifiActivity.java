package com.example.samplewifidirect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class WifiActivity extends AppCompatActivity {

    TextView textView;
    ListView listView;
    Button onOffButton, discoverButton;
    WifiManager mWifiManager;
    WifiP2pManager mWifiP2pManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    private boolean mIsWifiP2pEnabled = false;

    List<WifiP2pDevice> peers = new ArrayList<>();
    String[] deviceNameArray;
    WifiP2pDevice[] deviceArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        onOffButton = findViewById(R.id.onOffButton);
        discoverButton = findViewById(R.id.discoverButton);
        listView = findViewById(R.id.listView);
        // Wifi manager class provides api for managing peer to peer connectivity
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        mWifiP2pManager = (WifiP2pManager) getApplicationContext().getSystemService(WIFI_P2P_SERVICE);
        // Channel connects the application to wifi network
        mChannel = mWifiP2pManager.initialize(this, getMainLooper(), null);
        mReceiver = new WifiDirectBroadcastReceiver(mWifiP2pManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        onOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsWifiP2pEnabled) {
                    mWifiManager.setWifiEnabled(false);
                    onOffButton.setText("Turn On");

                } else {
                    mWifiManager.setWifiEnabled(true);
                    onOffButton.setText("Turn Off");
                }
            }
        });

        discoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "Started Discovery", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int i) {
                        Toast.makeText(getApplicationContext(), "Failed  Discovery", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * to register broadcast receiver
     */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }


    /**
     * to unregister broadcast receiver
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.mIsWifiP2pEnabled = isWifiP2pEnabled;
    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            if (!peerList.getDeviceList().equals(peers)) {
                peers.clear();
                peers.addAll(peerList.getDeviceList());

                deviceNameArray = new String[peerList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];
                int index = 0;

                for (WifiP2pDevice device : peerList.getDeviceList()) {
                    deviceNameArray[index] = device.deviceName;
                    deviceArray[index] = device;
                    index++;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                        android.R.layout.simple_list_item_1, deviceNameArray);
                listView.setAdapter(adapter);

            }

            if (peers.size() == 0) {
                Toast.makeText(getApplicationContext(), "No devices found!", Toast.LENGTH_SHORT).show();
            }

        }
    };
}
