package net.svamp.wifitracker;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

/**
 */
public interface Persistence {

    void storeApData(Collection<APDataStore> apData) throws IOException, JSONException;

    Collection<APDataStore> fetchApData() throws FileNotFoundException;

    APDataStore fetchApData(String bss) throws IOException;
}
