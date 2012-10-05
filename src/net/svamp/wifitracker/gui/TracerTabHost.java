package net.svamp.wifitracker.gui;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.Toast;
import net.svamp.wifitracker.APDataStore;
import net.svamp.wifitracker.CardListener;
import net.svamp.wifitracker.R;
import net.svamp.wifitracker.persistence.AbstractPersistence;
import net.svamp.wifitracker.persistence.Persistence;

import java.io.IOException;
import java.util.Collection;

public class TracerTabHost extends TabActivity implements View.OnClickListener {

    private CardListener cardListener;
    private Persistence persistence;
    private ProgressDialog dialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracer_tabs);

        //Start Cardlistener!
        cardListener = new CardListener(this);
        cardListener.startScan();

        //Make dialog showing when we are loading data...
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMessage(getString(R.string.loadingDataPointsFromPersistence));
        dialog.setCancelable(true);

        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = this.getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, CompassActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("compass").setIndicator("Compass Mode",
                res.getDrawable(R.drawable.ic_tab_compass))
                .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent = new Intent().setClass(this, TrackerMapActivity.class);
        spec = tabHost.newTabSpec("map").setIndicator("Map Mode",
        		res.getDrawable(R.drawable.ic_tab_map))
        		.setContent(intent);
        tabHost.addTab(spec);
        tabHost.setCurrentTab(0);

        Button recomputeButton = (Button) findViewById(R.id.button_recompute);
        recomputeButton.setOnClickListener(this);

        //Fetch the correct persistence class...
        persistence = AbstractPersistence.getPersistence(this);
        //Show dialog..
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run () {
                //Fetch and add persisted datapoints to data store.
                Collection<APDataStore> dataPoints = persistence.fetchApData();

                if(dataPoints.size()!=0) {
                    int increment = 100/dataPoints.size();
                    for(APDataStore store : dataPoints) {
                        if(store==null) System.out.println("WTF?!!!");
                        cardListener.addDataPoints(store);
                        dialog.incrementProgressBy(increment);
                    }
                }
                dialog.dismiss();
            }
        }).start();
    }
    protected void onPause() {
        super.onPause();
        cardListener.stopScan();
        try {
            Toast.makeText(this,R.string.savingDataPointsToPersistence,Toast.LENGTH_SHORT).show();
            persistence.storeApData(cardListener.getDataPoints());
        } catch (IOException e) {
            Log.e("IOException",e.getLocalizedMessage());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        cardListener.stopScan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cardListener.startScan();
    }

    @Override
    public void onClick (View view) {
        if(view.getId()==R.id.button_recompute) {
            cardListener.fireRecomputeOrder();
        }
    }
}
 
