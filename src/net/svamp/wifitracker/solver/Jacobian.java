package net.svamp.wifitracker.solver;

/**
 * The jacobian matrix of a given problem. Since the jacobian conatins
 * the derivatives of a given function in multiple dimensions, the
 * implementation is problem-specific. But, for extensibility,
 * all classes using the jacobian, should implement this interface.
 */
public interface Jacobian {

	/**
	 * Fetch an element in the jacobian matrix. Bounds checking is up to the implementer.
	 * @param row
	 * @param col
	 */
	double get(int row,int col);

	/**
	 * @return The number of rows contained in the jacobian
	 */
	int getRowSize();

	/**
	 * @return The number of columns contained in the jacobian.
	 */
	int getColSize();

	/**
	 * Sets new estimates for the variables in the jacobian, forcing it to recalculate all its derivatives.
	 * REMEMBER: This method must be run at least once prior to use!
	 * @param estimates New estimates for all the variables in the jacobian. The size of this parameter must be exactly the same as the number returned by getColSize().
	 */
	void setNewEstimates(double[] estimates);

}
