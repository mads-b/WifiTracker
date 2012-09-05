package net.svamp.wifitracker.core;

import android.graphics.PointF;

public class Point3D extends PointF {
	public double x=0;
	public double y=0;
	public double z=0;
	public Point3D(double x, double y) {
		this.x=x;
		this.y=y;
	}
	public Point3D(double x, double y,double z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	public Point3D(Point3D p) {
		this.x=p.x;
		this.y=p.y;
		this.z=p.z;
	}
	public String toString() {
		return " x="+x+"\n y="+y+"\n z="+z;
	}
	public static Point3D getCylindrical(int r,double angle,int z) {
		angle=angle+3*Math.PI/2;
		return new Point3D(r*Math.cos(angle),r*Math.sin(angle),z);
	}

	/**
	 * Gets the mean point of the array of points provided
	 * @param coords Points to calculate the mean of
	 * @param useWeighting Decides whether to use the z-axis as point weight
	 * @return (Weighted) mean of points provided.
	 */
	public static Point3D getCentroid(Point3D[] coords, boolean useWeighting) {
		double accX=0;
		double accY=0;
		double accZ=0;

		for(int i=0;i<coords.length;i++) {
			if(useWeighting) {
				accX+=coords[i].x*coords[i].z;
				accY+=coords[i].y*coords[i].z;
				accZ+=coords[i].z;
			}
			else {
				accX+=coords[i].x;
				accY+=coords[i].y;
			}
		}
		if(useWeighting) return new Point3D(accX/accZ,accY/accZ,accZ/coords.length);
		else 			 return new Point3D(accX/coords.length,accY/coords.length);
	}
}
 
