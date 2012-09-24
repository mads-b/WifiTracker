package net.svamp.wifitracker.solver;

import net.svamp.wifitracker.core.SignalDataPoint;

import java.util.Collection;
import java.util.Iterator;

/**
 * This class represents the Jacobian matrix specified in the paper Thompson(2009) et.al.
 */
public class ThompsonJacobian implements Jacobian {
	private double[][] jacobian;
	private Collection<SignalDataPoint> dataPoints;

	//ln(10) is used OFTEN! make sure we calculate it once.
	private final static double ln10 = Math.log(10);

	/**
	 * Initialize a Jacobian matrix tailored specifically for solving the problem in this app.
	 * @param dataPoints Datapoints to compute the Jacobian of.
	 */
	public ThompsonJacobian(Collection<SignalDataPoint> dataPoints) {
		jacobian = new double[dataPoints.size()][3]; //The paper specifies the unknowns x, y and n, in that order.
		this.dataPoints = dataPoints;
	}

	@Override
	public double get (int row, int col) throws IllegalArgumentException {
		if(row>=jacobian.length || col>=3) throw new IllegalArgumentException("Tried to access element in Jacobian out of bounds!");
		return jacobian[row][col];
	}

	@Override
	public int getRowSize () {
		return jacobian.length;
	}

	@Override
	public int getColSize () {
		return 3;
	}

	@Override
	public void setNewEstimates (double[] estimates) {
		SignalDataPoint curPoint;
		Iterator<SignalDataPoint> iterator = dataPoints.iterator();
		int row=0;
		while(iterator.hasNext()) {
			curPoint = iterator.next();
			/*Recompute the jacobian here! */
			//df/dx:
			jacobian[row][0] = 



			row++;
		}
	}
}
