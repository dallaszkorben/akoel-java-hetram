package hu.akoel.hetram;

public class YDThermicConnector extends DThermicConnector {

	private ThermicPoint northThermicPoint;
	private ThermicPoint southThermicPoint;
	
	public YDThermicConnector( ThermicPoint northThermicPoint, ThermicPoint southThermicPoint, double lambda) {
		super( Math.abs( northThermicPoint.getPosition().getY() - southThermicPoint.getPosition().getY() ), lambda );
		
		this.northThermicPoint = northThermicPoint;
		this.southThermicPoint = southThermicPoint;
		
		if( northThermicPoint.getPosition().getY() < southThermicPoint.getPosition().getY() ){
			setLoop( true );
		}
	}

	public ThermicPoint getNorthThermicPoint() {
		return northThermicPoint;
	}

	public ThermicPoint getSouthThermicPoint() {
		return southThermicPoint;
	}
	
}
