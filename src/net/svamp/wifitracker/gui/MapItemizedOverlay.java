package net.svamp.wifitracker.gui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import net.svamp.wifitracker.core.WifiItem;

import java.util.*;

class MapItemizedOverlay extends ItemizedOverlay {
    private final List<APOverlayItem> mOverlays = new LinkedList<APOverlayItem>();

    public MapItemizedOverlay(Drawable defaultMarker) {
        super(boundCenterBottom(defaultMarker));
        populate();
    }

    public MapItemizedOverlay(Drawable defaultMarker, Context context) {
        super(defaultMarker);
        populate();
    }

    @Override
    protected OverlayItem createItem(int i) {
        return mOverlays.get(i);
    }

    public void addOverlay(WifiItem wifiItem) {
        GeoPoint point = new GeoPoint((int)(wifiItem.location.getLat()*1e6),(int)(wifiItem.location.getLon()*1e6));
        APOverlayItem overlay = new APOverlayItem(point,wifiItem.ssid,wifiItem.bssid);

        //Remove it if it exists already.
        mOverlays.remove(wifiItem.bssid);
        //Add the new one.
        mOverlays.add(overlay);
        populate();
    }

    @Override
    public int size() {
        return mOverlays.size();
    }
//	@Override
//	protected boolean onTap(int index) {
//	  OverlayItem item = mOverlays.get(index);
//	  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
//	  dialog.setTitle(item.getTitle());
//	  dialog.setMessage(item.getSnippet());
//	  dialog.show();
//	  return true;
//	}


    /**
     * Helper class making OverlayItems comparable given only title and snippet
     */
    private class APOverlayItem extends OverlayItem {
        public APOverlayItem (GeoPoint geoPoint, String s, String s1) {
            super(geoPoint, s, s1);
        }

        public boolean equals(Object o) {
            if(o instanceof  APOverlayItem) {
                APOverlayItem item = (APOverlayItem) o;
                return item.getTitle().equals(getTitle()) && item.getSnippet().equals(getSnippet());
            }
            return false;
        }

    }
}
 
