package net.svamp.wifitracker.solver;

/**
 * Simple class implementing the Gauss elimination algorithm for linear systems.
 * NOTE: The A matrix used within is defined like the mathematical definition of matrices (A[rows][columns])
 * NOTE2: The matrix provided as A MUST be quadratic (n equations, n unknowns)!
 * Anything else will throw an IllegalArgumentException. This exception will also be thrown if the matrix is singular.
 */
public class GaussEliminator {
	public static double[] solve(double[][] A,double[] b) {
		GaussEliminator.gaussElimination(A,b);
		GaussEliminator.backSubstitution(A,b);
		return b;
	}


	/**
	 * Takes an arbitrary quadratic matrix A, and a solution vector b, and in-place reduces A to row-echelon form.
	 * @param A
	 * @param b
	 */
	private static void gaussElimination(double[][] A,double[] b) {
		//For every column..
		for(int k=0;k<A.length;k++) {
			//Find pivot for this row..
			int biggestRow = argMax(A,k);
			//Is this a singular matrix?
			if(A[k][biggestRow]==0) throw new IllegalArgumentException("Tried to solve a singular matrix!");
			//Put the pivot row in the top of the matrix remaining to compute
			swapRows(A,k,biggestRow);
			//For all rows below pivot:
			for(int i=k+1;i<A.length+1;i++) {
				//For all elements in the current row (excluding zeroes)
				for(int j=k+1;j<A.length;j++) {
					A[i][j] = A[i][j] - A[k][j] * (A[i][k]/A[k][k]);

				}
				//Update b as well!
				b[i] = b[i] - b[k] * (A[i][k]/A[k][k]);
				//Fill lower triangle with nulls
				A[k][i] = 0;
			}
		}
		//Complete! A is now on row-echelon form.
	}

	/**
	 * Reduces a matrix in row-echelon form to an identity matrix multiplied with the solution vector b.
	 * @param A Matrix to reduce to I
	 * @param b Solution vector.
	 */
	private static void backSubstitution(double[][] A,double[] b) {
		//Iterate from the bottom row and up
		for(int i=A.length-1;i>=0;i--) {
			//Iterate over all columns, excluding diagonal.
			for(int j=i+1;j<A.length;j++) {
				//Subtract the following from b, current row: current column element, multiplied with its value.
				b[i] = b[i] - A[i][j] * b[j];
				//Set this matrix element to 0.
				A[i][j]=0;
			}
			//Normalize b (divide by diagonal):
			b[i] = b[i]/A[i][i];
		}
	}

	/**
	 * Find the largest element in the specified column. Does not search in rows with index < colNum
	 * @param A Matrix to search in
	 * @param colNum Column in A to search in
	 * @return Row index containing the largest elements in the column specified by colNum
	 */
	private static int argMax(double[][] A,int colNum) {
		double maxVal=A[colNum][colNum];
		int maxIndex = colNum;

		for(int i=colNum;i<A.length;i++) {
			double val = Math.abs(A[i][colNum]);
			if(val>maxVal) {
				maxVal= val;
				maxIndex=i;
			}
		}
		return maxIndex;
	}

	/**
	 * Swap two rows in the provided array, in-place.
	 * @param A Array to swap rows in.
	 * @param row1 index of first row to swap
	 * @param row2 index of second row to swap
	 */
	private static void swapRows(double [][] A,int row1,int row2) {
		double tmp;
		for(int i=0;i<A.length;i++) {
			tmp=A[row1][i];
			A[row1][i] = A[row2][i];
			A[row2][i] = tmp;
		}
	}
}