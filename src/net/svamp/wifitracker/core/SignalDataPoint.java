package net.svamp.wifitracker.core;

import java.util.Collection;
import java.util.Iterator;

/**
 * Wrapper class for a LatLon object and the signal strength for an AP, measured in this area.
 * Object is immutable.
 */
public class SignalDataPoint {
	private final LatLon coords;
	private double signalStrength;

	public SignalDataPoint(LatLon coords, double signalStrength) {
		this.coords=coords;
		this.signalStrength=signalStrength;
	}

	public LatLon getCoords() { return coords; }
	public double getSignalStrength() { return signalStrength; }

    public static LatLon getCentroid(Collection<SignalDataPoint> coords) {
        double accLat=0;
        double accLon=0;

        Iterator<SignalDataPoint> it = coords.iterator();
        SignalDataPoint curPoint;

        while((curPoint=it.next())!=null) {
            accLat+=curPoint.getCoords().getLat();
            accLon+=curPoint.getCoords().getLon();
        }
        return new LatLon(accLat/coords.size(),accLon/coords.size());
    }
}
