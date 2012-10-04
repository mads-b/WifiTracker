package net.svamp.wifitracker.gui;

import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;
import net.svamp.wifitracker.R;

/**
 */
public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.settings_screen);

        makeStorageOptionList();
    }

    /**
     * Makes the list in the settings where the user can choose where to store his data points.
     */
    private void makeStorageOptionList() {
        final ListPreference storageOption = (ListPreference) this.findPreference("dataPointStorageOption");
        final String[] values = {"internal","external"};
        final String[] keys = {"Internal memory","External(SD) storage"};
        final Context context = this;
        storageOption.setEntries(keys);
        storageOption.setEntryValues(values);
        int selectionVal = storageOption.findIndexOfValue(storageOption.getValue());
        storageOption.setSummary(keys[selectionVal]);
        storageOption.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange (Preference preference, Object newValue) {
                int selectionVal = storageOption.findIndexOfValue((String) newValue);
                storageOption.setSummary(keys[selectionVal]);
                //Warn user about the fact that swapping storage solution results
                // in the other media becoming unavailable for storage.
                Toast.makeText(context,R.string.change_persistence_warning,Toast.LENGTH_LONG).show();
                return true;
            }
        });
    }
}
