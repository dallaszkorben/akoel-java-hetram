package hu.akoel.hetram;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import hu.akoel.hetram.accessories.BigDecimalPosition;
import hu.akoel.hetram.connectors.IThermicConnector;
import hu.akoel.hetram.connectors.OpenEdgeThermicConnector;
import hu.akoel.hetram.gui.StatusLine;
import hu.akoel.hetram.gui.drawingelements.OpenEdgeElement;
import hu.akoel.hetram.thermicpoint.ThermicPoint;
import hu.akoel.hetram.thermicpoint.ThermicPointList;
import hu.akoel.mgu.MGraphics;
import hu.akoel.mgu.drawnblock.DrawnBlock.Status;
import hu.akoel.mgu.drawnblock.DrawnBlockCanvas;

public class SelectedOpenEdgeForSumQList {
	private Collection<OpenEdgeElement> list = new ArrayList<OpenEdgeElement>();
	
	public void add( OpenEdgeElement openEdgeElement ){
		this.list.add( openEdgeElement );
	}
	
	public void clear(){
		this.list.clear();
	}
	
	public void drawOpenEdge( DrawnBlockCanvas canvas, MGraphics g2 ){
		Status status;
		
		for( OpenEdgeElement openEdgeElement: list){
	
			status = openEdgeElement.getStatus();
			openEdgeElement.setStatus( Status.SELECTED );
			openEdgeElement.draw(g2);
			openEdgeElement.setStatus( status );
			
		}
	}
	
	public void writeOpenEdge( ThermicPointList thermicPointList, StatusLine statusLine ){
			
		BigDecimal x1, x2, y1, y2;
		for( OpenEdgeElement openEdgeElement: list){
			
			ThermicPoint tp;
			BigDecimalPosition position;
			
			x1= openEdgeElement.getX1();
			x2= openEdgeElement.getX2();
			y1= openEdgeElement.getY1();
			y2= openEdgeElement.getY2();
			
			//-----------
			//
			//Vertikalis
			//
			//-----------
			if( x1.equals(x2) ){
			
				double eastCurrent = 0;
				double westCurrent = 0;
				
				IThermicConnector cE;
				IThermicConnector cW;
				OpenEdgeThermicConnector xE;
				OpenEdgeThermicConnector xW;
				
				for( int i = 0;  i < thermicPointList.getSize(); i++ ){
					tp = thermicPointList.get( i );
					
					position = tp.getPosition();
					
					//Azok a termikus pontok, akik az OpenEdge vonallal egybe esnek
					if( x1.equals( position.getX() ) && position.getY().compareTo(y1) >= 0 && position.getY().compareTo(y2) <= 0 ){
					
						//K fele OpenEdge - tehat innen jon az aram
						cE = tp.getEastThermicConnector();
						if( cE instanceof OpenEdgeThermicConnector ){
							eastCurrent += tp.getEastCurrent(); 
						}
						
						//NY fele OpenEdge - tehat innen jon az aram
						cW = tp.getWestThermicConnector();
						if( cW instanceof OpenEdgeThermicConnector ){
							westCurrent += tp.getWestCurrent(); 
						}
						
						xE = tp.getExtraEastOpenEdgeConnector();
						if( null != xE ){
							eastCurrent += tp.getExtraEastCurrent();
						}

						xW = tp.getExtraWestOpenEdgeConnector();
						if( null != xW ){
							westCurrent += tp.getExtraWestCurrent();
						}

					}
						
				}

				statusLine.setVerticalOpenEdgeSumQ( eastCurrent, westCurrent );
				
			//-------------
			//
			//Horizontalis				
			//
			//-------------
			}if( y1.equals(y2) ){
			
				double northCurrent = 0;
				double southCurrent = 0;
				
				IThermicConnector cN;
				IThermicConnector cS;
				OpenEdgeThermicConnector xN;
				OpenEdgeThermicConnector xS;
				
				for( int i = 0;  i < thermicPointList.getSize(); i++ ){
					tp = thermicPointList.get( i );
					
					position = tp.getPosition();
					
					//Azok a termikus pontok, akik az OpenEdge vonallal egybe esnek
					if( y1.equals( position.getY() ) && position.getX().compareTo(x1) >= 0 && position.getX().compareTo(x2) <= 0 ){

						//E fele OpenEdge - tehat innen jon az aram
						cN = tp.getNorthThermicConnector();
						if( cN instanceof OpenEdgeThermicConnector ){
							northCurrent += tp.getNorthCurrent(); 					
						}
						
						//D fele OpenEdge - tehat innen jon az aram
						cS = tp.getSouthThermicConnector();
						if( cS instanceof OpenEdgeThermicConnector ){
							southCurrent += tp.getSouthCurrent();							
						}
						
						xN = tp.getExtraNorthOpenEdgeConnector();
						if( null != xN ){
							northCurrent += tp.getExtraNorthCurrent();
						}

						xS = tp.getExtraSouthOpenEdgeConnector();
						if( null != xS ){
							southCurrent += tp.getExtraSouthCurrent();
						}

					}
						
				}

				statusLine.setHorizontalOpenEdgeSumQ( northCurrent, southCurrent );
				
			}
			
		}
	}	
}
