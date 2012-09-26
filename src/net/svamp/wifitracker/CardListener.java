
package net.svamp.wifitracker;


import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import net.svamp.wifitracker.core.WifiItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CardListener {
    //Processes used to harvest sensor data
    private final WifiProcessor wifiProcessor;
    private final LocationProcessor locationProcessor;
    private final CompassProcessor compassProcessor;
    //Mapping from BSSID to APDataStore object, which contains all datapoints related to this AP.
    private final Map<String,APDataStore> apDataStores = new HashMap<String,APDataStore>();
    //Handlers to send change messages to.
    private final ArrayList<Handler> handlers = new ArrayList<Handler>();
    //Last known location. Set only on good accuracy.
    private Location lastLocation;
    //This is a singleton.
    private static CardListener sInstance;

    public CardListener(Activity ac) {
        //Let's get some surveillance going!
        wifiProcessor = new WifiProcessor(ac,this);
        locationProcessor = new LocationProcessor(ac,this);
        compassProcessor = new CompassProcessor(ac);
        wifiProcessor.initializeProvider();
        locationProcessor.initializeProvider();
        sInstance = this;
    }

    public static CardListener getInstance() {
        return sInstance;
    }

    /*
      * List of handler listeners.
      */
    public void addHandler(Handler h) {
        handlers.add(h);
    }
    public void removeHandler(Handler h) {
        handlers.remove(h);
    }
    private void sendMessage(Message m) {
        for(Handler h : handlers) {
            Message m_sec = new Message();
            m_sec.copyFrom(m);
            m_sec.setTarget(h);
            m_sec.sendToTarget();
        }
    }

    /**
     * Called by the wifiprocessor when it has finished a scan of nearby networks.
     * Make a location-signal strength pair here and pass it along to the data store if it is accurate enough.
     * @param foundWifi List of wifi networks in the vicinity.
     */
    public void wifiScanFinished(List<WifiItem> foundWifi) {
        //Must have a location to merge data with. Return if we don't
        if(lastLocation==null) { return; }

        //Loction must be accurate enough for us to pass it along. Return if not.
        if(lastLocation.getAccuracy()>LocationProcessor.minAccuracy) { return; }


        Log.d("GPS LOCATION FOUND", "Found GPS location and merging data.");
        for(WifiItem item : foundWifi) {
            APDataStore apStore;
            //New AP found. Make new dataset
            if(!apDataStores.containsKey(item.bss))
                apStore = new APDataStore(item,this);
                //Existing found. Get dataset and add new info
            else
                apStore = apDataStores.get(item.bss);


            apStore.addData(lastLocation, item.level);
            apDataStores.put(item.bss, apStore);

            if(apDataStores.get(item.bss).getDataSize()>9) {
                apDataStores.get(item.bss).computeApPosition();
            }
        }

        //Update all handlers as number of datapoints just changed
        int dataPoints = getNumberOfDataPoints();
        Bundle b=new Bundle();
        b.putInt("dataPointNum", dataPoints);
        Message m=new Message();
        m.setData(b);
        sendMessage(m);
    }

    public LocationProcessor getLocationProcessor() {
        return locationProcessor;
    }

    /**
     * Called by the locationProcessor to notify that location has changed
     * @param location New GPS location
     */
    public void gpsLocationChanged(Location location) {
        //This location is only valid if accuracy is below 20m.
        if(location.getAccuracy()<LocationProcessor.minAccuracy) {
            lastLocation=location;
            Log.d("PRECISE","Found GPS location is usable");

            Bundle b=new Bundle();
            b.putBoolean("gps_accurate", true);
            b.putDouble("curLatitude", location.getLatitude());
            b.putDouble("curLongitude", location.getLongitude());
            Message m=new Message();
            m.setData(b);
            sendMessage(m);

        }
        else {
            Log.d("IMPRECISE", "Found GPS location is too imprecise to be of use");
            Bundle b=new Bundle();
            b.putBoolean("gps_accurate", false);
            Message m=new Message();
            m.setData(b);
            sendMessage(m);
        }
    }



    /*
      * Self explanatory methods. Starts and stops sniffing of data.
      */
    public void start() {
        wifiProcessor.startScan();
        locationProcessor.startProvider();
        compassProcessor.registerListeners();
    }
    public void stop() {
        wifiProcessor.stopScan();
        locationProcessor.stopProvider();
        compassProcessor.unregisterListeners();
    }
    public void onDestroy() {
        stop();
    }
    public void onSatNumChanged(int num) {
        Bundle b=new Bundle();
        b.putInt("satNum", num);
        Message m=new Message();
        m.setData(b);
        sendMessage(m);
    }

    public double getCompassOrientation() {
        return compassProcessor.getOrientation();
    }

    public int getNumberOfDataPoints() {
        int i=0;
        for(APDataStore a : apDataStores.values()) {
            i+=a.getDataSize();
        }
        return i;
    }

    /**
     * Callback method from APDataStore to notify this object when its AP position has been estimated.
     * This method takes this new data, bundles it, and sends it as an Android message
     * @param apStore The APDataStore
     */
    public void fireApPositionComputed(APDataStore apStore) {
        Location loc = new Location("pp");
        loc.setLongitude(apStore.getWifiItem().location.getLon());
        loc.setLatitude(apStore.getWifiItem().location.getLat());
        Bundle b=new Bundle();
        b.putBoolean("newAPPointData", true);
        b.putString("apName", apStore.getWifiItem().ssid);
        b.putDouble("apDistance", lastLocation.distanceTo(loc));
        b.putDouble("apBearing", lastLocation.bearingTo(loc));
        b.putDouble("apLatitude", loc.getLatitude());
        b.putDouble("apLongitude", loc.getLongitude());
        Message m=new Message();
        m.setData(b);
        sendMessage(m);
    }
}
