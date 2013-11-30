package hu.akoel.hetram.connectors;

import hu.akoel.hetram.ThermicPoint;

public class XDThermicConnector extends DThermicConnector{
	
	private ThermicPoint westThermicPoint;
	private ThermicPoint eastThermicPoint;
	
	public XDThermicConnector( ThermicPoint westThermicPoint, ThermicPoint eastThermicPoint, double lambda) {
		super( Math.abs( westThermicPoint.getPosition().getX() - eastThermicPoint.getPosition().getX() ), lambda );
		
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
