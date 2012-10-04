package net.svamp.wifitracker.persistence;

import net.svamp.wifitracker.APDataStore;
import org.json.JSONException;

import java.io.IOException;
import java.util.Collection;

/**
 */
public interface Persistence {

    /**
     * Store a quantity of AP dataPoints on memory. All previous data on the same APs are overwritten.
     * @param apData A collection of APDataStore objects to store persistently.
     * @throws IOException
     * @throws JSONException
     */
    void storeApData(Collection<APDataStore> apData) throws IOException;


    /**
     * Fetches all the AP datapoints stored on this device.
     * This method calls fetchApData(String bss) for every dataPoint.
     * @return Collection of APDataStores containing all the data on all the APs registered
     */
    Collection<APDataStore> fetchApData();

    /**
     * Fetches the dataset stored on this specific access point
     * @param bss BSSID of AP in question
     * @return The APDataStore containing all the data on this AP, or null if nothing was found
     * @throws IOException
     */
    APDataStore fetchApData(String bss) throws IOException;
}
