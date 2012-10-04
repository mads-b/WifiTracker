
package net.svamp.wifitracker;

import android.app.Activity;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

class GpsStatusListener implements GpsStatus.Listener{

    private final LocationManager locationManager;
    private GpsStatus gpsStatus;
    private int satsAvailable=0;
    private final Activity activity;
    private final CardListener listener;
    public GpsStatusListener(Activity ac,LocationManager l,CardListener listener) {
        this.locationManager=l;
        this.activity=ac;
        this.listener = listener;
    }

    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                gpsStatus = locationManager.getGpsStatus(gpsStatus);
                int sat_counter = 0;
                for (GpsSatellite gpsSatellite : gpsStatus.getSatellites()) {
                    sat_counter++;
                }
                satsAvailable = sat_counter;
                listener.onSatNumChanged(satsAvailable);
                System.out.println(satsAvailable+" satellites found");

                break;
            case GpsStatus.GPS_EVENT_STARTED:

                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                Toast.makeText(activity, R.string.first_gps_fix, Toast.LENGTH_LONG).show();
                Log.d("FIX GOTTEN","FIX GOTTEN WITH "+satsAvailable+" SATTELLITES");
                break;
        }
    }
}
