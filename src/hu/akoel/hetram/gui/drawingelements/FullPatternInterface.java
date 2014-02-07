package hu.akoel.hetram.gui.drawingelements;

import java.awt.Graphics2D;

public interface FullPatternInterface {

	public int getPatternWidth();
	
	public int getPatternHeight();
	
	public void drawPattern( Graphics2D g2, int patternWidth, int patternHeight );

}
