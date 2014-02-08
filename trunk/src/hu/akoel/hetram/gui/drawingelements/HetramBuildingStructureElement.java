package hu.akoel.hetram.gui.drawingelements;

import java.awt.Color;


public abstract class HetramBuildingStructureElement extends HetramDrawnElement{

	private static final long serialVersionUID = 4924553926760978507L;
	
	private Color lineColor;
	private Color backgroundColor;
	private double lambda;

	private HetramBuildingStructureElement( Status status, double x1, double y1 ){
		super( status, x1, y1 );
	}	
	
	public HetramBuildingStructureElement(Status status, double x1, double y1, java.lang.Double minLength, java.lang.Double maxLength, java.lang.Double minWidth, java.lang.Double maxWidth, double lambda, Color lineColor, Color backgroundColor ) {
		super(status, x1, y1, minLength, maxLength, minWidth, maxWidth );
		
		this.lambda = lambda;
		this.lineColor = lineColor;
		this.backgroundColor = backgroundColor;

	}

	public HetramBuildingStructureElement( Status status, double x1, double y1, double lambda, Color lineColor, Color backgroundColor ){
		super( status, x1, y1 );
		
		this.lambda = lambda;
		this.lineColor = lineColor;
		this.backgroundColor = backgroundColor;

	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	public Color getLineColor() {
		return lineColor;
	}

	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
}
