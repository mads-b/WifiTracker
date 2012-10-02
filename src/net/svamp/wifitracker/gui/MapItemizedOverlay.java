package net.svamp.wifitracker.gui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import java.util.ArrayList;

public class MapItemizedOverlay extends ItemizedOverlay {
    private final ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

    public MapItemizedOverlay(Drawable defaultMarker) {
        super(boundCenterBottom(defaultMarker));
        populate();
    }

    public MapItemizedOverlay(Drawable defaultMarker, Context context) {
        super(defaultMarker);
        Context mContext = context;
        populate();
    }

    @Override
    protected OverlayItem createItem(int i) {
        return mOverlays.get(i);
    }

    public void addOverlay(OverlayItem overlay) {
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

}
 
