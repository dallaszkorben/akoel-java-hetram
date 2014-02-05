package hu.akoel.hetram.drawingelements;

import java.awt.Graphics2D;

public class HatchFullPatternAdapter implements FullPatternInterface{

	@Override
	public void drawPattern(Graphics2D g2, int patternWidth, int patternHeight) {

		g2.drawLine( 0, 0, patternWidth, patternHeight );
		
	}

	@Override
	public int getPatternWidth() {		
		return 15;
	}

	@Override
	public int getPatternHeight() {
		return 15;
	}

}
