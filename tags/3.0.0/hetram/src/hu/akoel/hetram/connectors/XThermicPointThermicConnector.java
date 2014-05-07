package hu.akoel.hetram.connectors;

import hu.akoel.hetram.thermicpoint.ThermicPoint;

/**
 * Egy masik Termikus Pont-hoz való Horizontalis csatlakozast biztosito konnektor
 * @author akoel
 *
 */
public class XThermicPointThermicConnector extends AThermicPointThermicConnector{
	
	private ThermicPoint westThermicPoint;
	private ThermicPoint eastThermicPoint;
	
	public XThermicPointThermicConnector( ThermicPoint westThermicPoint, ThermicPoint eastThermicPoint, double lambda) {
		//super( westThermicPoint.getPosition().getX().subtract(eastThermicPoint.getPosition().getX()).abs(), lambda );
		super( lambda );
		//super( Math.abs( westThermicPoint.getPosition().getX() - eastThermicPoint.getPosition().getX() ), lambda );
		
		this.westThermicPoint = westThermicPoint;
		this.eastThermicPoint = eastThermicPoint;
		
	}

	public ThermicPoint getWestThermicPoint() {
		return westThermicPoint;
	}

	public ThermicPoint getEastThermicPoint() {
		return eastThermicPoint;
	}
	
}
