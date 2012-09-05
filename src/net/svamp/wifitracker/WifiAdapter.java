package net.svamp.wifitracker;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import net.svamp.wifitracker.core.WifiItem;

import java.util.ArrayList;



public class WifiAdapter extends BaseAdapter  implements OnItemClickListener {
	private ArrayList<WifiItem> results;
	private ArrayList<View> listItems = new ArrayList<View>();
	private ArrayList<Boolean> clicked = new ArrayList<Boolean>(); 
	static final String[] SECURITY_MODES = { "WEP", "WPA", "WPA2", "WPA_EAP", "IEEE8021X" };
	static final Integer[] DRAWABLE_ID =	   { R.drawable.wep, R.drawable.wpa, R.drawable.wpa2,R.drawable.wpa_eap, R.drawable.ieee8021x,R.drawable.none};
	public WifiAdapter(Context context, ArrayList<WifiItem> results) {
		this.results = results;
		View convertView;
		for(int i=0;i<results.size();i++) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.wifi_list_item, null);
			ImageView wifiLogo = (ImageView) convertView.findViewById(R.id.encryption_logo);
			wifiLogo.setImageResource(getScanResultSecurity(results.get(i)));
			TextView wifiName = (TextView) convertView.findViewById(R.id.wifi_name);
			wifiName.setText(results.get(i).ssid);
			clicked.add(false);
			listItems.add(convertView);
		}
	} 

	public View getView(int position, View convertView, ViewGroup viewGroup) {
		View view = listItems.get(position);
		if(clicked.get(position))
			view.setBackgroundColor(Color.YELLOW);
		else
			view.setBackgroundColor(Color.TRANSPARENT);
		return view;

	}

	/**
	 * @return The drawable.id of an image that represents the security of a given {@link WifiItem}.
	 */
	public static int getScanResultSecurity(WifiItem scanResult) {
		final String cap = scanResult.features;
		for (int i = SECURITY_MODES.length - 1; i >= 0; i--) {
			if (cap.contains(SECURITY_MODES[i])) {
				return DRAWABLE_ID[i];
			}
		}

		return DRAWABLE_ID[5];
	}


	public int getCount() {
		if(results==null) return 0;
		return results.size();
	}


	public Object getItem(int arg0) {
		return results.get(arg0);
	}


	public long getItemId(int arg0) {
		return results.indexOf(arg0);
	}


	public void onItemClick(AdapterView<?> parent, View curItem, int position, long id) {
		clicked.set(position, !clicked.get(position));
	}

	/*
	 * @return A list of the selected wifi AP's.
	 */
	public ArrayList<String> getClicked() {
		ArrayList<String> selected = new ArrayList<String>();
		for(int i=0;i<clicked.size();i++) {
			if(clicked.get(i))
				selected.add(results.get(i).bss);				
		}
		return selected;
	}


}
 
