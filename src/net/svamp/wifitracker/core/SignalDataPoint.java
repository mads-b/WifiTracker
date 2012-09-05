package net.svamp.wifitracker.core;

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
}
