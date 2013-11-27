package hu.akoel.hetram;

import hu.akoel.hetram.accessories.Position;

import java.util.HashSet;

public class Element {
	
	public static enum SideOrientation{
		NORTH,
		EAST,
		SOUTH,
		WEST
	}
	
	private double lambda;
	private Position startPosition;
	private Position endPosition;
	private HashSet<CloseElement> closeElementSet = new HashSet<>();
	
	public Element( double lambda, Position startPosition, Position endPosition ){
		this.setLambda(lambda);
		this.startPosition = new Position( startPosition );
		this.endPosition = new Position( endPosition );
	}

	public void setCloseElement( CloseElement closeElement ){
		closeElementSet.add( closeElement );
	}
	
	public HashSet<CloseElement> getCloseElements(){
		return closeElementSet;
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
	
	public String toString(){
		return "λ: " + lambda + " (" + startPosition + " " + endPosition + ")";
	}
}
