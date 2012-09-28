
package net.svamp.wifitracker;

import android.location.Location;
import android.util.Log;
import net.svamp.wifitracker.core.LatLon;
import net.svamp.wifitracker.core.SignalDataPoint;
import net.svamp.wifitracker.core.WifiItem;
import net.svamp.wifitracker.solver.GaussNewtonSolver;
import org.json.JSONArray;

import java.util.ArrayList;

public class APDataStore extends Thread {

    private final WifiItem wifiItem;
    //Number of datapoints in this location. Using this for increased computation speed.
    private final ArrayList<Integer> points = new ArrayList<Integer>();
    //Data points. x and y are latitude and longitude, z is signal strength.
    private final ArrayList<SignalDataPoint> coords = new ArrayList<SignalDataPoint>();
    private final CardListener apPositionListener;

    public APDataStore (WifiItem wifiItem, CardListener apPositionListener) {
        this.wifiItem=wifiItem;
        this.apPositionListener=apPositionListener;
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
        Log.d("ADDING_DATAPOINT", "Lat: " + loc.getLatitude() + " Long: " + loc.getLongitude() + " Strength: " + str);

        //If accuracy is too bad, don't do anything.
        if(loc.getAccuracy()>LocationProcessor.minAccuracy) { return; }

        SignalDataPoint newP = new SignalDataPoint(new LatLon(loc.getLatitude(),loc.getLongitude()),str);
        boolean spotExists=false;
        for(int i=0;i<coords.size();i++) {
            //If distance is less than the accuracy, it is assumed to be the same spot
            if(distanceBetween(coords.get(i).getCoords(),newP.getCoords())<LocationProcessor.minAccuracy && !spotExists) {
                //Compute average of the signal strengths in this spot
                //Now one more datapoint in this area
                points.set(i,points.get(i)+1);
                //Calculate centroid of these two points
                LatLon[] temp = {coords.get(i).getCoords(),newP.getCoords()};
                LatLon resultP = LatLon.getCentroid(temp);
                //Calculate average signal level in this area
                double newSignal = coords.get(i).getSignalStrength()*(1-1/points.get(i))
                        +newP.getSignalStrength()/points.get(i);

                SignalDataPoint result = new SignalDataPoint(resultP,newSignal);

                coords.set(i, result);
                return;
            }
        }
        //No point was found in the store that were close to this one. Add it as a new data point.
        coords.add(newP);
        points.add(1);
    }

    /**
     * This method takes the data points stored in this object, calculates a resaonable estimate, and iteratively
     * calculates the AP position by inputting the datapoints into the GaussNewtonSolver. For more iteration details,
     * @see GaussNewtonSolver
     * @see net.svamp.wifitracker.solver.ThompsonJacobian
     * @return A point in space where the AP described by this object i estimated to be.
     */
    public void computeApPosition () {
        this.start();
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


    /**
     * Computes the distance between two GPS coordinates. Uses the Haversine formula
     * @param p1 Distance from this point
     * @param p2 To this one
     * @return Distance in meters.
     */
    private static double distanceBetween(LatLon p1, LatLon p2) {
        double R = 6371000; // m
        double dLat = (p2.getLat()-p1.getLat());
        double dLon = (p2.getLon()-p1.getLon());
        double lat1 = p1.getLat();
        double lat2 = p2.getLat();

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
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
        GaussNewtonSolver solver = new GaussNewtonSolver(coords);
        solutionVector = solver.solve(solutionVector,5);
        //Input solution into WifiItem
        wifiItem.location = new LatLon(solutionVector[1],solutionVector[0]);

        apPositionListener.fireApPositionComputed(this);
    }

    public JSONArray toJson() {
        JSONArray json = new JSONArray(coords);
        return json;
    }
}
