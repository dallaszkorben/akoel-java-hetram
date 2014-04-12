package hu.akoel.mgu.drawnblock;

import java.math.BigDecimal;

public class Block {
	private BigDecimal startX, startY;
	private BigDecimal x1, y1, x2, y2;
	
	BigDecimal minLength = null;
	BigDecimal minWidth = null;
	BigDecimal maxLength = null;
	BigDecimal maxWidth = null;
	
	public Block( BigDecimal x1, BigDecimal y1 ){
		this.startX = x1;
		this.startY = y1;

		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x1;
		this.y2 = y1;
	}
	
	public void reset( BigDecimal x1, BigDecimal y1 ){
		this.startX = x1;
		this.startY = y1;

		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x1;
		this.y2 = y1;
	}
	
	public void setScale( int scale ){
		this.x1 = this.x1.setScale(scale);
		this.x2 = this.x2.setScale(scale);
		this.y1 = this.y1.setScale(scale);
		this.y2 = this.y2.setScale(scale);
	
	}
	
	public BigDecimal getStartX(){
		return this.startX;
	}
	
	public BigDecimal getStartY(){
		return this.startY;
	}
	
	public BigDecimal getStopX(){	
		
		int comp = this.startX.compareTo( this.x1 );
		if( comp == 0 ){
			return this.x2; 
		}else{
			return this.x1;
		}		
	}
	
	public BigDecimal getStopY(){	
		
		int comp = this.startY.compareTo( this.y1 );
		if( comp == 0 ){
			return this.y2; 
		}else{
			return this.y1;
		}		
	}
	
	public BigDecimal getX1(){
		return x1;
	}

	public BigDecimal getX2(){
		return x2;
	}

	public BigDecimal getY1(){
		return y1;
	}
	
	public BigDecimal getY2(){
		return y2;
	}
	
	public BigDecimal getWidth(){
		return x2.subtract( x1 );
	}
	
	public BigDecimal getHeight(){
		return y2.subtract( y1 );
	}
	
	public void setWidth( BigDecimal width ){
		this.x2 = this.x1.add( width );
	}
	
	public void setHeight( BigDecimal height ){
		this.y2 = this.y1.add( height );
	}
	
	public void changeSize( BigDecimal x, BigDecimal y ){
		
		int comp = x.compareTo( this.startX );
		if( comp < 0 ){
			this.x1 = x;
			this.x2 = this.startX;
		}else{
			this.x1 = this.startX;
			this.x2 = x;
		}
		
		comp = y.compareTo( this.startY );
		if( comp < 0 ){
			this.y1 = y;
			this.y2 = this.startY;
		}else{
			this.y1 = this.startY;
			this.y2 = y;
		}		
	}
	
	public String toString(){
		return new String( "(" + x1.toPlainString() + ", " + y1.toPlainString() + ") (" + x2.toPlainString() + ", " + y2.toPlainString() + ")" );
	}
}
