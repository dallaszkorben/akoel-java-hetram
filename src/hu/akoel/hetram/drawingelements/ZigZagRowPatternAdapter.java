package hu.akoel.hetram.drawingelements;

import hu.akoel.hetram.drawingelements.RowPatternBuildingStructuralElement.ORIENTATION;

import java.awt.Graphics2D;

public class ZigZagRowPatternAdapter implements RowPatternInterface{

	@Override
	public double getHeightPerWidth() {
		return 2;
	}

	@Override
	public void drawPattern(Graphics2D g2, ORIENTATION orientation, int shift, int patternWidth, int patternHeight) {

		if( orientation.equals( ORIENTATION.HORIZONTAL ) ){
			
			g2.drawLine( 0, shift, patternWidth / 2, patternHeight + shift );
			g2.drawLine( patternWidth, shift, patternWidth / 2, patternHeight + shift );	
			
			g2.drawLine( 0, shift-patternHeight, patternWidth / 2, shift );
			g2.drawLine( patternWidth, shift-patternHeight, patternWidth / 2, shift );
			
		}else if( orientation.equals( ORIENTATION.VERTICAL ) ){
			
			g2.drawLine( shift, 0, patternWidth + shift, patternHeight / 2 );
			g2.drawLine( shift, patternHeight, patternWidth + shift, patternHeight / 2 );

			g2.drawLine( shift - patternWidth, 0, shift, patternHeight / 2 );
			g2.drawLine( shift - patternWidth, patternHeight, shift, patternHeight / 2 );
			
		}
		
	}

}
