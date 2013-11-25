package hu.akoel.hetram;

public class ThermicSurface {

	private double alpha;
	private double airTemperature;
	
	public ThermicSurface( double alpha, double airTemperature ){
		this.alpha = alpha;
		this.airTemperature = airTemperature;
	}

	public double getAlpha() {
		return alpha;
	}

	public double getAirTemperature() {
		return airTemperature;
	}
}
