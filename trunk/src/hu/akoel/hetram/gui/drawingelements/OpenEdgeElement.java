package hu.akoel.hetram.gui.drawingelements;

import hu.akoel.hetram.accessories.Orientation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

public class OpenEdgeElement extends HetramDrawnElement{

	private static final long serialVersionUID = -1063105162303471067L;
	
	private static final Stroke NORMAL_STROKE = new BasicStroke(3);
	
	private static final Color SELECTED_COLOR = Color.red;
	private static final Stroke SELECTED_STROKE = new BasicStroke(3);
	
	private static final Color INFOCUS_COLOR = Color.yellow;
	private static final Stroke INFOCUS_STROKE = new BasicStroke(3);
	
	private static final Stroke INPROCESS_STROKE = new BasicStroke(5);
	
	private double alphaStart;
	private double alphaStop;
	private double temperature;
	
	private Orientation orientation;

	private OpenEdgeElement(Status status, double x1, double y1, java.lang.Double minLength, java.lang.Double maxLength, java.lang.Double minWidth, java.lang.Double maxWidth) {
		super(status, x1, y1, minLength, maxLength, minWidth, maxWidth );
	}

	private OpenEdgeElement( Status status, double x1, double y1 ){
		super( status, x1, y1 );
	}
	
	public OpenEdgeElement( Status status, double x1, double y1, java.lang.Double minLength, java.lang.Double maxLength, java.lang.Double minWidth, java.lang.Double maxWidth, double alphaStart, double alphaStop, double temperature, Color color ){
		super( status, x1, y1, minLength, maxLength, minWidth, maxWidth );
		
		this.alphaStart = alphaStart;
		this.alphaStop = alphaStop;
		this.temperature = temperature;
		
		setNormal( color, NORMAL_STROKE, color );
		setSelected( SELECTED_COLOR, SELECTED_STROKE, color );
		setInfocus(INFOCUS_COLOR, INFOCUS_STROKE, color );
		setInprocess( color, INPROCESS_STROKE, color );
		
		refreshStatus();
		
	}

	public double getAlphaStart() {
		return alphaStart;
	}

	public double getAlphaStop() {
		return alphaStop;
	}

	public double getTemperature() {
		return temperature;
	}
	
	public double getAlphaByPosition( double pos ){
		
		//Vizszintesen nyulik el
		if( getWidth() != 0 ){
			
			return ( pos - getStartX() ) / ( getStopX() - getStartX() ) * ( this.alphaStop - this.alphaStart ) + this.alphaStart;
			
			
		//Fuggoleges
		}else if( getHeight() != 0 ){
			
			return ( pos - getStartY() ) / ( getStopY() - getStartY() ) * ( this.alphaStop - this.alphaStart ) + this.alphaStart;
			
		//Ez nem lehet
		}else{
			
			throw new Error("Gaz van, ez nem lehet. Egy OpenEdgeElement objektum-nak nincs kiterjedese");
		}
	}
}
