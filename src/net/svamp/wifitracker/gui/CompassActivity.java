
package net.svamp.wifitracker.gui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import net.svamp.wifitracker.CardListener;
import net.svamp.wifitracker.R;

public class CompassActivity extends Activity {
    private TextView satsNumField;
    private TextView dataPointNumField;
    private TextView gpsAccurate;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compass_activity);
        //Tie cardlistener to compassview
        satsNumField = (TextView) findViewById(R.id.gps_satNum);
        satsNumField.setText("Sats Found: 0");
        dataPointNumField = (TextView) findViewById(R.id.manager_data_points);
        dataPointNumField.setText("Data Points Found: 0");
        gpsAccurate = (TextView) findViewById(R.id.gps_accurate);
        gpsAccurate.setText("GPS fix accurate: NO");

        Handler satNumHandle = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                /* get values from message */
                if(msg.getData().get("satNum")!=null)
                    satsNumField.setText("Sats Found: "+ msg.getData().getInt("satNum"));
                if(msg.getData().get("dataPointNum")!=null)
                    dataPointNumField.setText("Data Points Found: "+msg.getData().getInt("dataPointNum"));
                if(msg.getData().get("gps_accurate")!=null)
                    if(msg.getData().getBoolean("gps_accurate"))
                        gpsAccurate.setText("GPS fix accurate: YES");
                    else
                        gpsAccurate.setText("GPS fix accurate: NO");
            }
        };
        CardListener.getInstance().addHandler(satNumHandle);

    }

}
