package net.svamp.wifitracker;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class TracerTabHost extends TabActivity {

    private CardListener cardListener;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Start Cardlistener!
        cardListener = new CardListener(this);
        cardListener.start();

        setContentView(R.layout.tracer_tabs);

        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
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
    }
    protected void onPause() {
        super.onPause();
        cardListener.stop();
    }
    protected void onStop() {
        super.onStop();
        cardListener.stop();
    }
    protected void onResume() {
        super.onResume();
        cardListener.start();
    }

}
 
