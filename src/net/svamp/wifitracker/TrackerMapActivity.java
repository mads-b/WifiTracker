package net.svamp.wifitracker;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.google.android.maps.*;

import java.util.List;

public class TrackerMapActivity extends MapActivity {
	private GeoPoint geoPoint;
	private MapItemizedOverlay itemizedoverlay;
	private boolean centeredOnUser=false;
	private MapView mapView;
	
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.map_activity);
        mapView = (MapView) findViewById(R.id.mapview);
        geoPoint = new GeoPoint(0, 0);
        List<Overlay> mapOverlays = mapView.getOverlays();
        mapView.getController().setZoom(18);
        Drawable drawable = this.getResources().getDrawable(R.drawable.ap_icon);
        
        itemizedoverlay = new MapItemizedOverlay(drawable);
		mapOverlays.add(itemizedoverlay);
        
        
		Handler cvHandle = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				/* get values from message. */

				//New Data about an AP's position!
				if(msg.getData().get("newAPPointData")!=null) {
					String apName = msg.getData().getString("apName");
					double apLat = msg.getData().getDouble("apLatitude");
					double apLong = msg.getData().getDouble("apLongitude");
					//We have this AP on map already. Delete and re-add.
					GeoPoint point = new GeoPoint((int)(apLat*1e6),(int)(apLong*1e6));
					OverlayItem overlayitem = new OverlayItem(point, "Access Point", apName);
					itemizedoverlay.addOverlay(overlayitem);
					
					
				}
				if(msg.getData().get("gps_accurate")!=null) {
					setGeoPoint((int)(msg.getData().getDouble("curLatitude")*1e6),(int)(msg.getData().getDouble("curLongitude")*1e6));
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
	public void setGeoPoint(int latitude, int longitude) {
		
		geoPoint=new GeoPoint(latitude,longitude);
		if(!centeredOnUser) {
			mapView.getController().setCenter(geoPoint);
			
			centeredOnUser=true;
		}
	}

}
 
