package net.svamp.wifitracker.gui;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import net.svamp.wifitracker.R;

/**
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.settings_screen);
    }
}
