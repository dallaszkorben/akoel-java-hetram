package hu.akoel.hetram;

import hu.akoel.hetram.Element.SideOrientation;

public abstract class CloseElement {
	private SideOrientation orientation;
	private Length length;
	
	public CloseElement( SideOrientation orientation, Length length ){
		this.orientation = orientation;
		this.length = length;
	}

	public SideOrientation getOrientation() {
		return orientation;
	}

	public Length getLength() {
		return length;
	}
	
	@Override
	public boolean equals(Object o){
		if( ( o instanceof CloseElement ) ){

			if( ( (CloseElement)o ).orientation.equals( this.orientation ) && ((CloseElement)o).length.equals(this.length) ){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result + orientation.hashCode();
		result = prime * result + length.hashCode();
		
		return result;
	}
}
