package net.svamp.wifitracker.solver;

import net.svamp.wifitracker.core.SignalDataPoint;

import java.util.Collection;

/**
 * Alpha implementation of a Gauss-Newton solver for overdetermined nonlinear equations.
 * The Gauss-Newton algorithm (found on Wikipedia), takes a nonlinear function set to be solved, and linearizes it with the equation
 * J^T*J*Delta=J^T*r,where J is the jacobian of r, r is the residual function,
 * and Delta is the convergence direction to add to the current estimate.
 * This algorithm should have an abstract twin, but I'm too lazy to think about reusability now.
 */
public class GaussNewtonSolver {
	private Jacobian J;


	public GaussNewtonSolver(Collection<SignalDataPoint> dataPoints) {
		J = new ThompsonJacobian(dataPoints); //Point Jacobian to data.
	}

	public double[] solve(double[] curEstimate,int iterationNum) {
		//Iterate the provided number of times.
		for(int i=0;i<iterationNum;i++) {
			double[][] leftEq = getJTransposeTimesJ();
			double[] rightEq = getJTransposeTimesResidual();
			curEstimate = GaussEliminator.solve(leftEq,rightEq);
		}
		return curEstimate;
	}


	/**
	 * Calculates J^T*J. This is a symmetrical matrix, so we only compute the upper triangle, and mirror the matrix.
	 */
	private double[][] getJTransposeTimesJ() {
		//If Jacobian is nxm, J^T*J is mxm
		double[][] result = new double[J.getColSize()][J.getColSize()];
		/*Iterate over the upper triangle in the result matrix.
		 */
		for(int col = 0;col<result.length;col++) {
			for(int row = col;row<result.length;row++) {
				//Iterate (sum) the elements required to perform a complete matrix multiplication
				for(int i=0;i<J.getRowSize();i++) {
					result[row][col] += J.get(i,row)*J.get(i,col);
				}
			}
		}
		/* Iterate over the lower triangle and copy values from upper triangle.. This saves some runtime
		 */
		for(int row=1;row<result.length;row++) {
			for(int col=0;col<row;col++) {
				//Value in a_(i,j) is a_(j,i)
				result[row][col] = result[col][row];
			}
		}
		return result;
	}

	private double[] getJTransposeTimesResidual() {
		double[] result = new double[J.getRowSize()];
		//Iterate over all result entries
		for(int i=0;i<result.length;i++) {
			//Iterate over all row entries in jacobian (column entries in its transpose)
			for(int j=0;j<J.getRowSize();j++) {
				result[i] += result[j]*J.get(j,i);
			}
		}
		return result;
	}

}
