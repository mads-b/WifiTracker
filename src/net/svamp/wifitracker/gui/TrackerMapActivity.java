package net.svamp.wifitracker.gui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import com.google.android.maps.*;
import net.svamp.wifitracker.CardListener;
import net.svamp.wifitracker.R;
import net.svamp.wifitracker.core.WifiItem;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class TrackerMapActivity extends MapActivity {
    private GeoPoint geoPoint;
    private MapItemizedOverlay itemizedoverlay;
    private boolean centeredOnUser=false;
    private MapView mapView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        Toast.makeText(this,R.string.no_gps_fix_yet,Toast.LENGTH_LONG).show();

        mapView = (MapView) findViewById(R.id.mapview);
        geoPoint = new GeoPoint(0, 0);
        List<Overlay> mapOverlays = mapView.getOverlays();
        mapView.getController().setZoom(18);

        //Set AP logo on map
        Drawable drawable = this.getResources().getDrawable(R.drawable.ap_icon);
        itemizedoverlay = new MapItemizedOverlay(drawable,this,18);
        mapOverlays.add(itemizedoverlay);


        Handler cvHandle = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                /* get values from message. */
                //New Data about an AP's position!
                if(msg.getData().getBoolean("newAPPointData")) {
                    //Fetch and deserialize json.
                    WifiItem ap = null;
                    try {
                        ap = new WifiItem(new JSONObject(msg.getData().getString("wifiItemJson")));
                    } catch (JSONException e) {
                        Log.e("JSONException",e.getMessage());
                    }
                    //Add ap to overlay

                    itemizedoverlay.addOverlay(ap);

                }
                if(msg.getData().get("gpsAccurate") != null) {
                    int lat_e6 = (int) (msg.getData().getDouble("curLatitude")*1e6);
                    int lon_e6 = (int)(msg.getData().getDouble("curLongitude")*1e6);
                    if(msg.getData().getBoolean("gpsAccurate")) {
                        setGeoPoint(lat_e6,lon_e6);
                    }
                    else {
                        //Center on user on very first fix.
                        centerOnUser(lat_e6,lon_e6);
                    }
                }
            }
        };
        CardListener.getInstance().addHandler(cvHandle);
    }

    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }
    void setGeoPoint (int latitude, int longitude) {
        geoPoint=new GeoPoint(latitude,longitude);
    }

    private void centerOnUser(int latitude, int longitude) {
        if(!centeredOnUser) {
            mapView.getController().setCenter(geoPoint);
            centeredOnUser=true;
        }
    }

}
 
