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
}
