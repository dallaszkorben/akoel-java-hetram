package hu.akoel.hetram.gui.drawingelements;

import java.awt.Color;
import java.math.BigDecimal;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ColoredPatternBuildingSturcturalElement extends HetramBuildingStructureElement{

	public ColoredPatternBuildingSturcturalElement(Status status, BigDecimal x1, BigDecimal y1, BigDecimal minLength, BigDecimal maxLength, BigDecimal minWidth, BigDecimal maxWidth, double lambda, Color lineColor, Color backgroundColor ) {
		super(status, x1, y1, minLength, maxLength, minWidth, maxWidth, lambda, lineColor, backgroundColor );
		
	}
	
	public ColoredPatternBuildingSturcturalElement(Status status, BigDecimal x1, BigDecimal y1, double lambda, Color lineColor, Color backgroundColor ) {
		super( status, x1, y1, lambda, lineColor, backgroundColor );
	
	}

	@Override
	public TYPE getType() {		
		return TYPE.BUILDINSTRUCTURE_COLORED;
	}
	
	public Element getXMLElement( Document document ){
		Element element = super.getXMLElement(document);
		Attr attr;
		
		//TYPE
		attr = document.createAttribute("type");
		attr.setValue( getType().name() ) ;
		element.setAttributeNode( attr );

		//EXTRAINFO
		NodeList nl = element.getElementsByTagName("extrainfo");
		
		//EXTRAINFO - ROWTYPE		
//		Node nNode = nl.item(0);
//		Element eElement = (Element) nNode;
//		Element rawTypeElement = document.createElement( "homogenoustype" );		
//		rawTypeElement.appendChild(document.createTextNode( this.homogeneousPatternInterface.getType().name() ) );
//		eElement.appendChild( rawTypeElement );
			
		
		return element;		
	}
	
}
