
package net.svamp.wifitracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity implements OnClickListener {

    ListView wifiList;
    WifiAdapter adapter;
    WifiProcessor processor;

    public MainActivity() {
    }

    /** Called with the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate our UI from its XML layout description.
        setContentView(R.layout.main_activity);


        //Init wifi sniffing
        processor = new WifiProcessor(this,null);
        processor.initializeProvider();
        processor.startScan();
        //Wait for wifi result


        //Init help button
        Button helpButton = (Button) findViewById(R.id.button_help);
        helpButton.setOnClickListener(this);

        //Record buttons
        Button recordSelectedButton = (Button) findViewById(R.id.button_record_selected);
        recordSelectedButton.setOnClickListener(this);

        Button recordAllButton = (Button) findViewById(R.id.button_record_all);
        recordAllButton.setOnClickListener(this);
    }


    /**
     * Called when the activity is about to start interacting with the user.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }


    public void onWifiListGet() {
        //Stop sniffing
        processor.stopScan();
        //Create listview
        wifiList = (ListView) findViewById(R.id.list_wifi_scan);


        adapter = new WifiAdapter(this,processor.getLastResult());
        wifiList.setAdapter(adapter);
        wifiList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        wifiList.setOnItemClickListener(adapter);
    }

    public void onClick(View v) {
        Bundle b;
        Intent intent;

        switch(v.getId()) {
            //Help button clicked
            case R.id.button_help:
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setCancelable(true)
                        .setTitle(R.string.help_title)
                        .setMessage(R.string.help_text)
                        .setPositiveButton(R.string.button_help_close, null);
                builder.create().show();
                break;
            //Button for recording selected AP's selected
            case R.id.button_record_selected:

                b = new Bundle();
                b.putBoolean("hasSelection", true);
                b.putStringArrayList("bssids", adapter.getClicked());

                intent = new Intent(MainActivity.this, CompassActivity.class);
                intent.putExtras(b);
                startActivity(intent);
                break;
            case R.id.button_record_all:
                b = new Bundle();
                b.putBoolean("hasSelection", false);

                intent = new Intent(MainActivity.this, TracerTabWidget.class);
                intent.putExtras(b);
                startActivity(intent);
                break;
        }

    }
}