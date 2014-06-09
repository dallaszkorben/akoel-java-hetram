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

import hu.akoel.hetram.accessories.Displacement;
import hu.akoel.hetram.gui.MainPanel;
import hu.akoel.mgu.MGraphics;
import hu.akoel.mgu.drawnblock.DrawnBlockCanvas.Precision;

public class RowPatternBuildingStructuralElement extends HetramBuildingStructureElement{
		
	private RowPatternInterface rowPatternInterface;
	private MainPanel mainPanel;
	
	@Override
	public TYPE getType() {
		return TYPE.BUILDINGSTRUCTURE_ROWPATTERN;
	}
	
	
	public RowPatternBuildingStructuralElement(RowPatternFactory rowPatternFactory, MainPanel mainPanel, Status status, BigDecimal x1, BigDecimal y1, BigDecimal minLength, BigDecimal maxLength, BigDecimal minWidth, BigDecimal maxWidth, double lambda, Color lineColor, Color backgroundColor ) {
		super(status, x1, y1, minLength, maxLength, minWidth, maxWidth, lambda, lineColor, backgroundColor );	
		
		commonConstructor(rowPatternFactory, mainPanel);
	}

	public RowPatternBuildingStructuralElement( RowPatternFactory rowPatternFactory, MainPanel mainPanel, Status status, BigDecimal x1, BigDecimal y1, double lambda, Color lineColor, Color backgroundColor ) {
		super( status, x1, y1, lambda, lineColor, backgroundColor );	
		
		commonConstructor(rowPatternFactory, mainPanel);
	}
	
	public RowPatternBuildingStructuralElement( Precision precision, Element xmlElement, RowPatternFactory rowPatternFactory, MainPanel mainPanel ){
		super( precision, xmlElement );
		
		commonConstructor(rowPatternFactory, mainPanel);		
		
	}
	
	private void commonConstructor( RowPatternFactory rowPatternFactory, MainPanel mainPanel ){
		this.rowPatternInterface = rowPatternFactory.getRowPattern();
		this.mainPanel = mainPanel;			
		
	}
	

	public void draw( MGraphics g2 ){

		Displacement displacement; 
		
		int patternWidth;
		int patternHeight;
	
		TexturePaint texturePaint = null;	

		//Szelesebb mint magas
		if( getWidth().compareTo(getHeight() ) > 0 ){
//		if( getWidth() > getHeight() ){
			displacement = Displacement.HORIZONTAL;
//TODO figyelem BigDecimal to double			
			patternHeight = mainPanel.getCanvas().getPixelYLengthByWorld( getHeight().doubleValue() ) ;
			patternWidth = (int)(patternHeight / rowPatternInterface.getHeightPerWidth());
			
		//Magasabb mint szeles
		}else{
			displacement = Displacement.VERTICAL;
//TODO figyelem BigDecimal to double				
			patternWidth = mainPanel.getCanvas().getPixelXLengthByWorld( getWidth().doubleValue() );
			patternHeight = (int)(patternWidth / rowPatternInterface.getHeightPerWidth() );
			
		}
	
		//Ha nincs kiterjedese valamelyik iranyba, akkor nem lehet BufferedImage objektumot szerezni
		//De mindegy is, mert akkor nem rajzolunk kitoltest, hiszen csak szel van
		if( patternWidth > 0 && patternHeight > 0 ){		

			BufferedImage bi = new BufferedImage( patternWidth, patternHeight, BufferedImage.TYPE_INT_RGB); 
			Graphics2D gr2d = bi.createGraphics();
			gr2d.setColor( getBackgroundColor() );
			gr2d.fillRect( 0, 0, patternWidth, patternHeight );
			gr2d.setColor( getColor() ); 	
					
			int shift;
			
			//Szelesebb mint magas
			if( displacement.equals( Displacement.HORIZONTAL ) ){
//TODO figyelem BigDecimal to double	
				int pos = mainPanel.getCanvas().getPixelYPositionByWorldBeforeTranslate( getY1().doubleValue() );
				shift = pos - ( (int)(pos / patternHeight ) ) * patternHeight;
			
				if( pos < 0 ){
				
					gr2d.translate(0, patternHeight);
					
				}
			
			//Magasabb mint szeles
			}else{
//TODO figyelem BigDecimal to double				
				int pos = mainPanel.getCanvas().getPixelXPositionByWorldBeforeTranslate( getX1().doubleValue() );
				shift = pos - ( (int)(pos / patternWidth ) ) * patternWidth;
			
				if( pos < 0 ){
					gr2d.translate( patternWidth, 0 );
					
				}		
			}
			
			//A Pattern interface felelos a pattern kirajzolasaert
			gr2d.setStroke( getStroke() );
			rowPatternInterface.drawPattern(gr2d, displacement, shift, patternWidth, patternHeight);
			
			Rectangle r = new Rectangle( 0, 0, patternWidth, patternHeight );			
			texturePaint = new TexturePaint( bi, r );
			
			//nincs refreshStatus() !!! mert csak a texturePaint valtozot allitom
			setTexturalPaint(texturePaint);
			
		}
		
		super.draw(g2);

	}	
	
	public Element getXMLElement( Document document ){
		Element element = super.getXMLElement(document);
		Attr attr;
		
		//TYPE
		attr = document.createAttribute("type");
		attr.setValue( getType().name() ) ;
		element.setAttributeNode( attr );

		//FORM
		attr = document.createAttribute("form");
		attr.setValue( this.rowPatternInterface.getForm().name() ) ;
		element.setAttributeNode( attr );

		
/*		//EXTRAINFO
		NodeList nl = element.getElementsByTagName("extrainfo");
		
		//for (int temp = 0; temp < nl.getLength(); temp++) {
	
		//EXTRAINFO - ROWTYPE		
		Node nNode = nl.item(0);
//		if (nNode.getNodeType() == Node.ELEMENT_NODE) {	
		Element eElement = (Element) nNode;
		Element rawTypeElement = document.createElement( "rawtype" );		
		rawTypeElement.appendChild(document.createTextNode( this.rowPatternInterface.getType().name() ) );
		eElement.appendChild( rawTypeElement );
*/			
		
		return element;		
	}

	public RowPatternInterface getRowPatternInterface(){
		return rowPatternInterface;
	}
}












