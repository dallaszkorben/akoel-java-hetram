package hu.akoel.hetram.gui.drawingelements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.math.BigDecimal;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OpenEdgeElement extends HetramDrawnElement{
	
	private static final Stroke NORMAL_STROKE = new BasicStroke(3);
	
	private static final Color SELECTED_COLOR = Color.red;
	private static final Stroke SELECTED_STROKE = new BasicStroke(3);
	
	private static final Color INFOCUS_COLOR = Color.yellow;
	private static final Stroke INFOCUS_STROKE = new BasicStroke(3);
	
	private static final Stroke INPROCESS_STROKE = new BasicStroke(5);
	
	private double alphaStart;
	private double alphaStop;
	private double temperature;

	@Override
	public TYPE getType() {
		return TYPE.OPEN_EDGE;
	}
	
	private OpenEdgeElement(Status status, BigDecimal x1, BigDecimal y1, BigDecimal minLength, BigDecimal maxLength, BigDecimal minWidth, BigDecimal maxWidth) {
		super(status, x1, y1, minLength, maxLength, minWidth, maxWidth );
	}

	private OpenEdgeElement( Status status, BigDecimal x1, BigDecimal y1 ){
		super( status, x1, y1 );
	}
	
	public OpenEdgeElement( Status status, BigDecimal x1, BigDecimal y1, BigDecimal minLength, BigDecimal maxLength, BigDecimal minWidth, BigDecimal maxWidth, double alphaStart, double alphaStop, double temperature, Color color ){
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
		if( getWidth().compareTo(new BigDecimal("0")) != 0 ){
//		if( getWidth() != 0 ){			
//TODO figyelem BigDecimal double valtas			
			return ( pos - getStartX().doubleValue() ) / ( getStopX().doubleValue() - getStartX().doubleValue() ) * ( this.alphaStop - this.alphaStart ) + this.alphaStart;
//			return ( pos - getStartX() ) / ( getStopX() - getStartX() ) * ( this.alphaStop - this.alphaStart ) + this.alphaStart;
			
			
		//Fuggoleges
		}else if( getHeight().compareTo(new BigDecimal("0")) != 0 ){			
		//}else if( getHeight() != 0 ){
			
			return ( pos - getStartY().doubleValue() ) / ( getStopY().doubleValue() - getStartY().doubleValue() ) * ( this.alphaStop - this.alphaStart ) + this.alphaStart;
			//return ( pos - getStartY() ) / ( getStopY() - getStartY() ) * ( this.alphaStop - this.alphaStart ) + this.alphaStart;			
			
		//Ez nem lehet
		}else{
			
			throw new Error("Gaz van, ez nem lehet. Egy OpenEdgeElement objektum-nak nincs kiterjedese");
		}
	}
	
	public Element getXMLElement( Document document ){
		Element element = super.getXMLElement(document);
		Attr attr;
		
		//TYPE
		attr = document.createAttribute("type");
		attr.setValue( getType().name() ) ;
		element.setAttributeNode( attr );

		//EXTRAINFO
		Element extraInfoElement = document.createElement( "extrainfo" );
		element.appendChild( extraInfoElement );

		//EXTRAINFO - alphastart
		Element alphaStartElement = document.createElement( "alphastart" );		
		alphaStartElement.appendChild(document.createTextNode( Double.toString( this.alphaStart ) ) );
		extraInfoElement.appendChild( alphaStartElement );
		
		//EXTRAINFO - alphastop
		Element alphaStopElement = document.createElement( "alphastop" );		
		alphaStopElement.appendChild(document.createTextNode( Double.toString( this.alphaStop ) ) );
		extraInfoElement.appendChild( alphaStopElement );
		
		//EXTRAINFO - temperature
		Element temperatureElement = document.createElement( "temperature" );		
		temperatureElement.appendChild(document.createTextNode( Double.toString( this.temperature ) ) );
		extraInfoElement.appendChild( temperatureElement );

		return element;		
	}


}
