package net.svamp.wifitracker.core;

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

    public double getLat() { return lat; }
    public double getLon() { return lon; }


    public static LatLon getCentroid(LatLon[] coords) {
        double accLat=0;
        double accLon=0;

        for(int i=0;i<coords.length;i++) {
            accLat+=coords[i].getLat();
            accLon+=coords[i].getLon();
        }
        return new LatLon(accLat/coords.length,accLon/coords.length);
    }
}
