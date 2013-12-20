package hu.akoel.hetram.structures;

import hu.akoel.hetram.accessories.CommonOperations;
import hu.akoel.hetram.accessories.Position;
import hu.akoel.hetram.connectors.DThermicConnector;
import hu.akoel.hetram.structures.Structure.SideOrientation;
import hu.akoel.hetram.thermicpoint.ThermicPoint;
import hu.akoel.hetram.thermicpoint.ThermicPointList;
import hu.akoel.hetram.thermicpoint.ThermicPoint.ThermicPointOrientation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class StructureSet{

	private double precision = 10000;
	private HashSet<Structure> elementSet = new HashSet<>();	
	
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
	public boolean add( Structure element ){
		boolean result = elementSet.add( element );		
		doGenerateMaximumDifference();		
		return result;
	}
	
	public boolean remove( Structure element){
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

	public Iterator<Structure> iterator(){
		return elementSet.iterator();
	}
	
	/**
	 * Automatikusan felbontja kis differencialis negyzetekre az osszes elemet,
	 * legyartja a termikus pontokat es megteremti kozottuk a kapcsolatot
	 * !!! Szamitas nem tortenik !!!
	 * 
	 * @param askedHorizontalDifference
	 * @param askedVerticalDifference
	 * @return
	 */

	public ThermicPointList generateThermicPoints( ){
		
		verticalAppliedDifference = verticalMaximumDifference / verticalDifferenceDivider;
		horizontalAppliedDifference = horizontalMaximumDifference / horizontalDifferenceDivider;
		
		HashMap<Position, ThermicPoint> thermicPointMap = new HashMap<>();
		
		//----------------------------------------------
		//
		// Elso korben a Termikus Pont-ok legyartasa es
		// a DThermicConnector-ok kiosztasa
		//
		//----------------------------------------------
		
		//Minden elemen vegig megyek
		for( Structure element: elementSet ){
			
			//Veszem az elem kezdo es veg pozicioit
			Position startPoint = element.getStartPosition();
			Position endPoint = element.getEndPosition();
			double lambda;		

			//Mindig a kezdo vertikalis pontbol indulok
			double y = startPoint.getY();
			
			//Vertikalis felbontas
			int iSteps = (int)Math.round((endPoint.getY() - y) / verticalAppliedDifference );
			
			//Vegig a vertikalis pontokon
			for( int i = 0; i <= iSteps; i++){
				
				//Az aktualis vertikalis point
				y = CommonOperations.get10Decimals( startPoint.getY() + i * verticalAppliedDifference );
				
				//Elindul a kezdo horizontalis pontbol
				double x = startPoint.getX();			
				
				//Horizontalis felbontas
				int jSteps = (int)Math.round((endPoint.getX() - x) / horizontalAppliedDifference);
				
				//Vegig a horizontalis pontokon
				for( int j = 0; j <= jSteps; j++ ){
				
					//Az aktualis horizontalis pont
					x = CommonOperations.get10Decimals( startPoint.getX() + j * horizontalAppliedDifference );

					//Az aktualis pont pozicioja
					Position position = new Position(x, y);

					//Rakeresek a taroloban, hatha letezett mar ez elott is
					ThermicPoint tp = thermicPointMap.get( position );
					
					//Ha ez a pont meg nem letezett
					if( null == tp ){
						
						//akkor letrehozom
						tp = new ThermicPoint( position );
						
						//Es el is mentem
						thermicPointMap.put( position, tp );
						
					}
					
					//Az elem lamba-ja
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
						double previousX = CommonOperations.get10Decimals( startPoint.getX() + (j - 1) * horizontalAppliedDifference );

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
						double previousY = CommonOperations.get10Decimals( startPoint.getY() + (i - 1) * verticalAppliedDifference );						
						tp.connectToD(thermicPointMap.get(new Position(x, previousY)), ThermicPointOrientation.SOUTH, lambda );
					}					
				}
			}		
		}
		
		//--------------------------------------------------
		//
		// Masodik korben a kimaradt adatkapcsolatok potlasa
		// -A kulso korvonalat lezaro Connectorok 
		// -Szimmetria lezarast biztosito konnektor
		//
		//--------------------------------------------------
		
		//Minden elemen vegig megyek megegyszer
		for( Structure element: elementSet ){
		
			HashSet<AStructureSealing> closeElements = element.getCloseElements();
			
			Position startPoint = element.getStartPosition();
			Position endPoint = element.getEndPosition();			

			//Kezdo vertikalis pontbol indulok
			double y = startPoint.getY();
			
			//Vertikalis felbontas
			int iSteps = (int)Math.round((endPoint.getY() - y) / verticalAppliedDifference );
			
			//Vegig a vertikalis pontokon
			for( int i = 0; i <= iSteps; i++){

				//Az aktualis vertikalis pont
				y = CommonOperations.get10Decimals( startPoint.getY() + i * verticalAppliedDifference );				
				
				//Kezdo horizontalis pontbol indulok
				double x = startPoint.getX();		
				
				//Horizontalis felbontas
				int jSteps = (int)Math.round((endPoint.getX() - x) / horizontalAppliedDifference);
				
				//Vegig a horizontalis pontokon
				for( int j = 0; j <= jSteps; j++ ){
					
					//Az aktualis horizontalis pont
					x = CommonOperations.get10Decimals( startPoint.getX() + j * horizontalAppliedDifference );
					
					//Az aktualis pont pozicioja
					Position position = new Position(x, y);

					//Az aktualis pontban elhelyezkedo Termikus pont
					ThermicPoint actualThermicPoint = thermicPointMap.get( position );
					
					//----------------------------
					//
					// BAL SZELSO PONT WEST irany
					//
					//----------------------------
					
					//
					//Ha bal-szelso Pont es a Pont-nak nincs WEST iranyu DThermicConnector-a
					//
					if( j == 0 && null == actualThermicPoint.getWestThermicConnector() ){
						
						for( AStructureSealing closeElement: closeElements ){

							//Megfelelo pozicio
							if( closeElement.getOrientation().equals(SideOrientation.WEST ) && y >= closeElement.getLength().getStart() && y <= closeElement.getLength().getEnd() ){

								//Fal felulet
								if( closeElement instanceof SurfaceSealing ){

									//Alfa es homerseklet kapcsolasa
									actualThermicPoint.connectToO(ThermicPointOrientation.WEST, ((SurfaceSealing)closeElement).getAlpha(), ((SurfaceSealing)closeElement).getAirTemperature() );		
									break;
								
								//Szimmetria el
								}else if( closeElement instanceof SymmetricSealing ){
									
									actualThermicPoint.connectToS( ThermicPointOrientation.WEST);
									break;
								
								}									
							}
						}
					}
					
					//Ha esetleg meg mindig nincs lezarva a bal-szelso pont WEST iranyban
					//Ha nincs definialva semmilyen lezaras, akkor az szimmetria pont lesz
					if( j == 0 && null == actualThermicPoint.getWestThermicConnector() ){
						actualThermicPoint.connectToS( ThermicPointOrientation.WEST );
					}						
					
					//-----------------------------
					//
					// JOBB SZELSO PONT EAST irany
					//
					//-----------------------------
					
					//Ha jobb-szelso Pont es a Pont-nak nincs EAST iranyu DThermicConnector-a
					if( j == jSteps && null == actualThermicPoint.getEastThermicConnector() ){

						for( AStructureSealing closeElement: closeElements ){
						
							//Megfelelo pozicio
							if( closeElement.getOrientation().equals(SideOrientation.EAST ) && y >= closeElement.getLength().getStart() && y <= closeElement.getLength().getEnd() ){
							
								//Fal felulet
								if( closeElement instanceof SurfaceSealing ){
								
									//Alfa es homerseklet kapcsolasa
									actualThermicPoint.connectToO(ThermicPointOrientation.EAST, ((SurfaceSealing)closeElement).getAlpha(), ((SurfaceSealing)closeElement).getAirTemperature() );
									break;
									
								//Szimmetria el
								}else if( closeElement instanceof SymmetricSealing ){
	
									actualThermicPoint.connectToS( ThermicPointOrientation.EAST );
									break;
									
								}
							}
						}
					}
					
					//Ha esetleg meg mindig nincs lezarva a jobb-szelso pont EAST iranyban
					//Ha nincs definialva semmilyen lezaras, akkor az szimmetria pont lesz
					if( j == jSteps && null == actualThermicPoint.getEastThermicConnector() ){
						actualThermicPoint.connectToS( ThermicPointOrientation.EAST );
					}	
					
					//------------------------------
					//
					// ALSO SZELSO PONT SOUTH irany
					//
					//------------------------------

					//Ha also-szelso Pont es a Pont-nak nincs SOUTH iranyu DThermicConnector-a
					if( i == 0 && null == actualThermicPoint.getSouthThermicConnector()){
						
						for( AStructureSealing closeElement: closeElements ){
						
							//Megfelelo pozicio
							if( closeElement.getOrientation().equals(SideOrientation.SOUTH ) && x >= closeElement.getLength().getStart() && x <= closeElement.getLength().getEnd() ){
							
								//Fal felulet
								if( closeElement instanceof SurfaceSealing ){
								
									//Alfa es homerseklet kapcsolasa
									actualThermicPoint.connectToO(ThermicPointOrientation.SOUTH, ((SurfaceSealing)closeElement).getAlpha(), ((SurfaceSealing)closeElement).getAirTemperature() );
									break;
										
								//Szimmetria el
								}else if( closeElement instanceof SymmetricSealing ){
									
									actualThermicPoint.connectToS( ThermicPointOrientation.SOUTH );
									break;
									
								}
							}
						}
					}
					
					//Ha esetleg meg mindig nincs lezarva az also-szelso pont SOUTH iranyban
					//Ha nincs definialva semmilyen lezaras, akkor az szimmetria pont lesz
					if( i == 0 && null == actualThermicPoint.getSouthThermicConnector() ){
						actualThermicPoint.connectToS( ThermicPointOrientation.SOUTH );
					}	
					
					//------------------------------
					//
					// FELSO SZELSO PONT NORTH irany
					//
					//------------------------------

					//Ha felso-szelso Pont es a Pont-nak nincs NORTH iranyu DThermicConnector-a
					if( i == iSteps && null == actualThermicPoint.getNorthThermicConnector() ){
						
						for( AStructureSealing closeElement: closeElements ){
						
							//Megfelelo pozicio
							if( closeElement.getOrientation().equals(SideOrientation.NORTH ) && x >= closeElement.getLength().getStart() && x <= closeElement.getLength().getEnd() ){
							
								//Fal felulet
								if( closeElement instanceof SurfaceSealing ){								
								
									actualThermicPoint.connectToO(ThermicPointOrientation.NORTH, ((SurfaceSealing)closeElement).getAlpha(), ((SurfaceSealing)closeElement).getAirTemperature() );
									break;
							
								//Szimmetria el
								}else if( closeElement instanceof SymmetricSealing ){
								
									actualThermicPoint.connectToS( ThermicPointOrientation.NORTH);
									break;
								}
							}						
						}
					}
					
					//Ha esetleg meg mindig nincs lezarva a felso-szelso pont NORTH iranyban
					//Ha nincs definialva semmilyen lezaras, akkor az szimmetria pont lesz
					if( i == iSteps && null == actualThermicPoint.getNorthThermicConnector() ){
						actualThermicPoint.connectToS( ThermicPointOrientation.NORTH );
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
		for( Structure element: elementSet ){
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
	 * Legnagyobb kozos osztot adja vissza
	 * @param a
	 * @param b
	 * @return
	 */
	public int LNKO(int a, int b){
	     if (a == 0)
	        return b;
	     if (b == 0)
	        return a;
	 
	     if (a > b)
	        return LNKO(a % b, b);
	     else
	        return LNKO(a, b % a);
	}
	
	
	
	/**
	 * Visszaadja a parameterkent megadott listaban szereplo osztaskoz-tavolsagok-bol
	 * kiszamitott minimalis differencia erteket
	 * 
	 * @param sourceList
	 * @return
	 */
	private double getMaximumDifference( List<Double> sourceList ){
		int prec = 1000;
				
		int a = (int)( prec * CommonOperations.get10Decimals( sourceList.get(0) ) );
		int b;
		
		//Vegig az osztaskoz-tavolsagokon
		for( Double s: sourceList ){

			b = (int)( prec * CommonOperations.get10Decimals( s ) );

			a = LNKO(a, b);
			
		}
		return CommonOperations.get10Decimals( (double)a / (double)prec );
		
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
