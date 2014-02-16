package hu.akoel.hetram.accessories;

import java.math.BigDecimal;

public class BigDecimalPosition {

	BigDecimal x;
	BigDecimal y;
	
	public BigDecimalPosition( BigDecimal x, BigDecimal y ) {
		this.x = x;
		this.y = y;
	}
	
	public BigDecimalPosition( BigDecimalPosition position ){
		this.x = position.x;
		this.y = position.y;
	}
	
	public BigDecimal getX(){
		return this.x;
	}
	
	public BigDecimal getY(){
		return this.y;
	}

	@Override
	public boolean equals( Object o ){
		if( (o instanceof BigDecimalPosition) ){

			if( ((BigDecimalPosition)o).x.compareTo( this.x ) == 0 && ((BigDecimalPosition)o).y.compareTo( this.y ) == 0 ){
				return true;
			}
		}
		return false;
	}
	
	 @Override
	 public int hashCode() {
		 final int prime = 31;
		 int result = 1;
		 
		 result = result * prime + x.hashCode();
		 result = result * prime + y.hashCode();
		 
		 return result;
	 }
	 
	public String toString(){
		return new String( "(" + x + ", " + y + ")" );
	}
}
