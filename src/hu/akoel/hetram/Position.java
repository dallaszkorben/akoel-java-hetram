package hu.akoel.hetram;

import java.text.DecimalFormat;

public class Position {
	private double x;
	private double y;
	private DecimalFormat format = new DecimalFormat("0.00");
	
	public Position( double x, double y ){
		this.x = x;
		this.y = y;
	}
	
	public Position( Position position ){
		this.x = position.x;
		this.y = position.y;
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public String toString(){
		return "[" + format.format( this.x ) + ", " + format.format( this.y ) + "]";
	}
}
