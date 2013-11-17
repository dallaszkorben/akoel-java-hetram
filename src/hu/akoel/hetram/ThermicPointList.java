package hu.akoel.hetram;

public class ThermicPointList{
	private ThermicPoint[] list;
	private int position = 0;
	
	public ThermicPointList( int size ){
	
		list = new ThermicPoint[size];
		
	}
	
	public void add( ThermicPoint thermicPoint ){
		thermicPoint.setPositionInTheList( position );
		thermicPoint.setActualTemperature( 1 );
		list[position] = thermicPoint;
		position++;
	}
	
	public void doIteration(){
		
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
	
	public int getSize(){
		return position;
	}
	
	public ThermicPoint get( int position ){
		return list[position];
	}
}
