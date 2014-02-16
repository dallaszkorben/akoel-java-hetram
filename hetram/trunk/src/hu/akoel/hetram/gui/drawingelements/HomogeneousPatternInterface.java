package hu.akoel.hetram.gui.drawingelements;

import hu.akoel.hetram.gui.ElementSettingTab.HOMOGENEOUS_PATTERN;

import java.awt.Graphics2D;

public interface HomogeneousPatternInterface {

	public HOMOGENEOUS_PATTERN getForm();
	
	public int getPatternWidth();
	
	public int getPatternHeight();
	
	public void drawPattern( Graphics2D g2, int patternWidth, int patternHeight );

}
