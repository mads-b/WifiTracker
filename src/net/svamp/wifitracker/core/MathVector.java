package net.svamp.wifitracker.core;

public class MathVector implements Comparable<MathVector>{
	public double x=0;
	public double y=0;
	public double z=0;
	
	public MathVector(double xLen, double yLen) {
		this.x=xLen;
		this.y=yLen;		
	}
	public MathVector(double xLen, double yLen, double zLen) {
		this.x=xLen;
		this.y=yLen;		
		this.z=zLen;	
	}
	
	public double getLenSq() { 
		return Math.pow(x, 2)+Math.pow(y, 2)+Math.pow(z, 2); 
	}
	public double getLen() {
		return Math.sqrt(getLenSq());
	}
	public void invert() {
		x=-x;
		y=-y;
		z=-z;
	}
	
	public int compareTo(MathVector arg0) {
		return (int) (arg0.z - this.z);
		
	}
}
 
