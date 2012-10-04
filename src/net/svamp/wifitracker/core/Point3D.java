package net.svamp.wifitracker.core;

/**
 * Class describing 3D point in space
 */
public class Point3D {
    public final double x;
    public final double y;
    private double z;

    private Point3D (double x, double y, double z) {
        this.x=x;
        this.y=y;
        this.z=z;
    }
    public Point3D(double x,double y) {
        this.x=x;
        this.y=y;
    }


    public static Point3D getCylindrical(double r,double angle,double z) {
        return new Point3D(r*Math.cos(angle),r*Math.sin(angle),z);
    }
}
