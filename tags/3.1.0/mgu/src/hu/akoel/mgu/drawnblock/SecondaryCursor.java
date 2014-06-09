package hu.akoel.mgu.drawnblock;

import java.awt.Graphics2D;
import java.math.BigDecimal;

public interface SecondaryCursor {

	public void setPosition( BigDecimal x, BigDecimal y );
		
	public BigDecimal getX();
		
	public BigDecimal getY();
		
	public int getPixelXPositionByWorldBeforeTranslate( BigDecimal x );
	
	public int getPixelYPositionByWorldBeforeTranslate( BigDecimal y );
	
	public void draw( Graphics2D g2 );
		
}
