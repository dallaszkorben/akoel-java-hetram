package hu.akoel.hetram.accessories;

public class Length {
	private double start;
	private double end;
	
	public Length( double start, double end ){
		this.start = start;
		this.end = end;
	}

	public double getStart() {
		return start;
	}

	public double getEnd() {
		return end;
	}
	
	@Override
	public boolean equals(Object o){
		if( ( o instanceof Length ) ){

			if( ( (Length)o ).start == this.start && ((Length)o).end == this.end ){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = result * prime + Double.valueOf(start).hashCode();
		
		return result;
	}
}
