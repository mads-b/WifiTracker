package net.svamp.wifitracker.persistence;

import android.content.Context;
import android.util.Log;
import net.svamp.wifitracker.APDataStore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Persistence class for storing APDataStores to internal flash memory
 */
public class InternalPersistence extends AbstractPersistence {

    private Context context;
    public InternalPersistence (Context context) {
        this.context=context;
    }

    @Override
    public void storeApData (Collection<APDataStore> apData) throws IOException {
        for(APDataStore dataStore : apData) {
            try {
                this.writeStringToFile(context.openFileOutput(dataStore.getWifiItem().bssid+"-ApInfo.dat",Context.MODE_PRIVATE),
                        dataStore.getWifiItem().toJson().toString());
                this.writeStringToFile(context.openFileOutput(dataStore.getWifiItem().bssid+"-ApDataPoints.dat",Context.MODE_PRIVATE),
                        dataStore.toJson().toString());
            } catch (JSONException e) {
                Log.e("CORRUPT DATA","Failed to JSONify APData. "+e.getLocalizedMessage());
            }
        }
    }

    @Override
    public Collection<APDataStore> fetchApData () {
        ArrayList<APDataStore> apData = new ArrayList<APDataStore>();
        try {
            //Iterate over private files.
            for(String file : context.fileList()) {
                if(file.endsWith("ApInfo.dat")) {
                    String bss = file.split("-")[0]; //Since filename is bs:si:db:ss:id-ApInfo.dat
                    apData.add(fetchApData(bss));
                }
            }
        } catch (IOException e) {
            Log.e("IOException","File could not be read. "+e.getLocalizedMessage());
        }
        return apData;
    }

    @Override
    public APDataStore fetchApData (String bss) throws IOException {
        String jsonApDataPoints="";
        String jsonApInfo="";
        try {
            jsonApDataPoints = streamToString(context.openFileInput(bss+"-ApDataPoints.dat"));
            jsonApInfo       = streamToString(context.openFileInput(bss+"-ApInfo.dat"));
        }
        finally {
            try {
                if(jsonApDataPoints.length()!=0)
                    return new APDataStore(new JSONObject(jsonApInfo),new JSONArray(jsonApDataPoints));
                else return null;
            } catch (JSONException e) {
                Log.e("JSONEXCEPTION","Failed to parse json. "+e.getLocalizedMessage());
            } finally { return null; }
        }
    }
}
