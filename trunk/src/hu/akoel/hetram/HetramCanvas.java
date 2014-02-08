package hu.akoel.hetram;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.border.Border;

import hu.akoel.hetram.accessories.CommonOperations;
import hu.akoel.hetram.accessories.Orientation;
import hu.akoel.hetram.accessories.Position;
import hu.akoel.hetram.connectors.AThermicPointThermicConnector;
import hu.akoel.hetram.gui.drawingelements.HetramBuildingStructureElement;
import hu.akoel.hetram.gui.drawingelements.HetramDrawnElement;
import hu.akoel.hetram.structures.AStructureSealing;
import hu.akoel.hetram.structures.Structure;
import hu.akoel.hetram.structures.SurfaceSealing;
import hu.akoel.hetram.structures.SymmetricSealing;
import hu.akoel.hetram.thermicpoint.ThermicPoint;
import hu.akoel.hetram.thermicpoint.ThermicPointList;
import hu.akoel.mgu.PossiblePixelPerUnits;
import hu.akoel.mgu.drawnblock.DrawnBlock;
import hu.akoel.mgu.drawnblock.DrawnBlockCanvas;
import hu.akoel.mgu.values.TranslateValue;

public class HetramCanvas extends DrawnBlockCanvas{

	private double verticalMaximumDifference = -1;
	private double horizontalMaximumDifference = -1;
	
	private double verticalAppliedDifference;
	private double horizontalAppliedDifference;
	
	private int verticalDifferenceDivider = 1;
	private int horizontalDifferenceDivider = 1;
	
	public HetramCanvas(Border borderType, Color background, PossiblePixelPerUnits possiblePixelPerUnits, TranslateValue positionToMiddle) {
		super(borderType, background, possiblePixelPerUnits, positionToMiddle);
	}

	private static final long serialVersionUID = -6015123637668801411L;

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
	 * Hozzaad a megjelenitendo listahoz egy DrawnBlock-ot
	 * 
	 * @param drawnElement
	 */
	public void addDrawnBlock( HetramDrawnElement drawnElement ){
		super.addDrawnBlock(drawnElement);
	}
	
	/**
	 * Eltavolit egy DrawnBlock elemet a megjelenitendo DrawnBlock listabol
	 * 
	 * @param drawnBlock
	 */
	public void removeDrawnBlock( HetramDrawnElement drawnElement ){
		super.removeDrawnBlock(drawnElement);
	}
		
	/**
	 * Hozzaad egy DrawnBlock elemet a Temporary listahoz atmeneti megjelenitesre
	 * 
	 * @param drawnBlock
	 */
	public void addTemporaryDrawnBlock( HetramDrawnElement drawnElement ){
		super.addTemporaryDrawnBlock(drawnElement);
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
	
	/**
	 * Automatikusan felbontja kis differencialis negyzetekre az osszes elemet,
	 * legyartja a termikus pontokat es megteremti kozottuk a kapcsolatot
	 * !!! Szamitas nem tortenik !!!
	 * 
	 * @param askedHorizontalDifference
	 * @param askedVerticalDifference
	 * @return
	 */
/*
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
		for( HetramDrawnElement element: getDrawnBlockList() ){
			
			//Ha igazi Epuletszerkezetrol van szo es nem lezaro elemrol
			if( element instanceof HetramBuildingStructureElement ){
			
			//Veszem az elem kezdo es veg pozicioit
			double startXPoint = element.getX1();
			double startYPoint = element.getY1();
			double endXPoint = element.getX2();
			double endYPoint = element.getY2();
			
			double lambda;		

			//Mindig a kezdo vertikalis pontbol indulok
			double y = startYPoint;
			
			//Vertikalis felbontas
			int iSteps = (int)Math.round((endYPoint - y) / verticalAppliedDifference );
			
			//Vegig a vertikalis pontokon
			for( int i = 0; i <= iSteps; i++){
				
				//Az aktualis vertikalis point
				y = CommonOperations.get10Decimals( startYPoint + i * verticalAppliedDifference );
				
				//Elindul a kezdo horizontalis pontbol
				double x = startXPoint;			
				
				//Horizontalis felbontas
				int jSteps = (int)Math.round((endXPoint - x) / horizontalAppliedDifference);
				
				//Vegig a horizontalis pontokon
				for( int j = 0; j <= jSteps; j++ ){
				
					//Az aktualis horizontalis pont
					x = CommonOperations.get10Decimals( startYPoint + j * horizontalAppliedDifference );

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
						double previousX = CommonOperations.get10Decimals( startPoint.getX() + (j - 1) * horizontalAppliedDifference );

						//Es osszekottetest letesitek vele
						tp.connectToThermicPoint(thermicPointMap.get(new Position(previousX, y)), Orientation.WEST, lambda );
					
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
						double previousY = CommonOperations.get10Decimals( startPoint.getY() + (i - 1) * verticalAppliedDifference );						
						tp.connectToThermicPoint(thermicPointMap.get(new Position(x, previousY)), Orientation.SOUTH, lambda );
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
							if( closeElement.getOrientation().equals( Orientation.WEST ) && y >= closeElement.getLength().getStart() && y <= closeElement.getLength().getEnd() ){

								//Fal felulet
								if( closeElement instanceof SurfaceSealing ){

									//Alfa es homerseklet kapcsolasa
									actualThermicPoint.connectToOpenEdge( Orientation.WEST, ((SurfaceSealing)closeElement).getAlpha(), ((SurfaceSealing)closeElement).getAirTemperature() );		
									break;
								
								//Szimmetria el
								}else if( closeElement instanceof SymmetricSealing ){
									
									actualThermicPoint.connectToSymmetricEdge( Orientation.WEST);
									break;
								
								}									
							}
						}
					}
					
					//Ha esetleg meg mindig nincs lezarva a bal-szelso pont WEST iranyban
					//Ha nincs definialva semmilyen lezaras, akkor az szimmetria pont lesz
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

						for( AStructureSealing closeElement: closeElements ){
						
							//Megfelelo pozicio
							if( closeElement.getOrientation().equals( Orientation.EAST ) && y >= closeElement.getLength().getStart() && y <= closeElement.getLength().getEnd() ){
							
								//Fal felulet
								if( closeElement instanceof SurfaceSealing ){
								
									//Alfa es homerseklet kapcsolasa
									actualThermicPoint.connectToOpenEdge( Orientation.EAST, ((SurfaceSealing)closeElement).getAlpha(), ((SurfaceSealing)closeElement).getAirTemperature() );
									break;
									
								//Szimmetria el
								}else if( closeElement instanceof SymmetricSealing ){
	
									actualThermicPoint.connectToSymmetricEdge( Orientation.EAST );
									break;
									
								}
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
						
						for( AStructureSealing closeElement: closeElements ){
						
							//Megfelelo pozicio
							if( closeElement.getOrientation().equals( Orientation.SOUTH ) && x >= closeElement.getLength().getStart() && x <= closeElement.getLength().getEnd() ){
							
								//Fal felulet
								if( closeElement instanceof SurfaceSealing ){
								
									//Alfa es homerseklet kapcsolasa
									actualThermicPoint.connectToOpenEdge( Orientation.SOUTH, ((SurfaceSealing)closeElement).getAlpha(), ((SurfaceSealing)closeElement).getAirTemperature() );
									break;
										
								//Szimmetria el
								}else if( closeElement instanceof SymmetricSealing ){
									
									actualThermicPoint.connectToSymmetricEdge(  Orientation.SOUTH );
									break;
									
								}
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
						
						for( AStructureSealing closeElement: closeElements ){
						
							//Megfelelo pozicio
							if( closeElement.getOrientation().equals( Orientation.NORTH ) && x >= closeElement.getLength().getStart() && x <= closeElement.getLength().getEnd() ){
							
								//Fal felulet
								if( closeElement instanceof SurfaceSealing ){								
								
									actualThermicPoint.connectToOpenEdge( Orientation.NORTH, ((SurfaceSealing)closeElement).getAlpha(), ((SurfaceSealing)closeElement).getAirTemperature() );
									break;
							
								//Szimmetria el
								}else if( closeElement instanceof SymmetricSealing ){
								
									actualThermicPoint.connectToSymmetricEdge(  Orientation.NORTH);
									break;
								}
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

		
		
		return new ThermicPointList( thermicPointMap.values(), this );
		
	}
*/
}
