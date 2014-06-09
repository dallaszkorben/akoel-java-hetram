package hu.akoel.hetram.gui.drawingelements;

import hu.akoel.hetram.gui.tabs.ElementSettingTab.HOMOGENEOUS_PATTERN;

import java.awt.Graphics2D;

public class DotFullPatternAdapter implements HomogeneousPatternInterface{

	@Override
	public void drawPattern(Graphics2D g2, int patternWidth, int patternHeight) {

		int divider = 3;				
		double growth = patternWidth / divider;
		
		for( double i = 0; i < patternWidth; i += growth ){
			
			for( double j = 0; j < patternHeight; j += growth ){

				g2.drawLine( (int)i, (int)j, (int)i, (int)j);

			}
		}
		
//		g2.drawLine( 0, 0, patternWidth, patternHeight );
		
	}

	@Override
	public int getPatternWidth() {		
		return 15;
	}

	@Override
	public int getPatternHeight() {
		return 15;
	}

	@Override
	public HOMOGENEOUS_PATTERN getForm() {		
		return HOMOGENEOUS_PATTERN.DOT;
	}

}
