package hu.akoel.hetram;

import hu.akoel.hetram.Element.SideOrientation;
import hu.akoel.hetram.accessories.Length;

public class SurfaceClose extends CloseElement{
	
	private double alpha;
	private double airTemperature;
		
	public SurfaceClose( SideOrientation orientation, Length length, double alpha, double airTemperature ){
		super( orientation, length );		
		this.alpha = alpha;
		this.airTemperature = airTemperature;
	}

	public double getAlpha() {
		return alpha;
	}

	public double getAirTemperature() {
		return airTemperature;
	}

}
