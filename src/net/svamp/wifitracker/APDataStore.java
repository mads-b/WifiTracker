
package net.svamp.wifitracker;

import android.location.Location;
import android.util.Log;
import net.svamp.wifitracker.core.LatLon;
import net.svamp.wifitracker.core.SignalDataPoint;
import net.svamp.wifitracker.core.WifiItem;
import net.svamp.wifitracker.solver.GaussNewtonSolver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class APDataStore extends Thread {

    private final WifiItem wifiItem;
    //Data points. x and y are latitude and longitude, z is signal strength.
    private final ArrayList<SignalDataPoint> coords = new ArrayList<SignalDataPoint>();
    private CardListener apPositionListener;

    public APDataStore (WifiItem wifiItem) {
        this.wifiItem=wifiItem;
    }

    public APDataStore (JSONObject wifiItem,JSONArray jsonArray) throws JSONException {
        this.wifiItem = new WifiItem(wifiItem);
        for(int i=0;i<jsonArray.length();i++)
            this.coords.add(new SignalDataPoint((JSONObject) jsonArray.get(i)));
    }

    public void setListener(CardListener listener) {
        apPositionListener=listener;
    }

    /**
     * Adds new data point to the APDataStore store.
     * If the location is further than LocationProcessor.minAccuracy meters away,
     * the data point is simply added. If the new data point is closer than this
     * to any existing data point, these data points are merged and their signal strengths averaged.
     * @param loc Location of new data point.
     * @param str Signal strength of new data point.
     */
    public void addData(Location loc, double str) {
        //Check if datapoint is precise enough to use
        //If accuracy is too bad, don't do anything.
        if(loc.getAccuracy()>LocationProcessor.minAccuracy) { return; }

        SignalDataPoint newP = new SignalDataPoint(new LatLon(loc.getLatitude(),loc.getLongitude()),str);

        //Add it to the store
        coords.add(newP);
    }

    /**
     * This method takes the data points stored in this object, calculates a resaonable estimate, and iteratively
     * calculates the AP position by inputting the datapoints into the GaussNewtonSolver. For more iteration details,
     * @see GaussNewtonSolver
     * @see net.svamp.wifitracker.solver.ThompsonJacobian
     */
    public void computeApPosition () {
        if(coords.size()>=6) {
            Log.d("COMPUTING", "Computing position of AP: " + wifiItem.ssid + " with " + coords.size() + " points");
            this.start();
        }
    }

    /**
     * Fetches the size of the data store contained within this class.
     * @return Number of datapoints within this instance.
     */
    public int getDataSize() {
        return coords.size();
    }

    /**
     * Fetches the WifiItem of the AP this instance is storing data points for
     * @return WifiItem containing all the details of this AP.
     */
    public WifiItem getWifiItem() {
        return wifiItem;
    }

    @Override
    public void run() {
        //Vector on the format {x0,y0,n0};
        double[] solutionVector = new double[3];

        //First, we need some initial values (estimates)
        //Path loss exponent. Thompson had the best result with 1.72.
        solutionVector[2] = 1.72;

        //We have not computed this AP's location before.. Take a guess at its position!
        if(wifiItem.location==null) {
            //Make our estimate of AP position the mean value of all our data points! This is necessary, as a bad initial guess leads to a diverging solution.
            LatLon initialEstimate = SignalDataPoint.getCentroid(coords);
            //Simplicity assumption: On this scale, latitude and longitude are orthogonal
            solutionVector[0] = initialEstimate.getLon();
            solutionVector[1] = initialEstimate.getLat();
        }
        //We have a previous computation! Use it to improve the initial guess.
        else {
            solutionVector[0]= wifiItem.location.getLon();
            solutionVector[1]= wifiItem.location.getLat();
        }

        try {
        GaussNewtonSolver solver = new GaussNewtonSolver(coords);
        solutionVector = solver.solve(solutionVector,5);
        } catch (IllegalArgumentException e) {
            Log.d("SOLVER_FAILED",e.getLocalizedMessage());
            return;
        }
        //Input solution into WifiItem
        wifiItem.location = new LatLon(solutionVector[1],solutionVector[0]);

        try {
            if(apPositionListener!=null) {
                apPositionListener.fireApPositionComputed(this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONArray toJson() throws JSONException {
        JSONArray json = new JSONArray();
        for(SignalDataPoint s : coords) {
            json.put(s.toJson());
        }
        return json;
    }
}
