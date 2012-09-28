package net.svamp.wifitracker.core;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Iterator;

/**
 * Wrapper class for a LatLon object and the signal strength for an AP, measured in this area.
 * Object is immutable.
 */
public class SignalDataPoint {
	private final LatLon coords;
	private final double signalStrength;

	public SignalDataPoint(LatLon coords, double signalStrength) {
		this.coords=coords;
		this.signalStrength=signalStrength;
	}

    public SignalDataPoint(JSONObject json) throws JSONException {
        this.coords = new LatLon(json.getJSONObject("coords"));
        this.signalStrength = json.getDouble("signalStrength");

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

    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("signalStrength",signalStrength);
        json.put("coords",coords.toJson());
        return json;
    }
}
