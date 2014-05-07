package hu.akoel.hetram.gui.drawingelements;

import hu.akoel.mgu.drawnblock.DrawnBlockCanvas.Precision;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.math.BigDecimal;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * q=0
 * Masodfaju hotani peremfeltetel
 * 
 * @author akoel
 *
 */
public class SymmetricEdgeElement extends HetramDrawnElement{
	
	private static final Stroke NORMAL_STROKE = new BasicStroke(3);
	
	private static final Color SELECTED_COLOR = new Color(255,0,255);;
	private static final Stroke SELECTED_STROKE = new BasicStroke(3);
	
	private static final Color INFOCUS_COLOR = Color.yellow;
	private static final Stroke INFOCUS_STROKE = new BasicStroke(3);
	
	private static final Stroke INPROCESS_STROKE = new BasicStroke(5);

	@Override
	public TYPE getType() {
		return TYPE.EDGE_SYMMETRIC;
	}
	
	public SymmetricEdgeElement( Status status, BigDecimal x1, BigDecimal y1, BigDecimal minLength, BigDecimal maxLength, BigDecimal minWidth, BigDecimal maxWidth, Color color ){
		super( status, x1, y1, minLength, maxLength, minWidth, maxWidth );
		
		setNormal( color, NORMAL_STROKE, color );
		setSelected( SELECTED_COLOR, SELECTED_STROKE, color );
		setInfocus(INFOCUS_COLOR, INFOCUS_STROKE, color );
		setInprocess( color, INPROCESS_STROKE, color );
		
		refreshStatus();
		
	}

	public SymmetricEdgeElement( Precision precision, Element xmlElement ){
		super( precision, xmlElement );
		
	}
	
	public Element getXMLElement( Document document ){
		Element element = super.getXMLElement(document);
		Attr attr;
		
		attr = document.createAttribute("type");
		attr.setValue( getType().name() ) ;
		element.setAttributeNode( attr );

		return element;		
	}

}
