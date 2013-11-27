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
			
			double nevezo = 0;
			double szamlalo = 0;
			double temperature;
			
			ThermicConnector cN = list[i].getNorthThermicConnector();	
			ThermicConnector cE = list[i].getEastThermicConnector();
			ThermicConnector cS = list[i].getSouthThermicConnector();
			ThermicConnector cW = list[i].getWestThermicConnector();			
			
			double dx = 0;
			if( cW instanceof XDThermicConnector ){
				dx += ((XDThermicConnector)cW).getDelta()/2;
			}
			if( cE instanceof XDThermicConnector ){
				dx += ((XDThermicConnector)cE).getDelta()/2;
			}
			
			double dy = 0;
			if( cN instanceof YDThermicConnector ){
				dy += ((YDThermicConnector)cN).getDelta()/2;
			}
			if( cS instanceof YDThermicConnector ){
				dy += ((YDThermicConnector)cS).getDelta()/2;
			}
			
			//NORTH
			//
			//Termikus Pont-Termikus Pont
			if( cN instanceof YDThermicConnector ){
				
				YDThermicConnector dtc = (YDThermicConnector)cN;
				//szamlalo += ( dtc.getLambda()/dtc.getDelta()/2 ) * dtc.getNorthThermicPoint().getActualTemperature();
				//nevezo += dtc.getLambda()/dtc.getDelta()/2;
				szamlalo += dx * ( dtc.getLambda() / dy ) * dtc.getNorthThermicPoint().getActualTemperature();
				nevezo += dx * dtc.getLambda() / dy;

				
			//Termikus Pont - szimmetrikus kapcsoalt
			}else if( cN instanceof SThermicConnector ){
			
				//Veszi a Pont felett levo Pontot osszekoto konnektort
				YDThermicConnector cp = (YDThermicConnector)list[i].getSouthThermicConnector();
				//szamlalo += ( cp.getLambda()/cp.getDelta()/2 ) * cp.getSouthThermicPoint().getActualTemperature();
				//nevezo += cp.getLambda()/cp.getDelta()/2;
				szamlalo += dx * ( cp.getLambda()/cp.getDelta() / dy ) * cp.getSouthThermicPoint().getActualTemperature();
				nevezo += dx * cp.getLambda()/cp.getDelta() / dy;
				
			//Termikus Pont - Szabad felszin
			}else if( cN instanceof OThermicConnector ){
				
				szamlalo += ((OThermicConnector)cN).getAlpha() * ((OThermicConnector)cN).getAirTemperature() * dx; 
				nevezo += ((OThermicConnector)cN).getAlpha() * dx;
			
			//Hiba-Nem lehet, hogy egy pontot nem zar le Connector
			}else{
				//TODO exception
			}
			
			//EAST
			//
			//Termikus Pont-Termikus Pont
			if( cE instanceof XDThermicConnector ){
								
				//szamlalo += ( ((DThermicConnector) cE).getLambda()/((DThermicConnector) cE).getDelta()/2 ) * ((XDThermicConnector)cE).getEastThermicPoint().getActualTemperature();
				//nevezo += ((DThermicConnector) cE).getLambda()/((DThermicConnector) cE).getDelta()/2;
				szamlalo += dy * ( ((DThermicConnector) cE).getLambda() / dx ) * ((XDThermicConnector)cE).getEastThermicPoint().getActualTemperature();
				nevezo += dy * ((DThermicConnector) cE).getLambda() / dx;
			
			//Termikus Pont - szimmetrikus kapcsoalt
			}else if( cE instanceof SThermicConnector ){
			
				//Veszi a Pont-tol balra levo Pont-ot osszekoto Konnektort
				ThermicConnector cp = list[i].getWestThermicConnector();					
				//szamlalo += ( ((DThermicConnector) cp).getLambda()/((DThermicConnector) cp).getDelta()/2 ) * ((XDThermicConnector)cp).getWestThermicPoint().getActualTemperature();
				//nevezo += ((DThermicConnector) cp).getLambda()/((DThermicConnector) cp).getDelta()/2;
				szamlalo += dy * ( ((DThermicConnector) cp).getLambda() / dx ) * ((XDThermicConnector)cp).getWestThermicPoint().getActualTemperature();
				nevezo += dy * ((DThermicConnector) cp).getLambda() / dx;
					
			//Termikus Pont - Szabad felszin
			}else if( cE instanceof OThermicConnector ){
				
				szamlalo += ((OThermicConnector)cE).getAlpha() * ((OThermicConnector)cE).getAirTemperature() * dy;
				nevezo += ((OThermicConnector)cE).getAlpha() * dy;

			//Hiba-Nem lehet, hogy egy pontot nem zar le Connector
			}else{
				//TODO exception
			}
			
			//SOUTH
			//
			//Termikus Pont-Termikus Pont
			if( cS instanceof YDThermicConnector ){
				
				//szamlalo += ( ((DThermicConnector) cS).getLambda()/((DThermicConnector) cS).getDelta()/2 ) * ((YDThermicConnector)cS).getSouthThermicPoint().getActualTemperature();
				//nevezo += ((DThermicConnector) cS).getLambda()/((DThermicConnector) cS).getDelta()/2;
				szamlalo += dx * ( ((DThermicConnector) cS).getLambda() / dy ) * ((YDThermicConnector)cS).getSouthThermicPoint().getActualTemperature();
				nevezo += dx * ((DThermicConnector) cS).getLambda() / dy;

			//Termikus Pont - szimmetrikus kapcsoalt
			}else if( cS instanceof SThermicConnector ){
				
				//Veszi a Pont felett levo Pontot osszekoto Konnektort
				ThermicConnector cp = list[i].getNorthThermicConnector();		
				//szamlalo += ( ((DThermicConnector) cp).getLambda()/((DThermicConnector) cp).getDelta()/2 ) * ((YDThermicConnector)cp).getNorthThermicPoint().getActualTemperature();
				//nevezo += ((DThermicConnector) cp).getLambda()/((DThermicConnector) cp).getDelta()/2;
				szamlalo += dx * (((DThermicConnector) cp).getLambda()/ dy ) * ((YDThermicConnector)cp).getNorthThermicPoint().getActualTemperature();
				nevezo += dx * ((DThermicConnector) cp).getLambda()/ dy;
						
			//Termikus Pont - Szabad felszin
			}else if( cS instanceof OThermicConnector ){
				
				szamlalo += ((OThermicConnector)cS).getAlpha() * ((OThermicConnector)cS).getAirTemperature() * dx;
				nevezo += ((OThermicConnector)cS).getAlpha() * dx;
			
			//Hiba-Nem lehet, hogy egy pontot nem zar le Connector
			}else{
				//TODO exception
			}
						
			//WEST
			//
			//Termikus Pont-Termikus Pont
			if( cW instanceof XDThermicConnector ){
				
				//szamlalo += ( ((DThermicConnector) cW).getLambda()/((DThermicConnector) cW).getDelta()/2 ) * ((XDThermicConnector)cW).getWestThermicPoint().getActualTemperature();
				//nevezo += ((DThermicConnector) cW).getLambda()/((DThermicConnector) cW).getDelta()/2;
				szamlalo += dy * ( ((DThermicConnector) cW).getLambda() / dx ) * ((XDThermicConnector)cW).getWestThermicPoint().getActualTemperature();
				nevezo += dy * ((DThermicConnector) cW).getLambda() / dx;
				
			//Termikus Pont - szimmetrikus kapcsoalt
			}else if( cW instanceof SThermicConnector ){
				
				//Veszi a Ponttol jobbra levo Pontot osszekoto Konnektort
				ThermicConnector cp = list[i].getEastThermicConnector();				
				//szamlalo += ( ((DThermicConnector) cp).getLambda()/((DThermicConnector) cp).getDelta()/2 ) * ((XDThermicConnector)cp).getEastThermicPoint().getActualTemperature();
				//nevezo += ((DThermicConnector) cp).getLambda()/((DThermicConnector) cp).getDelta()/2;
				szamlalo += dy * ( ((DThermicConnector) cp).getLambda() / dx ) * ((XDThermicConnector)cp).getEastThermicPoint().getActualTemperature();
				nevezo += dy * ((DThermicConnector) cp).getLambda() / dx;
			
			//Termikus Pont - Szabad felszin
			}else if( cW instanceof OThermicConnector ){
				
				szamlalo += ((OThermicConnector)cW).getAlpha() * ((OThermicConnector)cW).getAirTemperature() * dy;
				nevezo += ((OThermicConnector)cW).getAlpha() * dy;
			
			//Hiba-Nem lehet, hogy egy pontot nem zar le Connector
			}else{
				//TODO exception
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
