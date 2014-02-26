package hu.akoel.hetram;

import java.math.BigDecimal;

import hu.akoel.hetram.gui.drawingelements.HetramDrawnElement;
import hu.akoel.mgu.drawnblock.DrawnBlockFactory;
import hu.akoel.mgu.drawnblock.DrawnBlock.Status;

public interface HetramDrawnElementFactory extends DrawnBlockFactory{

	public HetramDrawnElement getNewDrawnBlock( Status status, BigDecimal x1, BigDecimal y1 );
	
}
