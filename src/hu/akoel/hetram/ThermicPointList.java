package hu.akoel.hetram;

import hu.akoel.hetram.connectors.OThermicConnector;
import hu.akoel.hetram.connectors.DThermicConnector;
import hu.akoel.hetram.connectors.SThermicConnector;
import hu.akoel.hetram.connectors.ThermicConnector;
import hu.akoel.hetram.connectors.XDThermicConnector;
import hu.akoel.hetram.connectors.YDThermicConnector;

import java.util.Collection;

public class ThermicPointList{
	private ThermicPoint[] list;
	private int position = 0;
	
	public ThermicPointList( int size ){
	
		list = new ThermicPoint[size];
		
	}
	
	public ThermicPointList( Collection<ThermicPoint> thermicPointCollection ){
		
		list = new ThermicPoint[ thermicPointCollection.size() ];
		
		for( ThermicPoint tp :thermicPointCollection){
			add( tp );
		}
	}
	
	/**
	 * Termikus Pont hozzaadasa a listahoz
	 * 
	 * @param thermicPoint
	 */
	public void add( ThermicPoint thermicPoint ){
		
		//A Termikus pont lista-poziciojanak beallitasa
		thermicPoint.setPositionInTheList( position );
		
		//A Termikus pont Kezdeti erteke - Gauss iteracio kezdeti erteke
		thermicPoint.setActualTemperature( 1 );
		
		//A Termikus Pont elhelyezese a listaban
		list[position] = thermicPoint;
		
		//Lista mutatojanak novelese
		position++;
	}
	
	
	/**
	 * A sokismeretlenes egyenletrendszer megoldasa 
	 * eredmenye a Termikus Pontok homerseklete
	 * 
	 * @param minDifference
	 */
	public void solve( double minDifference ){
		
		double difference = -1;
	
		//Addig vegzi az iteraciot, amik a Termikus Pontok iteraciot megelozo
		//homersekletenek es az iteraciot koveto homersekletenek kulonbsege kisebb
		//nem lesz a parameterkent megadott engedelyezett elteresnel
		do{

			difference = -1;
			doIteration();
			
			for( int i = 0; i < getSize(); i++ ){
				difference = Math.max( difference, list[i].getTempDifference() );
			}
			
		}while( difference  > minDifference || difference < 0 );

	}
	
	/**
	 * 
	 * Egy iteracio elvegzese a lista teljes allomanyan
	 * 
	 */
	private void doIteration(){
		
		for( int i = 0; i < position; i++ ){
			
			ThermicConnector c;
			
			double nevezo = 0;
			double szamlalo = 0;
			double temperature;
			
			//NORTH
			c = list[i].getNorthThermicConnector();			
			//nevezo += list[i].getNorthThermicSurface();
			
			//Termikus Pont-Termikus Pont
			if( c instanceof YDThermicConnector ){
				
				YDThermicConnector dtc = (YDThermicConnector)c;
				szamlalo += ( dtc.getLambda()/dtc.getDelta()/2 ) * dtc.getNorthThermicPoint().getActualTemperature();
				nevezo += dtc.getLambda()/dtc.getDelta()/2;
				
			//Termikus Pont - szimmetrikus kapcsoalt
			}else if( c instanceof SThermicConnector ){
			
				YDThermicConnector cp = (YDThermicConnector)list[i].getSouthThermicConnector();
				szamlalo += ( cp.getLambda()/cp.getDelta()/2 ) * cp.getSouthThermicPoint().getActualTemperature();
				nevezo += cp.getLambda()/cp.getDelta()/2;
				
			//Termikus Pont - Szabad felszin
			}else if( c instanceof OThermicConnector ){
			
				szamlalo += ((OThermicConnector)c).getAlpha() * ((OThermicConnector)c).getAirTemperature(); 
				nevezo += ((OThermicConnector)c).getAlpha();
			}
			
			//EAST
			c = list[i].getEastThermicConnector();
			//nevezo += list[i].getEastThermicSurface();
			
			//Termikus Pont-Termikus Pont
			if( c instanceof XDThermicConnector ){
								
				szamlalo += ( ((DThermicConnector) c).getLambda()/((DThermicConnector) c).getDelta()/2 ) * ((XDThermicConnector)c).getEastThermicPoint().getActualTemperature();
				nevezo += ((DThermicConnector) c).getLambda()/((DThermicConnector) c).getDelta()/2;
			
			//Termikus Pont - szimmetrikus kapcsoalt
			}else if( c instanceof SThermicConnector ){
			
				ThermicConnector cp = list[i].getWestThermicConnector();
					
				szamlalo += ( ((DThermicConnector) cp).getLambda()/((DThermicConnector) cp).getDelta()/2 ) * ((XDThermicConnector)cp).getWestThermicPoint().getActualTemperature();
				nevezo += ((DThermicConnector) cp).getLambda()/((DThermicConnector) cp).getDelta()/2;
					
			//Termikus Pont - Szabad felszin
			}else if( c instanceof OThermicConnector ){
				
				//szamlalo += list[i].getEastThermicSurface() * ((AThermicConnector)c).getAirTemperature();
				szamlalo += ((OThermicConnector)c).getAlpha() * ((OThermicConnector)c).getAirTemperature();
				nevezo += ((OThermicConnector)c).getAlpha();
			}
			
			//SOUTH
			c = list[i].getSouthThermicConnector();
			//nevezo += list[i].getSouthThermicSurface();
			
			//Termikus Pont-Termikus Pont
			if( c instanceof YDThermicConnector ){
				
				szamlalo += ( ((DThermicConnector) c).getLambda()/((DThermicConnector) c).getDelta()/2 ) * ((YDThermicConnector)c).getSouthThermicPoint().getActualTemperature();
				nevezo += ((DThermicConnector) c).getLambda()/((DThermicConnector) c).getDelta()/2;

			//Termikus Pont - szimmetrikus kapcsoalt
			}else if( c instanceof SThermicConnector ){
				
				ThermicConnector cp = list[i].getNorthThermicConnector();		
				szamlalo += ( ((DThermicConnector) cp).getLambda()/((DThermicConnector) cp).getDelta()/2 ) * ((YDThermicConnector)cp).getNorthThermicPoint().getActualTemperature();
				nevezo += ((DThermicConnector) cp).getLambda()/((DThermicConnector) cp).getDelta()/2;
						
			//Termikus Pont - Szabad felszin
			}else if( c instanceof OThermicConnector ){
				
				szamlalo += ((OThermicConnector)c).getAlpha() * ((OThermicConnector)c).getAirTemperature();
				nevezo += ((OThermicConnector)c).getAlpha();
			}
						
			//WEST
			c = list[i].getWestThermicConnector();
			//nevezo += list[i].getWestThermicSurface();
			
			//Termikus Pont-Termikus Pont
			if( c instanceof XDThermicConnector ){
				
				szamlalo += ( ((DThermicConnector) c).getLambda()/((DThermicConnector) c).getDelta()/2 ) * ((XDThermicConnector)c).getWestThermicPoint().getActualTemperature();
				nevezo += ((DThermicConnector) c).getLambda()/((DThermicConnector) c).getDelta()/2;
				
			//Termikus Pont - szimmetrikus kapcsoalt
			}else if( c instanceof SThermicConnector ){
				
				ThermicConnector cp = list[i].getEastThermicConnector();
				
				szamlalo += ( ((DThermicConnector) cp).getLambda()/((DThermicConnector) cp).getDelta()/2 ) * ((XDThermicConnector)cp).getEastThermicPoint().getActualTemperature();
				nevezo += ((DThermicConnector) cp).getLambda()/((DThermicConnector) cp).getDelta()/2;
			
			//Termikus Pont - Szabad felszin
			}else if( c instanceof OThermicConnector ){
				
				szamlalo += ((OThermicConnector)c).getAlpha() * ((OThermicConnector)c).getAirTemperature();
				nevezo += ((OThermicConnector)c).getAlpha();
				
			}
			
			temperature = szamlalo/nevezo;
			list[i].setActualTemperature( temperature );
			
		}
		
	}
	
	/**
	 * Visszaadja a lista meretet
	 * 
	 * @return
	 */
	public int getSize(){
		return position;
	}
	
	/**
	 * Visszaadja az adott lista-pozicioban levo Termikus Pontot
	 * 
	 * @param position
	 * @return
	 */
	public ThermicPoint get( int position ){
		return list[position];
	}
}
