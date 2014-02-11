package hu.akoel.hetram.connectors;

import hu.akoel.hetram.thermicpoint.ThermicPoint;

/**
 * Egy masik Termikus Pont-hoz való Vertikalis csatlakozast biztosito konnektor
 * 
 * @author akoel
 *
 */
public class YThermicPointThermicConnector extends AThermicPointThermicConnector {

	private ThermicPoint northThermicPoint;
	private ThermicPoint southThermicPoint;
	
	public YThermicPointThermicConnector( ThermicPoint northThermicPoint, ThermicPoint southThermicPoint, double lambda) {
		super( northThermicPoint.getPosition().getY().subtract( southThermicPoint.getPosition().getY() ).abs(), lambda );
		//super( Math.abs( northThermicPoint.getPosition().getY() - southThermicPoint.getPosition().getY() ), lambda );
		
		this.northThermicPoint = northThermicPoint;
		this.southThermicPoint = southThermicPoint;
		
	}

	public ThermicPoint getNorthThermicPoint() {
		return northThermicPoint;
	}

	public ThermicPoint getSouthThermicPoint() {
		return southThermicPoint;
	}
	
}
