
package net.svamp.wifitracker.core;

import android.net.wifi.ScanResult;

import java.util.ArrayList;
import java.util.List;



public class WifiNetworkList {



    private ArrayList<WifiItem> mNetworkList;



    public void initialize(List<ScanResult> liveList) {
        mNetworkList = new ArrayList<WifiItem>();

        for (ScanResult sr : liveList) {
            WifiItem item = new WifiItem();
            item.ssid = sr.SSID;
            item.features = sr.capabilities;
            item.freq = sr.frequency;
            item.level = sr.level;
            item.bss = sr.BSSID;
            // Save for later use
            mNetworkList.add(item);
        }
    }
    public List<WifiItem> getNetworkList() {
        return mNetworkList;
    }

}
