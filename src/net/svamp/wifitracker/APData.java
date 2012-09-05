
package net.svamp.wifitracker;

import android.location.Location;
import android.util.Log;
import net.svamp.wifitracker.core.Point3D;

import java.util.ArrayList;
import java.util.Arrays;

public class APData {
	//Number of points to use for every estimate of direction to AP.
	private final int pointsInDirEstimate = 5;
	private String SSID;
	//Number of datapoints in this location. Using this for increased computation speed.
	private ArrayList<Integer> points = new ArrayList<Integer>();
	//Data points. x and y are latitude and longitude, z is signal strength.
	private ArrayList<Point3D> coords = new ArrayList<Point3D>();

	public APData(String SSID) {
		this.SSID = SSID;
	}

	//Please make str a positive number where it is bigger the closer it is to the AP.
	public void addData(Location loc, double str) {
		//Check if datapoint is precise enough to use
		Log.d("ADDING_DATAPOINT", "Lat: " + loc.getLatitude() + " Long: " + loc.getLongitude() + " Strength: " + str);
		if(loc.getAccuracy()<=LocationProcessor.minAccuracy) {
			Point3D newP = new Point3D(loc.getLatitude(),loc.getLongitude(),str);
			boolean spotExists=false;
			for(int i=0;i<coords.size();i++) {
				//If distance is less than the accuracy, it is assumed to be the same spot
				if(distanceBetween(coords.get(i),newP)<LocationProcessor.minAccuracy && !spotExists) {
					//Compute average of the signal strengths in this spot
					spotExists=true;
					//Now one more datapoint in this area
					points.set(i,points.get(i)+1);
					//Calculate centroid of these two points
					Point3D[] temp = {coords.get(i),newP};
					Point3D resultP = Point3D.getCentroid(temp, false);
					//Calculate average signal level in this area
					resultP.z=coords.get(i).z*(1-1/points.get(i))+newP.z/points.get(i);

					coords.set(i, resultP);

				}
			}
			if(!spotExists) {
				coords.add(newP);
				points.add(1);
			}
		}
	}

	/**
	 * Gauss-Newton implementation used to combine all datapoints and calculate where the real Wifi AP is!
	 * Formulas for implementation are taken from the paper "Outdoor localization of a WiFi source with unknown
	 * transmission power", by Thompson(2009) et. al. This is the main highlight of this app. This algorithm is
	 * hard/impossible to understand prior to reading the paper!
	 * For the most relevant info, see especially the definition of the Jacobian matrix H(theta) provided in the paper.
	 * TODO: THIS METHOD IS NOT TESTED YET!
	 * @return A point in space where the AP described by this object i estimated to be.
	 */
	public Point3D getApPosition() {
		//First, we need some initial values (estimates)
		//Path loss exponent
		final double n0 = 1.72;
		//Make our estimate of AP position the mean value of all our data points!
		Point3D initialEstimate = Point3D.getCentroid(coords.toArray(new Point3D[]{}), false);
		final double x0 = initialEstimate.x;
		final double y0 = initialEstimate.y;

		//Iterating values made to hold the ever-more accurate position and path loss exponents.
		double xi[] = new double[coords.size()];
		double yi[] = new double[coords.size()];
		double ni[] = new double[coords.size()];
		//Populate the estimators with initial values!
		Arrays.fill(xi,x0);
		Arrays.fill(yi,y0);
		Arrays.fill(ni,n0);

		//Very commonly used (in the formula): ln(10)
		final double ln10 = Math.log(10);


		//Fetch x1 and y1 (x and y in first row).
		double x1 = coords.get(0).x;
		double y1 = coords.get(0).y;
		// Calculate r1 squared. This is the reference row:
		double r1 = square(xi[0]-x1)+square(yi[0]-y1);

		/* Relax a number of times! */
		for(int n=0;n<7;n++) {
			/* Start relaxing the matrix. Don't relax row 1, as this is the reference row! */
			for(int i=1;i<points.size();i++) {
				final double x=coords.get(i).x;
				final double y=coords.get(i).y;

				//Calculate ri squared for this row:
				double ri = square(xi[i]-x)+square(yi[i]-y);
				//New xi and yi estimate for this row:
				xi[0] = 10*ni[0]/ln10*((x-xi[i])/ri-(x-x1)/r1);
				yi[0] = 10*ni[0]/ln10*((y-yi[i])/ri-(y-y1)/r1);
				//New ri estimate:
				ni[0] = 10*(Math.log10(r1)-Math.log10(ri));
			}
			/* Paper never mentioned this; But: Fill xi,yi and ni with averages of themselves? */
			double xiSum=0,yiSum=0,niSum=0;
			for(int i=0;i<xi.length;i++) { xiSum+=xi[i]; yiSum+=yi[i]; niSum+=ni[i]; }
			Arrays.fill(xi,xiSum/xi.length);
			Arrays.fill(yi,yiSum/yi.length);
			Arrays.fill(ni,niSum/ni.length);
			/* End averages fill */
		}
		//Return estimated coordinates to AP, and the path loss exponent for this AP.
		return new Point3D(xi[0],yi[0],ni[0]);
	}

	/**
	 * Faster squaring of numbers, as Java uses logarithms internally.
	 * @param v Var to square
	 * @return v*v
	 */
	private static double square(double v) { return v*v; }

	/**
	 * Fetches the size of the data store contained within this class.
	 * @return Number of datapoints within this instance.
	 */
	public int getDataSize() {
		return coords.size();
	}

	/**
	 * Fetches the SSID of the AP this instance is storing data points for
	 * @return SSID
	 */
	public String getSSID() {
		return SSID;
	}


	/**
	 * Computes the distance between two GPS coordinates. Uses the Haversine formula
	 * @param p1 Distance from this point
	 * @param p2 To this one
	 * @return Distance in meters.
	 */
	private static double distanceBetween(Point3D p1, Point3D p2) {
		double R = 6371000; // m
		double dLat = (p2.y-p1.y);
		double dLon = (p2.x-p1.x);
		double lat1 = p1.y;
		double lat2 = p2.y;

		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		return R * c;

	}
}
