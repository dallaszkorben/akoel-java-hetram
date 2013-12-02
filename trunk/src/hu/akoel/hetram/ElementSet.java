package hu.akoel.hetram;

import hu.akoel.hetram.Element.SideOrientation;
import hu.akoel.hetram.ThermicPoint.ThermicPointOrientation;
import hu.akoel.hetram.accessories.Position;
import hu.akoel.hetram.connectors.DThermicConnector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class ElementSet{

	private double precision = 10000;
	private HashSet<Element> elementSet = new HashSet<>();	
	
	private double verticalMaximumDifference = -1;
	private double horizontalMaximumDifference = -1;
	
	private double verticalAppliedDifference;
	private double horizontalAppliedDifference;
	
	private int verticalDifferenceDivider = 1;
	private int horizontalDifferenceDivider = 1;

	/**
	 * Hozzaad egy Element-et a listahoz
	 * 
	 * @param element
	 * @return
	 */
	public boolean add( Element element ){
		boolean result = elementSet.add( element );		
		doGenerateMaximumDifference();		
		return result;
	}
	
	public boolean remove( Element element){
		boolean result = elementSet.remove(element);
		doGenerateMaximumDifference();		
		return result;
	}	

	public double getVerticalAppliedDifference() {
		return verticalAppliedDifference;
	}

	public double getHorizontalAppliedDifference() {
		return horizontalAppliedDifference;
	}
	
	public double getVerticalMaximumDifference(){
		return verticalMaximumDifference;
	}
	
	public double getHorizontalMaximumDifference(){
		return horizontalMaximumDifference;
	}
	
	public int getVerticalDifferenceDivider() {
		return verticalDifferenceDivider;
	}

	public void setVerticalDifferenceDivider(int verticalDifferenceDivider) {
		this.verticalDifferenceDivider = verticalDifferenceDivider;
	}

	public int getHorizontalDifferenceDivider() {
		return horizontalDifferenceDivider;
	}

	public void setHorizontalDifferenceDivider(int horizontalDifferenceDivider) {
		this.horizontalDifferenceDivider = horizontalDifferenceDivider;
	}

	public Iterator<Element> iterator(){
		return elementSet.iterator();
	}
/*	
	public double getHorizontalSuggestedDifference( double askedDifference ){
		double maximumDifference = getHorizontalMaximumDifference();
		
		if( askedDifference >= maximumDifference ){
			return maximumDifference;
		}else{
			return maximumDifference / ((int)(maximumDifference / askedDifference ) );
		}
	}
	
	public double getVerticalSuggestedDifference( double askedDifference ){
		double maximumDifference = getVerticalMaximumDifference();
		
		if( askedDifference >= maximumDifference ){
			return maximumDifference;
		}else{
			return maximumDifference / ((int)(maximumDifference / askedDifference ) );
		}
	}
*/	
	
	/**
	 * Automatikusan felbontja kis differencialis negyzetekre az osszes elemet,
	 * legyartja a termikus pontokat es megteremti kozottuk a kapcsolatot
	 * !!! Szamitas nem tortenik !!!
	 * 
	 * @param askedHorizontalDifference
	 * @param askedVerticalDifference
	 * @return
	 */
	public ThermicPointList divideElements( ){
		
		//verticalAppliedDifference = getVerticalSuggestedDifference(askedVerticalDifference);
		//horizontalAppliedDifference = getHorizontalSuggestedDifference(askedHorizontalDifference);
		
		verticalAppliedDifference = verticalMaximumDifference / verticalDifferenceDivider;
		horizontalAppliedDifference = horizontalMaximumDifference / horizontalDifferenceDivider;
		
		HashMap<Position, ThermicPoint> thermicPointMap = new HashMap<>();
		
		//----------------------------------------------
		//
		// Elso korben a DThermicConnector-okat osztja ki
		//
		//----------------------------------------------
		
		//Minden elemen vegig megyek
		for( Element element: elementSet ){
			
			Position startPoint = element.getStartPosition();
			Position endPoint = element.getEndPosition();
			double lambda;		

			double y = startPoint.getY();
			int iSteps = (int)Math.round((endPoint.getY() - y) / verticalAppliedDifference );
			for( int i = 0; i <= iSteps; i++){
				
				y = CommonOperations.get3Decimals( startPoint.getY() + i * verticalAppliedDifference );
				
				double x = startPoint.getX();				
				int jSteps = (int)Math.round((endPoint.getX() - x) / horizontalAppliedDifference);
				for( int j = 0; j <= jSteps; j++ ){
				
					x = CommonOperations.get3Decimals( startPoint.getX() + j * horizontalAppliedDifference );

					Position position = new Position(x, y);

					//Rakeresek a taroloban, hatha letezett mar ez elott is
					ThermicPoint tp = thermicPointMap.get( position );
					
					//Ha ez a Pont meg nem letezett
					if( null == tp ){
						
						//akkor letrehozom
						tp = new ThermicPoint( position );
						
						//Es el is mentem
						thermicPointMap.put( position, tp );
						
					}
					
					lambda = element.getLambda();	
					
					//Ha nem az elso elem balrol, de fuggolegesen lehet barmelyik
					if( j != 0 ){
						
						//Akkor elkerem a regi WEST kapcsolatat, hatha letezik  
						DThermicConnector oldWestConnector = (DThermicConnector)tp.getWestThermicConnector();
						
						//Akkor letezik ha legalso vagy legfelso Point-rol van szo es kapcsolodik egy masik, mar lehelyezett Element-hez
						if( null != oldWestConnector ){
						
							//Akkor a 2 lambda atlagat szamoljuk
							lambda = (lambda + oldWestConnector.getLambda()) / 2;
						}

						//Veszem a baloldali kozvetlen kapcsolatat, ami bizonyosan letezik, mivel belso pont						
						double previousX = CommonOperations.get3Decimals( startPoint.getX() + (j - 1) * horizontalAppliedDifference );

						//Es osszekottetest letesitek vele
						tp.connectToD(thermicPointMap.get(new Position(previousX, y)), ThermicPointOrientation.WEST, lambda );
					
					}
					
					lambda = element.getLambda();	
					
					//Ha nem az elso elem lentrol, de vizszintesen lehet barmelyik
					if( i != 0 ){
						
						//Akkor elkerem a regi SOUTH kapcsolatat, hatha letezik  
						DThermicConnector oldSouthConnector = (DThermicConnector)tp.getSouthThermicConnector();
						
						//Akkor letezik ha jobb vagy baloldali Point-rol van szo es kapcsolodik egy masik, mar lehelyezett Element-hez
						if( null != oldSouthConnector ){
						
							//Akkor a 2 lambda atlagat szamoljuk
							lambda = (lambda + oldSouthConnector.getLambda()) / 2;
						}

						//Veszem az alatta levo kozvetlen kapcsolatat, ami bizonyosan letezik, mivel belso pont
						double previousY = CommonOperations.get3Decimals( startPoint.getY() + (i - 1) * verticalAppliedDifference );						
						tp.connectToD(thermicPointMap.get(new Position(x, previousY)), ThermicPointOrientation.SOUTH, lambda );
					}					
				}
			}		
		}
		
		//--------------------------------------------------
		//
		// Masodik korben a kimaradt adatkapcsolatok potlasa
		// -A kulso korvonalat lezaro Connectorok 
		//  (Szabad feluletet biztosito konnektor es a Szimmetria lezarast biztosito konnektor)
		//
		//--------------------------------------------------
		
		//Minden elemen vegig megyek megegyszer
		for( Element element: elementSet ){
		
			HashSet<CloseElement> closeElements = element.getCloseElements();
			
			Position startPoint = element.getStartPosition();
			Position endPoint = element.getEndPosition();			

			double y = startPoint.getY();
			int iSteps = (int)Math.round((endPoint.getY() - y) / verticalAppliedDifference );
			for( int i = 0; i <= iSteps; i++){

				y = CommonOperations.get3Decimals( startPoint.getY() + i * verticalAppliedDifference );				
				
				double x = startPoint.getX();				
				int jSteps = (int)Math.round((endPoint.getX() - x) / horizontalAppliedDifference);
				for( int j = 0; j <= jSteps; j++ ){
					
					x = CommonOperations.get3Decimals( startPoint.getX() + j * horizontalAppliedDifference );
					
					Position position = new Position(x, y);

					ThermicPoint actualThermicPoint = thermicPointMap.get( position );
								
					//Ha bal-szelso Pont es a Pont-nak nincs WEST iranyu DThermicConnector-a
					if( j == 0 && null == actualThermicPoint.getWestThermicConnector() ){
						
						for( CloseElement closeElement: closeElements ){

							//Megfelelo pozicio
							if( closeElement.getOrientation().equals(SideOrientation.WEST ) && y >= closeElement.getLength().getStart() && y <= closeElement.getLength().getEnd() ){

								//Fal felulet
								if( closeElement instanceof SurfaceClose ){

									//Alfa es homerseklet kapcsolasa
									actualThermicPoint.connectToO(ThermicPointOrientation.WEST, ((SurfaceClose)closeElement).getAlpha(), ((SurfaceClose)closeElement).getAirTemperature() );		
									break;
								
								//Szimmetria el
								}else if( closeElement instanceof SymmetricClose ){
									
									actualThermicPoint.connectToS( ThermicPointOrientation.WEST);
									break;
								
								}									
							}
						}
					}
										
					//Ha jobb-szelso Pont es a Pont-nak nincs EAST iranyu DThermicConnector-a
					if( j == jSteps && null == actualThermicPoint.getEastThermicConnector() ){

						for( CloseElement closeElement: closeElements ){
						
							//Megfelelo pozicio
							if( closeElement.getOrientation().equals(SideOrientation.EAST ) && y >= closeElement.getLength().getStart() && y <= closeElement.getLength().getEnd() ){
							
								//Fal felulet
								if( closeElement instanceof SurfaceClose ){
								
									//Alfa es homerseklet kapcsolasa
									actualThermicPoint.connectToO(ThermicPointOrientation.EAST, ((SurfaceClose)closeElement).getAlpha(), ((SurfaceClose)closeElement).getAirTemperature() );
									break;
									
								//Szimmetria el
								}else if( closeElement instanceof SymmetricClose ){
	
									actualThermicPoint.connectToS( ThermicPointOrientation.EAST );
									break;
									
								}
							}
						}
					}
					
					//Ha also-szelso Pont es a Pont-nak nincs SOUTH iranyu DThermicConnector-a
					if( i == 0 && null == actualThermicPoint.getSouthThermicConnector()){
						
						for( CloseElement closeElement: closeElements ){
						
							//Megfelelo pozicio
							if( closeElement.getOrientation().equals(SideOrientation.SOUTH ) && x >= closeElement.getLength().getStart() && x <= closeElement.getLength().getEnd() ){
							
								//Fal felulet
								if( closeElement instanceof SurfaceClose ){
								
									//Alfa es homerseklet kapcsolasa
									actualThermicPoint.connectToO(ThermicPointOrientation.SOUTH, ((SurfaceClose)closeElement).getAlpha(), ((SurfaceClose)closeElement).getAirTemperature() );
									break;
										
								//Szimmetria el
								}else if( closeElement instanceof SymmetricClose ){
									
									actualThermicPoint.connectToS( ThermicPointOrientation.SOUTH );
									break;
									
								}
							}
						}
					}
					
					//Ha felso-szelso Pont es a Pont-nak nincs NORTH iranyu DThermicConnector-a
					if( i == iSteps && null == actualThermicPoint.getNorthThermicConnector() ){
						
						for( CloseElement closeElement: closeElements ){
						
							//Megfelelo pozicio
							if( closeElement.getOrientation().equals(SideOrientation.NORTH ) && x >= closeElement.getLength().getStart() && x <= closeElement.getLength().getEnd() ){
							
								//Fal felulet
								if( closeElement instanceof SurfaceClose ){								
								
									actualThermicPoint.connectToO(ThermicPointOrientation.NORTH, ((SurfaceClose)closeElement).getAlpha(), ((SurfaceClose)closeElement).getAirTemperature() );
									break;
							
								//Szimmetria el
								}else if( closeElement instanceof SymmetricClose ){
								
									actualThermicPoint.connectToS( ThermicPointOrientation.NORTH);
									break;
								}
							}						
						}
					}
				}
			}			
		}

		/*
		for( Element element: elementSet ){
						
			Position startPoint = element.getStartPosition();
			Position endPoint = element.getEndPosition();		

			double y = startPoint.getY();
			int iSteps = (int)Math.round((endPoint.getY() - y) / verticalAppliedDifference );
			for( int i = 0; i <= iSteps; i++){
				//y = (double)Math.round( (startPoint.getY() + i * dv ) * precision ) / precision;
				y = CommonOperations.get3Decimals(startPoint.getY() + i * verticalAppliedDifference );				
				
				double x = startPoint.getX();				
				int jSteps = (int)Math.round((endPoint.getX() - x) / horizontalAppliedDifference);
				for( int j = 0; j <= jSteps; j++ ){
					//x = (double)Math.round( ( startPoint.getX() + j * dh ) * precision ) / precision;
					x = CommonOperations.get3Decimals( startPoint.getX() + j * horizontalAppliedDifference );
					
				}
			}
		}*/
		
		return new ThermicPointList( thermicPointMap.values(), this );
		
	}
	
	/**
	 * Megallapitja a lehetseges legnagyobb differencia ertekeket vizszitnes illetve fuggoleges iranyban
	 * 
	 */
	private void doGenerateMaximumDifference(){
		
		ElementDoubleComparator elementDoubleComparator = new ElementDoubleComparator();
		
		LinkedHashSet<Double> verticalSpacingSet = new LinkedHashSet<>();
		LinkedHashSet<Double> horizontalSpacingSet = new LinkedHashSet<>();
		
		//Osztaspontok kigyujtese
		for( Element element: elementSet ){
			verticalSpacingSet.add( element.getStartPosition().getY() );
			verticalSpacingSet.add( element.getEndPosition().getY() );
			
			horizontalSpacingSet.add( element.getStartPosition().getX());
			horizontalSpacingSet.add( element.getEndPosition().getX());
			
		}

		//Osztaspontok sorbarendezese
		ArrayList<Double> verticalSpacingList = new ArrayList<Double>(verticalSpacingSet); 
		ArrayList<Double> horizontalSpacingList = new ArrayList<Double>(horizontalSpacingSet); 

		Collections.sort(verticalSpacingList, elementDoubleComparator );
		Collections.sort(horizontalSpacingList, elementDoubleComparator );	
		
		//Osztaskoz-tavolsagok kiszamitasa		
		ArrayList<Double> verticalDifferencesList = new ArrayList<Double>();
		ArrayList<Double> horizontalDifferencesList = new ArrayList<Double>();
		
		double startVertical = verticalSpacingList.get(0);		
		for( Double value : verticalSpacingList ){
			double difference = Math.abs( value - startVertical );
			if( difference != 0 ){
				verticalDifferencesList.add(difference);				
			}
			startVertical = value;
		}
		
		double startHorizontal = horizontalSpacingList.get(0);
		for( Double value : horizontalSpacingList ){
			double difference = Math.abs( value - startHorizontal);
			if( difference != 0 ){
				horizontalDifferencesList.add(difference);				
			}
			startHorizontal = value;
		}
		
		//Osztaskoz-tavolsagok sorbarendezese
		Collections.sort(verticalDifferencesList, elementDoubleComparator );
		Collections.sort(horizontalDifferencesList, elementDoubleComparator );
		
		verticalMaximumDifference = getMaximumDifference( verticalDifferencesList );
		horizontalMaximumDifference = getMaximumDifference( horizontalDifferencesList );
		
	}
	
	/**
	 * Visszaadja a parameterkent megadott listaban szereplo osztaskoz-tavolsagok-bol
	 * kiszamitott minimalis differencia erteket
	 * 
	 * @param sourceList
	 * @return
	 */
	private synchronized double getMaximumDifference( List<Double> sourceList ){
				
		//Az elso osztaskoz-tavolsag
		double pr = 1;
		double a = sourceList.get(0);
		
		//Vegig az osztaskoz-tavolsagokon
		for( Double b: sourceList ){

			b = CommonOperations.get3Decimals(b);
			
			boolean ok = false;
			
			//Probalgatas
			for( int k = 0; k<= precision; k++){
				
				a = CommonOperations.get3Decimals(a);
				
				//m az azt mondja meg, hogy hany reszre kell felbontani b-t
				double n = (k+pr)*b/a;
		
				//Ha nagyjabol egesz szamra jon ki
				if( (int)n < n + 1/precision &&  (int)n > n - 1/precision ){

					a = b;
					pr = n;
					ok = true;
					break;
				}
			}
			if( !ok ){
				System.err.println("K tulhaladta a megadott erteket");
				System.exit(-1);
			}
		}	
		
		return CommonOperations.get3Decimals( sourceList.get( sourceList.size() - 1 ) / pr ); 
	}
	
	private static class ElementDoubleComparator implements Comparator<Double>{

		@Override
		public int compare(Double o1, Double o2) {
			
			if( o1 > o2 ){
				return 1;
			}else if( o1 < o2 ){
				return -1;
			}else{
				return 0;
			}
			
		}		
	}
}
