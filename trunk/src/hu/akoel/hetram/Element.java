package hu.akoel.hetram;

public class Element {
	public static enum AlphaOrientation{
		NORTH,
		EAST,
		SOUTH,
		WEST
	}
	
	private double lambda;
	private Position startPosition;
	private Position endPosition;
	private AThermicConnector northThermicConnector = null;
	private AThermicConnector eastThermicConnector = null;
	private AThermicConnector southThermicConnector = null;
	private AThermicConnector westThermicConnector = null;
	
	
	public Element( double lambda, Position startPosition, Position endPosition ){
		this.setLambda(lambda);
		this.startPosition = new Position( startPosition );
		this.endPosition = new Position( endPosition );
	}

	public void setAlpha( AlphaOrientation alphaOrientation, AThermicConnector thermicConnector ){
		if( alphaOrientation.equals( AlphaOrientation.NORTH ) ){
			northThermicConnector = thermicConnector;
		}else if( alphaOrientation.equals( AlphaOrientation.EAST ) ){
			eastThermicConnector = thermicConnector;
		}else if( alphaOrientation.equals( AlphaOrientation.SOUTH ) ){
			southThermicConnector = thermicConnector;			
		}else if( alphaOrientation.equals( AlphaOrientation.WEST ) ){
			westThermicConnector = thermicConnector;
		}
	}	
	
	public AThermicConnector getNorthAThermicConnector() {
		return northThermicConnector;
	}

	public AThermicConnector getEastAThermicConnector() {
		return eastThermicConnector;
	}

	public AThermicConnector getSouthAThermicConnector() {
		return southThermicConnector;
	}

	public AThermicConnector getWestAThermicConnector() {
		return westThermicConnector;
	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	public Position getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(Position startPosition) {
		this.startPosition = startPosition;
	}

	public Position getEndPosition() {
		return endPosition;
	}

	public void setEndPosition(Position endPosition) {
		this.endPosition = endPosition;
	}
}
