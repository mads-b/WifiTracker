package net.svamp.wifitracker.core;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Simple wrapper class for a latitude and a longitude coordinate.
 * This wrapper is immutable.
 */
public class LatLon {
    private final double lat,lon;

    public LatLon(double lat,double lon) {
        this.lat=lat;
        this.lon=lon;
    }
    public LatLon(JSONObject json) throws JSONException {
        this.lat = json.getDouble("lat");
        this.lon = json.getDouble("lon");
    }


    public double getLat() { return lat; }
    public double getLon() { return lon; }


    public static LatLon getCentroid(LatLon[] coords) {
        double accLat=0;
        double accLon=0;

        for (LatLon coord : coords) {
            accLat += coord.getLat();
            accLon += coord.getLon();
        }
        return new LatLon(accLat/coords.length,accLon/coords.length);
    }

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("lat",lat);
        json.put("lon",lon);
        return json;
    }

    /**
     * Computes the distance between two LatLon objects coordinates. Uses the Haversine formula
     * @param p1 Distance from this point
     * @param p2 To this one
     * @return Distance in meters.
     */
    public static double distanceBetween(LatLon p1, LatLon p2) {
        double R = 6367500; // m

        double dLon = Math.toRadians(p2.lon-p1.lon);
        double dLat = Math.toRadians(p2.lat-p1.lat);
        double lat1 = Math.toRadians(p1.lat);
        double lat2 = Math.toRadians(p2.lat);

        double a = (Math.sin(dLat/2))*Math.sin(dLat/2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(dLon/2)*Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;

    }

    /**
     * Calculates the bearing between two points if one would travel from p1 to p2.
     * Is greatly simplified to two dimensions.
     * @param p1 Point to calculate bearing from
     * @param p2 Point to salculate bearing to
     * @return Bearing (angle between the line straight north, and the line between p1 and p2)..
     */
    public static double bearingBetween(LatLon p1, LatLon p2) {
        return Math.atan2(p2.lon-p1.lon,p2.lat-p1.lat);
    }
}
