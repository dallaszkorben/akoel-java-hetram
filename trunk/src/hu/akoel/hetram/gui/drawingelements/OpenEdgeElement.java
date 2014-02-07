package hu.akoel.hetram.gui.drawingelements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import hu.akoel.mgu.drawnblock.DrawnBlock;

public class OpenEdgeElement extends DrawnBlock{

	private static final long serialVersionUID = -1063105162303471067L;
	
	private static final Stroke NORMAL_STROKE = new BasicStroke(3);
	
	private static final Color SELECTED_COLOR = Color.red;
	private static final Stroke SELECTED_STROKE = new BasicStroke(3);
	
	private static final Color INFOCUS_COLOR = Color.yellow;
	private static final Stroke INFOCUS_STROKE = new BasicStroke(3);
	
	private static final Stroke INPROCESS_STROKE = new BasicStroke(5);
	
	private double alphaBegin;
	private double alphaEnd;
	private double temperature;

	private OpenEdgeElement(Status status, double x1, double y1,
			java.lang.Double minLength, java.lang.Double maxLength,
			java.lang.Double minWidth, java.lang.Double maxWidth) {
		super(status, x1, y1, minLength, maxLength, minWidth, maxWidth);
	}

	private OpenEdgeElement( Status status, double x1, double y1 ){
		super( status, x1, y1 );
	}
	
	public OpenEdgeElement( Status status, double x1, double y1, java.lang.Double minLength, java.lang.Double maxLength, java.lang.Double minWidth, java.lang.Double maxWidth, double alphaBegin, double alphaEnd, double temperature, Color color ){
		super( status, x1, y1, minLength, maxLength, minWidth, maxWidth );
		
		this.alphaBegin = alphaBegin;
		this.alphaEnd = alphaEnd;
		this.temperature = temperature;
		
		setNormal( color, NORMAL_STROKE, color );
		setSelected( SELECTED_COLOR, SELECTED_STROKE, color );
		setInfocus(INFOCUS_COLOR, INFOCUS_STROKE, color );
		setInprocess( color, INPROCESS_STROKE, color );
		
		refreshStatus();
		
	}

	public double getAlphaStart() {
		return alphaBegin;
	}

	public double getAlphaEnd() {
		return alphaEnd;
	}

	public double getTemperature() {
		return temperature;
	}
	
}
