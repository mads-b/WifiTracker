package net.svamp.wifitracker;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import org.json.JSONException;

import java.io.*;
import java.util.Collection;

/**
 */
public class FlatFilePersistence implements Persistence {
    //Use SD card if possible.
    private boolean useSdCard= Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    private static final String dataFolderName = ".WifiTrackerData/";

    private Context context;
    public FlatFilePersistence(Context context) {
        this.context=context;
    }

    @Override
    public void storeApData (Collection<APDataStore> apData) throws IOException {
        String dir = context.getExternalFilesDir(null).getAbsolutePath();


        for(APDataStore dataStore : apData) {
            Writer wifiItemWriter,apDataPointsWriter;

            if(useSdCard) {
                String dst = dir + "/" + dataFolderName + dataStore.getWifiItem().bssid +"/";
                wifiItemWriter = getWriter(dst,"ApInfo.dat");
                apDataPointsWriter = getWriter(dst,"ApDataPoints.dat");
            }
            else {
                wifiItemWriter = new OutputStreamWriter(
                        context.openFileOutput(dataStore.getWifiItem().bssid+"-ApInfo.dat",Context.MODE_PRIVATE));
                apDataPointsWriter = new OutputStreamWriter(
                        context.openFileOutput(dataStore.getWifiItem().bssid+"-ApDataPoints.dat",Context.MODE_PRIVATE));
            }
            try {
                wifiItemWriter.write(dataStore.getWifiItem().toJson().toString(4));
                apDataPointsWriter.write(dataStore.toJson().toString(4));
            } catch (JSONException e) {
                Log.e("CORRUPT DATA","Failed to JSONify APData. "+e.getLocalizedMessage());
            }
        }
    }

    private Writer getWriter(String path,String file) throws IOException {
        File f = new File(path,file);
        f.mkdirs();
        return new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(new File(path))));
    }


    @Override
    public Collection<APDataStore> fetchApData () {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public APDataStore fetchApData (String bss) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
