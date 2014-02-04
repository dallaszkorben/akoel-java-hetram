package hu.akoel.hetram.drawingelements;

import java.awt.Color;

import hu.akoel.mgu.drawnblock.DrawnBlock;

public class BuildingStructureElement extends DrawnBlock{

	private static final long serialVersionUID = -8868671968355924643L;

	private double lambda;

	private BuildingStructureElement(Status status, double x1, double y1,
			java.lang.Double minLength, java.lang.Double maxLength,
			java.lang.Double minWidth, java.lang.Double maxWidth) {
		super(status, x1, y1, minLength, maxLength, minWidth, maxWidth);
	}

	private BuildingStructureElement( Status status, double x1, double y1 ){
		super( status, x1, y1 );
	}
	
	public BuildingStructureElement(Status status, double x1, double y1, double lambda, Color color, Color background ) {
		super(status, x1, y1);
		
		this.lambda = lambda;		
		
	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}
	
}
