package hu.akoel.mgu.scale.values;

import hu.akoel.mgu.scale.Scale.UNIT;

public class UnitValue {

	private UNIT unitX;
	private UNIT unitY;
	
	public UnitValue( UNIT unitX, UNIT unitY ){
		this.unitX = unitX;
		this.unitY = unitY;
	}
	
	public UNIT getUnitX(){
		return unitX;
	}
	
	public UNIT getUnitY(){
		return unitY;
	}
	
	public void setUnitX( UNIT unitX ){
		this.unitX = unitX;
	}

	public void setUnitY( UNIT unitY ){
		this.unitY = unitY;
	}
	
	public String toString(){
		return "[" + unitX.getSign() + ", " + unitY.getSign() + "]";
	}

}
