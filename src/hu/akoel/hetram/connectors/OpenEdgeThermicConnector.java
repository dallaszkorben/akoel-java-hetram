package hu.akoel.hetram.connectors;

/**
 * Szabad feluletet biztosito konnektor
 * 
 * @author akoel
 *
 */
public class OpenEdgeThermicConnector implements IThermicConnector{

	private double alpha;
	private double airTemperature;
	
	public OpenEdgeThermicConnector( double alpha, double airTemperature ){
		this.alpha = alpha;
		this.airTemperature = airTemperature;
	}
	
	public double getAlpha() {
		return alpha;
	}
	public void setAlpha(double alfa) {
		this.alpha = alfa;
	}
	public double getAirTemperature() {
		return airTemperature;
	}
	public void setTemperature(double temperature) {
		this.airTemperature = temperature;
	}
}
