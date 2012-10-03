package net.svamp.wifitracker.persistence;

import android.content.Context;
import android.util.Log;
import net.svamp.wifitracker.APDataStore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
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
            Writer wifiItemWriter,apDataPointsWriter;

            String dst = dataFolderName + dataStore.getWifiItem().bssid +"/";
            wifiItemWriter = getWriter(dst,"ApInfo.dat");
            apDataPointsWriter = getWriter(dst,"ApDataPoints.dat");
            try {
                wifiItemWriter.write(dataStore.getWifiItem().toJson().toString());
                apDataPointsWriter.write(dataStore.toJson().toString());
            } catch (JSONException e) {
                Log.e("CORRUPT DATA","Failed to JSONify APData. "+e.getLocalizedMessage());
            }
            finally {
                wifiItemWriter.close();
                apDataPointsWriter.close();
            }
        }
    }


    @Override
    public Collection<APDataStore> fetchApData () throws FileNotFoundException {
        ArrayList<APDataStore> apData = new ArrayList<APDataStore>();
        try {
            //Iterate over all data files on sd card
            for(String bss : new File(dataFolderName).list()) {
                String jsonApDataPoints = streamToString(getInputStream(dataFolderName + bss,"ApDataPoints.dat"));
                String jsonApInfo       = streamToString(getInputStream(dataFolderName + bss,"ApInfo.dat"));
                apData.add(new APDataStore(new JSONObject(jsonApInfo),new JSONArray(jsonApDataPoints)));
            }

        } catch (JSONException e) {
            Log.e("JSONEXCEPTION","Failed to parse json. "+e.getLocalizedMessage());
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
