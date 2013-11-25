package hu.akoel.hetram;

import java.text.DecimalFormat;

public class ThermicPoint {
	
	public static enum Orientation{
		NORTH,
		EAST,
		SOUTH,
		WEST
	}
	
	private double tempDifference;
	private int positionInTheList;
	private double actualTemperature;
	private Position position;
	private ThermicConnector northThermicConnector;
	private ThermicConnector eastThermicConnector;
	private ThermicConnector southThermicConnector;
	private ThermicConnector westThermicConnector;
	
	/**
	 * Egy Termikus Pont letrehozasa
	 * 
	 * @param position
	 */
	public ThermicPoint( Position position ){
		this.position = new Position(position);	
	}
	

	/**
	 * Termikus Pont szelso pontkent valo megjelolese
	 * 
	 * @param orientation
	 * @param alfa
	 * @param airTemperature
	 */
	public void connectTo( Orientation orientation, double alfa, double airTemperature ){
		
		 AThermicConnector connector = new AThermicConnector( alfa, airTemperature );
		
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
	public void connectTo( ThermicPoint pairThermicPoint, Orientation orientation, double lambda ){
		
		if( orientation.equals(Orientation.WEST) ){
		
			XDThermicConnector connector = new XDThermicConnector( pairThermicPoint, this, lambda );			
			this.westThermicConnector = connector;
			pairThermicPoint.eastThermicConnector = connector;
			
		}else if( orientation.equals( Orientation.EAST ) ){
			
			XDThermicConnector connector = new XDThermicConnector( this, pairThermicPoint, lambda );
			this.eastThermicConnector = connector;
			pairThermicPoint.westThermicConnector = connector;
			
		}else if( orientation.equals( Orientation.NORTH ) ){
			
			YDThermicConnector connector = new YDThermicConnector( pairThermicPoint, this, lambda );			
			this.northThermicConnector = connector;
			pairThermicPoint.southThermicConnector = connector;
			
		}else if( orientation.equals( Orientation.SOUTH ) ){
			
			YDThermicConnector connector = new YDThermicConnector( this, pairThermicPoint, lambda );
			this.southThermicConnector = connector;
			pairThermicPoint.northThermicConnector = connector;
		}
	}
	
	public ThermicConnector getNorthThermicConnector() {
		return northThermicConnector;
	}

	public ThermicConnector getEastThermicConnector() {
		return eastThermicConnector;
	}

	public ThermicConnector getSouthThermicConnector() {
		return southThermicConnector;
	}

	public ThermicConnector getWestThermicConnector() {
		return westThermicConnector;
	}
	
	public Position getPosition(){
		return this.position;
	}
	
	public ThermicPoint getNorthPair(){
		ThermicConnector tc = getNorthThermicConnector();

		//Igazi Termikus Pont kapcsolat van
		if( tc instanceof YDThermicConnector ){
			
			return ((YDThermicConnector)tc).getNorthThermicPoint();
			
		//Nem Termikus Pont kapcsolat van
		}else{
			return null;
		}
	}
	
	public ThermicPoint getSouthPair(){
		ThermicConnector tc = getSouthThermicConnector();

		//Igazi Termikus Pont kapcsolat van
		if( tc instanceof YDThermicConnector ){
			
			return ((YDThermicConnector)tc).getSouthThermicPoint();
			
		//Nem Termikus Pont kapcsolat van
		}else{
			return null;
		}
	}
	
	public ThermicPoint getWestPair(){
		ThermicConnector tc = getWestThermicConnector();

		//Igazi Termikus Pont kapcsolat van
		if( tc instanceof XDThermicConnector ){
			
			return ((XDThermicConnector)tc).getWestThermicPoint();
			
		//Nem Termikus Pont kapcsolat van
		}else{
			return null;
		}
	}
	
	public ThermicPoint getEastPair(){
		ThermicConnector tc = getEastThermicConnector();

		//Igazi Termikus Pont kapcsolat van
		if( tc instanceof XDThermicConnector ){
			
			return ((XDThermicConnector)tc).getEastThermicPoint();
			
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

	public String toString(){
		DecimalFormat temperatureFormat = new DecimalFormat("00.00");
		DecimalFormat deltaFormat = new DecimalFormat("#.####");
		
		String back = "T=" + temperatureFormat.format( getActualTemperature() ) + " " + this.getPosition() + " -> " + "N: ";
		
		if( null == getNorthPair() ){
			back += "(α=" + ((AThermicConnector)getNorthThermicConnector()).getAlpha() + " ";
			back += "T=" + ((AThermicConnector)getNorthThermicConnector()).getAirTemperature() + ")";
		}else{
			back += "(λ=" + ((DThermicConnector)getNorthThermicConnector()).getLambda() + " ";
			back += "δ=" + deltaFormat.format( ((DThermicConnector)getNorthThermicConnector()).getDelta() ) + " ";
			back += getNorthPair().getPosition() + ")";
		}
		
		back += " E: ";
		
		if( null == getEastPair() ){
			back += "(α=" + ((AThermicConnector)getEastThermicConnector()).getAlpha() + " ";
			back += "T=" + ((AThermicConnector)getEastThermicConnector()).getAirTemperature() + ")";
		}else{
			back += "(λ=" + ((DThermicConnector)getEastThermicConnector()).getLambda() + " ";
			back += "δ=" + deltaFormat.format( ((DThermicConnector)getEastThermicConnector()).getDelta() )+ " ";
			back += getEastPair().getPosition() + ")";
		}
		
		back += " S: ";
		
		if( null == getSouthPair() ){
			back += "(α=" + ((AThermicConnector)getSouthThermicConnector()).getAlpha() + " ";
			back += "T=" + ((AThermicConnector)getSouthThermicConnector()).getAirTemperature() + ")";
		}else{
			back += "(λ=" + ((DThermicConnector)getSouthThermicConnector()).getLambda() + " ";
			back += "δ=" + deltaFormat.format( ((DThermicConnector)getSouthThermicConnector()).getDelta() ) + " ";
			back += getSouthPair().getPosition() + ")";
		}
		
		back += " W: ";
		
		if( null == getWestPair() ){
			back += "(α=" + ((AThermicConnector)getWestThermicConnector()).getAlpha() + " ";
			back += "T=" + ((AThermicConnector)getWestThermicConnector()).getAirTemperature() + ")";
		}else{
			back += "(λ=" + ((DThermicConnector)getWestThermicConnector()).getLambda() + " ";
			back += "δ=" + deltaFormat.format( ((DThermicConnector)getWestThermicConnector()).getDelta() ) + " ";
			back += getWestPair().getPosition() + ")";
		}
		
		return back;
	}
}
