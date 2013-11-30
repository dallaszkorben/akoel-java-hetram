package hu.akoel.hetram.accessories;

import java.awt.Color;

public class ColorTransient {
	Color coldColor;
	Color hotColor;
	
	public ColorTransient(){
		this.coldColor = Color.blue;
		this.hotColor = Color.red;
	}
	
	public void setRange( Color coldColor, Color hotColor ){
		this.coldColor = coldColor;
		this.hotColor = hotColor;
	}
	
	public Color getColor( double percent ){
		return null;
	}
}