package hu.akoel.hetram.thermicpoint;

import hu.akoel.hetram.accessories.CommonOperations;
import hu.akoel.hetram.accessories.Position;
import hu.akoel.hetram.connectors.OpenEdgeThermicConnector;
import hu.akoel.hetram.connectors.AThermicPointThermicConnector;
import hu.akoel.hetram.connectors.SymmetricEdgeThermicConnector;
import hu.akoel.hetram.connectors.IThermicConnector;
import hu.akoel.hetram.connectors.XThermicPointThermicConnector;
import hu.akoel.hetram.connectors.YThermicPointThermicConnector;
import hu.akoel.hetram.listeners.CalculationListener;
import hu.akoel.hetram.structures.StructureSet;
import hu.akoel.mgu.MCanvas;
import hu.akoel.mgu.MGraphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.Collection;

import javax.swing.SwingUtilities;

public class ThermicPointList{
	private ThermicPoint[] list;
	private StructureSet elementSet;
	private int position = 0;
	private CalculationListener calculationListener = null;
	private CURRENT_TYPE currentType = CURRENT_TYPE.TRAJECTORY;
	
	public static enum CURRENT_TYPE{
		VECTORPAIR,
		VECTOR,
		TRAJECTORY
	}
	public ThermicPointList( Collection<ThermicPoint> thermicPointCollection, StructureSet elementSet ){
		
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
	
	public void setCurrentType( CURRENT_TYPE currentType ){
		this.currentType = currentType;
	}
	
	public CURRENT_TYPE getCurrentType(){
		return this.currentType;
	}
	
	/**
	 *  Visszaadja a parameterkent megadott poziciohoz tartozo Termikus Pontot
	 *  
	 * @param x
	 * @param y
	 * @return
	 */
	public ThermicPoint getThermicPointByPosition( double x, double y ){
		
		if ( this.getSize() > 0 ) {
			
			IThermicConnector c;
			double dy1, dy2, dx1, dx2;
			
			// Vegig a Termikus Pontokon
			for (int j = 0; j < this.getSize(); j++) {
				
				Position position = this.get(j).getPosition();
				dy1 = 0;
				dy2 = 0;
				dx1 = 0;
				dx2 = 0;
				
				c = this.get(j).getNorthThermicConnector();
				if (c instanceof YThermicPointThermicConnector) {
					dy2 = ((YThermicPointThermicConnector)c).getDelta() / 2;
				}

				c = this.get(j).getEastThermicConnector();
				if (c instanceof XThermicPointThermicConnector) {
					dx2 = ((XThermicPointThermicConnector)c).getDelta() / 2;
				}

				c = this.get(j).getSouthThermicConnector();
				if (c instanceof YThermicPointThermicConnector) {
					dy1 = ((YThermicPointThermicConnector)c).getDelta() / 2;
				}

				c = this.get(j).getWestThermicConnector();
				if (c instanceof XThermicPointThermicConnector) {
					dx1 = ((XThermicPointThermicConnector)c).getDelta() / 2;
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
	 * 
	 * @param canvas
	 * @param g2
	 */
	public void drawPoint( MCanvas canvas, MGraphics g2, Color thermicPointColor, double thermicPointRadius ){
		
		// Termikus pontok megjelenitese
		for (int j = 0; j < this.getSize(); j++) {

			// A pont geometriai elhelyezkedese
			Position position = this.get(j).getPosition();
			
			g2.setColor( thermicPointColor );
			g2.fillOval( position.getX(), position.getY(), thermicPointRadius );

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
			
			IThermicConnector c;
			
			// Vegig a Termikus Pontokon
			for (int j = 0; j < this.getSize(); j++) {

				double delta = 10;
				
				c = this.get(j).getEastThermicConnector();
				if (c instanceof XThermicPointThermicConnector) {
					delta = ((XThermicPointThermicConnector)c).getDelta();
				}

				c = this.get(j).getWestThermicConnector();
				if (c instanceof XThermicPointThermicConnector) {
					delta = ((XThermicPointThermicConnector)c).getDelta();
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
	public void drawCurrent( MCanvas canvas, MGraphics g2 ){
		
		double maximumCurrent = 0;
		double maxDeltaY = 0;
		double maxDeltaX = 0;
		
		//Ha vannak termikus pontjaim
		if ( this.getSize() > 0 ) {
			
			IThermicConnector c;
			
			//
			// Maximalis hoaram szamitasa a maximalis hosszusagu nyil megallapitasahoz
			//
			for (int j = 0; j < this.getSize(); j++) {

				c = this.get(j).getNorthThermicConnector();
				if (c instanceof YThermicPointThermicConnector) {
					maxDeltaY = Math.max( maxDeltaY, Math.abs( ((YThermicPointThermicConnector)c).getDelta() ));
				}

				c = this.get(j).getEastThermicConnector();
				if (c instanceof XThermicPointThermicConnector) {
					maxDeltaX = Math.max( maxDeltaX, Math.abs( ((XThermicPointThermicConnector)c).getDelta() ));
				}

				c = this.get(j).getSouthThermicConnector();
				if (c instanceof YThermicPointThermicConnector) {
					maxDeltaY = Math.max( maxDeltaY, Math.abs( ((YThermicPointThermicConnector)c).getDelta() ));
				}

				c = this.get(j).getWestThermicConnector();
				if (c instanceof XThermicPointThermicConnector) {
					maxDeltaX = Math.max( maxDeltaX, Math.abs( ((XThermicPointThermicConnector)c).getDelta() ));
				}
				
				if( null != this.get(j).getEastCurrent() )
					maximumCurrent = Math.max( maximumCurrent, Math.abs( this.get(j).getEastCurrent() ) );
				if( null != this.get(j).getWestCurrent() )
					maximumCurrent = Math.max( maximumCurrent, Math.abs( this.get(j).getWestCurrent() ) );
				if( null != this.get(j).getNorthCurrent() )
					maximumCurrent = Math.max( maximumCurrent, Math.abs( this.get(j).getNorthCurrent() ) );
				if( null != this.get(j).getSouthCurrent() )
					maximumCurrent = Math.max( maximumCurrent, Math.abs( this.get(j).getSouthCurrent() ) );
						
			}

			double maxLength = Math.max( maxDeltaX, maxDeltaY ) * 0.9;
			double vY = 0;
			double vX = 0;
			double yLengthPercentage = 0;
			double xLengthPercentage = 0;
			
			//
			// Nyilak elhelyezese a Termikus pontokba 
			//
			for (int j = 0; j < this.getSize(); j++) {

				// A pont geometriai elhelyezkedese
				Position position = this.get(j).getPosition();
				vX = position.getX();
				vY = position.getY();
				yLengthPercentage = 0;
				Double current;
				double arrowLength;
						
				//----------
				//
				//NORTH
				//
				//----------
				current = this.get(j).getNorthCurrent();
				c = this.get(j).getNorthThermicConnector();
				
				//Ebbol a pontbol mutat NORTH fele
				if( current > 0 ){
				
					yLengthPercentage = Math.abs( current / maximumCurrent );
					
					if (c instanceof YThermicPointThermicConnector) {
					
						vY = position.getY() + yLengthPercentage * ((YThermicPointThermicConnector) c).getDelta();

					//Szabad feluletu pont					
					}else if( c instanceof OpenEdgeThermicConnector ){
										
						vY = position.getY() + yLengthPercentage * maxDeltaY;
					}

				}

				//---------------
				//
				//SOUTH
				//
				//---------------
				current = this.get(j).getSouthCurrent();
				c = this.get(j).getSouthThermicConnector();
				
				//Ebbol a pontbol mutat SOUTH fele
				if( current > 0 ){
					
					yLengthPercentage = Math.abs( current / maximumCurrent );
					
					if (c instanceof YThermicPointThermicConnector) {
					
						vY = position.getY() - yLengthPercentage * ((YThermicPointThermicConnector) c).getDelta();												

					//Szabad feluletu pont
					}else if( c instanceof OpenEdgeThermicConnector ){
					
						vY = position.getY() - yLengthPercentage * maxDeltaY;
					
					}

				}
								
				//----------
				//
				//EAST
				//
				//----------
				current = this.get(j).getEastCurrent();
				c = this.get(j).getEastThermicConnector();

				//Ebbol a pontbol mutat EAST fele
				if( current > 0 ){
				
					xLengthPercentage = Math.abs( current / maximumCurrent );
					
					//Normal termikus pont
					if (c instanceof XThermicPointThermicConnector) {

						vX = position.getX() + xLengthPercentage * ((XThermicPointThermicConnector) c).getDelta();
										
					//Szabad feluletu pont
					}else if( c instanceof OpenEdgeThermicConnector ){
						
						vX = position.getX() + xLengthPercentage * maxDeltaX;
						
					}
					
				}

				//----------
				//
				//WEST
				//
				//----------
				current = this.get(j).getWestCurrent();
				c = this.get(j).getWestThermicConnector();
		
				//Ebbol a pontbol mutat WEST fele
				if( current > 0 ){
				
					xLengthPercentage = Math.abs( current / maximumCurrent );
					
					//Normal termikus pont
					if (c instanceof XThermicPointThermicConnector) {
				
						vX = position.getX() - xLengthPercentage * ((XThermicPointThermicConnector) c).getDelta();
						
					//Szabad feluletu pont
					}else if( c instanceof OpenEdgeThermicConnector ){
					
						vX = position.getX() - xLengthPercentage * maxDeltaX;
					}

				}

				//----------------------
				//
				// MEGJELENITES
				//
				//----------------------
				
				//g2.setColor( getWhiteBlack( yLengthPercentage ) );
				g2.setColor( Color.white);
				g2.setStroke(new BasicStroke(1));
				
				//
				// Vektropar kirajzolasa
				//
				if( currentType.equals( CURRENT_TYPE.VECTORPAIR ) ){
	
					//Fuggoleges nyil szara
					g2.drawLine( position.getX(), position.getY(), position.getX(), vY);

					//Fuggoleges nyil hegye
					arrowLength = (vY - position.getY()) / 4;					
					g2.drawLine( position.getX(), vY, position.getX() + arrowLength/2, vY - arrowLength );
					g2.drawLine( position.getX(), vY, position.getX() - arrowLength/2, vY - arrowLength );

					//Vizszintes nyil szara
					g2.drawLine( position.getX(), position.getY(), vX, position.getY());

					//Vizszintes nyil hegye
					arrowLength = (vX - position.getX()) / 4;					
					g2.drawLine( vX, position.getY(), vX - arrowLength, position.getY() + arrowLength/2);
					g2.drawLine( vX, position.getY(), vX - arrowLength, position.getY() - arrowLength/2 );
				

				//
				// Vektor kirajzolas
				//
				}else if( currentType.equals( CURRENT_TYPE.VECTOR ) ){
								
					//Vektor iranyanak kirajzolasa
					g2.drawLine( position.getX(), position.getY(), vX, vY);
				
					//Vektor nyil hegye				
					arrowLength = Math.sqrt( (vX - position.getX() ) * (vX - position.getX() ) + (vY - position.getY() ) * (vY - position.getY() ) ) / 4;
					Path2D.Double path = new Path2D.Double();
					path.moveTo(vX - arrowLength / 2, vY - arrowLength );
					path.lineTo(vX, vY);
					path.lineTo( vX + arrowLength / 2, vY - arrowLength );
					AffineTransform at = new AffineTransform();				
				
					double theta = Math.atan2( (vY - position.getY() ), ( vX - position.getX() ) );
					at.rotate( theta-Math.PI/2d, vX , vY );
					path.transform(at);
					g2.drawPath( path );
				
				//
				// Trajektoria kirajzolsa
				//					
				}else if( currentType.equals( CURRENT_TYPE.TRAJECTORY ) ){
				
					Vector2D vector = new Vector2D( vX - position.getX(), vY - position.getY() );
					
					//Minden trajektoria vonal egyforma hosszu
					//vector = vector.getVector( maxLength );
					
					Path2D.Double trajektoriaPath = new Path2D.Double();
					trajektoriaPath.moveTo(
							position.getX() - vector.x / 2,
							position.getY() - vector.y / 2
					);
					trajektoriaPath.lineTo(
							position.getX() + vector.x / 2,
							position.getY() + vector.y / 2
					);
					//trajektoriaPath.moveTo( position.getX() - ( vX - position.getX() ) / 2, position.getY() - ( vY - position.getY() ) / 2 );
					//trajektoriaPath.lineTo( position.getX() + ( vX - position.getX() ) / 2, position.getY() + ( vY - position.getY() ) / 2 );

					AffineTransform trajektoriaAT = new AffineTransform();				
				
					trajektoriaAT.rotate( Math.PI/2d, position.getX(), position.getY() );
					trajektoriaPath.transform(trajektoriaAT);
					g2.drawPath( trajektoriaPath );
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
			IThermicConnector cNorth, cEast, cSouth, cWest;
			IThermicConnector tc;
			ThermicPoint tP;			
			
			for (int j = 0; j < this.getSize(); j++) {

				cNorth = this.get(j).getNorthThermicConnector();
				if (cNorth instanceof AThermicPointThermicConnector) {
					delta = Math.max(delta,	((AThermicPointThermicConnector) cNorth).getDelta());
				}

				cEast = this.get(j).getEastThermicConnector();
				if (cEast instanceof AThermicPointThermicConnector) {
					delta = Math.max(delta,	((AThermicPointThermicConnector) cEast).getDelta());
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
				
				cNorth = this.get(j).getNorthThermicConnector();
				if (cNorth instanceof AThermicPointThermicConnector) {
					dNorth = ((AThermicPointThermicConnector) cNorth).getDelta() / 2;							
				}

				cEast = this.get(j).getEastThermicConnector();
				if (cEast instanceof AThermicPointThermicConnector) {
					dEast = ((AThermicPointThermicConnector) cEast).getDelta() / 2;
				}

				cSouth = this.get(j).getSouthThermicConnector();
				if (cSouth instanceof AThermicPointThermicConnector) {
					dSouth = ((AThermicPointThermicConnector) cSouth).getDelta() / 2;
				}

				cWest = this.get(j).getWestThermicConnector();
				if (cWest instanceof AThermicPointThermicConnector) {
					dWest = ((AThermicPointThermicConnector) cWest).getDelta() / 2;
				}

				//double xStart = position.getX() - dWest;
				//double yStart = position.getY() - dSouth;
				
				double xStart = position.getX();
				double yStart = position.getY();

				//A Termikus Ponthoz tartozo szin
				g2.setColor(getRedBluByPercent((this.get(j).getActualTemperature() - minimumTemperature) / deltaTemperature));
				
				//
				//A teljes negyzet negyedekre valo felbontasa azert szukseges, mert
				//Ha van az adott negyedet meghatarozo iranyokba mutato Termikus Konnektor az meg nem jelenti azt, 
				//hogy az adott negyed belul van a fizikai keresztmetszeten
				//Praktikusan ilyen helyzet a negativ falsarok -> L. Itt van mondjuk Kelet es Eszak iranyba is Termikus Konnektor
				//ennek ellenere a negativ sarok pontban E-K negyedre megsem kell rajzolni, hiszen fizikailag az mar nem
				//a keresztmetszet resze
				//
				
				//
				//E-K negyzet kirajzolasa
				//
				//Ha van Eszakra es Keletre is mutato Termikus konnektor meg nem jelenti azt, hogy az adott negyed belul van a keresztmetszeten
				if( dEast > 0 && dNorth > 0 ){					
					
					//Meg kell nezni hogy a tole Keletre levo Termikus Pont rendelkezik-e Eszak fele mutato Termikus Konnektorral
					//Meg kene nezni, a tole Eszakra levo Termikus Pontot is
					tP = ((XThermicPointThermicConnector)cEast).getEastThermicPoint();
					
					//Ha rendelkezik D-fele mutato Termikus Konnektorral
					tc = tP.getNorthThermicConnector();
					if( tc instanceof AThermicPointThermicConnector ){
						g2.fillRectangle(xStart, yStart, xStart + dEast, yStart + dNorth);
					}					
					
				}
				
				//
				//D-K negyzet kirajzolasa
				//				
				//Ha van Keletre es Delre is mutato Termikus konnektor meg nem jelenti azt, hogy az adott negyed belul van a keresztmetszeten
				if( dEast > 0 && dSouth > 0 ){					
					
					//Meg kell nezni hogy a tole Keletre levo Termikus Pont rendelkezik-e Del fele mutato Termikus Konnektorral
					//Meg kene nezni, a tole Delre levo Termikus Pontot is 
					tP = ((XThermicPointThermicConnector)cEast).getEastThermicPoint();
					
					//Ha rendelkezik D-fele mutato Termikus Konnektorral
					tc = tP.getSouthThermicConnector();
					if( tc instanceof AThermicPointThermicConnector ){
						g2.fillRectangle(xStart, yStart, xStart + dEast, yStart - dSouth);
					}					
					
				}
				
				//
				//D-NY negyzet kirajzolasa
				//
				//Ha van  Delre es Nyugatra is mutato Termikus konnektor meg nem jelenti azt, hogy az adott negyed belul van a keresztmetszeten
				if( dSouth > 0 && dWest > 0 ){					
					
					//Meg kell nezni hogy a tole Nyugatra levo Termikus Pont rendelkezik-e Del fele mutato Termikus Konnektorral
					//Meg kene nezni, a tole Delre levo Termikus Pontot is 
					tP = ((XThermicPointThermicConnector)cWest).getWestThermicPoint();
					
					//Ha rendelkezik D-fele mutato Termikus Konnektorral
					tc = tP.getSouthThermicConnector();
					if( tc instanceof AThermicPointThermicConnector ){
						g2.fillRectangle(xStart, yStart, xStart - dWest, yStart - dSouth);
					}					
					
				}
				
				//
				//E-NY negyzet kirajzolasa
				//
				//Ha van  Delre es Nyugatra is mutato Termikus konnektor meg nem jelenti azt, hogy az adott negyed belul van a keresztmetszeten
				if( dNorth > 0 && dWest > 0 ){					
					
					//Meg kell nezni hogy a tole Nyugatra levo Termikus Pont rendelkezik-e Eszak fele mutato Termikus Konnektorral
					//Meg kene nezni, a tole Eszakra levo Termikus Pontot is 
					tP = ((XThermicPointThermicConnector)cWest).getWestThermicPoint();
					
					//Ha rendelkezik E-fele mutato Termikus Konnektorral
					tc = tP.getNorthThermicConnector();
					if( tc instanceof AThermicPointThermicConnector ){
						g2.fillRectangle(xStart, yStart, xStart - dWest, yStart + dNorth);
					}					
					
				}
				
				//g2.fillRectangle(xStart, yStart, xStart + dWest + dEast, yStart + dSouth + dNorth);
				
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

	private class CalculationListenerThread extends Thread{
		private double difference;
		private CalculationListener calculationListener;
		
		public CalculationListenerThread( CalculationListener calculationListener, double difference ){
			this.calculationListener = calculationListener;
			this.difference = difference;
		}
		
		@Override
		    public void run() {
				calculationListener.getDifference(difference);		
		 }
	}
	
	/**
	 * A sokismeretlenes egyenletrendszer megoldasa 
	 * eredmenye a Termikus Pontok homerseklete
	 * 
	 * @param minDifference
	 */
	public void solve( double minDifference ){
		
		double difference = -1;
	
		//Addig vegzi az iteraciot, amig a Termikus Pontok iteraciot megelozo
		//homersekletenek es az iteraciot koveto homersekletenek kulonbsege kisebb
		//nem lesz a parameterkent megadott engedelyezett elteresnel
		do{

			difference = -1;
			
			//Elvegez egy iteraciot az egyenletrendszeren
			oneStepToCalculateTemperature();
			
			for( int i = 0; i < getSize(); i++ ){
				difference = Math.max( difference, list[i].getTempDifference() );
			}
			
			//Ha volt definialva figyelo interfesz, akkor elkuldi neki az elozo szamitashoz kepesti elterest
			if( null != calculationListener){
				
				SwingUtilities.invokeLater(new CalculationListenerThread(calculationListener, difference));
				
			}
			
		}while( difference  > minDifference || difference < 0 );

		//
		//Q szamitasa a kiszamitott T-k alapjan
		//
		for( int i = 0; i < getSize(); i++ ){
			
			IThermicConnector c;
			double dx = 0;
			double dy = 0;
			double dXNormal = 0;
			double dYNormal = 0;
			double dXPerpendicular = 0;
			double dYPerpendicular = 0;
			
			IThermicConnector cN = list[i].getNorthThermicConnector();	
			IThermicConnector cE = list[i].getEastThermicConnector();
			IThermicConnector cS = list[i].getSouthThermicConnector();
			IThermicConnector cW = list[i].getWestThermicConnector();			
			
			//Van WEST es EAST iranyba is Normal termikus pont kapcsolat
			if( cW instanceof XThermicPointThermicConnector && cE instanceof XThermicPointThermicConnector ){
				dXNormal = ((XThermicPointThermicConnector)cW).getDelta() / 2;
				dXNormal += ((XThermicPointThermicConnector)cE).getDelta() / 2;
				dXPerpendicular = dXNormal;
			
			//Csak WEST iranyba van Normal termikus kapcsolata
			}else if( cW instanceof XThermicPointThermicConnector ){
				dXNormal = ((XThermicPointThermicConnector)cW).getDelta();
				dXPerpendicular = dXNormal / 2;
				
			//Csak EAST iranyba van Normal termikus kapcsoalt	
			}else if( cE instanceof XThermicPointThermicConnector ){
				dXNormal = ((XThermicPointThermicConnector)cE).getDelta();
				dXPerpendicular = dXNormal / 2;
			}
			
			//Van NORTH es SOUTH iranyba is Normal termikus pon kapcsolat
			if( cN instanceof YThermicPointThermicConnector && cS instanceof YThermicPointThermicConnector ){
				dYNormal = ((YThermicPointThermicConnector)cN).getDelta() / 2;
				dYNormal += ((YThermicPointThermicConnector)cS).getDelta() / 2;
				dYPerpendicular = dYNormal;
			
			//Csak NORTH iranyba van Normal termikus kapcsolata
			}else if( cN instanceof YThermicPointThermicConnector ){
				dYNormal = ((YThermicPointThermicConnector)cN).getDelta();
				dYPerpendicular = dYNormal / 2;
				
			//Csak SOUTH iranyba van Normal termikus kapcsoalt	
			}else if( cS instanceof YThermicPointThermicConnector ){
				dYNormal = ((YThermicPointThermicConnector)cS).getDelta();
				dYPerpendicular = dYNormal / 2;
			}
			
			
			//----------------------------------
			//
			// NORTH 
			//
			//----------------------------------
			c = this.get(i).getNorthThermicConnector();
			
			//WEST es EAST iranyba normal termikus pont kapcsolata van
			if( dXPerpendicular == dXNormal ){
				dx = dXNormal;
				dy = dYNormal;
			//WEST vagy EAST iranyba Szabadfelszin, vagy Szimmetrikus kapcsolat van 
			}else{
				dx = dXPerpendicular;
				dy = dYNormal;
			}		
			
			//
			//Szabad feluletu kapcsolat
			//
			if( c instanceof OpenEdgeThermicConnector ){
				double alpha = (( OpenEdgeThermicConnector)c).getAlpha();
				double deltaT = this.get(i).getActualTemperature() - ((OpenEdgeThermicConnector)c).getAirTemperature();
								
				double q = alpha * deltaT * dx;
				this.get(i).setNorthCurrent( q );
			
			//
			//Normal termikus kapcsolat
			//
			}else if (c instanceof AThermicPointThermicConnector) {
				
				double lambda = ((YThermicPointThermicConnector)c).getLambda();
				ThermicPoint pairThermicPoint = ((YThermicPointThermicConnector) c).getNorthThermicPoint();
				double deltaT = this.get(i).getActualTemperature() - pairThermicPoint.getActualTemperature();
				
				double q = lambda * deltaT * dx / dy;				
				this.get(i).setNorthCurrent( q );

			//
			//szimmetria tengely kapcsolat
			//
			}else if( c instanceof SymmetricEdgeThermicConnector ){
				this.get(i).setNorthCurrent( 0.0 );
			}
			
			//----------------------------------
			//
			// EAST
			//
			//---------------------------------
			c = this.get(i).getEastThermicConnector();

			//NORTH es SOUTH iranyba normal termikus pont kapcsolata van
			if( dYPerpendicular == dYNormal ){
				dx = dXNormal;
				dy = dYNormal;
			//NORTH vagy SOUTH iranyba Szabadfelszin, vagy Szimmetrikus kapcsolat van 
			}else{
				dx = dXNormal;
				dy = dYPerpendicular;
			}		
			
			//
			//Szabad feluletu kapcsolat
			//
			if( c instanceof OpenEdgeThermicConnector ){
				double alpha = (( OpenEdgeThermicConnector)c).getAlpha();
				double deltaT = this.get(i).getActualTemperature() - ((OpenEdgeThermicConnector)c).getAirTemperature();
				
				double q = alpha * deltaT * dy;
				this.get(i).setEastCurrent( q );
			
			//
			// Normal termikus kapcsolat
			//
			}else if (c instanceof AThermicPointThermicConnector) {
				
				double lambda = ((XThermicPointThermicConnector)c).getLambda();
				ThermicPoint pairThermicPoint = ((XThermicPointThermicConnector) c).getEastThermicPoint();
				double deltaT = this.get(i).getActualTemperature() - pairThermicPoint.getActualTemperature();
				
				double q = lambda * deltaT * dy / dx;
				this.get(i).setEastCurrent( q );
				
			//
			//szimmetria tengely kapcsolat
			//
			}else if( c instanceof SymmetricEdgeThermicConnector ){
				this.get(i).setEastCurrent( 0.0 );
			}
			
			//-------------------------------
			//
			// SOUTH
			//
			//-------------------------------
			c = this.get(i).getSouthThermicConnector();
			
			//WEST es EAST iranyba normal termikus pont kapcsolata van
			if( dXPerpendicular == dXNormal ){
				dx = dXNormal;
				dy = dYNormal;
			//WEST vagy EAST iranyba Szabadfelszin, vagy Szimmetrikus kapcsolat van 
			}else{
				dx = dXPerpendicular;
				dy = dYNormal;
			}		
			
			//
			//Szabad feluletu kapcsolat
			//
			if( c instanceof OpenEdgeThermicConnector ){
				double alpha = (( OpenEdgeThermicConnector)c).getAlpha();
				double deltaT = this.get(i).getActualTemperature() - ((OpenEdgeThermicConnector)c).getAirTemperature();
				
				double q = alpha * deltaT * dx;
				this.get(i).setSouthCurrent( q );
			
			//
			//Normal termikus kapcsolat
			//
			}else if (c instanceof AThermicPointThermicConnector) {
				double lambda = ((YThermicPointThermicConnector)c).getLambda();
				ThermicPoint pairThermicPoint = ((YThermicPointThermicConnector) c).getSouthThermicPoint();
				double deltaT = this.get(i).getActualTemperature() - pairThermicPoint.getActualTemperature();
				
				double q = lambda * deltaT * dx / dy;				
				this.get(i).setSouthCurrent( q );
				//pairThermicPoint.setNorthCurrent( -q );
			
			//
			//szimmetria tengely kapcsolat
			//
			}else if( c instanceof SymmetricEdgeThermicConnector ){
				this.get(i).setSouthCurrent( 0.0 );
			}

			//--------------------------------
			//
			// WEST
			//
			//--------------------------------
			c = this.get(i).getWestThermicConnector();
			
			//NORTH es SOUTH iranyba normal termikus pont kapcsolata van
			if( dYPerpendicular == dYNormal ){
				dx = dXNormal;
				dy = dYNormal;
			//NORTH vagy SOUTH iranyba Szabadfelszin, vagy Szimmetrikus kapcsolat van 
			}else{
				dx = dXNormal;
				dy = dYPerpendicular;
			}		
			
			//
			//Szabad feluletu kapcsolat
			//
			if( c instanceof OpenEdgeThermicConnector ){
				double alpha = (( OpenEdgeThermicConnector)c).getAlpha();
				double deltaT = this.get(i).getActualTemperature() - ((OpenEdgeThermicConnector)c).getAirTemperature();
				
				double q = alpha * deltaT * dy;
				this.get(i).setWestCurrent( q );
			
			//
			//Normal termikus kapcsolat
			//
			}else if (c instanceof AThermicPointThermicConnector) {
				double lambda = ((XThermicPointThermicConnector)c).getLambda();
				ThermicPoint pairThermicPoint = ((XThermicPointThermicConnector) c).getWestThermicPoint();
				double deltaT = this.get(i).getActualTemperature() - pairThermicPoint.getActualTemperature();
				
				double q = lambda * deltaT * dy / dx;
				this.get(i).setWestCurrent( q );
				//pairThermicPoint.setEastCurrent( -q );
				
			//
			//szimmetria tengely kapcsolat
			//
			}else if( c instanceof SymmetricEdgeThermicConnector ){
				this.get(i).setWestCurrent( 0.0 );
			}
			
		}
		
	}
	
	/**
	 * 
	 * Egy iteracio elvegzese a lista teljes allomanyan
	 * 
	 */
	private void oneStepToCalculateTemperature(){
		
		for( int i = 0; i < position; i++ ){
			
			double nevezo = 0;
			double szamlalo = 0;
			double temperature;
			
			IThermicConnector cN = list[i].getNorthThermicConnector();	
			IThermicConnector cE = list[i].getEastThermicConnector();
			IThermicConnector cS = list[i].getSouthThermicConnector();
			IThermicConnector cW = list[i].getWestThermicConnector();			
			
			double dx = 0;
			double dy = 0;
			double dXNormal = 0;
			double dYNormal = 0;
			double dXPerpendicular = 0;
			double dYPerpendicular = 0;
			
			//Van WEST es EAST iranyba is Normal termikus pont kapcsolat
			if( cW instanceof XThermicPointThermicConnector && cE instanceof XThermicPointThermicConnector ){
				dXNormal = ((XThermicPointThermicConnector)cW).getDelta() / 2;
				dXNormal += ((XThermicPointThermicConnector)cE).getDelta() / 2;
				dXPerpendicular = dXNormal;
			
			//Csak WEST iranyba van Normal termikus kapcsolata
			}else if( cW instanceof XThermicPointThermicConnector ){
				dXNormal = ((XThermicPointThermicConnector)cW).getDelta();
				dXPerpendicular = dXNormal / 2;
				
			//Csak EAST iranyba van Normal termikus kapcsoalt	
			}else if( cE instanceof XThermicPointThermicConnector ){
				dXNormal = ((XThermicPointThermicConnector)cE).getDelta();
				dXPerpendicular = dXNormal / 2;
			}
			
			//Van NORTH es SOUTH iranyba is Normal termikus pon kapcsolat
			if( cN instanceof YThermicPointThermicConnector && cS instanceof YThermicPointThermicConnector ){
				dYNormal = ((YThermicPointThermicConnector)cN).getDelta() / 2;
				dYNormal += ((YThermicPointThermicConnector)cS).getDelta() / 2;
				dYPerpendicular = dYNormal;
			
			//Csak NORTH iranyba van Normal termikus kapcsolata
			}else if( cN instanceof YThermicPointThermicConnector ){
				dYNormal = ((YThermicPointThermicConnector)cN).getDelta();
				dYPerpendicular = dYNormal / 2;
				
			//Csak SOUTH iranyba van Normal termikus kapcsoalt	
			}else if( cS instanceof YThermicPointThermicConnector ){
				dYNormal = ((YThermicPointThermicConnector)cS).getDelta();
				dYPerpendicular = dYNormal / 2;
			}

			//
			//NORTH
			//
			
			//WEST es EAST iranyba normal termikus pont kapcsolata van
			if( dXPerpendicular == dXNormal ){
				dx = dXNormal;
				dy = dYNormal;
			//WEST vagy EAST iranyba Szabadfelszin, vagy Szimmetrikus kapcsolat van 
			}else{
				dx = dXPerpendicular;
				dy = dYNormal;
			}			
			
			//Termikus Pont-Termikus Pont
			if( cN instanceof YThermicPointThermicConnector ){
				
				YThermicPointThermicConnector dtc = (YThermicPointThermicConnector)cN;
				szamlalo += dx * ( dtc.getLambda() / dy ) * dtc.getNorthThermicPoint().getActualTemperature();
				nevezo += dx * dtc.getLambda() / dy;
				
			//Termikus Pont - szimmetrikus kapcsolat
			}else if( cN instanceof SymmetricEdgeThermicConnector ){
			
				//Veszi a Pont felett levo Pontot osszekoto konnektort
				YThermicPointThermicConnector cp = (YThermicPointThermicConnector)list[i].getSouthThermicConnector();
				szamlalo += dx * ( cp.getLambda()/cp.getDelta() / dy ) * cp.getSouthThermicPoint().getActualTemperature();
				nevezo += dx * cp.getLambda()/cp.getDelta() / dy;
				
			//Termikus Pont - Szabad felszin
			}else if( cN instanceof OpenEdgeThermicConnector ){
				
				szamlalo += ((OpenEdgeThermicConnector)cN).getAlpha() * ((OpenEdgeThermicConnector)cN).getAirTemperature() * dx; 
				nevezo += ((OpenEdgeThermicConnector)cN).getAlpha() * dx;
			
			//Hiba-Nem lehet, hogy egy pontot nem zar le Connector
			}else{
				//TODO exception
			}	
		
			//
			//EAST
			//
			
			//NORTH es SOUTH iranyba normal termikus pont kapcsolata van
			if( dYPerpendicular == dYNormal ){
				dx = dXNormal;
				dy = dYNormal;
			//NORTH vagy SOUTH iranyba Szabadfelszin, vagy Szimmetrikus kapcsolat van 
			}else{
				dx = dXNormal;
				dy = dYPerpendicular;
			}		
			
			//Termikus Pont-Termikus Pont
			if( cE instanceof XThermicPointThermicConnector ){
								
				szamlalo += dy * ( ((AThermicPointThermicConnector) cE).getLambda() / dx ) * ((XThermicPointThermicConnector)cE).getEastThermicPoint().getActualTemperature();
				nevezo += dy * ((AThermicPointThermicConnector) cE).getLambda() / dx;
			
			//Termikus Pont - szimmetrikus kapcsoalt
			}else if( cE instanceof SymmetricEdgeThermicConnector ){
			
				//Veszi a Pont-tol balra levo Pont-ot osszekoto Konnektort
				IThermicConnector cp = list[i].getWestThermicConnector();					
				szamlalo += dy * ( ((AThermicPointThermicConnector) cp).getLambda() / dx ) * ((XThermicPointThermicConnector)cp).getWestThermicPoint().getActualTemperature();
				nevezo += dy * ((AThermicPointThermicConnector) cp).getLambda() / dx;
					
			//Termikus Pont - Szabad felszin
			}else if( cE instanceof OpenEdgeThermicConnector ){
				
				szamlalo += ((OpenEdgeThermicConnector)cE).getAlpha() * ((OpenEdgeThermicConnector)cE).getAirTemperature() * dy;
				nevezo += ((OpenEdgeThermicConnector)cE).getAlpha() * dy;

			//Hiba-Nem lehet, hogy egy pontot nem zar le Connector
			}else{
				//TODO exception
			}
			
			//
			//SOUTH
			//
			
			//WEST es EAST iranyba normal termikus pont kapcsolata van
			if( dXPerpendicular == dXNormal ){
				dx = dXNormal;
				dy = dYNormal;
			//WEST vagy EAST iranyba Szabadfelszin, vagy Szimmetrikus kapcsolat van 
			}else{
				dx = dXPerpendicular;
				dy = dYNormal;
			}		
			
			//Termikus Pont-Termikus Pont
			if( cS instanceof YThermicPointThermicConnector ){
				
				szamlalo += dx * ( ((AThermicPointThermicConnector) cS).getLambda() / dy ) * ((YThermicPointThermicConnector)cS).getSouthThermicPoint().getActualTemperature();
				nevezo += dx * ((AThermicPointThermicConnector) cS).getLambda() / dy;

			//Termikus Pont - szimmetrikus kapcsoalt
			}else if( cS instanceof SymmetricEdgeThermicConnector ){
				
				//Veszi a Pont felett levo Pontot osszekoto Konnektort
				IThermicConnector cp = list[i].getNorthThermicConnector();		
				szamlalo += dx * (((AThermicPointThermicConnector) cp).getLambda()/ dy ) * ((YThermicPointThermicConnector)cp).getNorthThermicPoint().getActualTemperature();
				nevezo += dx * ((AThermicPointThermicConnector) cp).getLambda()/ dy;
						
			//Termikus Pont - Szabad felszin
			}else if( cS instanceof OpenEdgeThermicConnector ){
				
				szamlalo += ((OpenEdgeThermicConnector)cS).getAlpha() * ((OpenEdgeThermicConnector)cS).getAirTemperature() * dx;
				nevezo += ((OpenEdgeThermicConnector)cS).getAlpha() * dx;
			
			//Hiba-Nem lehet, hogy egy pontot nem zar le Connector
			}else{
				//TODO exception
			}
					
			//
			//WEST
			//
			
			//NORTH es SOUTH iranyba normal termikus pont kapcsolata van
			if( dYPerpendicular == dYNormal ){
				dx = dXNormal;
				dy = dYNormal;
			//NORTH vagy SOUTH iranyba Szabadfelszin, vagy Szimmetrikus kapcsolat van 
			}else{
				dx = dXNormal;
				dy = dYPerpendicular;
			}		
			
			//Termikus Pont-Termikus Pont
			if( cW instanceof XThermicPointThermicConnector ){
				
				szamlalo += dy * ( ((AThermicPointThermicConnector) cW).getLambda() / dx ) * ((XThermicPointThermicConnector)cW).getWestThermicPoint().getActualTemperature();
				nevezo += dy * ((AThermicPointThermicConnector) cW).getLambda() / dx;
				
			//Termikus Pont - szimmetrikus kapcsoalt
			}else if( cW instanceof SymmetricEdgeThermicConnector ){
				
				//Veszi a Ponttol jobbra levo Pontot osszekoto Konnektort
				IThermicConnector cp = list[i].getEastThermicConnector();				
				szamlalo += dy * ( ((AThermicPointThermicConnector) cp).getLambda() / dx ) * ((XThermicPointThermicConnector)cp).getEastThermicPoint().getActualTemperature();
				nevezo += dy * ((AThermicPointThermicConnector) cp).getLambda() / dx;
			
			//Termikus Pont - Szabad felszin
			}else if( cW instanceof OpenEdgeThermicConnector ){
				
				szamlalo += ((OpenEdgeThermicConnector)cW).getAlpha() * ((OpenEdgeThermicConnector)cW).getAirTemperature() * dy;
				nevezo += ((OpenEdgeThermicConnector)cW).getAlpha() * dy;
			
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

class Vector2D{
	public double x;
	public double y;
	double theta;
	
	public Vector2D( double x, double y ){
		this.x = x;
		this.y = y;	
		this.theta = Math.atan2( y, x );
	}
	
	public double getTheta(){
		return theta;
	}
	
/*		public double getLength(){
		return Math.sqrt( x*x + y*y );
	}
*/		
	public Vector2D getVector( double length ){
		return new Vector2D(
			length * Math.cos( theta ),
			length * Math.sin( theta )
		);
	}
}
