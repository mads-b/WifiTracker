package net.svamp.wifitracker.persistence;

import android.content.Context;
import android.util.Log;
import net.svamp.wifitracker.APDataStore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;

/**
 */
public class InternalPersistence extends AbstractPersistence {

    private Context context;
    public InternalPersistence (Context context) {
        this.context=context;
    }

    @Override
    public void storeApData (Collection<APDataStore> apData) throws IOException {
        for(APDataStore dataStore : apData) {
            Writer wifiItemWriter,apDataPointsWriter;

            wifiItemWriter = new OutputStreamWriter(
                    context.openFileOutput(dataStore.getWifiItem().bssid+"-ApInfo.dat",Context.MODE_PRIVATE));
            apDataPointsWriter = new OutputStreamWriter(
                    context.openFileOutput(dataStore.getWifiItem().bssid+"-ApDataPoints.dat",Context.MODE_PRIVATE));

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
            //Iterate over private files.
            for(String file : context.fileList()) {
                if(file.endsWith("ApInfo.dat")) {
                    String bss = file.split("-")[0];
                    String jsonApDataPoints = streamToString(context.openFileInput(bss+"-ApDataPoints.dat"));
                    String jsonApInfo       = streamToString(context.openFileInput(file));
                    apData.add(new APDataStore(new JSONObject(jsonApInfo),new JSONArray(jsonApDataPoints)));
                }
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
