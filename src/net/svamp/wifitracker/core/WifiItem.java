package net.svamp.wifitracker.core;

import net.svamp.wifitracker.R;

public class WifiItem {
    //Constants for determining what icon this item should have.
    static final String[] SECURITY_MODES = { "WEP", "WPA", "WPA2", "WPA_EAP", "IEEE8021X" };
    static final Integer[] DRAWABLE_ID = { R.drawable.wep, R.drawable.wpa, R.drawable.wpa2,R.drawable.wpa_eap, R.drawable.ieee8021x,R.drawable.none};

    public String ssid;
    public String features;
    public int freq;
    public int level;
    public String bss;


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
} 
