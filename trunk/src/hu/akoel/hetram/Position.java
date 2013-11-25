package hu.akoel.hetram;

import java.text.DecimalFormat;

public class Position {
	private double x;
	private double y;
	private DecimalFormat format = new DecimalFormat("0.000");
	
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
	
	@Override
	public String toString(){
		return "[" + format.format( this.x ) + ", " + format.format( this.y ) + "]";
	}
	
	@Override
	public boolean equals(Object o){
		if( (o instanceof Position) ){

			if( ((Position)o).x == this.x && ((Position)o).y == this.y ){
				return true;
			}
		}
		return false;
	}
	
	 @Override
	 public int hashCode() {
		 final int prime = 31;
		 int result = 1;
		 
		 result = result * prime + Double.valueOf(x).hashCode();
		 result = result * prime + Double.valueOf(y).hashCode();
		 
		 return result;
	 }
}
