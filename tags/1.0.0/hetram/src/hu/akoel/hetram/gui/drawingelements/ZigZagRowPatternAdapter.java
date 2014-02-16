package hu.akoel.hetram.gui.drawingelements;

import hu.akoel.hetram.accessories.Displacement;
import hu.akoel.hetram.gui.ElementSettingTab.ROW_PATTERN;

import java.awt.Graphics2D;

public class ZigZagRowPatternAdapter implements RowPatternInterface{

	@Override
	public double getHeightPerWidth() {
		return 2;
	}

	@Override
	public void drawPattern(Graphics2D g2, Displacement orientation, int shift, int patternWidth, int patternHeight) {

		if( orientation.equals( Displacement.HORIZONTAL ) ){
			
			g2.drawLine( 0, shift, patternWidth / 2, patternHeight + shift );
			g2.drawLine( patternWidth, shift, patternWidth / 2, patternHeight + shift );	
			
			g2.drawLine( 0, shift-patternHeight, patternWidth / 2, shift );
			g2.drawLine( patternWidth, shift-patternHeight, patternWidth / 2, shift );
			
		}else if( orientation.equals( Displacement.VERTICAL ) ){
			
			g2.drawLine( shift, 0, patternWidth + shift, patternHeight / 2 );
			g2.drawLine( shift, patternHeight, patternWidth + shift, patternHeight / 2 );

			g2.drawLine( shift - patternWidth, 0, shift, patternHeight / 2 );
			g2.drawLine( shift - patternWidth, patternHeight, shift, patternHeight / 2 );
			
		}
		
	}

	@Override
	public ROW_PATTERN getForm() {		
		return ROW_PATTERN.ZIGZAG;
	}

}
