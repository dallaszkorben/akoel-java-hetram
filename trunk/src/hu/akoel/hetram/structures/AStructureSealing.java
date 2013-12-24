package hu.akoel.hetram.structures;

import hu.akoel.hetram.accessories.Length;
import hu.akoel.hetram.accessories.Orientation;

/**
 * 
 * @author afoldvarszky
 *
 */
public abstract class AStructureSealing {
	private Orientation orientation;
	private Length length;
	
	public AStructureSealing( Orientation orientation, Length length ){
		this.orientation = orientation;
		this.length = length;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public Length getLength() {
		return length;
	}
	
	@Override
	public boolean equals(Object o){
		if( ( o instanceof AStructureSealing ) ){

			if( ( (AStructureSealing)o ).orientation.equals( this.orientation ) && ((AStructureSealing)o).length.equals(this.length) ){
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
