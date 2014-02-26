package hu.akoel.mgu.drawnblock;

import java.math.BigDecimal;

import hu.akoel.mgu.drawnblock.DrawnBlock.Status;

public interface DrawnBlockFactory {

	public DrawnBlock getNewDrawnBlock( Status status, BigDecimal x1, BigDecimal y1 );
	
}
