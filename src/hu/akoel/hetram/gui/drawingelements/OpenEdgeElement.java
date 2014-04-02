package hu.akoel.hetram.gui.drawingelements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.math.BigDecimal;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * alpha+T
 * Harmadfaju hotani peremfeltetel
 * 
 * @author akoel
 *
 */
public class OpenEdgeElement extends HetramDrawnElement{
	
	private static final Stroke NORMAL_STROKE = new BasicStroke(3);
	
	private static final Color SELECTED_COLOR = new Color(255,125,209);
	private static final Stroke SELECTED_STROKE = new BasicStroke(3);
	
	private static final Color INFOCUS_COLOR = Color.yellow;
	private static final Stroke INFOCUS_STROKE = new BasicStroke(3);
	
	private static final Stroke INPROCESS_STROKE = new BasicStroke(5);
	
	private double alphaStart;
	private double alphaEnd;
	private double temperature;

	@Override
	public TYPE getType() {
		return TYPE.EDGE_OPEN;
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
		this.alphaEnd = alphaStop;
		this.temperature = temperature;
		
		setNormal( color, NORMAL_STROKE, color );
		setSelected( SELECTED_COLOR, SELECTED_STROKE, color );
		setInfocus(INFOCUS_COLOR, INFOCUS_STROKE, color );
		setInprocess( color, INPROCESS_STROKE, color );
		
		refreshStatus();
		
	}

	public OpenEdgeElement( Element xmlElement ){
		super( xmlElement );
		
		NodeList mainNodeList = xmlElement.getChildNodes();
		for (int i = 0; i < mainNodeList.getLength(); i++) {
			
			Node mainNode = mainNodeList.item( i );
			
			if (mainNode.getNodeType() == Node.ELEMENT_NODE) {
				Element mainElement = (Element) mainNode;
				
				//Ha egy APLHASTART elemrol van szo
				if( mainElement.getNodeName().equals( "extrainfo" )){
					
					NodeList extrainfoNodeList = mainElement.getChildNodes();
					for (int j = 0; j < extrainfoNodeList.getLength(); j++) {
						
						Node extrainfoNode = extrainfoNodeList.item( j );
						
						if (extrainfoNode.getNodeType() == Node.ELEMENT_NODE) {
							Element extrainfoElement = (Element) extrainfoNode;

							//Ha egy APLHASTART elemrol van szo
							if( extrainfoElement.getNodeName().equals( "alphastart" )){
								this.alphaStart = Double.valueOf( extrainfoElement.getTextContent() );
							
							//Ha egy APLHASTOP elemrol van szo
							}else if( extrainfoElement.getNodeName().equals( "alphastop" )){
									this.alphaEnd = Double.valueOf( extrainfoElement.getTextContent() );

							//Ha egy TEMPERATURE elemrol van szo
							}else if( extrainfoElement.getNodeName().equals( "temperature" )){
								this.temperature = Double.valueOf( extrainfoElement.getTextContent() );
							}
						}
					} 
				}
			}
		}		
	}
	
	
	

	
	
	public double getAlphaStart() {
		return alphaStart;
	}

	public double getAlphaEnd() {
		return alphaEnd;
	}

	public double getTemperature() {
		return temperature;
	}
	
	public double getAlphaByPosition( double pos ){
		
		//Vizszintesen nyulik el
		if( getWidth().compareTo(new BigDecimal("0")) != 0 ){
//		if( getWidth() != 0 ){			
//TODO figyelem BigDecimal double valtas			
			return ( pos - getStartX().doubleValue() ) / ( getStopX().doubleValue() - getStartX().doubleValue() ) * ( this.alphaEnd - this.alphaStart ) + this.alphaStart;
//			return ( pos - getStartX() ) / ( getStopX() - getStartX() ) * ( this.alphaStop - this.alphaStart ) + this.alphaStart;
			
			
		//Fuggoleges
		}else if( getHeight().compareTo(new BigDecimal("0")) != 0 ){			
		//}else if( getHeight() != 0 ){
			
			return ( pos - getStartY().doubleValue() ) / ( getStopY().doubleValue() - getStartY().doubleValue() ) * ( this.alphaEnd - this.alphaStart ) + this.alphaStart;
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
		alphaStopElement.appendChild(document.createTextNode( Double.toString( this.alphaEnd ) ) );
		extraInfoElement.appendChild( alphaStopElement );
		
		//EXTRAINFO - temperature
		Element temperatureElement = document.createElement( "temperature" );		
		temperatureElement.appendChild(document.createTextNode( Double.toString( this.temperature ) ) );
		extraInfoElement.appendChild( temperatureElement );

		return element;		
	}


}
