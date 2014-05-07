package hu.akoel.mgu.sprite;

import java.util.ArrayList;
import java.util.HashSet;

import hu.akoel.mgu.MGraphics;
import hu.akoel.mgu.values.PositionValue;
import hu.akoel.mgu.values.SizeValue;

public class Sprite {
	private boolean enableToPlaceWithoutConnection = true;
	private boolean inFocus = false;
	private SizeValue boundBox;
	private PositionValue position = new PositionValue(0,0);
	private ArrayList<SpriteElement> elements = new ArrayList<SpriteElement>();
	private HashSet<Magnet> magnetList = new HashSet<Magnet>();
	private HashSet<ChangeSizeListener> changeWidthListenerList = new HashSet<ChangeSizeListener>();
	private HashSet<ChangeSizeListener> changeHeightListenerList = new HashSet<ChangeSizeListener>();

	public Sprite( SizeValue boundBox ){
		commonConstructor(boundBox, true);
	}
	
	public Sprite( SizeValue boundBox, boolean enableToPlaceWithoutConnection ){
		commonConstructor(boundBox, enableToPlaceWithoutConnection);
	}
	
	private void commonConstructor( SizeValue boundBox, boolean enableToPlaceWithoutConnection ){
		this.boundBox = boundBox;
		this.enableToPlaceWithoutConnection = enableToPlaceWithoutConnection;
	}
	
	public void setBoundBoxXMin( double xMin ){
		this.boundBox.setXMin(xMin);
	}
	
	public void setBoundBoxXMax( double xMax ){
		this.boundBox.setXMax(xMax);
	}
	
	public void setBoundYMin( double yMin ){
		this.boundBox.setYMin(yMin);
	}
	
	public void setBoundYMax( double yMax ){
		this.boundBox.setYMax(yMax);
	}
		
	public void addChangeWidthListener( ChangeSizeListener changeWidthListener ){
		this.changeWidthListenerList.add( changeWidthListener );
	}

	public void addChangeHeightListener( ChangeSizeListener changeHeightListener ){
		this.changeHeightListenerList.add( changeHeightListener );
	}

	public boolean isEnableToPlaceWithoutConnection() {
		return enableToPlaceWithoutConnection;
	}

	public void setEnableToPlaceWithoutConnection( boolean enableToPlaceWithoutConnection ) {
		this.enableToPlaceWithoutConnection = enableToPlaceWithoutConnection;
	}
	
	public SizeValue getBoundBoxAbsolute(){
		return new SizeValue(this.boundBox.getXMin() + position.getX(), this.boundBox.getYMin() + position.getY(), this.boundBox.getXMax() + position.getX(), this.boundBox.getYMax() + position.getY());
	}
	
	public void addMagnet( Magnet magnet ){
		this.magnetList.add(magnet);
	}
	
	public HashSet<Magnet> getMagnetList(){
		return magnetList;
	}
	
	public void addElement( SpriteElement element ){
		elements.add(element);
	}
	
	public void setPosition( PositionValue position ){
		this.position.setX(position.getX());
		this.position.setY(position.getY());
	}
	
	public void setPosition( double positionX, double positionY ){
		this.position.setX( positionX );
		this.position.setY( positionY );
	}
	
	public PositionValue getPosition(){
		return new PositionValue(position.getX(), position.getY());
	}
	
	public void changeWidthTo( double xMin, double xMax ){
		
		this.setBoundBoxXMin(xMin);
		this.setBoundBoxXMax(xMax);
		for( ChangeSizeListener changeWidthListener: changeWidthListenerList ){
			changeWidthListener.changed( xMin, xMax );
		}
	}
	
	public void changeHeightTo( double yMin, double yMax ){
		for( ChangeSizeListener changeHeightListener: changeHeightListenerList ){
			changeHeightListener.changed( yMin, yMax );
		}		
	}
	
	public void draw( MGraphics g2 ){
		
		if( isConnected() ){
			
			for( SpriteElement element: elements){
				element.setPosition(position);
				element.drawConnected(g2);
			}
			
			for( Magnet magnet: magnetList){
				if( null == magnet.getConnectedTo() ){
					magnet.draw(g2);
				}else{
					magnet.drawConnected(g2);
				}
			}
			
		}else{
			for( SpriteElement element: elements){
				element.setPosition(position);
				element.draw(g2);
			}
			
			for( Magnet magnet: magnetList){		
					magnet.draw(g2);				
			}
		}
	}
	
	public void drawFocus( MGraphics g2){
		for( SpriteElement element: elements){
			element.setPosition(position);
			element.drawFocus(g2);
		}

		for( Magnet magnet: magnetList){
			if( null == magnet.getConnectedTo() ){
				magnet.drawFocus(g2);
			}else{
				magnet.drawConnected(g2);
			}
		}
		
		
	}
	

/*	public void drawConnected( MGraphics g2){
		for( SpriteElement element: elements){
			element.setPosition(position);
			element.drawConnected(g2);
		}
	}
*/	
	public boolean isInFocus(){
		return inFocus;
	}
	
	public void setInFocus( boolean inFocus ){
		this.inFocus = inFocus;
	}
	
	public boolean isConnected(){
		boolean isConnected = false;
		for( Magnet magnet: getMagnetList()){
			if( null != magnet.getConnectedTo() ){
				isConnected = true;
				break;
			}
		}
		return isConnected;
	}
}
