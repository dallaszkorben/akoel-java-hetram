package hu.akoel.hetram.connectors;

import hu.akoel.hetram.gui.drawingelements.OpenEdgeElement;

/**
 * Szabad feluletet biztosito konnektor
 * 
 * @author akoel
 *
 */
public class OpenEdgeThermicConnector implements IThermicConnector{

	private double alpha;
	private double airTemperature;
	private OpenEdgeElement openEdgeElement;
	
	public OpenEdgeThermicConnector( double alpha, double airTemperature, OpenEdgeElement openEdgeElement ){
		this.alpha = alpha;
		this.airTemperature = airTemperature;
		this.openEdgeElement = openEdgeElement;
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
	public void setAirTemperature(double airTemperature) {
		this.airTemperature = airTemperature;
	}
	
	public OpenEdgeElement getOpenEdgeElement() {
		return openEdgeElement;
	}

	public void setOpenEdgeElement(OpenEdgeElement openEdgeElement) {
		this.openEdgeElement = openEdgeElement;
	}
	
}
