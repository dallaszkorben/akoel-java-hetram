package hu.akoel.hetram.gui.drawingelements;

import java.awt.Color;
import java.math.BigDecimal;

public abstract class HetramBuildingStructureElement extends HetramDrawnElement{
	
	private Color lineColor;
	private Color backgroundColor;
	private double lambda;

	private HetramBuildingStructureElement( Status status, BigDecimal x1, BigDecimal y1 ){
		super( status, x1, y1 );
	}	
	
	public HetramBuildingStructureElement(Status status, BigDecimal x1, BigDecimal y1, BigDecimal minLength, BigDecimal maxLength, BigDecimal minWidth, BigDecimal maxWidth, double lambda, Color lineColor, Color backgroundColor ) {
		super(status, x1, y1, minLength, maxLength, minWidth, maxWidth );
		
		this.lambda = lambda;
		this.lineColor = lineColor;
		this.backgroundColor = backgroundColor;

	}

	public HetramBuildingStructureElement( Status status, BigDecimal x1, BigDecimal y1, double lambda, Color lineColor, Color backgroundColor ){
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
