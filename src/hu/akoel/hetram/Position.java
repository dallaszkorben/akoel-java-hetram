package hu.akoel.hetram;

public class Position {
	private double x;
	private double y;
	
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
		return "[" + this.x + ", " + this.y + "]";
	}
}
