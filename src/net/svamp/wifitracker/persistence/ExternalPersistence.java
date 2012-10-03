package net.svamp.wifitracker.persistence;

import android.content.Context;
import android.util.Log;
import net.svamp.wifitracker.APDataStore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 */
public class ExternalPersistence extends AbstractPersistence {
    private static String dataFolderName;

    private Context context;

    public ExternalPersistence (Context context) {
        this.context=context;
        dataFolderName = context.getExternalFilesDir(null).getAbsolutePath()+"/.WifiTrackerData/";
    }

    @Override
    public void storeApData (Collection<APDataStore> apData) throws IOException {
        for(APDataStore dataStore : apData) {
            String dst = dataFolderName + dataStore.getWifiItem().bssid +"/";

            try {
                this.writeStringToFile(dst+"ApInfo.dat",dataStore.getWifiItem().toJson().toString());
                this.writeStringToFile(dst+"ApDataPoints.dat",dataStore.toJson().toString());
            } catch (JSONException e) {
                Log.e("CORRUPT DATA","Failed to JSONify APData. "+e.getLocalizedMessage());
            }
        }
    }


    @Override
    public Collection<APDataStore> fetchApData () {
        ArrayList<APDataStore> apData = new ArrayList<APDataStore>();
        try {
            //Iterate over all data files on sd card
            for(String bss : new File(dataFolderName).list()) {
                apData.add(fetchApData(bss));
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
        if(new File(dataFolderName+bss).isDirectory()) {
            jsonApDataPoints = streamToString(getInputStream(dataFolderName + bss,"ApDataPoints.dat"));
            jsonApInfo       = streamToString(getInputStream(dataFolderName + bss,"ApInfo.dat"));
        }
        try {
            if(jsonApDataPoints.length()!=0)
                return new APDataStore(new JSONObject(jsonApInfo),new JSONArray(jsonApDataPoints));
            else return null;
        } catch (JSONException e) {
            Log.e("JSONEXCEPTION","Failed to parse json. "+e.getLocalizedMessage());
        } finally { return null; }
    }
}
