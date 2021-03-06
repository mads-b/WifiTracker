package net.svamp.wifitracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class LocationProcessor implements LocationListener {
    //Accuracy in meters. All datapoints within this radius of each other is defined to be the same point.
    public static int minAccuracy;
    private boolean displayedLocationProviderSelectionScreen = false;
    final GpsStatus status = null;
    private final LocationManager locationManager;
    private String provider;
    private final GpsStatusListener gpsStatusListener;
    private final CardListener listener;
    private final Activity activity;


    public LocationProcessor(Activity ac, CardListener listener) {
        this.activity = ac;
        this.listener=listener;
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        gpsStatusListener = new GpsStatusListener(activity,locationManager,listener);

        //Set min accuracy from settings:
        minAccuracy = PreferenceManager.getDefaultSharedPreferences(activity).getInt("minAccuracy",10);
    }

    public void initializeProvider() {
        provider = LocationManager.GPS_PROVIDER;
        if (!locationManager.isProviderEnabled(provider) && !displayedLocationProviderSelectionScreen) {
            displayedLocationProviderSelectionScreen = true;
            openSettings();
            Toast.makeText(activity, R.string.gps_disabled, Toast.LENGTH_LONG).show();
            if (!locationManager.isProviderEnabled(provider) && !displayedLocationProviderSelectionScreen) {
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
        float minDistance = 0;
        //Update every second.
        long minTime = 1;
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
        stopProvider();
    }


    public void onLocationChanged(Location location) {
        //Send new GPS result to our listener
        listener.gpsLocationChanged(location);
    }


    public void onProviderDisabled(String arg0) {}


    public void onProviderEnabled(String arg0) {}

    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}



}
 
