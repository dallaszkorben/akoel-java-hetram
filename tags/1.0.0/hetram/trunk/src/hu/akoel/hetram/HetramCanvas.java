package hu.akoel.hetram;

import java.awt.Color;
import java.awt.Graphics2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import javax.swing.border.Border;

import hu.akoel.hetram.accessories.BigDecimalPosition;
import hu.akoel.hetram.accessories.Orientation;
import hu.akoel.hetram.connectors.AThermicPointThermicConnector;
import hu.akoel.hetram.gui.MainPanel;
import hu.akoel.hetram.gui.drawingelements.HetramBuildingStructureElement;
import hu.akoel.hetram.gui.drawingelements.HetramDrawnElement;
import hu.akoel.hetram.gui.drawingelements.OpenEdgeElement;
import hu.akoel.hetram.gui.drawingelements.SymmetricEdgeElement;
import hu.akoel.hetram.thermicpoint.ThermicPoint;
import hu.akoel.hetram.thermicpoint.ThermicPointList;
import hu.akoel.mgu.MCanvas;
import hu.akoel.mgu.MGraphics;
import hu.akoel.mgu.PainterListener;
import hu.akoel.mgu.PossiblePixelPerUnits;
import hu.akoel.mgu.drawnblock.DrawnBlock;
import hu.akoel.mgu.drawnblock.DrawnBlockCanvas;
import hu.akoel.mgu.values.TranslateValue;

public class HetramCanvas extends DrawnBlockCanvas{

	private static final long serialVersionUID = -6015123637668801411L;
	
	private MainPanel mainPanel;
	
	public HetramCanvas(Border borderType, Color background, PossiblePixelPerUnits possiblePixelPerUnits, TranslateValue positionToMiddle, MainPanel mainPanel, Precision precision ) {
		super(borderType, background, possiblePixelPerUnits, positionToMiddle, precision );
		this.mainPanel = mainPanel;
		
		//A regi painterlistener(eke)t torlem
		removePainterListenersFromMiddle();
		
		addPainterListenerToMiddle( new DrawnBlockPainterListener() );
	}

	
	class DrawnBlockPainterListener implements PainterListener{

		@Override
		public void paintByWorldPosition(MCanvas canvas, MGraphics g2) {

			for( DrawnBlock drawnBlock: getDrawnBlockList() ){
				
				if( drawnBlock instanceof HetramBuildingStructureElement ){
					drawnBlock.draw(g2);
				}
			}

			for( DrawnBlock drawnBlock: getDrawnBlockList() ){
				
				if( ! (drawnBlock instanceof HetramBuildingStructureElement) ){
					drawnBlock.draw(g2);
				}
			}

			
		}
		
		@Override
		public void paintByCanvasAfterTransfer(MCanvas canvas, Graphics2D g2) {}
		
	}
	
	
	/**
	 * Egy DrawnBlock rajzolasat elvegzo factory megadasa
	 * @param drawnBlockFactory
	 */
	public void setDrawnBlockFactory( HetramDrawnElementFactory drawnBlockFactory ){
		super.setDrawnBlockFactory(drawnBlockFactory);
	}
	
	@SuppressWarnings("unchecked")
	public Iterator<HetramDrawnElement> iterator(){
		return (Iterator<HetramDrawnElement>) super.iterator();
	}
	
	/**
	 * Visszaadja a kirajzolando elemek listajat
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<HetramDrawnElement> getDrawnBlockList(){
		return (ArrayList<HetramDrawnElement>) super.getDrawnBlockList();
	}

	/**
	 * Hozzaad a listahoz egy DrawnBlock elemet
	 */
	public void addDrawnBlock( DrawnBlock drawnBlock ){
		super.addDrawnBlock(drawnBlock);
		doGenerateMaximumDifference();
	}
	
	/**
	 * eltavolit a listabol egy DrawnBlock element
	 */
	public void removeDrawnBlock( int drawnBlock ){
		super.removeDrawnBlock(drawnBlock);
		doGenerateMaximumDifference();
	}
	
/*	
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
*/
	
	class OpenEdgeElementWithPosition{
		Orientation orientation;
		OpenEdgeElement element;
	
		public OpenEdgeElementWithPosition( OpenEdgeElement element, Orientation orientation ){
			
			this.element = element;
			this.orientation = orientation;
		}
	}
	
	class SymmetricEdgeElementWithPosition{
		Orientation orientation;
		SymmetricEdgeElement element;
	
		public SymmetricEdgeElementWithPosition( SymmetricEdgeElement element, Orientation orientation ){
			
			this.element = element;
			this.orientation = orientation;
		}
	}
	
	/**
	 * A parameterkent megadott BuildingStructureElement szamara osszegyujti a hozzakotott
	 * osszes OpenEdgeElement-et
	 * 
	 * @param buildingStructureElement
	 * @return
	 */
	public HashSet<OpenEdgeElementWithPosition> getOpenEdgeElements( HetramBuildingStructureElement buildingStructureElement ){
		HashSet<OpenEdgeElementWithPosition> openEdgeElementList = new HashSet<OpenEdgeElementWithPosition>();
		
		Iterator<HetramDrawnElement> it = this.iterator();
		while( it.hasNext() ){
			HetramDrawnElement e = it.next();
			
			if( e instanceof OpenEdgeElement ){
				
				OpenEdgeElement openEdgeElement = (OpenEdgeElement)e;
				
				//HORTH
				if( openEdgeElement.getWidth().compareTo( new BigDecimal("0") ) != 0 && buildingStructureElement.getY2().compareTo( openEdgeElement.getY1() ) == 0 ){
					BigDecimal dx1 = openEdgeElement.getX1().max( buildingStructureElement.getX1() );
					BigDecimal dx2 = openEdgeElement.getX2().min( buildingStructureElement.getX2() );
					if( dx2.subtract( dx1 ).compareTo(new BigDecimal("0")) > 0 ){
						openEdgeElementList.add( new OpenEdgeElementWithPosition( openEdgeElement, Orientation.NORTH ) );
					}
					
//				if( openEdgeElement.getWidth() != 0 && buildingStructureElement.getY2() == openEdgeElement.getY() ){
//					double dx1 = Math.max(openEdgeElement.getX1(), buildingStructureElement.getX1() );
//					double dx2 = Math.min(openEdgeElement.getX2(), buildingStructureElement.getX2() );
//					if( dx2 - dx1 > 0 ){
//						openEdgeElementList.add( new OpenEdgeElementWithPosition( openEdgeElement, Orientation.NORTH ) );
//					}
					
				//SOUTH
				}else if( openEdgeElement.getWidth().compareTo( new BigDecimal("0") ) != 0 && buildingStructureElement.getY1().compareTo( openEdgeElement.getY1() ) == 0 ){
					BigDecimal dx1 = openEdgeElement.getX1().max( buildingStructureElement.getX1() );
					BigDecimal dx2 = openEdgeElement.getX2().min( buildingStructureElement.getX2() );
					if( dx2.subtract( dx1 ).compareTo( new BigDecimal( "0" ) ) > 0 ){
						openEdgeElementList.add( new OpenEdgeElementWithPosition( openEdgeElement, Orientation.SOUTH ) );
					}				

//				}else if( openEdgeElement.getWidth() != 0 && buildingStructureElement.getY1() == openEdgeElement.getY() ){
//					double dx1 = Math.max(openEdgeElement.getX1(), buildingStructureElement.getX1() );
//					double dx2 = Math.min(openEdgeElement.getX2(), buildingStructureElement.getX2() );
//					if( dx2 - dx1 > 0 ){
//						openEdgeElementList.add( new OpenEdgeElementWithPosition( openEdgeElement, Orientation.SOUTH ) );
//					}			
					
				//EAST
				}else if( openEdgeElement.getHeight().compareTo( new BigDecimal( "0" ) ) != 0 && buildingStructureElement.getX2().compareTo( openEdgeElement.getX1() ) == 0 ){
					BigDecimal dy1 = openEdgeElement.getY1().max( buildingStructureElement.getY1() );
					BigDecimal dy2 = openEdgeElement.getY2().min( buildingStructureElement.getY2() );
					if( dy2.subtract( dy1 ).compareTo( new BigDecimal("0") ) > 0 ){
						openEdgeElementList.add( new OpenEdgeElementWithPosition( openEdgeElement, Orientation.EAST ) );
					}
					
//				}else if( openEdgeElement.getHeight() != 0 && buildingStructureElement.getX2() == openEdgeElement.getX() ){
//					double dy1 = Math.max(openEdgeElement.getY1(), buildingStructureElement.getY1() );
//					double dy2 = Math.min(openEdgeElement.getY2(), buildingStructureElement.getY2() );
//					if( dy2 - dy1 > 0 ){
//						openEdgeElementList.add( new OpenEdgeElementWithPosition( openEdgeElement, Orientation.EAST ) );
//					}
					
				//WEST
				}else if( openEdgeElement.getHeight().compareTo( new BigDecimal("0")) != 0 && buildingStructureElement.getX1().compareTo( openEdgeElement.getX1() ) == 0 ){
					BigDecimal dy1 = openEdgeElement.getY1().max( buildingStructureElement.getY1() );
					BigDecimal dy2 = openEdgeElement.getY2().min( buildingStructureElement.getY2() );
					if( dy2.subtract( dy1 ).compareTo( new BigDecimal("0") ) > 0 ){
						openEdgeElementList.add( new OpenEdgeElementWithPosition( openEdgeElement, Orientation.WEST ) );
					}	
					
//				}else if( openEdgeElement.getHeight() != 0 && buildingStructureElement.getX1() == openEdgeElement.getX() ){
//					double dy1 = Math.max(openEdgeElement.getY1(), buildingStructureElement.getY1() );
//					double dy2 = Math.min(openEdgeElement.getY2(), buildingStructureElement.getY2() );
//					if( dy2 - dy1 > 0 ){
//						openEdgeElementList.add( new OpenEdgeElementWithPosition( openEdgeElement, Orientation.WEST ) );
//					}	
				}
			}
		}
		
		return openEdgeElementList;
	}

	/**
 	 * A parameterkent megadott BuildingStructureElement szamara osszegyujti a hozzakotott
	 * osszes SymmetricEdgeElement-et
	 * 
	 * @param buildingStructureElement
	 * @return
	 */
	public HashSet<SymmetricEdgeElementWithPosition> getSymmetricEdgeElements( HetramBuildingStructureElement buildingStructureElement ){
		HashSet<SymmetricEdgeElementWithPosition> symmetricEdgeElementList = new HashSet<SymmetricEdgeElementWithPosition>();
		
		Iterator<HetramDrawnElement> it = this.iterator();
		while( it.hasNext() ){
			HetramDrawnElement e = it.next();
			
			if( e instanceof SymmetricEdgeElement ){
				
				SymmetricEdgeElement symmetricEdgeElement = (SymmetricEdgeElement)e;
				
				//HORTH
				if( symmetricEdgeElement.getWidth().compareTo( new BigDecimal("0") ) != 0 && buildingStructureElement.getY2().compareTo( symmetricEdgeElement.getY1() ) == 0 ){
					BigDecimal dx1 = symmetricEdgeElement.getX1().max( buildingStructureElement.getX1() );
					BigDecimal dx2 = symmetricEdgeElement.getX2().min( buildingStructureElement.getX2() );
					if( dx2.subtract( dx1 ).compareTo(new BigDecimal("0")) > 0 ){
						symmetricEdgeElementList.add( new SymmetricEdgeElementWithPosition( symmetricEdgeElement, Orientation.NORTH ) );
					}			
			
//				if( symmetricEdgeElement.getWidth() != 0 && buildingStructureElement.getY2() == symmetricEdgeElement.getY() ){
//					double dx1 = Math.max(symmetricEdgeElement.getX1(), buildingStructureElement.getX1() );
//					double dx2 = Math.min(symmetricEdgeElement.getX2(), buildingStructureElement.getX2() );
//					if( dx2 - dx1 > 0 ){
//						symmetricEdgeElementList.add( new SymmetricEdgeElementWithPosition( symmetricEdgeElement, Orientation.NORTH ) );
//					}
					
				//SOUTH
				}else if( symmetricEdgeElement.getWidth().compareTo( new BigDecimal("0") ) != 0 && buildingStructureElement.getY1().compareTo( symmetricEdgeElement.getY1() ) == 0 ){
					BigDecimal dx1 = symmetricEdgeElement.getX1().max( buildingStructureElement.getX1() );
					BigDecimal dx2 = symmetricEdgeElement.getX2().min( buildingStructureElement.getX2() );
					if( dx2.subtract( dx1 ).compareTo( new BigDecimal( "0" ) ) > 0 ){
						symmetricEdgeElementList.add( new SymmetricEdgeElementWithPosition( symmetricEdgeElement, Orientation.SOUTH ) );
					}						
//				}else if( symmetricEdgeElement.getWidth() != 0 && buildingStructureElement.getY1() == symmetricEdgeElement.getY() ){
//					double dx1 = Math.max(symmetricEdgeElement.getX1(), buildingStructureElement.getX1() );
//					double dx2 = Math.min(symmetricEdgeElement.getX2(), buildingStructureElement.getX2() );
//					if( dx2 - dx1 > 0 ){
//						symmetricEdgeElementList.add( new SymmetricEdgeElementWithPosition( symmetricEdgeElement, Orientation.SOUTH ) );
//					}				

				//EAST
				}else if( symmetricEdgeElement.getHeight().compareTo( new BigDecimal( "0" ) ) != 0 && buildingStructureElement.getX2().compareTo( symmetricEdgeElement.getX1() ) == 0 ){
					BigDecimal dy1 = symmetricEdgeElement.getY1().max( buildingStructureElement.getY1() );
					BigDecimal dy2 = symmetricEdgeElement.getY2().min( buildingStructureElement.getY2() );
					if( dy2.subtract( dy1 ).compareTo( new BigDecimal("0") ) > 0 ){
						symmetricEdgeElementList.add( new SymmetricEdgeElementWithPosition( symmetricEdgeElement, Orientation.EAST ) );
					}
					
//				}else if( symmetricEdgeElement.getHeight() != 0 && buildingStructureElement.getX2() == symmetricEdgeElement.getX() ){
//					double dy1 = Math.max(symmetricEdgeElement.getY1(), buildingStructureElement.getY1() );
//					double dy2 = Math.min(symmetricEdgeElement.getY2(), buildingStructureElement.getY2() );
//					if( dy2 - dy1 > 0 ){
//						symmetricEdgeElementList.add( new SymmetricEdgeElementWithPosition( symmetricEdgeElement, Orientation.EAST ) );
//					}
					
				//WEST
				}else if( symmetricEdgeElement.getHeight().compareTo( new BigDecimal("0")) != 0 && buildingStructureElement.getX1().compareTo( symmetricEdgeElement.getX1() ) == 0 ){
					BigDecimal dy1 = symmetricEdgeElement.getY1().max( buildingStructureElement.getY1() );
					BigDecimal dy2 = symmetricEdgeElement.getY2().min( buildingStructureElement.getY2() );
					if( dy2.subtract( dy1 ).compareTo( new BigDecimal("0") ) > 0 ){
						symmetricEdgeElementList.add( new SymmetricEdgeElementWithPosition( symmetricEdgeElement, Orientation.WEST ) );
					}	
					
//				}else if( symmetricEdgeElement.getHeight() != 0 && buildingStructureElement.getX1() == symmetricEdgeElement.getX() ){
//					double dy1 = Math.max(symmetricEdgeElement.getY1(), buildingStructureElement.getY1() );
//					double dy2 = Math.min(symmetricEdgeElement.getY2(), buildingStructureElement.getY2() );
//					if( dy2 - dy1 > 0 ){
//						symmetricEdgeElementList.add( new SymmetricEdgeElementWithPosition( symmetricEdgeElement, Orientation.WEST ) );
//					}					
				}
			}
		}
		
		return symmetricEdgeElementList;
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
		int scale = getPrecision().getScale();
		
		HetramBuildingStructureElement element;
		
		BigDecimal verticalAppliedDifference = mainPanel.getVerticalMaximumDifference().divide( new BigDecimal( String.valueOf( mainPanel.getVerticalDifferenceDivider() ) ), 10, RoundingMode.HALF_UP  );
		BigDecimal horizontalAppliedDifference = mainPanel.getHorizontalMaximumDifference().divide( new BigDecimal( String.valueOf( mainPanel.getHorizontalDifferenceDivider() ) ), 10, RoundingMode.HALF_UP  );
		
		mainPanel.setHorizontalAppliedDifference( horizontalAppliedDifference );
		mainPanel.setVerticalAppliedDifference( verticalAppliedDifference );
		
		HashMap<BigDecimalPosition, ThermicPoint> thermicPointMap = new HashMap<>();
		
		//----------------------------------------------
		//
		// Elso korben a Termikus Pont-ok legyartasa es
		// a DThermicConnector-ok kiosztasa
		//
		//----------------------------------------------
		
		//Minden elemen vegig megyek
		for( HetramDrawnElement e: getDrawnBlockList() ){
			
			//Ha igazi Epuletszerkezetrol van szo es nem lezaro elemrol
			if( e instanceof HetramBuildingStructureElement ){
			
				element = (HetramBuildingStructureElement)e;
				
				//Veszem az elem kezdo es veg pozicioit
				BigDecimal startXPoint = element.getX1();
				BigDecimal startYPoint = element.getY1();
				BigDecimal endXPoint = element.getX2();
				BigDecimal endYPoint = element.getY2();
			
				double lambda;	

				//Mindig a kezdo vertikalis pontbol indulok
				BigDecimal y = startYPoint;
			
				//Vertikalis felbontas
				int iSteps = endYPoint.subtract( y ).divide( verticalAppliedDifference, scale, RoundingMode.HALF_UP  ).intValue();
				//int iSteps = (int)Math.round((endYPoint - y) / verticalAppliedDifference );
			
				//Vegig a vertikalis pontokon
				for( int i = 0; i <= iSteps; i++){
				
					//Az aktualis vertikalis point
					y = (new BigDecimal(String.valueOf(i)).multiply(verticalAppliedDifference )).add( startYPoint ).setScale( scale, RoundingMode.HALF_UP );					
					//y = CommonOperations.get10Decimals( startYPoint + i * verticalAppliedDifference );
				
					//Elindul a kezdo horizontalis pontbol
					BigDecimal x = startXPoint;			
				
					//Horizontalis felbontas
					int jSteps = endXPoint.subtract( x ).divide(horizontalAppliedDifference, scale, RoundingMode.HALF_UP ).intValue();
					//int jSteps = (int)Math.round((endXPoint - x) / horizontalAppliedDifference);
				
					//Vegig a horizontalis pontokon
					for( int j = 0; j <= jSteps; j++ ){
				
						//Az aktualis horizontalis pont
						x = (new BigDecimal(String.valueOf(j)).multiply(horizontalAppliedDifference )).add( startXPoint ).setScale( scale, RoundingMode.HALF_UP );		
						//x = CommonOperations.get10Decimals( startXPoint + j * horizontalAppliedDifference );

						//Az aktualis pont pozicioja
						BigDecimalPosition position = new BigDecimalPosition(x, y);

						//Rakeresek a taroloban, hatha letezett mar ez elott is
						ThermicPoint tp = thermicPointMap.get( position );
					
						//Ha ez a pont meg nem letezett
						if( null == tp ){
						
							//akkor letrehozom
							tp = new ThermicPoint( position );
						
							//Es el is mentem
							thermicPointMap.put( position, tp );
						}
					
						//Az elem lambda-ja
						lambda = element.getLambda();	
					
						//Ha nem az elso elem balrol, de fuggolegesen lehet barmelyik
						if( j != 0 ){
						
							//Akkor elkerem a regi WEST kapcsolatat, hatha letezik  
							AThermicPointThermicConnector oldWestConnector = (AThermicPointThermicConnector)tp.getWestThermicConnector();
						
							//Akkor letezik ha legalso vagy legfelso Point-rol van szo es kapcsolodik egy masik, mar lehelyezett Element-hez
							if( null != oldWestConnector ){
						
								//Akkor a 2 lambda atlagat szamoljuk
								lambda = (lambda + oldWestConnector.getLambda()) / 2;
							}

							//Veszem a baloldali kozvetlen kapcsolatat, ami bizonyosan letezik, mivel belso pont						
							BigDecimal previousX = new BigDecimal( String.valueOf( j - 1 ) ).multiply( horizontalAppliedDifference ).add( startXPoint ).setScale( scale, RoundingMode.HALF_UP);						
							//double previousX = CommonOperations.get10Decimals( startXPoint + (j - 1) * horizontalAppliedDifference );

							//Es osszekottetest letesitek vele
							tp.connectToThermicPoint(thermicPointMap.get(new BigDecimalPosition( previousX, y ) ), Orientation.WEST, lambda );
					
						}
					
						lambda = element.getLambda();	
					
						//Ha nem az elso elem lentrol, de vizszintesen lehet barmelyik
						if( i != 0 ){
						
							//Akkor elkerem a regi SOUTH kapcsolatat, hatha letezik  
							AThermicPointThermicConnector oldSouthConnector = (AThermicPointThermicConnector)tp.getSouthThermicConnector();
						
							//Akkor letezik ha jobb vagy baloldali Point-rol van szo es kapcsolodik egy masik, mar lehelyezett Element-hez
							if( null != oldSouthConnector ){
						
								//Akkor a 2 lambda atlagat szamoljuk
								lambda = (lambda + oldSouthConnector.getLambda()) / 2;
							}

							//Veszem az alatta levo kozvetlen kapcsolatat, ami bizonyosan letezik, mivel belso pont
							BigDecimal previousY = new BigDecimal( String.valueOf( i - 1 ) ).multiply( verticalAppliedDifference ).add( startYPoint ).setScale( scale, RoundingMode.HALF_UP);		
							
							//Es osszekottetest letesitek vele
							tp.connectToThermicPoint(thermicPointMap.get(new BigDecimalPosition( x, previousY ) ), Orientation.SOUTH, lambda );
							
							//double previousY = CommonOperations.get10Decimals( startYPoint + (i - 1) * verticalAppliedDifference );						
							//tp.connectToThermicPoint(thermicPointMap.get(new Position(x, previousY)), Orientation.SOUTH, lambda );
						}					
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
		for( HetramDrawnElement e: getDrawnBlockList() ){
					
			//Ha igazi Epuletszerkezetrol van szo es nem lezaro elemrol
			if( e instanceof HetramBuildingStructureElement ){
					
				element = (HetramBuildingStructureElement)e;
		
				HashSet<OpenEdgeElementWithPosition> openEdgeElementList = getOpenEdgeElements( element );
				HashSet<SymmetricEdgeElementWithPosition> symmetricEdgeElementList = getSymmetricEdgeElements( element );
			
				//Veszem az elem kezdo es veg pozicioit
				BigDecimal startXPoint = element.getX1();
				BigDecimal startYPoint = element.getY1();
				BigDecimal endXPoint = element.getX2();
				BigDecimal endYPoint = element.getY2();
			
				//Kezdo vertikalis pontbol indulok
				BigDecimal y = startYPoint;
			
				//Vertikalis felbontas
				int iSteps = endYPoint.subtract( y ).divide( verticalAppliedDifference , scale, RoundingMode.HALF_UP  ).intValue();
				//int iSteps = (int)Math.round((endYPoint - y) / verticalAppliedDifference );
			
				//Vegig a vertikalis pontokon
				for( int i = 0; i <= iSteps; i++){

					//Az aktualis vertikalis pont
					y = (new BigDecimal(String.valueOf(i)).multiply(verticalAppliedDifference )).add( startYPoint ).setScale( scale, RoundingMode.HALF_UP );	
					//y = CommonOperations.get10Decimals( startYPoint + i * verticalAppliedDifference );				
				
					//Kezdo horizontalis pontbol indulok
					BigDecimal x = startXPoint;		
				
					//Horizontalis felbontas
					int jSteps = endXPoint.subtract( x ).divide(horizontalAppliedDifference, scale, RoundingMode.HALF_UP ).intValue();
					//int jSteps = (int)Math.round((endXPoint - x) / horizontalAppliedDifference);
				
					//Vegig a horizontalis pontokon
					for( int j = 0; j <= jSteps; j++ ){
					
						//Az aktualis horizontalis pont
						x = (new BigDecimal(String.valueOf(j)).multiply(horizontalAppliedDifference )).add( startXPoint ).setScale( scale, RoundingMode.HALF_UP );		
						//x = CommonOperations.get10Decimals( startXPoint + j * horizontalAppliedDifference );
					
						//Az aktualis pont pozicioja
						BigDecimalPosition position = new BigDecimalPosition(x, y);

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

							//
							// OpenEdge
							//							
							//Vegig az OpenEdge elemeken, melyek az Epuletszerkezet korul vannak
							for( OpenEdgeElementWithPosition openEdgeElementWithPosition: openEdgeElementList ){

								//Megfelelo pozicio
								if( openEdgeElementWithPosition.orientation.equals( Orientation.WEST ) && y.compareTo( openEdgeElementWithPosition.element.getY1() ) >= 0 && y.compareTo( openEdgeElementWithPosition.element.getY2() ) <= 0 ){
								//if( openEdgeElementWithPosition.orientation.equals( Orientation.WEST ) && y >= openEdgeElementWithPosition.element.getY1() && y <= openEdgeElementWithPosition.element.getY2() ){

									//Alfa es homerseklet kapcsolasa
									actualThermicPoint.connectToOpenEdge( Orientation.WEST, openEdgeElementWithPosition.element.getAlphaByPosition( y.doubleValue() ), openEdgeElementWithPosition.element.getTemperature() );		
									break;
								
								}
							}
							
							//
							// SymmetricEdge 
							// 
							// TODO valojaban teljesen felesleges, mert ha kimarad egy kapcsolat az automatikusan SymmetricEdge-re allitom a vegen
							//							
							//Vegig az OpenEdge elemeken, melyek az Epuletszerkezet korul vannak
							for( SymmetricEdgeElementWithPosition symmetricEdgeElementWithPosition: symmetricEdgeElementList ){

								//Megfelelo pozicio
								if( symmetricEdgeElementWithPosition.orientation.equals( Orientation.WEST ) && y.compareTo( symmetricEdgeElementWithPosition.element.getY1() ) >= 0 && y.compareTo( symmetricEdgeElementWithPosition.element.getY2() ) <= 0 ){
								//if( symmetricEdgeElementWithPosition.orientation.equals( Orientation.WEST ) && y >= symmetricEdgeElementWithPosition.element.getY1() && y <= symmetricEdgeElementWithPosition.element.getY2() ){

									actualThermicPoint.connectToSymmetricEdge( Orientation.WEST );
									break;		
																		
								}
							}
						}
					
						//Ha esetleg meg mindig nincs lezarva a bal-szelso pont WEST iranyban
						//Ha nincs definialva semmilyen lezaras, akkor az SZIMMERTIA pont lesz mindenkeppen
						if( j == 0 && null == actualThermicPoint.getWestThermicConnector() ){
							actualThermicPoint.connectToSymmetricEdge( Orientation.WEST );
						}						
					
						//-----------------------------
						//
						// JOBB SZELSO PONT EAST irany
						//
						//-----------------------------
					
						//Ha jobb-szelso Pont es a Pont-nak nincs EAST iranyu DThermicConnector-a
						if( j == jSteps && null == actualThermicPoint.getEastThermicConnector() ){

							//
							// OpenEdge
							//							
							//Vegig az OpenEdge elemeken, melyek az Epuletszerkezet korul vannak
							for( OpenEdgeElementWithPosition openEdgeElementWithPosition: openEdgeElementList ){
						
								//Megfelelo pozicio
								if( openEdgeElementWithPosition.orientation.equals( Orientation.EAST ) && y.compareTo( openEdgeElementWithPosition.element.getY1() ) >= 0 && y.compareTo( openEdgeElementWithPosition.element.getY2() ) <= 0 ){
								//if( openEdgeElementWithPosition.orientation.equals( Orientation.EAST ) && y >= openEdgeElementWithPosition.element.getY1() && y <= openEdgeElementWithPosition.element.getY2() ){									
							
									//Alfa es homerseklet kapcsolasa
									actualThermicPoint.connectToOpenEdge( Orientation.EAST, openEdgeElementWithPosition.element.getAlphaByPosition( y.doubleValue() ), openEdgeElementWithPosition.element.getTemperature() );		
									
								}
							}
						
							//
							// SymmetricEdge 
							// 
							// TODO valojaban teljesen felesleges, mert ha kimarad egy kapcsolat az automatikusan SymmetricEdge-re allitom a vegen
							//							
							//Vegig az OpenEdge elemeken, melyek az Epuletszerkezet korul vannak
							for( SymmetricEdgeElementWithPosition symmetricEdgeElementWithPosition: symmetricEdgeElementList ){

								//Megfelelo pozicio
								if( symmetricEdgeElementWithPosition.orientation.equals( Orientation.EAST ) && y.compareTo( symmetricEdgeElementWithPosition.element.getY1() ) >= 0 && y.compareTo( symmetricEdgeElementWithPosition.element.getY2() ) <= 0 ){
								//if( symmetricEdgeElementWithPosition.orientation.equals( Orientation.EAST ) && y >= symmetricEdgeElementWithPosition.element.getY1() && y <= symmetricEdgeElementWithPosition.element.getY2() ){

									actualThermicPoint.connectToSymmetricEdge( Orientation.EAST );
									break;																	
								}
							}
						}
											
						//Ha esetleg meg mindig nincs lezarva a jobb-szelso pont EAST iranyban
						//Ha nincs definialva semmilyen lezaras, akkor az szimmetria pont lesz
						if( j == jSteps && null == actualThermicPoint.getEastThermicConnector() ){
							actualThermicPoint.connectToSymmetricEdge( Orientation.EAST );
						}	
					
						//------------------------------
						//
						// ALSO SZELSO PONT SOUTH irany
						//
						//------------------------------

						//Ha also-szelso Pont es a Pont-nak nincs SOUTH iranyu DThermicConnector-a
						if( i == 0 && null == actualThermicPoint.getSouthThermicConnector()){
							
							//
							// OpenEdge
							//							
							//Vegig az OpenEdge elemeken, melyek az Epuletszerkezet korul vannak
							for( OpenEdgeElementWithPosition openEdgeElementWithPosition: openEdgeElementList ){
						
								//Megfelelo pozicio
								if( openEdgeElementWithPosition.orientation.equals( Orientation.SOUTH ) && x.compareTo( openEdgeElementWithPosition.element.getX1() ) >= 0 && x.compareTo( openEdgeElementWithPosition.element.getX2() ) <= 0 ){
								//if( openEdgeElementWithPosition.orientation.equals( Orientation.SOUTH ) && x >= openEdgeElementWithPosition.element.getX1() && x <= openEdgeElementWithPosition.element.getX2() ){
							
									//Alfa es homerseklet kapcsolasa
									actualThermicPoint.connectToOpenEdge( Orientation.SOUTH, openEdgeElementWithPosition.element.getAlphaByPosition( x.doubleValue() ), openEdgeElementWithPosition.element.getTemperature() );		
									
								}
							}
							
							//
							// SymmetricEdge 
							// 
							// TODO valojaban teljesen felesleges, mert ha kimarad egy kapcsolat az automatikusan SymmetricEdge-re allitom a vegen
							//							
							//Vegig az OpenEdge elemeken, melyek az Epuletszerkezet korul vannak
							for( SymmetricEdgeElementWithPosition symmetricEdgeElementWithPosition: symmetricEdgeElementList ){

								//Megfelelo pozicio
								if( symmetricEdgeElementWithPosition.orientation.equals( Orientation.SOUTH ) && x.compareTo( symmetricEdgeElementWithPosition.element.getX1() ) >= 0 && x.compareTo( symmetricEdgeElementWithPosition.element.getX2() ) <= 0 ){
								//if( symmetricEdgeElementWithPosition.orientation.equals( Orientation.SOUTH ) && x >= symmetricEdgeElementWithPosition.element.getX1() && x <= symmetricEdgeElementWithPosition.element.getX2() ){									

									actualThermicPoint.connectToSymmetricEdge( Orientation.SOUTH );
									break;																	
								}
							}						
						}
					
						//Ha esetleg meg mindig nincs lezarva az also-szelso pont SOUTH iranyban
						//Ha nincs definialva semmilyen lezaras, akkor az szimmetria pont lesz
						if( i == 0 && null == actualThermicPoint.getSouthThermicConnector() ){
							actualThermicPoint.connectToSymmetricEdge(  Orientation.SOUTH );
						}	
					
						//------------------------------
						//
						// FELSO SZELSO PONT NORTH irany
						//
						//------------------------------

						//Ha felso-szelso Pont es a Pont-nak nincs NORTH iranyu DThermicConnector-a
						if( i == iSteps && null == actualThermicPoint.getNorthThermicConnector() ){
						
							//
							// OpenEdge
							//							
							//Vegig az OpenEdge elemeken, melyek az Epuletszerkezet korul vannak
							for( OpenEdgeElementWithPosition openEdgeElementWithPosition: openEdgeElementList ){
						
								//Megfelelo pozicio
								if( openEdgeElementWithPosition.orientation.equals( Orientation.NORTH) && x.compareTo( openEdgeElementWithPosition.element.getX1() ) >= 0 && x.compareTo( openEdgeElementWithPosition.element.getX2() ) <= 0 ){
								//if( openEdgeElementWithPosition.orientation.equals( Orientation.NORTH) && x >= openEdgeElementWithPosition.element.getX1() && x <= openEdgeElementWithPosition.element.getX2() ){
									
									//Alfa es homerseklet kapcsolasa
									actualThermicPoint.connectToOpenEdge( Orientation.NORTH, openEdgeElementWithPosition.element.getAlphaByPosition( x.doubleValue() ), openEdgeElementWithPosition.element.getTemperature() );		
									
								}
							}
							
							//
							// SymmetricEdge 
							// 
							// TODO valojaban teljesen felesleges, mert ha kimarad egy kapcsolat az automatikusan SymmetricEdge-re allitom a vegen
							//							
							//Vegig az OpenEdge elemeken, melyek az Epuletszerkezet korul vannak
							for( SymmetricEdgeElementWithPosition symmetricEdgeElementWithPosition: symmetricEdgeElementList ){

								//Megfelelo pozicio
								if( symmetricEdgeElementWithPosition.orientation.equals( Orientation.NORTH ) && x.compareTo( symmetricEdgeElementWithPosition.element.getX1() ) >= 0 && x.compareTo( symmetricEdgeElementWithPosition.element.getX2() ) <= 0 ){
								//if( symmetricEdgeElementWithPosition.orientation.equals( Orientation.NORTH ) && x >= symmetricEdgeElementWithPosition.element.getX1() && x <= symmetricEdgeElementWithPosition.element.getX2() ){									

									actualThermicPoint.connectToSymmetricEdge( Orientation.NORTH );
									break;																	
								}
							}
											
						}
					
						//Ha esetleg meg mindig nincs lezarva a felso-szelso pont NORTH iranyban
						//Ha nincs definialva semmilyen lezaras, akkor az szimmetria pont lesz
						if( i == iSteps && null == actualThermicPoint.getNorthThermicConnector() ){
							actualThermicPoint.connectToSymmetricEdge(  Orientation.NORTH );
						}	
					}
				}			
			}
		}
		
		return new ThermicPointList( thermicPointMap.values() );
		
	}
	
	/**
	 * Megallapitja a lehetseges legnagyobb differencia ertekeket vizszitnes illetve fuggoleges iranyban
	 * 
	 */
	public void doGenerateMaximumDifference(){
		
		ElementDoubleComparator elementDoubleComparator = new ElementDoubleComparator();
		
		LinkedHashSet<BigDecimal> verticalSpacingSet = new LinkedHashSet<>();
		LinkedHashSet<BigDecimal> horizontalSpacingSet = new LinkedHashSet<>();
		
		//Osztaspontok kigyujtese
		for( HetramDrawnElement e: getDrawnBlockList() ){
					
			//Ha igazi Epuletszerkezetrol van szo es nem lezaro elemrol
			if( e instanceof HetramBuildingStructureElement ){
					
				HetramBuildingStructureElement element = (HetramBuildingStructureElement)e;
		
				verticalSpacingSet.add( element.getY1() );
				verticalSpacingSet.add( element.getY2() );
			
				horizontalSpacingSet.add( element.getX1() );
				horizontalSpacingSet.add( element.getX2() );
			}
		}

		//Osztaspontok sorbarendezese
		ArrayList<BigDecimal> verticalSpacingList = new ArrayList<>(verticalSpacingSet); 
		ArrayList<BigDecimal> horizontalSpacingList = new ArrayList<>(horizontalSpacingSet); 

		Collections.sort(verticalSpacingList, elementDoubleComparator );
		Collections.sort(horizontalSpacingList, elementDoubleComparator );	
		
		//Osztaskoz-tavolsagok kiszamitasa		
		ArrayList<BigDecimal> verticalDifferencesList = new ArrayList<>();
		ArrayList<BigDecimal> horizontalDifferencesList = new ArrayList<>();
		
		if( verticalSpacingList.size() != 0 && horizontalSpacingList.size() != 0 ){
		
			BigDecimal startVertical = verticalSpacingList.get(0);		
			for( BigDecimal value : verticalSpacingList ){
				BigDecimal difference = value.subtract( startVertical ).abs();
				if( difference.compareTo( new BigDecimal("0")) != 0 ){
					verticalDifferencesList.add(difference);				
				}
				startVertical = value;
			}
		
			BigDecimal startHorizontal = horizontalSpacingList.get(0);
			for( BigDecimal value : horizontalSpacingList ){
				BigDecimal difference = value.subtract( startHorizontal ).abs();
				if( difference.compareTo( new BigDecimal("0")) != 0 ){
					horizontalDifferencesList.add(difference);				
				}
				startHorizontal = value;
			}
		
			//Osztaskoz-tavolsagok sorbarendezese
			Collections.sort(verticalDifferencesList, elementDoubleComparator );
			Collections.sort(horizontalDifferencesList, elementDoubleComparator );
		
			mainPanel.setVerticalMaximumDifference( getMaximumDifference( verticalDifferencesList ) );
			mainPanel.setHorizontalMaximumDifference( getMaximumDifference( horizontalDifferencesList ) );
		}else{
			mainPanel.setVerticalMaximumDifference( null );
			mainPanel.setHorizontalMaximumDifference( null );
		}
		
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
	private BigDecimal getMaximumDifference( List<BigDecimal> sourceList ){
		int scale = getPrecision().getScale();
		double powered = getPrecision().getPowered();
				
		int a = sourceList.get(0).setScale( scale, RoundingMode.HALF_UP ).multiply( new BigDecimal(powered)).intValue();
		int b;
		
		//vegig az osztaskoz-tavolsagokon
		for( BigDecimal s: sourceList ){

			b = s.setScale( scale, RoundingMode.HALF_UP ).multiply( new BigDecimal(powered)).intValue();
			a = LNKO(a, b);
			
		}
		
		return this.getRoundedBigDecimalWithPrecision( (double)a / powered );
	
/*		int prec = 1000;
		
		int a = (int)( prec * CommonOperations.get10Decimals( sourceList.get(0) ) );
		int b;
		
		//Vegig az osztaskoz-tavolsagokon
		for( Double s: sourceList ){

			b = (int)( prec * CommonOperations.get10Decimals( s ) );

			a = LNKO(a, b);
			
		}
		return CommonOperations.get10Decimals( (double)a / (double)prec );
*/
		
	}
	
	private static class ElementDoubleComparator implements Comparator<BigDecimal>{

		@Override
		public int compare(BigDecimal o1, BigDecimal o2) {
	
			return o1.compareTo(o2);
/*			if( o1 > o2 ){
				return 1;
			}else if( o1 < o2 ){
				return -1;
			}else{
				return 0;
			}
*/			
		}		
	}
}
