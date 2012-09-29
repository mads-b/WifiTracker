package net.svamp.wifitracker.core;

import net.svamp.wifitracker.R;
import org.json.JSONException;
import org.json.JSONObject;

public class WifiItem {
    //Constants for determining what icon this item should have.
    static final String[] SECURITY_MODES = { "WEP", "WPA", "WPA2", "WPA_EAP", "IEEE8021X" };
    static final Integer[] DRAWABLE_ID = { R.drawable.wep, R.drawable.wpa, R.drawable.wpa2,R.drawable.wpa_eap, R.drawable.ieee8021x,R.drawable.none};

    public String ssid;
    public String features;
    public int freq;
    public int level;
    public String bssid;

    //Set by APDataStore when it manages to compute its location.
    public LatLon location;

    public WifiItem() {}

    public WifiItem(JSONObject json) throws JSONException {
        ssid = json.getString("ssid");
        features = json.getString("features");
        freq = json.getInt("freq");
        level = json.getInt("level");
        bssid = json.getString("bssid");
        location = new LatLon(json.getJSONObject("location"));
    }
    /**
     * @return The drawable.id of an image that represents the security of a given {@link WifiItem}.
     */
    public int getDrawableId() {
        for (int i = SECURITY_MODES.length - 1; i >= 0; i--) {
            if (features.contains(SECURITY_MODES[i])) {
                return DRAWABLE_ID[i];
            }
        }
        return DRAWABLE_ID[5];
    }


    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("ssid", ssid);
        object.put("features",features);
        object.put("freq",freq);
        object.put("level",level);
        object.put("bssid", bssid);
        object.putOpt("location", location.toJson());
        return object;
    }

} 
