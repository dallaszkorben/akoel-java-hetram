package hu.akoel.hetram.gui.drawingelements;

import java.math.BigDecimal;

import org.w3c.dom.Element;

import hu.akoel.mgu.drawnblock.DrawnBlock;
import hu.akoel.mgu.drawnblock.DrawnBlockCanvas.Precision;

public abstract class HetramDrawnElement extends DrawnBlock{
	
	public static enum TYPE{
		EDGE_SYMMETRIC,
		EDGE_OPEN,
		BUILDINGSTRUCTURE_COLORED,
		BUILDINGSTRUCTURE_HOMOGENEOUSPATTERN,
		BUILDINGSTRUCTURE_ROWPATTERN
	}

	public HetramDrawnElement( Precision precision, Element xmlElement ){
		super( precision, xmlElement );
	}
	
	public HetramDrawnElement(Status status, BigDecimal x1, BigDecimal y1, BigDecimal minLength, BigDecimal maxLength, BigDecimal minWidth, BigDecimal maxWidth) {
		super(status, x1, y1, minLength, maxLength, minWidth, maxWidth);
	}

	public HetramDrawnElement( Status status, BigDecimal x1, BigDecimal y1 ){
		super( status, x1, y1 );
	}

	public abstract TYPE getType();
}
