package hu.akoel.hetram.connectors;

/**
 * Szabad feluletet biztosito kapcsolat
 * 
 * @author akoel
 *
 */
public class OThermicConnector implements ThermicConnector{

	private double alpha;
	private double airTemperature;
	
	public OThermicConnector( double alpha, double airTemperature ){
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
