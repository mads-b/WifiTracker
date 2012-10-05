package net.svamp.wifitracker.solver;

import net.svamp.wifitracker.core.LatLon;
import net.svamp.wifitracker.core.SignalDataPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Alpha implementation of a Gauss-Newton solver for overdetermined nonlinear equations.
 * The Gauss-Newton algorithm (found on Wikipedia), takes a nonlinear function set to be solved, and linearizes it with the equation
 * J^T*J*Delta=J^T*r,where J is the jacobian of r, r is the residual function,
 * and Delta is the convergence direction to add to the current estimate.
 * This algorithm should have an abstract twin, but I'm too lazy to think about reusability now.
 */
public class GaussNewtonSolver {
    private final Jacobian J;


    public GaussNewtonSolver(Collection<SignalDataPoint> dataPoints) {
        J = new ThompsonJacobian(dataPoints); //Point Jacobian to data.
    }

    public double[] solve(double[] curEstimate,int iterationNum) {
        //Iterate the provided number of times.
        for(int i=0;i<iterationNum;i++) {
            //Remember to input next guess!
            J.setNewEstimates(curEstimate);

            double[][] leftEq = getJTransposeTimesJ();
            for(double cols[] : leftEq) {
                System.out.println(Arrays.toString(cols));
            }

            double[] rightEq = getJTransposeTimesResidual();
            double[] newEstimate = GaussEliminator.solve(leftEq,rightEq);

            for(int n=0;n<curEstimate.length;n++) {
                curEstimate[n] += newEstimate[n];
            }
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
        for(int row = 0; row<result.length;row++) {
            for(int col = row ; col<result.length; col++) {
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
        double[] result = new double[J.getColSize()];
        //Iterate over all result entries
        for(int i=0;i<result.length;i++) {
            //Iterate over all row entries in jacobian (column entries in its transpose)
            for(int j=0;j<J.getRowSize();j++) {
                result[i] += J.getResidual(j)*J.get(j,i);
            }
        }
        return result;
    }


    public static void main(String[] args) {
        ArrayList<SignalDataPoint> testPoints = new ArrayList<SignalDataPoint>();
        testPoints.add(new SignalDataPoint(new LatLon(1,1),-5.4));
        testPoints.add(new SignalDataPoint(new LatLon(5,10),-4.5));
        testPoints.add(new SignalDataPoint(new LatLon(2,6),-1));
        testPoints.add(new SignalDataPoint(new LatLon(6,5),-3.2));
        testPoints.add(new SignalDataPoint(new LatLon(3,3),-3));
        testPoints.add(new SignalDataPoint(new LatLon(4,5),-1.4));
        GaussNewtonSolver solver = new GaussNewtonSolver(testPoints);
        double[] curEstimate = {4,4,1.5};
        solver.solve(curEstimate,100);
        System.out.println(Arrays.toString(curEstimate));
    }
}
