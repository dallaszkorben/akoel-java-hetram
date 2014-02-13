package hu.akoel.hetram.gui.drawingelements;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class HomogeneousPatternBuildingStructuralElement extends HetramBuildingStructureElement{

	private HomogeneousPatternInterface homogeneousPatternInterface;
	
	@Override
	public TYPE getType() {
		return TYPE.BUILDINGSTRUCTURE_HOMOGENEOUSPATTERN;
	}
	
	private HomogeneousPatternBuildingStructuralElement(Status status, BigDecimal x1, BigDecimal y1, BigDecimal minLength, BigDecimal maxLength, BigDecimal minWidth, BigDecimal maxWidth, double lambda, Color lineColor, Color backgroundColor) {
		super(status, x1, y1, minLength, maxLength, minWidth, maxWidth, lambda, lineColor, backgroundColor );
	}
	
	public HomogeneousPatternBuildingStructuralElement(HomogeneousPatternInterface homogeneousPatternInterface, Status status, BigDecimal x1, BigDecimal y1, double lambda, Color lineColor, Color backgroundColor ) {
		super( status, x1, y1, lambda, lineColor, backgroundColor );
		
		this.homogeneousPatternInterface = homogeneousPatternInterface;
		
		int patternWidth = homogeneousPatternInterface.getPatternWidth();
		int patternHeight = homogeneousPatternInterface.getPatternHeight();
		
		TexturePaint normalTexturePaint;
		TexturePaint selectedTexturePaint;
		TexturePaint infocusTexturePaint;
		TexturePaint inprocessTexturePaint;

		Rectangle r = new Rectangle( 0, 0, patternWidth, patternHeight );
	
		//
		//Normal
		//
		BufferedImage bi1 = new BufferedImage( patternWidth, patternHeight, BufferedImage.TYPE_INT_RGB); 
		Graphics2D big1 = bi1.createGraphics();
		big1.setColor( getBackgroundColor() );
		big1.fillRect( 0, 0, patternWidth, patternHeight );
		big1.setColor( getColor() ); 
		homogeneousPatternInterface.drawPattern(big1, patternWidth, patternHeight);
		normalTexturePaint = new TexturePaint( bi1,r ); 
	
		//
		// Selected
		//
		BufferedImage bi2 = new BufferedImage( patternWidth, patternHeight, BufferedImage.TYPE_INT_RGB); 
		Graphics2D big2 = bi2.createGraphics();
		big2.setColor( getSelectedBackgroundColor() );
		big2.fillRect( 0, 0, patternWidth, patternHeight );
		big2.setColor( getSelectedColor() ); 
		homogeneousPatternInterface.drawPattern(big2, patternWidth, patternHeight);
		selectedTexturePaint = new TexturePaint( bi2,r ); 
	
		//
		// Infocus
		//
		BufferedImage bi3 = new BufferedImage( patternWidth, patternHeight, BufferedImage.TYPE_INT_RGB); 
		Graphics2D big3 = bi3.createGraphics();
		big3.setColor( getInfocusBackgroundColor() );
		big3.fillRect( 0, 0, patternWidth, patternHeight );
		big3.setColor( getInfocusColor() ); 
		homogeneousPatternInterface.drawPattern(big3, patternWidth, patternHeight);
		infocusTexturePaint = new TexturePaint( bi3,r ); 
	
		//
		// Inprocess
		//
		BufferedImage bi4 = new BufferedImage( patternWidth, patternHeight, BufferedImage.TYPE_INT_RGB); 
		Graphics2D big4 = bi4.createGraphics();
		big4.setColor( getInprocessBackgroundColor() );
		big4.fillRect( 0, 0, patternWidth, patternHeight );
		big4.setColor( getInprocessColor() ); 
		homogeneousPatternInterface.drawPattern(big4, patternWidth, patternHeight);		 
		inprocessTexturePaint = new TexturePaint( bi4,r ); 
	
		setNormalTexturalPaint( normalTexturePaint );
		setSelectedTexturalPaint( selectedTexturePaint );
		setInfocusTexturalPaint( infocusTexturePaint );
		setInprocessTexturalPaint( inprocessTexturePaint );
		
		refreshStatus();
		
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
		Node nNode = nl.item(0);
//		if (nNode.getNodeType() == Node.ELEMENT_NODE) {	
		Element eElement = (Element) nNode;
		Element rawTypeElement = document.createElement( "homogenoustype" );		
		rawTypeElement.appendChild(document.createTextNode( this.homogeneousPatternInterface.getType().name() ) );
		eElement.appendChild( rawTypeElement );
			
		
		return element;		
	}
	
}
