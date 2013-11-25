package hu.akoel.hetram;

import hu.akoel.hetram.ThermicPoint.Orientation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

public class ElementSet{

	private double precisionInM = 0.0001;
	private HashSet<Element> elementSet = new HashSet<>();	
	private double verticalMaximumDifference = -1;
	private double horizontalMaximumDifference = -1;

	/**
	 * Hozzaad egy Element-et a listahoz
	 * 
	 * @param element
	 * @return
	 */
	public boolean add( Element element ){
		boolean result = elementSet.add( element );		
		doGenerateMinimalDifference();		
		return result;
	}
	
	public boolean remove( Element element){
		boolean result = elementSet.remove(element);
		doGenerateMinimalDifference();		
		return result;
	}	
	
	
	public double getVerticalMaximumDifference(){
		return verticalMaximumDifference;
	}
	
	public double getHorizontalMaximumDifference(){
		return horizontalMaximumDifference;
	}
	
	public Iterator<Element> iterator(){
		return elementSet.iterator();
	}
	
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
	
	public Collection<ThermicPoint> doValami( double askedHorizontalDifference, double askedVerticalDifference ){
		
		double dv = getVerticalSuggestedDifference(askedVerticalDifference);
		double dh = getHorizontalSuggestedDifference(askedHorizontalDifference);
		
		HashMap<Position, ThermicPoint> thermicPointMap = new HashMap<>();
		
//System.err.println("dv: " + dv + " dh: " + dh);		
		//Minden elemen vegig megyek
		for( Element element: elementSet ){
			
			Position startPoint = element.getStartPosition();
			Position endPoint = element.getEndPosition();
//System.err.println("Elem: " + element);			

			double y = startPoint.getY();
			int iSteps = (int)Math.round((endPoint.getY() - y) / dv );
			for( int i = 0; i <= iSteps; i++){
				y = (double)Math.round( (startPoint.getY() + i * dv ) / precisionInM ) * precisionInM;
				
				double x = startPoint.getX();				
				int jSteps = (int)Math.round((endPoint.getX() - x) / dh);
				for( int j = 0; j <= jSteps; j++ ){
					x = (double)Math.round( ( startPoint.getX() + j * dh ) / precisionInM ) * precisionInM;

					Position position = new Position(x, y);

					//Termikus pont letrehozas
					ThermicPoint tp = new ThermicPoint( position );
				
					//Termikus pont elhelyezese a taroloban
					thermicPointMap.put( position, tp );
					
					double previousX;
					double previousY;

					//Az elobbb elmentett pont visszaolvasasa. Mert lehet, hogy mar letezett ezzel a kulcsal
					ThermicPoint actualTermalPoint = thermicPointMap.get( position );
					
					//Ha nem az elso elem balrol
					if( x != 0 ){
						
						//Biztos, hogy DThermicConnector WEST fele, mert belso pont 
						DThermicConnector oldWestConnector = (DThermicConnector)actualTermalPoint.getWestThermicConnector();
						double lambda = element.getLambda();
						
						//Mar definialva volt egy WEST kapcsolat, vagyis ez egy Masik element EAST tagja
						if( null != oldWestConnector ){
						
							//Akkor a 2 lambda atlagat szamoljuk
							lambda = (lambda + oldWestConnector.getLambda()) / 2;
						}
							
						//Ha volt mar, ha nem, a WEST iranyu kapcsolatot letrehozom/felulirom
						previousX = (double)Math.round( ( startPoint.getX() + (j - 1) * dh ) / precisionInM ) * precisionInM;
						tp.connectTo(thermicPointMap.get(new Position(previousX, y)), Orientation.WEST, lambda );
					
					}
					
					//Ha nem az elso elem lentrol
					if( y != 0 ){
						
						//Biztos, hogy DThermicConnector SOUTH fele, mert belso pont 
						DThermicConnector oldSouthConnector = (DThermicConnector)actualTermalPoint.getSouthThermicConnector();
						double lambda = element.getLambda();
						
						//Mar definialva volt egy SOUTH kapcsolat, vagyis ez egy masik Element NORTH tagja
						if( null != oldSouthConnector ){
						
							//Akkor a 2 lambda atlagat szamoljuk
							lambda = (lambda + oldSouthConnector.getLambda()) / 2;
						}
						
						//Ha volt mar, ha nem, a SOUTH iranyu kapcsolatot letrehozom/felulirom
						previousY = (double)Math.round( ( startPoint.getY() + (i - 1) * dv ) / precisionInM ) * precisionInM;
						tp.connectTo(thermicPointMap.get(new Position(x, previousY)), Orientation.SOUTH, lambda );
					}
					
//System.out.println("("+x + ", " + y+")" + " " + actualTermalPoint.getActualTemperature());					
				}
			}
			
//System.err.println();			
		}
		
		//
		//Kimaradt adatkapcsolatok potlasa
		//
		//Minden elemen vegig megyek megegyszer
		for( Element element: elementSet ){
		
			AThermicConnector ntc = element.getNorthAThermicConnector();
			AThermicConnector etc = element.getEastAThermicConnector();
			AThermicConnector stc = element.getSouthAThermicConnector();
			AThermicConnector wtc = element.getWestAThermicConnector();
						
			Position startPoint = element.getStartPosition();
			Position endPoint = element.getEndPosition();		

			double y = startPoint.getY();
			int iSteps = (int)Math.round((endPoint.getY() - y) / dv );
			for( int i = 0; i <= iSteps; i++){
				y = (double)Math.round( (startPoint.getY() + i * dv ) / precisionInM ) * precisionInM;
				
				double x = startPoint.getX();				
				int jSteps = (int)Math.round((endPoint.getX() - x) / dh);
				for( int j = 0; j <= jSteps; j++ ){
					x = (double)Math.round( ( startPoint.getX() + j * dh ) / precisionInM ) * precisionInM;
					
					double lambda = element.getLambda();
					
					Position position = new Position(x, y);

					ThermicPoint actualThermalPoint = thermicPointMap.get( position );
								
					//Bal szelso ThermicPoint es van AThermicConnector definialva szamara, vagyis szelso elem
					if( j == 0 && null != wtc ){
						actualThermalPoint.connectTo(Orientation.WEST, wtc.getAlpha(), wtc.getAirTemperature() );
					}
					//Jobb szelso ThermicPoint es van AThermicConnector definialva
					if( j == jSteps && null != etc ){
						actualThermalPoint.connectTo(Orientation.EAST, etc.getAlpha(), etc.getAirTemperature() );
					}
					//Deli ThermicPoint es van AThermicConnector definialva
					if( i == 0 && null != stc ){
						actualThermalPoint.connectTo(Orientation.SOUTH, stc.getAlpha(), stc.getAirTemperature() );
					}
					//Eszaki ThermicPoint es van AThermicConnector definialva
					if( i == iSteps && null != ntc ){
						actualThermalPoint.connectTo(Orientation.NORTH, ntc.getAlpha(), ntc.getAirTemperature() );
					}
					
//!!!!!!!!!!!!
//Ezt parameterezni kell !!!\
//!!!!!!!!!!!
					
					if( j == jSteps ){
						
//						double x0 = (double)Math.round( ( startPoint.getX() + 0 * dh ) / precisionInM ) * precisionInM;
//						ThermicPoint tp0 = thermicPointMap.get( new Position( x0, y) );
						
//						actualThermalPoint.connectTo(tp0, Orientation.EAST, lambda);
					}
				}
			}
		}

		
		for( Element element: elementSet ){
						
			Position startPoint = element.getStartPosition();
			Position endPoint = element.getEndPosition();		

			double y = startPoint.getY();
			int iSteps = (int)Math.round((endPoint.getY() - y) / dv );
			for( int i = 0; i <= iSteps; i++){
				y = (double)Math.round( (startPoint.getY() + i * dv ) / precisionInM ) * precisionInM;
				
				double x = startPoint.getX();				
				int jSteps = (int)Math.round((endPoint.getX() - x) / dh);
				for( int j = 0; j <= jSteps; j++ ){
					x = (double)Math.round( ( startPoint.getX() + j * dh ) / precisionInM ) * precisionInM;
					
					ThermicPoint actualThermalPoint = thermicPointMap.get( new Position(x,y) );
System.out.println("("+x + ", " + y+")" + " " + actualThermalPoint );
					
				}
			}
		}

		
		return thermicPointMap.values();
		
	}
	
	private void doGenerateMinimalDifference(){
		
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
			double difference = value - startVertical;
			if( difference != 0 ){
				verticalDifferencesList.add(difference);				
			}
			startVertical = value;
		}
		
		double startHorizontal = horizontalSpacingList.get(0);
		for( Double value : horizontalSpacingList ){
			double difference = value - startHorizontal;
			if( difference != 0 ){
				horizontalDifferencesList.add(difference);				
			}
			startHorizontal = value;
		}
		
		//Osztaskoz-tavolsagok sorbarendezese
		Collections.sort(verticalDifferencesList, elementDoubleComparator );
		Collections.sort(horizontalDifferencesList, elementDoubleComparator );
		
		verticalMaximumDifference = getMinimalDifference( verticalDifferencesList );
		horizontalMaximumDifference = getMinimalDifference( horizontalDifferencesList );
		
	}
	
	/**
	 * Visszaadja a parameterkent megadott listaban szereplo osztaskoz-tavolsagok-bol
	 * kiszamitott minimalis differencia erteket
	 * 
	 * @param sourceList
	 * @return
	 */
	private double getMinimalDifference( List<Double> sourceList ){
				
		//Az elso osztaskoz-tavolsag
		double pr = 1;
		double a = sourceList.get(0);
		
		//Vegig az osztaskoz-tavolsagokon
		for( Double b: sourceList ){
			boolean ok = false;
			
			//Probalgatas
			for( int k = 0; k<= 1/precisionInM; k++){			
				double n = (k+pr)*b/a;
				
				//Ha nagyjabol egesz szamra jon ki
				if( (int)n < n + precisionInM &&  (int)n > n - precisionInM ){

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
		
		return sourceList.get( sourceList.size() - 1 ) / pr; 
	}
	
	static class ElementDoubleComparator implements Comparator<Double>{

		@Override
		public int compare(Double o1, Double o2) {
			
	      return (int) ( o1 - o2 );
			
		}		
	}
}
