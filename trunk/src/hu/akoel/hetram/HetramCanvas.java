package hu.akoel.hetram;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import javax.swing.border.Border;

import hu.akoel.hetram.accessories.BigDecimalPosition;
import hu.akoel.hetram.accessories.Orientation;
import hu.akoel.hetram.gui.MainPanel;
import hu.akoel.hetram.gui.drawingelements.HetramBuildingStructureElement;
import hu.akoel.hetram.gui.drawingelements.HetramDrawnElement;
import hu.akoel.hetram.gui.drawingelements.OpenEdgeElement;
import hu.akoel.hetram.gui.drawingelements.SymmetricEdgeElement;
import hu.akoel.hetram.listeners.HetramMouseListener;
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
	HetramMouseListener hetramMouseListener;
	
	public HetramCanvas(Border borderType, Color background, PossiblePixelPerUnits possiblePixelPerUnits, TranslateValue positionToMiddle, MainPanel mainPanel, Precision precision ) {
		super(borderType, background, possiblePixelPerUnits, positionToMiddle, precision );
		this.mainPanel = mainPanel;
		
		//A regi painterlistener(eke)t torlem
		removePainterListenersFromMiddle();
		
		addPainterListenerToMiddle( new DrawnBlockPainterListener() );
		
		hetramMouseListener = new HetramMouseListener(this);

		//Sajat egerfigyelo hozzaadasa
		this.setDrawnBlockMouseListener( hetramMouseListener );
		
		//
		//Del figyelese
		//
		this.addKeyListener( new KeyAdapter(){
			public void keyPressed(KeyEvent ke){

				//ctrl-z
				if( ke.getKeyCode() == KeyEvent.VK_DELETE && null != hetramMouseListener ){					
						
						//elem kitorlese
						removeDrawnBlock( hetramMouseListener.getSelectedElement() );

						//Ujrarajzoltatom a Canvas-t a torolt DrawnBlock nelkul
						revalidateAndRepaintCoreCanvas();
				}
			 }		
		});
				
	}
	
	/**
	 * Amikor kirajzoltatodik a Middle reteg, akkor a paintByWorldPosition metodus hajtodik vegre,
	 * ami vegig megy a DrawnBlockList-en es kirajzolja az ott levo DrawnBlock-okat
	 * Azert nem az eredetit hasznalom, mert most az eleket a legfelso szinten kell kirajzolnom
	 * 
	 * @author akoel
	 *
	 */
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
	
	
	
	public void clearAllSelected(){
		hetramMouseListener.clearAllSelected();
	}
	
	public MainPanel getMainPanel(){
		return this.mainPanel;
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
	
	/**
	 * eltavolit a listabol egy DrawnBlock element
	 */
	public void removeDrawnBlock( DrawnBlock element ){
		super.removeDrawnBlock( element );
		doGenerateMaximumDifference();
	}
	
	class OpenEdgeElementWithOrientation{
		Orientation orientation;
		OpenEdgeElement element;
	
		public OpenEdgeElementWithOrientation( OpenEdgeElement element, Orientation orientation ){
			
			this.element = element;
			this.orientation = orientation;
		}
	}
	
	class SymmetricEdgeElementWithOrientation{
		Orientation orientation;
		SymmetricEdgeElement element;
	
		public SymmetricEdgeElementWithOrientation( SymmetricEdgeElement element, Orientation orientation ){
			
			this.element = element;
			this.orientation = orientation;
		}
	}
	
	/**
	 * Megallapitja a lehetseges legnagyobb differencia ertekeket vizszitnes illetve fuggoleges iranyban
	 * Egy elem hozzaadasakor illetve elvetelekor kerul meghivasra
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
			
//			mainPanel.setVerticalAppliedDifference(new BigDecimal("1"));
//			mainPanel.setHorizontalAppliedDifference(new BigDecimal("1"));
			
		}else{
			mainPanel.setVerticalMaximumDifference( null );
			mainPanel.setHorizontalMaximumDifference( null );
		}
//		mainPanel.setVerticalAppliedDifference(new BigDecimal("1"));
//		mainPanel.setHorizontalAppliedDifference(new BigDecimal("1"));
		
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

	
//TODO at kell helyezni a mainPanel-be  !!!mit keres itt?????	
	
	class TemporaryThermicPoint{
		double lambda;
	
		public TemporaryThermicPoint( double lambda ){
			this.lambda = lambda;
		}
	}
	
	class TemporaryOpenEdge{
		double alphaStart;
		double alphaEnd;
		double temperature;
		public TemporaryOpenEdge( double alphaStart, double alphaEnd, double temperature ){
			this.alphaEnd = alphaEnd;
			this.alphaStart = alphaStart;
			this.temperature = temperature;
		}		
	}
	
	public ThermicPointList generateThermicPointList( ){

		//Egyel nagyobb pontossag kell a ThermicPoint-ok poziciojanak, mivel a rajzolasi pontossagot megfelezi
		int scale = getPrecision().getScale() + 1;	
		
		BigDecimal halfVerticalAppliedDifference = mainPanel.getVerticalAppliedDifference().divide( new BigDecimal("2"), RoundingMode.HALF_UP);
		BigDecimal halfHorizontalAppliedDifference = mainPanel.getHorizontalAppliedDifference().divide( new BigDecimal("2"), RoundingMode.HALF_UP);
		
		HashMap<BigDecimalPosition, ThermicPoint> thermicPointMap = new HashMap<>();
		HashMap<BigDecimalPosition, TemporaryThermicPoint> temporaryThermicPointMap = new HashMap<>();
		
		/**
		 * STEP 1
		 * 
		 * Csak a termikus pontok osszegyujtese egy MAP-ba a koordinatakkal es lambdakkal
		 */
		for( HetramDrawnElement e: getDrawnBlockList() ){
			
			HetramBuildingStructureElement element;
			
			//Ha igazi Epuletszerkezetrol van szo es nem lezaro elemrol
			if( e instanceof HetramBuildingStructureElement ){
			
				element = (HetramBuildingStructureElement)e;
				
				//Veszem az elem kezdo es veg pozicioit
				BigDecimal startXPoint = element.getX1();
				BigDecimal startYPoint = element.getY1();
				BigDecimal endXPoint = element.getX2();
				BigDecimal endYPoint = element.getY2();
				BigDecimal halfY = element.getY1().add( halfVerticalAppliedDifference );
				BigDecimal halfX = element.getX1().add( halfHorizontalAppliedDifference );
			
				//Mindig a kezdo vertikalis pontbol indulok
				BigDecimal y = startYPoint;
			
				//Vertikalis felbontas
				int iSteps = endYPoint.subtract( y ).divide( mainPanel.getVerticalAppliedDifference(), scale, RoundingMode.HALF_UP  ).intValue();
			
				//Vegig a vertikalis pontokon
				for( int i = 0; i < iSteps; i++){
				
					//Az aktualis vertikalis point
					y = (new BigDecimal(String.valueOf(i)).multiply(mainPanel.getVerticalAppliedDifference() ) ).add( halfY ).setScale( scale, RoundingMode.HALF_UP );
				
					//Elindul a kezdo horizontalis pontbol
					BigDecimal x = startXPoint;			
				
					//Horizontalis felbontas
					int jSteps = endXPoint.subtract( x ).divide( mainPanel.getHorizontalAppliedDifference(), scale, RoundingMode.HALF_UP ).intValue();
				
					//Vegig a horizontalis pontokon
					for( int j = 0; j < jSteps; j++ ){
				
						//Az aktualis horizontalis pont
						x = (new BigDecimal(String.valueOf(j) ).multiply( mainPanel.getHorizontalAppliedDifference() ) ).add( halfX ).setScale( scale, RoundingMode.HALF_UP );

						//Az aktualis pont pozicioja
						BigDecimalPosition position = new BigDecimalPosition(x, y);

						//Rakeresek a taroloban, hatha letezett mar ez elott is
						TemporaryThermicPoint tp = temporaryThermicPointMap.get( position );
					
						//Ha ez a pont meg nem letezett
						if( null == tp ){
						
							//akkor letrehozom
							tp = new TemporaryThermicPoint( element.getLambda() );
						
							//Es el is mentem
							temporaryThermicPointMap.put( position, tp );
					
						}					
					}
				}
			}
		}
		

		/**
		 * STEP 2
		 * 
		 * Az osszes termikus pont-termikus pont kapcsolatanak felepitese
		 */
		TemporaryThermicPoint actualTemporaryThermicPoint;
		BigDecimalPosition westPosition;
		BigDecimalPosition southPosition;
		BigDecimalPosition eastPosition;
		BigDecimalPosition northPosition;
		TemporaryThermicPoint westNeighbourTemporaryTermicPoint;
		TemporaryThermicPoint eastNeighbourTemporaryTermicPoint;
		TemporaryThermicPoint northNeighbourTemporaryTermicPoint;
		TemporaryThermicPoint southNeighbourTemporaryTermicPoint;
		
		for (Map.Entry<BigDecimalPosition, TemporaryThermicPoint> ttpm : temporaryThermicPointMap.entrySet() ) {
			BigDecimalPosition actualPosition = ttpm.getKey();
			actualTemporaryThermicPoint = ttpm.getValue();

			//Eloszor is legyartom ezt a termikus pontot
			ThermicPoint tp = new ThermicPoint( actualPosition );
			
			//Es el is mentem
			thermicPointMap.put( actualPosition, tp );
			
			//Lehetseges WEST iranyu kapcsolat pozicioja
			westPosition = new BigDecimalPosition( actualPosition.getX().subtract( mainPanel.getHorizontalAppliedDifference().setScale(scale, RoundingMode.HALF_UP) ), actualPosition.getY() );

			//Lehetseges SOUTH iranyu kapcsolat pozicioja
			southPosition = new BigDecimalPosition(  actualPosition.getX(), actualPosition.getY().subtract( mainPanel.getVerticalAppliedDifference().setScale(scale, RoundingMode.HALF_UP) ) );
			
			//Lehetseges EAST iranyu kapcsolat pozicioja
			eastPosition = new BigDecimalPosition( actualPosition.getX().add( mainPanel.getHorizontalAppliedDifference().setScale(scale, RoundingMode.HALF_UP) ), actualPosition.getY() );
			
			//Lehetseges NORH iranyu kapcsolat pozicioja
			northPosition = new BigDecimalPosition(  actualPosition.getX(), actualPosition.getY().add( mainPanel.getVerticalAppliedDifference().setScale(scale, RoundingMode.HALF_UP) ) );
			
			//Lehetseges WEST iranyu kapcsolat
			westNeighbourTemporaryTermicPoint = temporaryThermicPointMap.get( westPosition );
			
			//Lehetseges EAST iranyu kapcsolat
			eastNeighbourTemporaryTermicPoint = temporaryThermicPointMap.get( eastPosition );
			
			//Lehetseges NORTH iranyu kapcsolat
			northNeighbourTemporaryTermicPoint = temporaryThermicPointMap.get( northPosition );
			
			//Lehetseges SOUTH iranyu kapcsolat
			southNeighbourTemporaryTermicPoint = temporaryThermicPointMap.get( southPosition );
			
			//Van WEST iranyu kapcsolata
			if( null != westNeighbourTemporaryTermicPoint ){
				
				//A WEST iranyu kapcsolat termikus pontja
				ThermicPoint wtp = thermicPointMap.get( westPosition );
				
				//Ha ez a pont meg nem letezett
				if( null == wtp ){
				
					//akkor letrehozom
					wtp = new ThermicPoint( westPosition );
				
					//Es el is mentem
					thermicPointMap.put( westPosition, wtp );
				}
				
				double lambda = ( actualTemporaryThermicPoint.lambda + westNeighbourTemporaryTermicPoint.lambda ) / 2;
				
				//Letrehozom a WEST kapcsolatot
				tp.connectToThermicPoint( wtp, Orientation.WEST, lambda );
			
			}
			
			//Van EAST iranyu kapcsolata
			if( null != eastNeighbourTemporaryTermicPoint ){
				
				//A EAST iranyu kapcsolat termikus pontja
				ThermicPoint etp = thermicPointMap.get( eastPosition );
				
				//Ha ez a pont meg nem letezett
				if( null == etp ){
				
					//akkor letrehozom
					etp = new ThermicPoint( eastPosition );
				
					//Es el is mentem
					thermicPointMap.put( eastPosition, etp );
				}
				
				double lambda = ( actualTemporaryThermicPoint.lambda + eastNeighbourTemporaryTermicPoint.lambda ) / 2;
				
				//Letrehozom a EAST kapcsolatot
				tp.connectToThermicPoint( etp, Orientation.EAST, lambda );
			
			}
			
			//Van NORTH iranyu kapcsolata
			if( null != northNeighbourTemporaryTermicPoint ){
				
				//A NORTH iranyu kapcsolat termikus pontja
				ThermicPoint ntp = thermicPointMap.get( northPosition );
				
				//Ha ez a pont meg nem letezett
				if( null == ntp ){
				
					//akkor letrehozom
					ntp = new ThermicPoint( northPosition );
				
					//Es el is mentem
					thermicPointMap.put( northPosition, ntp );
				}
				
				double lambda = ( actualTemporaryThermicPoint.lambda + northNeighbourTemporaryTermicPoint.lambda ) / 2;
				
				//Letrehozom a SOUTH kapcsolatot
				tp.connectToThermicPoint( ntp, Orientation.NORTH, lambda );
			}
			
			//Van SOUTH iranyu kapcsolata
			if( null != southNeighbourTemporaryTermicPoint ){
				
				//A SOUTH iranyu kapcsolat termikus pontja
				ThermicPoint stp = thermicPointMap.get( southPosition );
				
				//Ha ez a pont meg nem letezett
				if( null == stp ){
				
					//akkor letrehozom
					stp = new ThermicPoint( southPosition );
				
					//Es el is mentem
					thermicPointMap.put( southPosition, stp );
				}
				
				double lambda = ( actualTemporaryThermicPoint.lambda + southNeighbourTemporaryTermicPoint.lambda ) / 2;
				
				//Letrehozom a SOUTH kapcsolatot
				tp.connectToThermicPoint( stp, Orientation.SOUTH, lambda );
			}
		}

		/**
		 * STEP 3
		 * 
		 * OPENEDGE kapcsolatok kiepitese
		 */		
		
		//Mar nincs szuksegem erre a MAP-re
		temporaryThermicPointMap.clear();
		//Minden elemen vegig megyek megegyszer
		Collection<ThermicPoint> tplCollection = thermicPointMap.values();
		for( ThermicPoint actualThermicPoint : tplCollection ){				
			
			BigDecimalPosition actualPosition = actualThermicPoint.getPosition();
		
			//Ha nincs WEST kapcsolata, akkor lehet hogy van OPENEDGE
			if( null == actualThermicPoint.getWestPair() ){

				//Lehetseges WEST iranyu kapcsolat pozicioja
				westPosition = new BigDecimalPosition( actualPosition.getX().subtract( halfHorizontalAppliedDifference ).setScale(scale, RoundingMode.HALF_UP), actualPosition.getY() );
		
				//
				// OpenEdge
				//							
				//Vegig az OpenEdge elemeken
				for( HetramDrawnElement e: getDrawnBlockList() ){
					
					OpenEdgeElement openEdgeElement;
					
					//Ha igazi OpenEdgeElemt-rol van szo
					if( e instanceof OpenEdgeElement ){
					
						openEdgeElement = (OpenEdgeElement)e;
						
						//A megnovelt pontossagot kell hasznalni osszehasonlitashoz
						BigDecimal oX1 = openEdgeElement.getX1().setScale(scale);
						BigDecimal oY1 = openEdgeElement.getY1().setScale(scale);
						BigDecimal oY2 = openEdgeElement.getY2().setScale(scale);						
						
						if( openEdgeElement.getX1().equals(openEdgeElement.getX2() ) && oX1.equals(westPosition.getX() ) && oY1.compareTo( westPosition.getY() ) <= 0 && oY2.compareTo( westPosition.getY() ) >= 0 ){
							actualThermicPoint.connectToOpenEdge( Orientation.WEST, openEdgeElement.getAlphaByPosition( actualThermicPoint.getPosition().getY().doubleValue() ), openEdgeElement.getTemperature(), openEdgeElement );
						}												
					}
				}
			}
			
			//Ha nincs EAST kapcsolata, akkor lehet hogy van OPENEDGE
			if( null == actualThermicPoint.getEastPair() ){
				
				//Lehetseges EAST iranyu kapcsolat pozicioja
				eastPosition = new BigDecimalPosition( actualPosition.getX().add( halfHorizontalAppliedDifference ).setScale(scale, RoundingMode.HALF_UP), actualPosition.getY() );
	
				//
				// OpenEdge
				//							
				//Vegig az OpenEdge elemeken
				for( HetramDrawnElement e: getDrawnBlockList() ){
					
					OpenEdgeElement openEdgeElement;
					
					//Ha igazi OpenEdgeElemt-rol van szo
					if( e instanceof OpenEdgeElement ){
					
						openEdgeElement = (OpenEdgeElement)e;
						
						//A megnovelt pontossagot kell hasznalni osszehasonlitashoz
						BigDecimal oX1 = openEdgeElement.getX1().setScale(scale);
						BigDecimal oY1 = openEdgeElement.getY1().setScale(scale);
						BigDecimal oY2 = openEdgeElement.getY2().setScale(scale);		
						
						if( openEdgeElement.getX1().equals(openEdgeElement.getX2() ) && oX1.equals(eastPosition.getX() ) && oY1.compareTo( eastPosition.getY() ) <= 0 && oY2.compareTo( eastPosition.getY() ) >= 0 ){
							actualThermicPoint.connectToOpenEdge( Orientation.EAST, openEdgeElement.getAlphaByPosition( actualThermicPoint.getPosition().getY().doubleValue() ), openEdgeElement.getTemperature(), openEdgeElement );
						}												
					}
				}
			}
			
			//Ha nincs NORTH kapcsolata, akkor lehet hogy van OPENEDGE
			if( null == actualThermicPoint.getNorthPair() ){

				//Lehetseges NORH iranyu kapcsolat pozicioja
				northPosition = new BigDecimalPosition(  actualPosition.getX(), actualPosition.getY().add( halfVerticalAppliedDifference ).setScale(scale, RoundingMode.HALF_UP) );
				
				//
				// OpenEdge
				//							
				//Vegig az OpenEdge elemeken
				for( HetramDrawnElement e: getDrawnBlockList() ){
					
					OpenEdgeElement openEdgeElement;
					
					//Ha igazi OpenEdgeElemt-rol van szo
					if( e instanceof OpenEdgeElement ){
					
						openEdgeElement = (OpenEdgeElement)e;
						
						//A megnovelt pontossagot kell hasznalni osszehasonlitashoz
						BigDecimal oY1 = openEdgeElement.getY1().setScale(scale);
						BigDecimal oX1 = openEdgeElement.getX1().setScale(scale);
						BigDecimal oX2 = openEdgeElement.getX2().setScale(scale);	
						
						if( openEdgeElement.getY1().equals(openEdgeElement.getY2() ) && oY1.equals(northPosition.getY() ) && oX1.compareTo( northPosition.getX() ) <= 0 && oX2.compareTo( northPosition.getX()) >= 0 ){
							actualThermicPoint.connectToOpenEdge( Orientation.NORTH, openEdgeElement.getAlphaByPosition( actualThermicPoint.getPosition().getX().doubleValue() ), openEdgeElement.getTemperature(), openEdgeElement );
						}												
					}
				}
			}
			
			//Ha nincs SOUTH kapcsolata, akkor lehet hogy van OPENEDGE
			if( null == actualThermicPoint.getSouthPair() ){				
			
				//Lehetseges SOUTH iranyu kapcsolat pozicioja
				southPosition = new BigDecimalPosition(  actualPosition.getX(), actualPosition.getY().subtract( halfVerticalAppliedDifference ).setScale(scale, RoundingMode.HALF_UP) );
			
				//
				// OpenEdge
				//							
				//Vegig az OpenEdge elemeken
				for( HetramDrawnElement e: getDrawnBlockList() ){
					
					OpenEdgeElement openEdgeElement;
					
					//Ha igazi OpenEdgeElemt-rol van szo
					if( e instanceof OpenEdgeElement ){
					
						openEdgeElement = (OpenEdgeElement)e;
						
						//A megnovelt pontossagot kell hasznalni osszehasonlitashoz
						BigDecimal oY1 = openEdgeElement.getY1().setScale(scale);
						BigDecimal oX1 = openEdgeElement.getX1().setScale(scale);
						BigDecimal oX2 = openEdgeElement.getX2().setScale(scale);	
						
						if( openEdgeElement.getY1().equals(openEdgeElement.getY2() ) && oY1.equals(southPosition.getY() ) && oX1.compareTo( southPosition.getX() ) <= 0 && oX2.compareTo( southPosition.getX()) >= 0 ){
							actualThermicPoint.connectToOpenEdge( Orientation.SOUTH, openEdgeElement.getAlphaByPosition( actualThermicPoint.getPosition().getX().doubleValue() ), openEdgeElement.getTemperature(), openEdgeElement );
						}												
					}
				}				
			}
		}		
	
		/**
		 * STEP 4
		 * 
		 * SYMMETRIC kapcsolatok kiepitese
		 */		
		
		//Minden elemen vegig megyek megegyszer utoljara
		tplCollection = thermicPointMap.values();
		for( ThermicPoint actualThermicPoint : tplCollection ){				
				
			//BigDecimalPosition actualPosition = actualThermicPoint.getPosition();
				
			//Ha nincs WEST kapcsolata, akkor az SYMMETRIC lesz
			if( null == actualThermicPoint.getWestThermicConnector() ){
				actualThermicPoint.connectToSymmetricEdge( Orientation.WEST );
			}
				
			//Ha nincs EAST kapcsolata, akkor az SYMMETRIC lesz
			if( null == actualThermicPoint.getEastThermicConnector() ){
				actualThermicPoint.connectToSymmetricEdge( Orientation.EAST );
			}
				
			//Ha nincs NORTH kapcsolata, akkor az SYMMETRIC lesz
			if( null == actualThermicPoint.getNorthThermicConnector() ){
				actualThermicPoint.connectToSymmetricEdge( Orientation.NORTH );					
			}
				
			//Ha nincs SOUTH kapcsolata, akkor az SYMMETRIC lesz
			if( null == actualThermicPoint.getSouthThermicConnector() ){				
				actualThermicPoint.connectToSymmetricEdge( Orientation.SOUTH );								
			}

		}				
		
		return new ThermicPointList( tplCollection, mainPanel.getHorizontalAppliedDifference(), mainPanel.getVerticalAppliedDifference() );
	
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
		
	}
	
	private static class ElementDoubleComparator implements Comparator<BigDecimal>{

		@Override
		public int compare(BigDecimal o1, BigDecimal o2) {
	
			return o1.compareTo(o2);
			
		}		
	}
	
	
}
