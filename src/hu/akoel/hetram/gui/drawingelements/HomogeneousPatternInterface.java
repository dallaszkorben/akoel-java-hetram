package hu.akoel.hetram.gui.drawingelements;

import hu.akoel.hetram.gui.ElementSettingTab.HOMOGEN_PATTERNEOUS;

import java.awt.Graphics2D;

public interface HomogeneousPatternInterface {

	public HOMOGEN_PATTERNEOUS getType();
	
	public int getPatternWidth();
	
	public int getPatternHeight();
	
	public void drawPattern( Graphics2D g2, int patternWidth, int patternHeight );

}
