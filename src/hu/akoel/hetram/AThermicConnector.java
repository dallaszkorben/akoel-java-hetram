package hu.akoel.hetram;

public class AThermicConnector implements ThermicConnector{

	private double alfa;
	private double airTemperature;
	
	public AThermicConnector( double alfa, double airTemperature ){
		this.alfa = alfa;
		this.airTemperature = airTemperature;
	}
	
	public double getAlpha() {
		return alfa;
	}
	public void setAlfa(double alfa) {
		this.alfa = alfa;
	}
	public double getAirTemperature() {
		return airTemperature;
	}
	public void setTemperature(double temperature) {
		this.airTemperature = temperature;
	}
}
