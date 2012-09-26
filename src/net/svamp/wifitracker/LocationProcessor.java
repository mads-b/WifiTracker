package net.svamp.wifitracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

public class LocationProcessor implements LocationListener {
    //Accuracy in meters. All datapoints within this radius of each other is defined to be the same point.
    public static int minAccuracy=10;
    private boolean displayedLocationProviderSelectionScreen = false;
    GpsStatus status = null;
    private LocationManager locationManager;
    private float minDistance=0;
    private long minTime=1;
    private String provider;
    private Location lastLocation;
    private GpsStatusListener gpsStatusListener;
    private CardListener listener;
    private Activity activity;


    public LocationProcessor(Activity ac, CardListener listener) {
        this.activity = ac;
        this.listener=listener;
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        gpsStatusListener = new GpsStatusListener(activity,locationManager,listener);
    }

    public void initializeProvider() {
        provider = LocationManager.GPS_PROVIDER;
        if (!locationManager.isProviderEnabled(provider)) {
            if (!displayedLocationProviderSelectionScreen) {
                displayedLocationProviderSelectionScreen = true;
                openSettings();
                Toast.makeText(activity, R.string.gps_disabled, Toast.LENGTH_LONG).show();
            }

        } else {
            locationManager.addGpsStatusListener(gpsStatusListener);
        }
    }

    private void openSettings() {
        try {
            Intent location_settings = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activity.startActivity(location_settings);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void startProvider() {
        // Requesting updates
        locationManager.requestLocationUpdates(provider, minTime, minDistance,this);
    }

    public void stopProvider() {
        try{
            locationManager.removeUpdates(this);
            locationManager.removeGpsStatusListener(gpsStatusListener);
        }
        catch(IllegalArgumentException ex) {}

    }

    public void onDestroy() {
        this.locationManager = null;
    }
    public Location getLastLocation() { return lastLocation; }
    public GpsStatus getGpsStatus() { return status; }


    public void onLocationChanged(Location location) {
        //Send new GPS result to our listener
        listener.gpsLocationChanged(location);
    }


    public void onProviderDisabled(String arg0) {}


    public void onProviderEnabled(String arg0) {}

    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}



}
 
