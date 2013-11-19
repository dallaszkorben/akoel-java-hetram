package hu.akoel.hetram;

public class XDThermicConnector extends DThermicConnector{

	private ThermicPoint westThermicPoint;
	private ThermicPoint eastThermicPoint;
	
	public XDThermicConnector( ThermicPoint westThermicPoint, ThermicPoint eastThermicPoint, double lambda) {
		super( Math.abs( westThermicPoint.getPosition().getX() - eastThermicPoint.getPosition().getX() ), lambda );
		
		this.westThermicPoint = westThermicPoint;
		this.eastThermicPoint = eastThermicPoint;
		
		if( westThermicPoint.getPosition().getX() > eastThermicPoint.getPosition().getX() ){
			setLoop( true );
		}
		
	}

	public ThermicPoint getWestThermicPoint() {
		return westThermicPoint;
	}

	public ThermicPoint getEastThermicPoint() {
		return eastThermicPoint;
	}

	
	
}
