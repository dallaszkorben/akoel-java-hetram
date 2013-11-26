package hu.akoel.hetram;

import hu.akoel.hetram.connectors.OThermicConnector;
import hu.akoel.hetram.connectors.DThermicConnector;
import hu.akoel.hetram.connectors.SThermicConnector;
import hu.akoel.hetram.connectors.ThermicConnector;
import hu.akoel.hetram.connectors.XDThermicConnector;
import hu.akoel.hetram.connectors.YDThermicConnector;
import java.text.DecimalFormat;

public class ThermicPoint {
	
	public static enum ThermicPointOrientation{
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
	 * Termikus Pont szimmetria elkent valo megjelolese
	 * 
	 * @param backThermicPoint
	 * @param orientation
	 */
	public void connectToS( ThermicPointOrientation orientation ){
		
		SThermicConnector connector = new SThermicConnector( );	
		
		if( orientation.equals(ThermicPointOrientation.WEST) ){

			this.westThermicConnector = connector;			
			
		}else if( orientation.equals( ThermicPointOrientation.EAST ) ){
			
			this.eastThermicConnector = connector;
			
		}else if( orientation.equals( ThermicPointOrientation.NORTH ) ){
			
			this.northThermicConnector = connector;
			
		}else if( orientation.equals( ThermicPointOrientation.SOUTH ) ){
			
			this.southThermicConnector = connector;
			
		}
		
	}
	
	/**
	 * Termikus Pont szabad feluleti pontkent valo megjelolese
	 * 
	 * @param orientation
	 * @param alfa
	 * @param airTemperature
	 */
	public void connectToO( ThermicPointOrientation orientation, double alfa, double airTemperature ){
		
		 OThermicConnector connector = new OThermicConnector( alfa, airTemperature );
		
		if( orientation.equals( ThermicPointOrientation.NORTH ) ){
			northThermicConnector = connector;
		}else if( orientation.equals( ThermicPointOrientation.EAST ) ){
			eastThermicConnector = connector;
		}else if( orientation.equals( ThermicPointOrientation.SOUTH ) ){
			southThermicConnector = connector;
		}else if( orientation.equals( ThermicPointOrientation.WEST ) ){
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
	public void connectToD( ThermicPoint pairThermicPoint, ThermicPointOrientation orientation, double lambda ){
		
		if( orientation.equals(ThermicPointOrientation.WEST) ){
		
			XDThermicConnector connector = new XDThermicConnector( pairThermicPoint, this, lambda );			
			this.westThermicConnector = connector;
			pairThermicPoint.eastThermicConnector = connector;
			
		}else if( orientation.equals( ThermicPointOrientation.EAST ) ){
			
			XDThermicConnector connector = new XDThermicConnector( this, pairThermicPoint, lambda );
			this.eastThermicConnector = connector;
			pairThermicPoint.westThermicConnector = connector;
			
		}else if( orientation.equals( ThermicPointOrientation.NORTH ) ){
			
			YDThermicConnector connector = new YDThermicConnector( pairThermicPoint, this, lambda );			
			this.northThermicConnector = connector;
			pairThermicPoint.southThermicConnector = connector;
			
		}else if( orientation.equals( ThermicPointOrientation.SOUTH ) ){
			
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
		ThermicConnector tc;
		
		String back = "T=" + temperatureFormat.format( getActualTemperature() ) + " " + this.getPosition() + " -> "; 
		
				
		back += " N: ";
		tc = getNorthThermicConnector();
		if( tc instanceof YDThermicConnector ){
			back += "(λ=" + ((DThermicConnector)tc).getLambda() + " ";
			back += "δ=" + deltaFormat.format( ((DThermicConnector)tc).getDelta() ) + " ";
			back += getNorthPair().getPosition() + ")";
		}else if( tc instanceof OThermicConnector ){
			back += "(α=" + ((OThermicConnector)tc).getAlpha() + " ";
			back += "T=" + ((OThermicConnector)tc).getAirTemperature() + ")";
		}else if( tc instanceof SThermicConnector ){
			back += "_";
		}
		
		back += " E: ";
		tc = getEastThermicConnector();
		if( tc instanceof XDThermicConnector ){
			back += "(λ=" + ((DThermicConnector)tc).getLambda() + " ";
			back += "δ=" + deltaFormat.format( ((DThermicConnector)tc).getDelta() ) + " ";
			back += getEastPair().getPosition() + ")";
		}else if( tc instanceof OThermicConnector ){
			back += "(α=" + ((OThermicConnector)tc).getAlpha() + " ";
			back += "T=" + ((OThermicConnector)tc).getAirTemperature() + ")";
		}else if( tc instanceof SThermicConnector ){
			back += "|";
		}
		
		back += " S: ";
		tc = getSouthThermicConnector();
		if( tc instanceof YDThermicConnector ){
			back += "(λ=" + ((DThermicConnector)tc).getLambda() + " ";
			back += "δ=" + deltaFormat.format( ((DThermicConnector)tc).getDelta() ) + " ";
			back += getSouthPair().getPosition() + ")";
		}else if( tc instanceof OThermicConnector ){
			back += "(α=" + ((OThermicConnector)tc).getAlpha() + " ";
			back += "T=" + ((OThermicConnector)tc).getAirTemperature() + ")";
		}else if( tc instanceof SThermicConnector ){
			back += "_";
		}
		
		back += " W: ";		
		tc = getWestThermicConnector();
		if( tc instanceof XDThermicConnector ){
			back += "(λ=" + ((DThermicConnector)tc).getLambda() + " ";
			back += "δ=" + deltaFormat.format( ((DThermicConnector)tc).getDelta() ) + " ";
			back += getWestPair().getPosition() + ")";
		}else if( tc instanceof OThermicConnector ){
			back += "(α=" + ((OThermicConnector)tc).getAlpha() + " ";
			back += "T=" + ((OThermicConnector)tc).getAirTemperature() + ")";
		}else if( tc instanceof SThermicConnector ){
			back += "|";
		}
		
		return back;
	}
}
