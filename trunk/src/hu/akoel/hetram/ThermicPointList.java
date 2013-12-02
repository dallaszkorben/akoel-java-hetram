package hu.akoel.hetram;

import hu.akoel.hetram.accessories.Position;
import hu.akoel.hetram.connectors.OThermicConnector;
import hu.akoel.hetram.connectors.DThermicConnector;
import hu.akoel.hetram.connectors.SThermicConnector;
import hu.akoel.hetram.connectors.ThermicConnector;
import hu.akoel.hetram.connectors.XDThermicConnector;
import hu.akoel.hetram.connectors.YDThermicConnector;
import hu.akoel.hetram.listeners.CalculationListener;
import hu.akoel.mgu.MCanvas;
import hu.akoel.mgu.MGraphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.util.ArrayList;
import java.util.Collection;

public class ThermicPointList{
	private ThermicPoint[] list;
	private ElementSet elementSet;
	private int position = 0;
	private CalculationListener calculationListener = null;
	
	public ThermicPointList( Collection<ThermicPoint> thermicPointCollection, ElementSet elementSet ){
		
		list = new ThermicPoint[ thermicPointCollection.size() ];
		this.elementSet = elementSet;
		
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
	
	public ThermicPoint getThermicPointByPosition( double x, double y ){
		
		if ( this.getSize() > 0 ) {
			
			ThermicConnector c;
			double dy1, dy2, dx1, dx2;
			
			// Vegig a Termikus Pontokon
			for (int j = 0; j < this.getSize(); j++) {
				
				Position position = this.get(j).getPosition();
				dy1 = 0;
				dy2 = 0;
				dx1 = 0;
				dx2 = 0;
				
				c = this.get(j).getNorthThermicConnector();
				if (c instanceof YDThermicConnector) {
					dy2 = ((YDThermicConnector)c).getDelta() / 2;
				}

				c = this.get(j).getEastThermicConnector();
				if (c instanceof XDThermicConnector) {
					dx2 = ((XDThermicConnector)c).getDelta() / 2;
				}

				c = this.get(j).getSouthThermicConnector();
				if (c instanceof YDThermicConnector) {
					dy1 = ((YDThermicConnector)c).getDelta() / 2;
				}

				c = this.get(j).getWestThermicConnector();
				if (c instanceof XDThermicConnector) {
					dx1 = ((XDThermicConnector)c).getDelta() / 2;
				}	
				
				if( y <= position.getY() + dy2 && y >= position.getY() - dy1 && x <= position.getX() + dx2 && x >= position.getX() - dx1 ){
					return this.get( j );
				}
			}
		}
		return null;
	}
/*
	public Double getTemperatureByPosition( double x, double y ){
		
		ThermicPoint tp = getThermicPointByPosition(x, y);
		if( null == tp ){
			return null;
		}else{
			return tp.getActualTemperature();
		}
	}
*/	
	
	public void setCalculationListener( CalculationListener calculationListener ){
		this.calculationListener =  calculationListener;
	}
	
	/**
	 * Egy kitoltott korrel reprezentalja az egyes ThermicPoint-okat
	 * @param canvas
	 * @param g2
	 */
	public void drawPoint( MCanvas canvas, MGraphics g2 ){
		
		//
		// Termikus pont megjelenitese
		//
		for (int j = 0; j < this.getSize(); j++) {

			// A pont geometriai elhelyezkedese
			Position position = this.get(j).getPosition();
			
			double r = 0.002;
			g2.setColor( Color.green );
			g2.fillOval( position.getX() - r, position.getY() - r, 2 * r, 2 * r );

		}		
	}
	
	/**
	 * Az egyes ThermicPoint-ok homersekletet irja ki a pontok fole
	 * 
	 * @param canvas
	 * @param g2
	 */
	public void drawPointTemperatureByFont( MCanvas canvas, MGraphics g2 ){
		
		//Ha vannak termikus pontjaim
		if ( this.getSize() > 0 ) {
			
			ThermicConnector c;
			
			// Vegig a Termikus Pontokon
			for (int j = 0; j < this.getSize(); j++) {

				double delta = 10;
				
				c = this.get(j).getEastThermicConnector();
				if (c instanceof XDThermicConnector) {
					delta = ((XDThermicConnector)c).getDelta();
				}

				c = this.get(j).getWestThermicConnector();
				if (c instanceof XDThermicConnector) {
					delta = ((XDThermicConnector)c).getDelta();
				}	
				
				// A pont geometriai elhelyezkedese
				Position position = this.get(j).getPosition();
		
				int fontSize = canvas.getPixelXLengthByWorld( delta )/4;
				Font font = new Font("Default", Font.PLAIN, fontSize);
				FontRenderContext frc = g2.getFontRenderContext();

				g2.setColor(Color.white);
				TextLayout textLayout = new	TextLayout(String.valueOf( CommonOperations.get2Decimals( this.get( j ).getActualTemperature() ) ), font, frc );
				g2.drawFont( textLayout, position.getX(), position.getY());
			}
		}
	}
	
	/**
	 * Nyilakkal reprezentalja az egyes ThermicPoint-ok kozott fellepo hoaramot
	 * 
	 * @param canvas
	 * @param g2
	 */
	public void drawCurrentByArrow( MCanvas canvas, MGraphics g2 ){
		
		double maximumCurrent = 0;
		
		//Ha vannak termikus pontjaim
		if ( this.getSize() > 0 ) {
			
			Font font = new Font("Default", Font.PLAIN, 14);
			FontRenderContext frc = g2.getFontRenderContext();

			ThermicConnector c;
			
			//
			// Maximalis hoaram szamitasa a maximalis hosszusagu nyil megallapitasahoz
			//
			for (int j = 0; j < this.getSize(); j++) {

				double pointTemperature = this.get(j).getActualTemperature();
				
				c = this.get(j).getNorthThermicConnector();
				if (c instanceof YDThermicConnector) {
					maximumCurrent = Math.max( maximumCurrent, Math.abs((((YDThermicConnector)c).getNorthThermicPoint().getActualTemperature() - pointTemperature ) * ((YDThermicConnector)c).getLambda() ) );
				}

				c = this.get(j).getEastThermicConnector();
				if (c instanceof XDThermicConnector) {
					maximumCurrent = Math.max( maximumCurrent, Math.abs((((XDThermicConnector)c).getEastThermicPoint().getActualTemperature() - pointTemperature ) * ((XDThermicConnector)c).getLambda() ) );
				}

				c = this.get(j).getSouthThermicConnector();
				if (c instanceof YDThermicConnector) {
					maximumCurrent = Math.max( maximumCurrent, Math.abs((((YDThermicConnector)c).getSouthThermicPoint().getActualTemperature() - pointTemperature ) * ((YDThermicConnector)c).getLambda() ) );
				}

				c = this.get(j).getWestThermicConnector();
				if (c instanceof XDThermicConnector) {
					maximumCurrent = Math.max( maximumCurrent, Math.abs((((XDThermicConnector)c).getWestThermicPoint().getActualTemperature() - pointTemperature ) * ((XDThermicConnector)c).getLambda() ) );
				}				
			}

			//
			// Nyilak elhelyezese a Termikus pontokba 
			//
			for (int j = 0; j < this.getSize(); j++) {

				// A pont geometriai elhelyezkedese
				Position position = this.get(j).getPosition();
				
				c = this.get(j).getNorthThermicConnector();
				if (c instanceof YDThermicConnector) {					
				}

				c = this.get(j).getEastThermicConnector();
				if (c instanceof DThermicConnector) {
				}

				//SOUTH
				c = this.get(j).getSouthThermicConnector();
				if (c instanceof YDThermicConnector) {
					
					ThermicPoint pairThermicPoint = ((YDThermicConnector) c).getSouthThermicPoint();
					Position pairPosition = pairThermicPoint.getPosition();
					//double current = ((YDThermicConnector)c).getCurrent();
					double current = this.get(j).getSouthCurrent();
					double startX, startY, endX, endY;
					
					double lengthPercentage = Math.abs( current / maximumCurrent );
					
					//Felfele mutat
					if( current < 0 ){
						startX = pairPosition.getX();
						startY = pairPosition.getY();
						endX = position.getX();
						endY = pairPosition.getY() + lengthPercentage * ((YDThermicConnector) c).getDelta();
						
					//Lefele mutat
					}else{
						startX = position.getX();
						startY = position.getY();
						endX = pairPosition.getX();
						endY = position.getY() - lengthPercentage * ((YDThermicConnector) c).getDelta();
												
					}	
					
					g2.setColor( getWhiteBlack( lengthPercentage ) );
					g2.setColor( Color.white);
					g2.setStroke(new BasicStroke(1));
					g2.drawLine(startX, startY, endX, endY);					
					double arrowLength = (endY - startY) / 4;					
					g2.drawLine( endX, endY, endX + arrowLength/2, endY - arrowLength );
					g2.drawLine( endX, endY, endX - arrowLength/2, endY - arrowLength );

					
				}

				//WEST
				c = this.get(j).getWestThermicConnector();
				if (c instanceof XDThermicConnector) {
		
					ThermicPoint pairThermicPoint = ((XDThermicConnector) c).getWestThermicPoint();
					Position pairPosition = pairThermicPoint.getPosition();
					//double current = ((XDThermicConnector)c).getCurrent();
					double current = this.get(j).getWestCurrent();
					
					double startX, startY, endX, endY;
					
					double lengthPercentage = current / maximumCurrent;
					
					if( current < 0 ){
						startX = pairPosition.getX();
						startY = pairPosition.getY();
						endX = pairPosition.getX() + lengthPercentage * ((XDThermicConnector) c).getDelta();
						endY = pairPosition.getY();
					}else{
						startX = position.getX();
						startY = position.getY();
						endX = position.getX() - lengthPercentage * ((XDThermicConnector) c).getDelta();;
						endY = position.getY();
					}
					
					g2.setColor( getWhiteBlack( lengthPercentage ) );
					g2.setColor(Color.white);
					g2.setStroke(new BasicStroke(1));
					g2.drawLine(startX, startY, endX, endY);					
					double arrowLength = (endX - startX) / 4;					
					g2.drawLine( endX, endY, endX - arrowLength, endY + arrowLength/2 );
					g2.drawLine( endX, endY, endX - arrowLength, endY - arrowLength/2 );
	
					
				}
			}
		}		
	}
	
	
	/**
	 * Szinekkel reprezentalja az egyes ThermicPoint pontok homersekletet a pontok geometriai poziciojaban
	 * es korulotte delta tavolsagban
	 * 
	 * @param canvas
	 * @param g2
	 */
	public void drawTemperatureByColor( MCanvas canvas, MGraphics g2 ){
		double minimumTemperature = 0;
		double maximumTemperature = 0;
		double deltaTemperature;

		//Ha vannak termikus pontjaim
		if ( this.getSize() > 0 ) {
			
			// Megkeresi a minimalais es maximalis homersekletet
			for (int j = 0; j < this.getSize(); j++) {
				minimumTemperature = Math.min(minimumTemperature, this.get(j).getActualTemperature());
				maximumTemperature = Math.max(maximumTemperature, this.get(j).getActualTemperature());
			}
			deltaTemperature = maximumTemperature - minimumTemperature;

			// Megkeresi a legnagyobb Delta-t ami a nyilak 100%-a lesz
			double delta = 0;
			ThermicConnector c;
			for (int j = 0; j < this.getSize(); j++) {

				c = this.get(j).getNorthThermicConnector();
				if (c instanceof DThermicConnector) {
					delta = Math.max(delta,	((DThermicConnector) c).getDelta());
				}

				c = this.get(j).getEastThermicConnector();
				if (c instanceof DThermicConnector) {
					delta = Math.max(delta,	((DThermicConnector) c).getDelta());
				}
			}

			// Vegig a Termikus Pontokon
			for (int j = 0; j < this.getSize(); j++) {

				// A pont geometriai elhelyezkedese
				Position position = this.get(j).getPosition();

				double dNorth = 0;
				double dEast = 0;
				double dSouth = 0;
				double dWest = 0;
				
				c = this.get(j).getNorthThermicConnector();
				if (c instanceof DThermicConnector) {
					dNorth = ((DThermicConnector) c).getDelta() / 2;							
				}

				c = this.get(j).getEastThermicConnector();
				if (c instanceof DThermicConnector) {
					dEast = ((DThermicConnector) c).getDelta() / 2;
				}

				c = this.get(j).getSouthThermicConnector();
				if (c instanceof DThermicConnector) {
					dSouth = ((DThermicConnector) c).getDelta() / 2;
				}

				c = this.get(j).getWestThermicConnector();
				if (c instanceof DThermicConnector) {
					dWest = ((DThermicConnector) c).getDelta() / 2;
				}

				double xStart = position.getX() - dWest;
				double yStart = position.getY() - dSouth;

				g2.setColor(getRedBluByPercent((this.get(j).getActualTemperature() - minimumTemperature) / deltaTemperature));
				g2.fillRectangle(xStart, yStart, xStart + dWest + dEast, yStart + dSouth + dNorth);

				
			}
		}
	}
	
		
	
	/**
	 * A szazalekban megadott ertekhez egy szint rendel
	 * 
	 * @param percent
	 * @return
	 */
	private Color getRedBluByPercent(double percent) {
		/*
				int maxLength = 255;
				int value = (int) Math.round(percent * maxLength);

				int blue = ( value % 5 ) * 20;
				int red = 0;
				int green = 0;
				
				return new Color(red, 0, blue);
		*/		
				int red = 0;
				int blue = 0;
				int maxLength = 255;

				int value = (int) Math.round(percent * maxLength);

				blue = 255 - value;
				red = value;

				return new Color(red, 0, blue);
	}

	private Color getWhiteBlack(double percent) {
				int red = 0;
				int blue = 0;
				int green = 0;
				int maxLength = 255;

				int value = (int) Math.round(percent * maxLength);

				blue = value;
				red = value;
				green = value;

				return new Color(red, green, blue);
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
			
			//Ha volt definialva figyelo interfesz, akkor elkuldi neki az elozo szamitashoz kepesti elterest
			if( null != calculationListener){
				calculationListener.getDifference(difference);
			}
			
		}while( difference  > minDifference || difference < 0 );

		//Q szamitasa a kiszamitott T-k alapjan
		for( int i = 0; i < getSize(); i++ ){
			
			ThermicConnector c;
			
			//SOUTH
			c = this.get(i).getSouthThermicConnector();
			if (c instanceof DThermicConnector) {
				double lambda = ((YDThermicConnector)c).getLambda();
				ThermicPoint pairThermicPoint = ((YDThermicConnector) c).getSouthThermicPoint();
				double deltaT = this.get(i).getActualTemperature() - pairThermicPoint.getActualTemperature();
				double q = lambda * deltaT;
				this.get(i).setSouthCurrent( q );
				pairThermicPoint.setNorthCurrent( -q );
			}

			//WEST
			c = this.get(i).getWestThermicConnector();
			if (c instanceof DThermicConnector) {
				double lambda = ((XDThermicConnector)c).getLambda();
				ThermicPoint pairThermicPoint = ((XDThermicConnector) c).getWestThermicPoint();
				double deltaT = this.get(i).getActualTemperature() - pairThermicPoint.getActualTemperature();
				double q = lambda * deltaT;
				this.get(i).setWestCurrent( q );
				pairThermicPoint.setEastCurrent( -q );
			}
			
		}
		
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
