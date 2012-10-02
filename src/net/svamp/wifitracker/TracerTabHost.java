package net.svamp.wifitracker;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;

public class TracerTabHost extends TabActivity implements View.OnClickListener {

    private CardListener cardListener;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Start Cardlistener!
        cardListener = new CardListener(this);
<<<<<<< HEAD
        cardListener.startScan();
=======
        cardListener.start();
>>>>>>> 4edb34c973022b37201fe4a8aa6147846d86e34c
        setContentView(R.layout.tracer_tabs);

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
        //intent = new Intent().setClass(this, TrackerMapActivity.class);
        //spec = tabHost.newTabSpec("map").setIndicator("Map Mode",
        //		res.getDrawable(R.drawable.ic_tab_map))
        //		.setContent(intent);
        //tabHost.addTab(spec);
        tabHost.setCurrentTab(0);

        Button recomputeButton = (Button) findViewById(R.id.button_recompute);
        recomputeButton.setOnClickListener(this);
    }
    protected void onPause() {
        super.onPause();
<<<<<<< HEAD
        cardListener.stopScan();
    }
    protected void onStop() {
        super.onStop();
        cardListener.stopScan();
    }
    protected void onResume() {
        super.onResume();
        cardListener.startScan();
=======
        cardListener.stop();
    }
    protected void onStop() {
        super.onStop();
        cardListener.stop();
    }
    protected void onResume() {
        super.onResume();
        cardListener.start();
>>>>>>> 4edb34c973022b37201fe4a8aa6147846d86e34c
    }

    @Override
    public void onClick (View view) {
        if(view.getId()==R.id.button_recompute) {
            cardListener.fireRecomputeOrder();


        }
    }
}
 
