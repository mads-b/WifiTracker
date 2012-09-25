package net.svamp.wifitracker.solver;

import net.svamp.wifitracker.core.SignalDataPoint;

import java.util.Collection;
import java.util.Iterator;

/**
 * This class represents the Jacobian matrix specified in the paper Thompson(2009) et.al.
 */
public class ThompsonJacobian implements Jacobian {
	private double[][] jacobian;
	private double[] residuals;
	private Collection<SignalDataPoint> dataPoints;

	//ln(10) is used OFTEN! make sure we calculate it once.
	private final static double ln10 = Math.log(10);

	/**
	 * Initialize a Jacobian matrix tailored specifically for solving the problem in this app.
	 * @param dataPoints Datapoints to compute the Jacobian of.
	 */
	public ThompsonJacobian(Collection<SignalDataPoint> dataPoints) {
		jacobian = new double[dataPoints.size()][3]; //The paper specifies the unknowns x, y and n, in that order.
		residuals = new double[dataPoints.size()];
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

	/**
	 *
	 * @param estimates New estimates for all the variables in the jacobian. The size of this parameter must be exactly the same as the number returned by getColSize(). Format of this parameter is: xEstimate, yEstimate and nEstimate.
	 */
	@Override
	public void setNewEstimates (double[] estimates) {
		//Just giving them better names.
		final double x = estimates[0];
		final double y = estimates[1];
		final double n = estimates[2];

		//Start iterating over the dataset.
		SignalDataPoint curPoint;
		Iterator<SignalDataPoint> iterator = dataPoints.iterator();

		/**
		 * First point is the reference point for now. this point is pretty relevant,
		 * I think, as it's an element in every equation in the jacobian.
		 * TODO: Maybe take the most accurate datapoint as reference point here? What about taking every second point as reference to spread risk?
		 */
		SignalDataPoint firstPoint = iterator.next();
		final double x1 = firstPoint.getCoords().getLon();
		final double y1 = firstPoint.getCoords().getLat();


		//Distance between data point and estimated AP position squared.
		final double r1Sq = (x1-x)*(x1-x)+(y1-y)*(y1-y); // r_1, squared

		int row=1;
		while(iterator.hasNext()) {
			curPoint = iterator.next();
			double xi = curPoint.getCoords().getLon();
			double yi = curPoint.getCoords().getLat();
			double riSq = (xi-x)*(xi-x)+(yi-y)*(yi-y); //r_i, squared.

			/*Recompute the jacobian here! All differentiations are differentiations on the DRSS function in Thompson.*/
			//df/dx:
			jacobian[row][0] = (10*n/ln10)*((x-xi)/riSq - (x-x1)/r1Sq);
			//df/dy:
			jacobian[row][1] = (10*n/ln10)*((y-yi)/riSq - (y-y1)/r1Sq);
			//df/dn:
			jacobian[row][2] = (10/ln10)*Math.log(Math.sqrt(r1Sq/riSq));

			//Compute the residuals for all these DRSS expressions. Formula is the DRSS in Thompson.
			residuals[row] = 10*n*Math.log10(Math.sqrt(riSq/r1Sq))+curPoint.getSignalStrength()-firstPoint.getSignalStrength();


			row++;
		}
	}

	@Override
	public double getResidual (int num) {
		return residuals[num];
	}
}
