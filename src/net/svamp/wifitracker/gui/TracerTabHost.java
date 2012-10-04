package net.svamp.wifitracker.gui;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TabHost;
import net.svamp.wifitracker.CardListener;
import net.svamp.wifitracker.R;
import net.svamp.wifitracker.persistence.ExternalPersistence;
import net.svamp.wifitracker.persistence.InternalPersistence;
import net.svamp.wifitracker.persistence.Persistence;
import org.json.JSONException;

import java.io.IOException;

public class TracerTabHost extends TabActivity implements View.OnClickListener {

    private CardListener cardListener;
    private ProgressBar persistenceProgress;
    private FrameLayout tabContent;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Start Cardlistener!
        cardListener = new CardListener(this);
        cardListener.startScan();
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

        //The two views below will compete for screen space on heavy operations.
        persistenceProgress = (ProgressBar) this.findViewById(R.id.persistence_progress);
        persistenceProgress.setVisibility(ProgressBar.GONE);
        tabContent = this.getTabHost().getTabContentView();
    }
    protected void onPause() {
        super.onPause();
        cardListener.stopScan();
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

    @Override
    public void onBackPressed() {
        final Persistence persistence;
        //Does the user want to store his data points onto SD card or internally?
        String persistenceType = PreferenceManager.getDefaultSharedPreferences(this).getString("dataPointStorageOption","internal");
        if(persistenceType.equals("internal")) {
            persistence = new InternalPersistence(this);
        }
        else {
            persistence = new ExternalPersistence(this);
        }
        //Hide the standard Tab content. We're showing the progress bar now.
        tabContent.setVisibility(FrameLayout.GONE);
        persistenceProgress.setVisibility(ProgressBar.VISIBLE);
        //Save to storage
        new Thread(new Runnable() {
            @Override
            public void run () {
                try {
                    persistence.storeApData(cardListener.getDataPoints());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        finish();
    }
}
 
