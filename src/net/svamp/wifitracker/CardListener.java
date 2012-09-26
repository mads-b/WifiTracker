
package net.svamp.wifitracker;


import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import net.svamp.wifitracker.core.LatLon;
import net.svamp.wifitracker.core.WifiItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CardListener {
    //Processes used to harvest sensor data
    private final WifiProcessor wifiProcessor;
    private final LocationProcessor locationProcessor;
    private final CompassProcessor compassProcessor;
    //Mapping from BSSID to APData object, which contains all datapoints related to this AP.
    private final HashMap<String,APData> apData = new HashMap<String,APData>();
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
            APData ap;
            //New AP found. Make new dataset
            if(!apData.containsKey(item.bss))
                ap = new APData(item.ssid);
                //Existing found. Get dataset and add new info
            else
                ap = apData.get(item.bss);


            ap.addData(lastLocation, item.level);
            apData.put(item.bss, ap);

            if(apData.get(item.bss).getDataSize()>9) {
                LatLon apPosition = apData.get(item.bss).getApPosition();
                Location loc = new Location("pp");
                loc.setLongitude(apPosition.getLon());
                loc.setLatitude(apPosition.getLat());
                Bundle b=new Bundle();
                b.putBoolean("newAPPointData", true);
                b.putString("apName", item.ssid);
                b.putDouble("apDistance", lastLocation.distanceTo(loc));
                b.putDouble("apBearing", lastLocation.bearingTo(loc));
                b.putDouble("apLatitude", loc.getLatitude());
                b.putDouble("apLongitude", loc.getLongitude());
                Message m=new Message();
                m.setData(b);
                sendMessage(m);
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
        for(APData a : apData.values()) {
            i+=a.getDataSize();
        }
        return i;
    }
}
