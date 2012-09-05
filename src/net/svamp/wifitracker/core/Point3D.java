package net.svamp.wifitracker.core;

/**
 * Class describing 3D point in space
 */
public class Point3D {
	public double x;
	public double y;
	public double z;

	public Point3D(double x,double y, double z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	public Point3D(double x,double y) {
		this.x=x;
		this.y=y;
	}


	public static Point3D getCylindrical(int r,double angle,int z) {
		angle=angle+3*Math.PI/2;
		return new Point3D(r*Math.cos(angle),r*Math.sin(angle),z);
	}
}
