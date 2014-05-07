package hu.akoel.mgu.values;

public class SizeValue {

	private double xMin;
	private double yMin;
	private double xMax;
	private double yMax;
	
	public SizeValue( double xMin, double yMin, double xMax, double yMax ){
		this.xMin = xMin;
		this.yMin = yMin;
		this.xMax = xMax;
		this.yMax = yMax;
	}
	
	public void setXMin( double xMin ){
		this.xMin = xMin;
	}
	
	public void setXMax( double xMax ){
		this.xMax = xMax;
	}
	
	public void setYMin( double yMin ){
		this.yMin = yMin;
	}
	
	public void setYMax( double yMax ){
		this.yMax = yMax;
	}
	
	public double getWidth() {
		return xMax - xMin;
	}

	public double getHeight() {
		return yMax - yMin;
	}
	
	public double getXMin(){
		return xMin;
	}
	
	public double getYMin(){
		return yMin;
	}
	
	public double getXMax(){
		return xMax;
	}
	
	public double getYMax(){
		return yMax;
	}

	public String toString(){
		return xMin + ", " + yMin + ", " + xMax + ", " + yMax;
	}
}
