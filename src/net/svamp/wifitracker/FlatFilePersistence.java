package net.svamp.wifitracker;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

/**
 */
public class FlatFilePersistence implements Persistence {
    //Use SD card if possible.
    private boolean useSdCard= Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    private static String dataFolderName;

    private Context context;
    public FlatFilePersistence(Context context) {
        this.context=context;
        dataFolderName = context.getExternalFilesDir(null).getAbsolutePath()+"/.WifiTrackerData/";
    }

    @Override
    public void storeApData (Collection<APDataStore> apData) throws IOException {
        for(APDataStore dataStore : apData) {
            Writer wifiItemWriter,apDataPointsWriter;

            if(useSdCard) {
                String dst = dataFolderName + dataStore.getWifiItem().bssid +"/";
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

    private Writer getWriter(String path,String file) throws IOException {
        File f = new File(path,file);
        f.mkdirs();
        return new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(f)));
    }

    private InputStream getInputStream(String path, String file) throws FileNotFoundException {
        File f = new File(path,file);
        return new BufferedInputStream(new FileInputStream(f));
    }

    private String streamToString(InputStream stream) {
        return new Scanner(stream).useDelimiter("\\A").next();
    }


    @Override
    public Collection<APDataStore> fetchApData () throws FileNotFoundException {
        ArrayList<APDataStore> apData = new ArrayList<APDataStore>();
        try {
        if(useSdCard) {
            //Iterate over all data files on sd card
            for(String bss : new File(dataFolderName).list()) {
                String jsonApDataPoints = streamToString(getInputStream(dataFolderName + bss,"ApDataPoints.dat"));
                String jsonApInfo       = streamToString(getInputStream(dataFolderName + bss,"ApInfo.dat"));
                apData.add(new APDataStore(new JSONObject(jsonApInfo),new JSONArray(jsonApDataPoints)));
            }
        }
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
        }

        return apData;
    }

    @Override
    public APDataStore fetchApData (String bss) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
