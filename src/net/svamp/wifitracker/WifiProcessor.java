package net.svamp.wifitracker;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;
import net.svamp.wifitracker.core.WifiItem;
import net.svamp.wifitracker.core.WifiNetworkList;

import java.util.List;

public class WifiProcessor extends BroadcastReceiver {
    private Activity activity;
    private WifiManager wifiManager;
    private CardListener listener;
    private boolean running;
    private List<WifiItem> lastResult;

    public WifiProcessor(Activity activity, CardListener s) {
        this.activity=activity;
        this.listener=s;
        this.wifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);;
    }

    public void initializeProvider() {
        if(!wifiManager.isWifiEnabled()) {
            openSettings();
        }
    }
    private void openSettings() {
        try {
            Intent location_settings = new Intent(
                    android.provider.Settings.ACTION_WIFI_SETTINGS);
            activity.startActivity(location_settings);
            Toast.makeText(activity, R.string.wifi_disabled, Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
    public void startScan() {
        //Initiate listening for scan results
        IntentFilter i = new IntentFilter();
        i.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        activity.registerReceiver(this,i);
        if(wifiManager.startScan()) {
            running=true;
        }
        else {
            running=false;
            System.out.println("Startscan failed");
        }
    }
    @Override
    public void onReceive(Context c, Intent i) {
        //If we're not running, we don't care about input!
        if(!running) { return; }

        WifiNetworkList list = new WifiNetworkList();
        list.initialize(wifiManager.getScanResults());
        // Code to execute when SCAN_RESULTS_AVAILABLE_ACTION event occurs
        List<WifiItem> networkList = list.getNetworkList();
        if(listener!=null)
            listener.wifiScanFinished(networkList); // Returns a <list> of scanResults
        lastResult=networkList;
        //Start scanning again! Maybe a sleep is smart here, depending on scan speed. (1 second on most devices)

        //If current activity is mainactivity, draw the rest of the GUI..
        if(activity instanceof MainActivity) {
            ((MainActivity) activity).onWifiListGet();
        }
        wifiManager.startScan();
    }
    //To stop the infinite scan loop
    public void stopScan() {
        //Stop may be called more than once!
        try {
            activity.unregisterReceiver(this);
        }

        catch(IllegalArgumentException ex) {}

        running=false;
    }
    public List<WifiItem> getLastResult() {
        return lastResult;
    }
    public boolean isRunning() { return running; }
}
 
