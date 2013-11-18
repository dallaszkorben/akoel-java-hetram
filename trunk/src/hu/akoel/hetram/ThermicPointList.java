package hu.akoel.hetram;

public class ThermicPointList{
	private ThermicPoint[] list;
	private int position = 0;
	
	public ThermicPointList( int size ){
	
		list = new ThermicPoint[size];
		
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
			nevezo += list[i].getNorthTag();
			if( c instanceof YDThermicConnector ){
				
				szamlalo += list[i].getNorthTag() * ((YDThermicConnector)c).getNorthThermicPoint().getActualTemperature();		
				
			}else if( c instanceof AThermicConnector ){
			
				szamlalo += list[i].getNorthTag() * ((AThermicConnector)c).getAirTemperature(); 
			}
			
			//EAST
			c = list[i].getEastThermicConnector();
			nevezo += list[i].getEastTag();
			if( c instanceof XDThermicConnector ){
								
				szamlalo += list[i].getEastTag() * ((XDThermicConnector)c).getEastThermicPoint().getActualTemperature();
				
			}else if( c instanceof AThermicConnector ){
				
				szamlalo += list[i].getEastTag() * ((AThermicConnector)c).getAirTemperature();
			}
			
			//SOUTH
			c = list[i].getSouthThermicConnector();
			nevezo += list[i].getSouthTag();
			if( c instanceof YDThermicConnector ){
				
				szamlalo += list[i].getSouthTag() * ((YDThermicConnector)c).getSouthThermicPoint().getActualTemperature();
				
			}else if( c instanceof AThermicConnector ){
				
				szamlalo += list[i].getSouthTag() * ((AThermicConnector)c).getAirTemperature();
			}
						
			//WEST
			c = list[i].getWestThermicConnector();
			nevezo += list[i].getWestTag();
			if( c instanceof XDThermicConnector ){
				
				szamlalo += list[i].getWestTag() * ((XDThermicConnector)c).getWestThermicPoint().getActualTemperature();
				
			}else if( c instanceof AThermicConnector ){
				
				szamlalo += list[i].getWestTag() * ((AThermicConnector)c).getAirTemperature();
				
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
