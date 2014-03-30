package hu.akoel.hetram.thermicpoint;

import hu.akoel.hetram.accessories.BigDecimalPosition;
import hu.akoel.hetram.accessories.Orientation;
import hu.akoel.hetram.connectors.OpenEdgeThermicConnector;
import hu.akoel.hetram.connectors.AThermicPointThermicConnector;
import hu.akoel.hetram.connectors.SymmetricEdgeThermicConnector;
import hu.akoel.hetram.connectors.IThermicConnector;
import hu.akoel.hetram.connectors.XThermicPointThermicConnector;
import hu.akoel.hetram.connectors.YThermicPointThermicConnector;

import java.text.DecimalFormat;

public class ThermicPoint {
	
	private double tempDifference;
	private int positionInTheList;
	private double actualTemperature;
	private BigDecimalPosition position;
	private IThermicConnector northThermicConnector;
	private IThermicConnector eastThermicConnector;
	private IThermicConnector southThermicConnector;
	private IThermicConnector westThermicConnector;
	private Double northCurrent;
	private Double eastCurrent;
	private Double southCurrent;
	private Double westCurrent;
	
	private double northDeltaNormal;
	private double southDeltaNormal;
	private double eastDeltaNormal;
	private double westDeltaNormal;
	
	private double northDelta;
	private double southDelta;
	private double eastDelta;
	private double westDelta;
	
	/**
	 * Egy Termikus Pont letrehozasa
	 * 
	 * @param position
	 */
	public ThermicPoint( BigDecimalPosition position ){
		this.position = new BigDecimalPosition(position);	
	}
	
	/**
	 * Termikus Pont szimmetria elkent valo megjelolese
	 * 
	 * @param backThermicPoint
	 * @param orientation
	 */
	public void connectToSymmetricEdge( Orientation orientation ){
		
		SymmetricEdgeThermicConnector connector = new SymmetricEdgeThermicConnector( );	
		
		if( orientation.equals(Orientation.WEST) ){

			this.westThermicConnector = connector;			
			
		}else if( orientation.equals( Orientation.EAST ) ){
			
			this.eastThermicConnector = connector;
			
		}else if( orientation.equals( Orientation.NORTH ) ){
			
			this.northThermicConnector = connector;
			
		}else if( orientation.equals( Orientation.SOUTH ) ){
			
			this.southThermicConnector = connector;
			
		}
		
	}
	
	/**
	 * Termikus Pont szabad feluleti pontkent valo megjelolese
	 * 
	 * @param orientation
	 * @param alpha
	 * @param airTemperature
	 */
	public void connectToOpenEdge( Orientation orientation, double alpha, double airTemperature ){
	
		 OpenEdgeThermicConnector connector = new OpenEdgeThermicConnector( alpha, airTemperature );
		
		if( orientation.equals( Orientation.NORTH ) ){
			northThermicConnector = connector;
		}else if( orientation.equals( Orientation.EAST ) ){
			eastThermicConnector = connector;
		}else if( orientation.equals( Orientation.SOUTH ) ){
			southThermicConnector = connector;
		}else if( orientation.equals( Orientation.WEST ) ){
			westThermicConnector = connector;
		}		
	}

	/**
	 * Termikus Pont csatlakoztatasa egy masik termikus ponthoz
	 * 
	 * @param pairThermicPoint
	 * @param orientation
	 * @param delta
	 * @param lambda
	 */
	public void connectToThermicPoint( ThermicPoint pairThermicPoint, Orientation orientation, double lambda ){
		
		if( orientation.equals(Orientation.WEST) ){
		
			XThermicPointThermicConnector connector = new XThermicPointThermicConnector( pairThermicPoint, this, lambda );			
			this.westThermicConnector = connector;
			pairThermicPoint.eastThermicConnector = connector;
			
		}else if( orientation.equals( Orientation.EAST ) ){
			
			XThermicPointThermicConnector connector = new XThermicPointThermicConnector( this, pairThermicPoint, lambda );
			this.eastThermicConnector = connector;
			pairThermicPoint.westThermicConnector = connector;
			
		}else if( orientation.equals( Orientation.NORTH ) ){
			
			YThermicPointThermicConnector connector = new YThermicPointThermicConnector( pairThermicPoint, this, lambda );			
			this.northThermicConnector = connector;
			pairThermicPoint.southThermicConnector = connector;
			
		}else if( orientation.equals( Orientation.SOUTH ) ){
			
			YThermicPointThermicConnector connector = new YThermicPointThermicConnector( this, pairThermicPoint, lambda );
			this.southThermicConnector = connector;
			pairThermicPoint.northThermicConnector = connector;
		}
	}
	
	
	
	public double getNorthDelta() {
		return northDelta;
	}

	public void setNorthDelta(double northDelta) {
		this.northDelta = northDelta;
	}

	public double getSouthDelta() {
		return southDelta;
	}

	public void setSouthDelta(double southDelta) {
		this.southDelta = southDelta;
	}

	public double getEastDelta() {
		return eastDelta;
	}

	public void setEastDelta(double eastDelta) {
		this.eastDelta = eastDelta;
	}

	public double getWestDelta() {
		return westDelta;
	}

	public void setWestDelta(double westDelta) {
		this.westDelta = westDelta;
	}

	public double getNorthDeltaNormal() {
		return northDeltaNormal;
	}

	public void setNorthDeltaNormal(double northDeltaNormal) {
		this.northDeltaNormal = northDeltaNormal;
	}

	public double getSouthDeltaNormal() {
		return southDeltaNormal;
	}

	public void setSouthDeltaNormal(double southDeltaNormal) {
		this.southDeltaNormal = southDeltaNormal;
	}

	public double getEastDeltaNormal() {
		return eastDeltaNormal;
	}

	public void setEastDeltaNormal(double eastDeltaNormal) {
		this.eastDeltaNormal = eastDeltaNormal;
	}

	public double getWestDeltaNormal() {
		return westDeltaNormal;
	}

	public void setWestDeltaNormal(double westDeltaNormal) {
		this.westDeltaNormal = westDeltaNormal;
	}

	public Double getNorthCurrent() {
		return northCurrent;
	}

	public void setNorthCurrent(Double northCurrent) {
		this.northCurrent = northCurrent;
	}

	public Double getEastCurrent() {
		return eastCurrent;
	}

	public void setEastCurrent(Double eastCurrent) {
		this.eastCurrent = eastCurrent;
	}

	public Double getSouthCurrent() {
		return southCurrent;
	}

	public void setSouthCurrent(Double southCurrent) {
		this.southCurrent = southCurrent;
	}

	public Double getWestCurrent() {
		return westCurrent;
	}

	public void setWestCurrent(Double westCurrent) {
		this.westCurrent = westCurrent;
	}

	public IThermicConnector getNorthThermicConnector() {
		return northThermicConnector;
	}

	public IThermicConnector getEastThermicConnector() {
		return eastThermicConnector;
	}

	public IThermicConnector getSouthThermicConnector() {
		return southThermicConnector;
	}

	public IThermicConnector getWestThermicConnector() {
		return westThermicConnector;
	}
	
	public BigDecimalPosition getPosition(){
		return this.position;
	}
	
	public ThermicPoint getNorthPair(){
		IThermicConnector tc = getNorthThermicConnector();

		//Igazi Termikus Pont kapcsolat van
		if( tc instanceof YThermicPointThermicConnector ){
			
			return ((YThermicPointThermicConnector)tc).getNorthThermicPoint();
			
		//Nem Termikus Pont kapcsolat van
		}else{
			return null;
		}
	}
	
	public ThermicPoint getSouthPair(){
		IThermicConnector tc = getSouthThermicConnector();

		//Igazi Termikus Pont kapcsolat van
		if( tc instanceof YThermicPointThermicConnector ){
			
			return ((YThermicPointThermicConnector)tc).getSouthThermicPoint();
			
		//Nem Termikus Pont kapcsolat van
		}else{
			return null;
		}
	}
	
	public ThermicPoint getWestPair(){
		IThermicConnector tc = getWestThermicConnector();

		//Igazi Termikus Pont kapcsolat van
		if( tc instanceof XThermicPointThermicConnector ){
			
			return ((XThermicPointThermicConnector)tc).getWestThermicPoint();
			
		//Nem Termikus Pont kapcsolat van
		}else{
			return null;
		}
	}
	
	public ThermicPoint getEastPair(){
		IThermicConnector tc = getEastThermicConnector();

		//Igazi Termikus Pont kapcsolat van
		if( tc instanceof XThermicPointThermicConnector ){
			
			return ((XThermicPointThermicConnector)tc).getEastThermicPoint();
			
		//Nem Termikus Pont kapcsolat van
		}else{
			return null;
		}
	}
	
	//return ((DThermicConnector) c).getLambda()/((DThermicConnector) c).getDelta();
	
	public double getActualTemperature() {
		return actualTemperature;
	}


	public void setActualTemperature(double actualTemperature) {
		
		this.tempDifference = Math.abs( this.actualTemperature-actualTemperature );

		this.actualTemperature = actualTemperature;
	}

	public void setPositionInTheList(int orderInTheList) {
		this.positionInTheList = orderInTheList;
	}
	
	public double getTempDifference() {
		return tempDifference;
	}

	@Override
	public boolean equals(Object o){
		if( ( o instanceof ThermicPoint ) ){
		
			if( ((ThermicPoint)o).position.equals(this.position) ){
				return true;
			}
		}
		return false;
	}
	
	 @Override
	 public int hashCode() {
		 final int prime = 31;
		 int result = 1;
		 
		 result = result * prime + position.hashCode();
		 
		 return result;
	 }
	
	public String toString(){
		DecimalFormat temperatureFormat = new DecimalFormat("00.00");
		DecimalFormat deltaFormat = new DecimalFormat("#.####");
		IThermicConnector tc;
		
		String back = "T=" + temperatureFormat.format( getActualTemperature() ) + " " + this.getPosition() + " -> "; 
		
				
		back += " N: ";
		tc = getNorthThermicConnector();
		if( tc instanceof YThermicPointThermicConnector ){
			back += "(λ=" + ((AThermicPointThermicConnector)tc).getLambda() + " ";
			back += "δ=" + deltaFormat.format( ((AThermicPointThermicConnector)tc).getDelta() ) + " ";
			back += "q=" + this.getNorthCurrent() + " ";
			back += getNorthPair().getPosition() + ")";
		}else if( tc instanceof OpenEdgeThermicConnector ){
			back += "(α=" + ((OpenEdgeThermicConnector)tc).getAlpha() + " ";
			back += "T=" + ((OpenEdgeThermicConnector)tc).getAirTemperature() + ")";
		}else if( tc instanceof SymmetricEdgeThermicConnector ){
			back += "_";
		}
		
		back += " E: ";
		tc = getEastThermicConnector();
		if( tc instanceof XThermicPointThermicConnector ){
			back += "(λ=" + ((AThermicPointThermicConnector)tc).getLambda() + " ";
			back += "δ=" + deltaFormat.format( ((AThermicPointThermicConnector)tc).getDelta() ) + " ";
			back += "q=" + this.getEastCurrent() + " ";
			back += getEastPair().getPosition() + ")";
		}else if( tc instanceof OpenEdgeThermicConnector ){
			back += "(α=" + ((OpenEdgeThermicConnector)tc).getAlpha() + " ";
			back += "T=" + ((OpenEdgeThermicConnector)tc).getAirTemperature() + ")";
		}else if( tc instanceof SymmetricEdgeThermicConnector ){
			back += "|";
		}
		
		back += " S: ";
		tc = getSouthThermicConnector();
		if( tc instanceof YThermicPointThermicConnector ){
			back += "(λ=" + ((AThermicPointThermicConnector)tc).getLambda() + " ";
			back += "δ=" + deltaFormat.format( ((AThermicPointThermicConnector)tc).getDelta() ) + " ";
			back += getSouthPair().getPosition() + ")";
			back += "q=" + this.getSouthCurrent() + " ";
		}else if( tc instanceof OpenEdgeThermicConnector ){
			back += "(α=" + ((OpenEdgeThermicConnector)tc).getAlpha() + " ";
			back += "T=" + ((OpenEdgeThermicConnector)tc).getAirTemperature() + ")";
		}else if( tc instanceof SymmetricEdgeThermicConnector ){
			back += "_";
		}
		
		back += " W: ";		
		tc = getWestThermicConnector();
		if( tc instanceof XThermicPointThermicConnector ){
			back += "(λ=" + ((AThermicPointThermicConnector)tc).getLambda() + " ";
			back += "δ=" + deltaFormat.format( ((AThermicPointThermicConnector)tc).getDelta() ) + " ";
			back += "q=" + this.getWestCurrent() + " ";
			back += getWestPair().getPosition() + ")";
		}else if( tc instanceof OpenEdgeThermicConnector ){
			back += "(α=" + ((OpenEdgeThermicConnector)tc).getAlpha() + " ";
			back += "T=" + ((OpenEdgeThermicConnector)tc).getAirTemperature() + ")";
		}else if( tc instanceof SymmetricEdgeThermicConnector ){
			back += "|";
		}
		
		return back;
	}
}
