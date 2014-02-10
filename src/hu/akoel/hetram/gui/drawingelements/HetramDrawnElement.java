package hu.akoel.hetram.gui.drawingelements;

import java.math.BigDecimal;

import hu.akoel.mgu.drawnblock.DrawnBlock;

public abstract class HetramDrawnElement extends DrawnBlock{
	
	public HetramDrawnElement(Status status, BigDecimal x1, BigDecimal y1, BigDecimal minLength, BigDecimal maxLength, BigDecimal minWidth, BigDecimal maxWidth) {
		super(status, x1, y1, minLength, maxLength, minWidth, maxWidth);

	}

	public HetramDrawnElement( Status status, BigDecimal x1, BigDecimal y1 ){
		super( status, x1, y1 );


	}

}
