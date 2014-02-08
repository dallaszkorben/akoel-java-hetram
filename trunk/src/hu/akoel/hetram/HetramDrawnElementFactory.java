package hu.akoel.hetram;

import hu.akoel.hetram.gui.drawingelements.HetramDrawnElement;
import hu.akoel.mgu.drawnblock.DrawnBlockFactory;
import hu.akoel.mgu.drawnblock.DrawnBlock.Status;

public interface HetramDrawnElementFactory extends DrawnBlockFactory{

	public HetramDrawnElement getNewDrawnBlock( Status status, double x1, double y1 );
	
}
