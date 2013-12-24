package hu.akoel.hetram.structures;

import hu.akoel.hetram.accessories.Position;

import java.util.HashSet;

/**
 * Egy epuletszerkezetet negyzet negyzet-keresztmetszetet reprezentalja a vizsgalt sikban
 * Egy ilyen keresztmetszet a kovetkezo tulajdonsagokkal rendelkezik:
 * -Hovezetokepesseggel (λ)
 * -A negyzetkeresztmetszet kezdo es vegkoordinatai (startPosition, endPosition)
 * 
 * Egy ilyen epuletszerkezeti keresztmetszet az eleinel kapcsolodhat egy masik 
 * -epuletszerkezeti keresztmetszethez (StructuralCrossSection), 
 * -vagy egy zaro elemhez (ElementCloser). 
 * 
 * @author afoldvarszky
 *
 */
public class Structure {
	
	private double lambda;
	private Position startPosition;
	private Position endPosition;
	private HashSet<AStructureSealing> closeElementSet = new HashSet<>();
	
	public Structure( double lambda, Position startPosition, Position endPosition ){
		this.setLambda(lambda);
		this.startPosition = new Position( startPosition );
		this.endPosition = new Position( endPosition );
	}

	public void setCloseElement( AStructureSealing closeElement ){
		closeElementSet.add( closeElement );
	}
	
	public HashSet<AStructureSealing> getCloseElements(){
		return closeElementSet;
	}

	public double getLambda() {
		return lambda;
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	public Position getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(Position startPosition) {
		this.startPosition = startPosition;
	}

	public Position getEndPosition() {
		return endPosition;
	}

	public void setEndPosition(Position endPosition) {
		this.endPosition = endPosition;
	}
	
	public String toString(){
		return "λ: " + lambda + " (" + startPosition + " " + endPosition + ")";
	}
}
