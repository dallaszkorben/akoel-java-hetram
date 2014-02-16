package hu.akoel.hetram.accessories;

import java.awt.Color;

public class ColorTransient {
	double minimumHue = 0;
	double maximumHue = 0.7;
	
	public ColorTransient(){
		
	}
	
	public ColorTransient( double minimumHue, double maximumHue ){
		this.minimumHue = minimumHue;
		this.maximumHue = maximumHue;
	}
	
	public Color getColor( double percent ){
		if( percent > 1.0 ){
			percent = 1.0;
		}
		if( percent < 0.0 ){
			percent = 0.0;
		}
				
		float hue = (float) (( this.maximumHue - this.minimumHue ) * (1 - percent ) + this.minimumHue); 
		return Color.getHSBColor(hue, 1.0f, 1.0f);
	}
}