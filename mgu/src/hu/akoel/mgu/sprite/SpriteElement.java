package hu.akoel.mgu.sprite;

import hu.akoel.mgu.MGraphics;
import hu.akoel.mgu.values.PositionValue;


public abstract class SpriteElement {
	private PositionValue position = new PositionValue(0,0);
	
	public void setPositionX( double x ){
		this.position.setX(x);
	}
	
	public void setPositionY( double y ){
		this.position.setY(y);
		
	}
	
	public void setPosition( PositionValue translate ){
		this.position.setX(translate.getX());
		this.position.setY(translate.getY());
	}
	
	public double getPositionX(){
		return position.getX();
	}
	
	public double getPositionY(){
		return position.getY();
	}
	
	public abstract void draw( MGraphics g2 );
	
	public abstract void drawFocus( MGraphics g2);
	
	public abstract void drawConnected( MGraphics g2);
}
